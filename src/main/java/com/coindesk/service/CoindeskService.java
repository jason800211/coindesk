package com.coindesk.service;

import com.coindesk.dto.CurrencyDto;
import com.coindesk.dto.CoindeskDTO;

import java.util.List;

/**
 * Coindesk 服務介面
 */
public interface CoindeskService {
    
    /**
     * 從 Coindesk API 獲取最新的比特幣匯率數據
     * @return 原始 Coindesk API 數據
     */
    String fetchCoindeskData();
    
    /**
     * 將 Coindesk API 獲取的數據存入數據庫
     * @param coindeskData Coindesk API 數據
     */
    void saveCoindeskData(String coindeskData);
    
    /**
     * 獲取最新的 Coindesk 數據
     * @return 轉換後的 CoindeskDTO
     */
    CoindeskDTO getLatestCoindeskData();
    
    /**
     * 獲取所有貨幣信息
     * @return 貨幣信息列表
     */
    List<CurrencyDto> getAllCurrencies();
    
    /**
     * 根據代碼獲取貨幣信息
     * @param code 貨幣代碼
     * @return 貨幣信息
     */
    CurrencyDto getCurrencyByCode(String code);
    
    /**
     * 更新貨幣信息
     * @param code 貨幣代碼
     * @param currencyDto 更新的貨幣信息
     * @return 更新後的貨幣信息
     */
    CurrencyDto updateCurrency(String code, CurrencyDto currencyDto);
    
    /**
     * 創建新的貨幣信息
     * @param currencyDto 貨幣信息
     * @return 創建的貨幣信息
     */
    CurrencyDto createCurrency(CurrencyDto currencyDto);
    
    /**
     * 刪除貨幣信息
     * @param code 貨幣代碼
     */
    void deleteCurrency(String code);
    
    /**
     * 獲取 Coindesk API 原始資料
     * @return Coindesk API 原始資料
     */
    CoindeskDTO getOriginalData();
    
    /**
     * 獲取轉換後的幣別資訊列表
     * @return 幣別資訊列表
     */
    List<CurrencyDto> getTransformedCurrencyData();
    
    /**
     * 新增幣別資訊
     * @param currencyDto 幣別資訊
     * @return 新增的幣別資訊
     */
    CurrencyDto addCurrency(CurrencyDto currencyDto);
    
    /**
     * 獲取特定幣別資訊
     * @param code 幣別代碼
     * @return 幣別資訊
     */
    CurrencyDto getCurrency(String code);
} 