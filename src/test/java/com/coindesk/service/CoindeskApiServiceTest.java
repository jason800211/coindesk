package com.coindesk.service;

import com.coindesk.dto.CustomCurrencyResponse;
import com.coindesk.dto.coindesk.CoindeskResponse;
import com.coindesk.entity.CurrencyEntity;
import com.coindesk.entity.CoindeskDataEntity;
import com.coindesk.repository.CoindeskDataRepository;
import com.coindesk.repository.CurrencyRepository;
import com.coindesk.repository.ExchangeRateRepository;
import com.coindesk.service.impl.CoindeskApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Coindesk API 服務單元測試
 * 專注於測試資料轉換相關邏輯
 */
@ExtendWith(MockitoExtension.class)
public class CoindeskApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CoindeskDataRepository coindeskDataRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Spy
    @InjectMocks
    private CoindeskApiServiceImpl coindeskApiService;

    private CoindeskResponse sampleResponse;

    @BeforeEach
    void setUp() {
        // 設置測試數據 - 僅初始化共用的測試數據，不進行任何模擬
        sampleResponse = createSampleCoindeskResponse();
    }

    /**
     * 測試資料轉換邏輯
     */
    @Test
    void testTransformedCoindeskData() {
        // 模擬 currencyRepository 行為 - 只在此測試中需要的模擬
        CurrencyEntity usdEntity = new CurrencyEntity();
        usdEntity.setCode("USD");
        usdEntity.setChineseName("美元");
        usdEntity.setEnglishName("United States Dollar");

        CurrencyEntity gbpEntity = new CurrencyEntity();
        gbpEntity.setCode("GBP");
        gbpEntity.setChineseName("英鎊");
        gbpEntity.setEnglishName("British Pound Sterling");

        CurrencyEntity eurEntity = new CurrencyEntity();
        eurEntity.setCode("EUR");
        eurEntity.setChineseName("歐元");
        eurEntity.setEnglishName("Euro");

        when(currencyRepository.findById("USD")).thenReturn(Optional.of(usdEntity));
        when(currencyRepository.findById("GBP")).thenReturn(Optional.of(gbpEntity));
        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(eurEntity));
        
        // 使用 doReturn 而不是 when 來避免 WrongTypeOfReturnValue 錯誤
        doReturn(sampleResponse).when(coindeskApiService).getOriginalCoindeskData();

        // 呼叫服務方法
        CustomCurrencyResponse transformedData = coindeskApiService.getTransformedCoindeskData();

        // 驗證轉換後的數據
        System.out.println("\n========== 測試資料轉換邏輯 ==========");
        System.out.println("原始 Coindesk 數據：");
        System.out.println(" - 時間：" + sampleResponse.getTime().getUpdatedISO());
        System.out.println(" - 幣別數量：" + sampleResponse.getBpi().size());
        
        System.out.println("\n轉換後數據：");
        System.out.println(" - 時間：" + transformedData.getUpdateTime());
        System.out.println(" - 幣別數量：" + transformedData.getCurrencies().size());
        System.out.println(" - 幣別列表：" + transformedData.getCurrencies());

        // 檢查轉換是否正確
        assertThat(transformedData).isNotNull();
        assertThat(transformedData.getUpdateTime()).isNotEmpty();
        assertThat(transformedData.getCurrencies()).hasSize(sampleResponse.getBpi().size());
        
        // 檢查每個幣別的資訊是否正確
        transformedData.getCurrencies().forEach(currency -> {
            String code = currency.getCode();
            assertThat(code).isIn("USD", "GBP", "EUR");
            
            CoindeskResponse.CurrencyInfo originalInfo = sampleResponse.getBpi().get(code);
            assertThat(currency.getRate()).isEqualTo(originalInfo.getRateFloat());
            
            // 確認幣別中文名稱
            if ("USD".equals(code)) {
                assertThat(currency.getChineseName()).isEqualTo("美元");
            } else if ("GBP".equals(code)) {
                assertThat(currency.getChineseName()).isEqualTo("英鎊");
            } else if ("EUR".equals(code)) {
                assertThat(currency.getChineseName()).isEqualTo("歐元");
            }
        });
    }
    
    /**
     * 測試存儲 Coindesk 數據邏輯
     */
    @Test
    void testSaveCoindeskData() {
        // 1. 創建一個實體以模擬保存操作
        CoindeskDataEntity savedEntity = new CoindeskDataEntity();
        savedEntity.setId(1L);
        
        // 2. 只模擬實際會使用到的方法
        when(coindeskDataRepository.save(any(CoindeskDataEntity.class))).thenReturn(savedEntity);
        
        // 3. 使用簡單的驗證方式而不是復雜的模擬
        System.out.println("\n========== 測試存儲 Coindesk 數據邏輯 ==========");
        
        // 執行測試方法
        coindeskApiService.saveCoindeskData(sampleResponse);
        
        // 4. 驗證保存方法被調用了，而不是模擬其行為
        verify(coindeskDataRepository).save(any(CoindeskDataEntity.class));
        System.out.println("數據保存成功");
    }

    /**
     * 創建測試用的 Coindesk 響應
     */
    private CoindeskResponse createSampleCoindeskResponse() {
        CoindeskResponse response = new CoindeskResponse();
        
        // 設置基本信息
        response.setChartName("Bitcoin Chart");
        response.setDisclaimer("Sample disclaimer");
        
        // 設置時間信息
        CoindeskResponse.TimeInfo timeInfo = new CoindeskResponse.TimeInfo();
        timeInfo.setUpdated("May 9, 2023 00:00:00 UTC");
        timeInfo.setUpdatedISO("2023-05-09T00:00:00+00:00");
        timeInfo.setUpdateduk("May 9, 2023 at 01:00 BST");
        response.setTime(timeInfo);
        
        // 設置幣別信息
        Map<String, CoindeskResponse.CurrencyInfo> bpi = new HashMap<>();
        
        // 添加 USD
        CoindeskResponse.CurrencyInfo usd = new CoindeskResponse.CurrencyInfo();
        usd.setCode("USD");
        usd.setSymbol("&#36;");
        usd.setRate("28,000.00");
        usd.setDescription("United States Dollar");
        usd.setRateFloat(28000.00);
        bpi.put("USD", usd);
        
        // 添加 GBP
        CoindeskResponse.CurrencyInfo gbp = new CoindeskResponse.CurrencyInfo();
        gbp.setCode("GBP");
        gbp.setSymbol("&pound;");
        gbp.setRate("22,000.00");
        gbp.setDescription("British Pound Sterling");
        gbp.setRateFloat(22000.00);
        bpi.put("GBP", gbp);
        
        // 添加 EUR
        CoindeskResponse.CurrencyInfo eur = new CoindeskResponse.CurrencyInfo();
        eur.setCode("EUR");
        eur.setSymbol("&euro;");
        eur.setRate("26,000.00");
        eur.setDescription("Euro");
        eur.setRateFloat(26000.00);
        bpi.put("EUR", eur);
        
        response.setBpi(bpi);
        
        return response;
    }
} 