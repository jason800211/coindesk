package com.coindesk.dto.coindesk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CoindeskResponse {
    private TimeInfo time;
    private String disclaimer;
    private String chartName;
    private Map<String, CurrencyInfo> bpi;
    
    @Data
    public static class TimeInfo {
        private String updated;
        private String updatedISO;
        private String updateduk;
    }
    
    @Data
    public static class CurrencyInfo {
        private String code;
        private String symbol;
        private String rate;
        private String description;
        
        @JsonProperty("rate_float")
        private Double rateFloat;
    }
} 