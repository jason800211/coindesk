package com.coindesk.controller;

import com.coindesk.dto.ApiResponse;
import com.coindesk.dto.CustomCurrencyResponse;
import com.coindesk.dto.coindesk.CoindeskResponse;
import com.coindesk.service.CoindeskApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coindesk")
public class CoindeskApiController {

    private final CoindeskApiService coindeskApiService;

    @Autowired
    public CoindeskApiController(CoindeskApiService coindeskApiService) {
        this.coindeskApiService = coindeskApiService;
    }

    @PostMapping("/input")
    public ResponseEntity<ApiResponse<Void>> inputCoindeskData(@RequestBody CoindeskResponse inputData) {
        coindeskApiService.saveCoindeskData(inputData);
        return ResponseEntity.ok(ApiResponse.success(null, "Coindesk 數據保存成功"));
    }

    @GetMapping("/transform")
    public ResponseEntity<ApiResponse<CustomCurrencyResponse>> getTransformedCoindeskData() {
        CustomCurrencyResponse transformedData = coindeskApiService.getTransformedCoindeskData();
        return ResponseEntity.ok(ApiResponse.success(transformedData, "成功獲取轉換後的幣別資訊"));
    }
} 