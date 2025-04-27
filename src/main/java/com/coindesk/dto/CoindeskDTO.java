package com.coindesk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Coindesk API 的回應數據對象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoindeskDTO {
    
    /**
     * 時間數據
     */
    private TimeDTO time;
    
    /**
     * 免責聲明
     */
    private String disclaimer;
    
    /**
     * 貨幣代碼
     */
    private String chartName;
    
    /**
     * 幣別匯率數據
     */
    @JsonProperty("bpi")
    private Map<String, BpiDTO> bpi;
    
    /**
     * 時間相關數據對象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeDTO {
        /**
         * 更新時間
         */
        private String updated;
        
        /**
         * ISO 格式的更新時間
         */
        private String updatedISO;
        
        /**
         * UK 格式的更新時間
         */
        private String updateduk;
    }
    
    /**
     * 幣別價格指數數據對象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BpiDTO {
        /**
         * 幣別代碼
         */
        private String code;
        
        /**
         * 幣別符號
         */
        private String symbol;
        
        /**
         * 幣別匯率 (字串格式)
         */
        private String rate;
        
        /**
         * 幣別描述
         */
        private String description;
        
        /**
         * 幣別匯率 (浮點數格式)
         */
        @JsonProperty("rate_float")
        private Double rateFloat;
    }
} 