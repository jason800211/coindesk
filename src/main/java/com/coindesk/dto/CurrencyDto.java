package com.coindesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 幣別資訊數據傳輸對象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {
    
    /**
     * 幣別代碼 (例如：USD, EUR, GBP)
     */
    private String code;
    
    /**
     * 幣別中文名稱
     */
    private String chineseName;
    
    /**
     * 幣別英文名稱
     */
    private String englishName;
    
    /**
     * 幣別符號
     */
    private String symbol;
    
    /**
     * 匯率 (字串格式)
     */
    private String rate;
    
    /**
     * 幣別描述
     */
    private String description;
    
    /**
     * 匯率 (浮點數格式)
     */
    private Double rateFloat;
    
    /**
     * 資料更新時間
     */
    private String updateTime;

    /**
     * 簡單構造函數（用於基本幣別資訊）
     */
    public CurrencyDto(String code, String chineseName, String englishName) {
        this.code = code;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }
} 