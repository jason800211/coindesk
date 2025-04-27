package com.coindesk.service;

import com.coindesk.dto.CustomCurrencyResponse;
import com.coindesk.dto.coindesk.CoindeskResponse;

public interface CoindeskApiService {
    CoindeskResponse getOriginalCoindeskData();
    CustomCurrencyResponse getTransformedCoindeskData();
    void saveCoindeskData(CoindeskResponse coindeskResponse);
} 