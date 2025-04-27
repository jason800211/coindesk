package com.coindesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomCurrencyResponse {
    private String updateTime;
    private List<CurrencyInfo> currencies;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyInfo {
        private String code;
        private String chineseName;
        private Double rate;
    }
} 