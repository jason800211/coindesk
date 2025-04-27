package com.coindesk.service;

import com.coindesk.dto.CoindeskDTO;
import com.coindesk.dto.CurrencyDto;

import java.util.List;

/**
 * 幣別資訊服務介面
 */
public interface CurrencyService {
    
    /**
     * 直接呼叫 Coindesk API 獲取原始資料
     * 
     * @return Coindesk API 的原始回應資料
     */
    CoindeskDTO getOriginalData();
    
    /**
     * 取得所有幣別資訊
     * 
     * @return 所有幣別資訊列表
     */
    List<CurrencyDto> getAllCurrencies();
    
    /**
     * 根據幣別代碼獲取特定幣別資訊
     * 
     * @param code 幣別代碼
     * @return 特定幣別資訊
     */
    CurrencyDto getCurrencyByCode(String code);
    
    /**
     * 創建新的幣別資訊
     * 
     * @param currencyDto 幣別資訊
     * @return 創建的幣別資訊
     */
    CurrencyDto createCurrency(CurrencyDto currencyDto);
    
    /**
     * 更新幣別資訊
     * 
     * @param code 幣別代碼
     * @param currencyDto 更新的幣別資訊
     * @return 更新後的幣別資訊
     */
    CurrencyDto updateCurrency(String code, CurrencyDto currencyDto);
    
    /**
     * 刪除幣別資訊
     * 
     * @param code 幣別代碼
     */
    void deleteCurrency(String code);
    
    /**
     * 取得轉換過的幣別資訊列表
     * 
     * @return 轉換過的幣別資訊列表
     */
    List<CurrencyDto> getTransformedCurrencies();
} 