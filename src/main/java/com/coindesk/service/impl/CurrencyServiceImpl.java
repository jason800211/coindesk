package com.coindesk.service.impl;

import com.coindesk.dto.CurrencyDto;
import com.coindesk.dto.CoindeskDTO;
import com.coindesk.dto.coindesk.CoindeskResponse;
import com.coindesk.entity.CurrencyEntity;
import com.coindesk.repository.CurrencyRepository;
import com.coindesk.repository.ExchangeRateRepository;
import com.coindesk.service.CoindeskApiService;
import com.coindesk.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CoindeskApiService coindeskApiService;

    @Autowired
    public CurrencyServiceImpl(
            CurrencyRepository currencyRepository,
            CoindeskApiService coindeskApiService,
            ExchangeRateRepository exchangeRateRepository) {
        this.currencyRepository = currencyRepository;
        this.coindeskApiService = coindeskApiService;
    }

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyDto getCurrencyByCode(String code) {
        CurrencyEntity entity = currencyRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException("找不到幣別代碼: " + code));
        return convertToDto(entity);
    }

    @Override
    public CurrencyDto createCurrency(CurrencyDto currencyDto) {
        CurrencyEntity entity = convertToEntity(currencyDto);
        CurrencyEntity savedEntity = currencyRepository.save(entity);
        return convertToDto(savedEntity);
    }

    @Override
    public CurrencyDto updateCurrency(String code, CurrencyDto currencyDto) {
        if (!currencyRepository.existsById(code)) {
            throw new EntityNotFoundException("找不到幣別代碼: " + code);
        }
        
        CurrencyEntity entity = convertToEntity(currencyDto);
        entity.setCode(code);
        CurrencyEntity updatedEntity = currencyRepository.save(entity);
        return convertToDto(updatedEntity);
    }

    @Override
    public void deleteCurrency(String code) {
        if (!currencyRepository.existsById(code)) {
            throw new EntityNotFoundException("找不到幣別代碼: " + code);
        }
        currencyRepository.deleteById(code);
    }
    
    @Override
    public List<CurrencyDto> getTransformedCurrencies() {
        // 目前簡單返回所有幣別，實際應用中可能需要進行更多轉換
        return getAllCurrencies();
    }
    
    @Override
    public CoindeskDTO getOriginalData() {
        // 這裡應該調用外部 API 或返回緩存數據
        // 暫時返回空對象，實際應用中需要實現這個方法
        return new CoindeskDTO();
    }
    
    private CurrencyDto convertToDto(CurrencyEntity entity) {
        CurrencyDto dto = new CurrencyDto(
                entity.getCode(),
                entity.getChineseName(),
                entity.getEnglishName()
        );
        
        // 嘗試從 Coindesk API 獲取最新匯率數據
        try {
            CoindeskResponse coindeskResponse = coindeskApiService.getOriginalCoindeskData();
            Map<String, CoindeskResponse.CurrencyInfo> bpi = coindeskResponse.getBpi();
            if (bpi != null && bpi.containsKey(entity.getCode())) {
                CoindeskResponse.CurrencyInfo currencyInfo = bpi.get(entity.getCode());
                dto.setSymbol(currencyInfo.getSymbol());
                dto.setRate(currencyInfo.getRate());
                dto.setDescription(currencyInfo.getDescription());
                dto.setRateFloat(currencyInfo.getRateFloat());
                dto.setUpdateTime(coindeskResponse.getTime().getUpdatedISO());
            }
        } catch (Exception e) {
            // 如果無法從 API 獲取數據，嘗試從數據庫獲取
            // 這裡可以選擇記錄錯誤日誌
            System.err.println("無法從 Coindesk API 獲取匯率: " + e.getMessage());
        }
        
        return dto;
    }
    
    private CurrencyEntity convertToEntity(CurrencyDto dto) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setCode(dto.getCode());
        entity.setChineseName(dto.getChineseName());
        entity.setEnglishName(dto.getEnglishName());
        return entity;
    }
} 