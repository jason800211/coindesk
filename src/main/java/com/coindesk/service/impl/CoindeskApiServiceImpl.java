package com.coindesk.service.impl;

import com.coindesk.dto.CustomCurrencyResponse;
import com.coindesk.dto.coindesk.CoindeskResponse;
import com.coindesk.entity.CoindeskDataEntity;
import com.coindesk.entity.CurrencyEntity;
import com.coindesk.entity.ExchangeRateEntity;
import com.coindesk.repository.CoindeskDataRepository;
import com.coindesk.repository.CurrencyRepository;
import com.coindesk.repository.ExchangeRateRepository;
import com.coindesk.service.CoindeskApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class CoindeskApiServiceImpl implements CoindeskApiService {

    private final CurrencyRepository currencyRepository;
    private final CoindeskDataRepository coindeskDataRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ObjectMapper objectMapper;
    private CoindeskResponse cachedResponse;
    
    @Autowired
    public CoindeskApiServiceImpl(
            RestTemplate restTemplate,
            CurrencyRepository currencyRepository,
            CoindeskDataRepository coindeskDataRepository,
            ExchangeRateRepository exchangeRateRepository,
            @Value("${coindesk.api.url}") String coindeskApiUrl) {
        this.currencyRepository = currencyRepository;
        this.coindeskDataRepository = coindeskDataRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CoindeskResponse getOriginalCoindeskData() {
        if (cachedResponse != null) {
            return cachedResponse;
        }
        
        // 先從數據庫查詢最新記錄
        Optional<CoindeskDataEntity> latestData = coindeskDataRepository.findLatest();
        if (latestData.isPresent()) {
            CoindeskDataEntity entity = latestData.get();
            List<ExchangeRateEntity> rates = exchangeRateRepository.findByCoindeskData(entity);
            
            if (!rates.isEmpty()) {
                return convertToCoindeskResponse(entity, rates);
            }
        }
        
        try {
            // 從靜態文件讀取 JSON 數據
            ClassPathResource resource = new ClassPathResource("static/sample-data.json");
            cachedResponse = objectMapper.readValue(resource.getInputStream(), CoindeskResponse.class);
            return cachedResponse;
        } catch (IOException e) {
            throw new RuntimeException("無法讀取範例數據文件", e);
        }
    }
    
    @Override
    @Transactional
    public void saveCoindeskData(CoindeskResponse coindeskResponse) {
        // 儲存到快取
        this.cachedResponse = coindeskResponse;
        
        // 創建並保存 CoindeskData 實體
        CoindeskDataEntity coindeskEntity = new CoindeskDataEntity();
        coindeskEntity.setUpdatedTime(coindeskResponse.getTime().getUpdated());
        coindeskEntity.setUpdatedIso(coindeskResponse.getTime().getUpdatedISO());
        coindeskEntity.setUpdatedUk(coindeskResponse.getTime().getUpdateduk());
        coindeskEntity.setDisclaimer(coindeskResponse.getDisclaimer());
        coindeskEntity.setChartName(coindeskResponse.getChartName());
        coindeskEntity.setCreateTime(LocalDateTime.now());
        
        CoindeskDataEntity savedCoindeskEntity = coindeskDataRepository.save(coindeskEntity);
        
        // 儲存幣別和匯率資訊
        Map<String, CoindeskResponse.CurrencyInfo> bpiMap = coindeskResponse.getBpi();
        for (Map.Entry<String, CoindeskResponse.CurrencyInfo> entry : bpiMap.entrySet()) {
            String code = entry.getKey();
            CoindeskResponse.CurrencyInfo currencyInfo = entry.getValue();
            
            // 處理幣別實體
            CurrencyEntity currencyEntity = processCurrencyEntity(code, currencyInfo);
            
            // 創建並保存匯率實體
            ExchangeRateEntity rateEntity = new ExchangeRateEntity();
            rateEntity.setCoindeskData(savedCoindeskEntity);
            rateEntity.setCurrency(currencyEntity);
            rateEntity.setSymbol(currencyInfo.getSymbol());
            rateEntity.setRate(currencyInfo.getRate());
            rateEntity.setRateFloat(currencyInfo.getRateFloat());
            
            exchangeRateRepository.save(rateEntity);
        }
    }

    @Override
    public CustomCurrencyResponse getTransformedCoindeskData() {
        CoindeskResponse originalData = getOriginalCoindeskData();
        
        // 格式化時間為要求的格式 (1990/01/01 00:00:00)
        String formattedTime = formatDateTime(originalData.getTime().getUpdatedISO());
        
        // 組合幣別資訊
        List<CustomCurrencyResponse.CurrencyInfo> currencyInfoList = new ArrayList<>();
        Map<String, CoindeskResponse.CurrencyInfo> bpiMap = originalData.getBpi();
        
        for (Map.Entry<String, CoindeskResponse.CurrencyInfo> entry : bpiMap.entrySet()) {
            String code = entry.getKey();
            CoindeskResponse.CurrencyInfo currencyInfo = entry.getValue();
            
            // 從數據庫取得中文名稱
            String chineseName = getCurrencyChineseName(code);
            
            CustomCurrencyResponse.CurrencyInfo customInfo = new CustomCurrencyResponse.CurrencyInfo(
                    code,
                    chineseName,
                    currencyInfo.getRateFloat()
            );
            
            currencyInfoList.add(customInfo);
        }
        
        return new CustomCurrencyResponse(formattedTime, currencyInfoList);
    }
    
    /**
     * 從 CoindeskDataEntity 和 ExchangeRateEntity 列表轉換為 CoindeskResponse
     */
    private CoindeskResponse convertToCoindeskResponse(CoindeskDataEntity entity, List<ExchangeRateEntity> rates) {
        CoindeskResponse response = new CoindeskResponse();
        
        // 設置基本資訊
        response.setDisclaimer(entity.getDisclaimer());
        response.setChartName(entity.getChartName());
        
        // 設置時間資訊
        CoindeskResponse.TimeInfo timeInfo = new CoindeskResponse.TimeInfo();
        timeInfo.setUpdated(entity.getUpdatedTime());
        timeInfo.setUpdatedISO(entity.getUpdatedIso());
        timeInfo.setUpdateduk(entity.getUpdatedUk());
        response.setTime(timeInfo);
        
        // 設置幣別資訊
        Map<String, CoindeskResponse.CurrencyInfo> bpi = new java.util.HashMap<>();
        
        for (ExchangeRateEntity rate : rates) {
            CoindeskResponse.CurrencyInfo currencyInfo = new CoindeskResponse.CurrencyInfo();
            currencyInfo.setCode(rate.getCurrency().getCode());
            currencyInfo.setSymbol(rate.getSymbol());
            currencyInfo.setRate(rate.getRate());
            currencyInfo.setDescription(rate.getCurrency().getEnglishName());
            currencyInfo.setRateFloat(rate.getRateFloat());
            
            bpi.put(rate.getCurrency().getCode(), currencyInfo);
        }
        
        response.setBpi(bpi);
        return response;
    }
    
    /**
     * 處理幣別實體：如果存在則返回，否則創建新的
     */
    private CurrencyEntity processCurrencyEntity(String code, CoindeskResponse.CurrencyInfo currencyInfo) {
        // 檢查資料庫中是否已存在此幣別
        Optional<CurrencyEntity> existingCurrency = currencyRepository.findById(code);
        
        // 創建或更新幣別實體
        CurrencyEntity entity;
        if (existingCurrency.isPresent()) {
            entity = existingCurrency.get();
            entity.setEnglishName(currencyInfo.getDescription());
            // 中文名稱保持不變
        } else {
            entity = new CurrencyEntity();
            entity.setCode(code);
            entity.setEnglishName(currencyInfo.getDescription());
            
            // 設置預設中文名稱 (可根據幣別代碼添加不同的預設名稱)
            switch (code) {
                case "USD":
                    entity.setChineseName("美元");
                    break;
                case "GBP":
                    entity.setChineseName("英鎊");
                    break;
                case "EUR":
                    entity.setChineseName("歐元");
                    break;
                case "JPY":
                    entity.setChineseName("日圓");
                    break;
                default:
                    entity.setChineseName(code); // 如果沒有預設中文名稱，使用代碼代替
            }
        }
        
        // 保存到資料庫
        return currencyRepository.save(entity);
    }
    
    private String formatDateTime(String isoDateTimeString) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDateTimeString);
            
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // 如果解析失敗，返回原始字符串
            return isoDateTimeString;
        }
    }
    
    private String getCurrencyChineseName(String code) {
        return currencyRepository.findById(code)
                .map(CurrencyEntity::getChineseName)
                .orElse(code); // 如果找不到中文名稱，返回代碼作為替代
    }
} 