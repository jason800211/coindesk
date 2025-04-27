package com.coindesk.controller;

import com.coindesk.dto.ApiResponse;
import com.coindesk.dto.CurrencyDto;
import com.coindesk.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CurrencyDto>>> getAllCurrencies() {
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(ApiResponse.success(currencies, "取得所有幣別資訊成功"));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CurrencyDto>> getCurrencyByCode(@PathVariable String code) {
        CurrencyDto currency = currencyService.getCurrencyByCode(code);
        return ResponseEntity.ok(ApiResponse.success(currency, "取得幣別資訊成功"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CurrencyDto>> createCurrency(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto createdCurrency = currencyService.createCurrency(currencyDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCurrency, "新增幣別成功"));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<CurrencyDto>> updateCurrency(
            @PathVariable String code,
            @RequestBody CurrencyDto currencyDto) {
        CurrencyDto updatedCurrency = currencyService.updateCurrency(code, currencyDto);
        return ResponseEntity.ok(ApiResponse.success(updatedCurrency, "更新幣別成功"));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteCurrency(@PathVariable String code) {
        currencyService.deleteCurrency(code);
        return ResponseEntity.ok(ApiResponse.success(null, "刪除幣別成功"));
    }
} 