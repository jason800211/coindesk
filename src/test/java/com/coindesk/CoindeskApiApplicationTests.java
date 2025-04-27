package com.coindesk;

import com.coindesk.dto.ApiResponse;
import com.coindesk.dto.CurrencyDto;
import com.coindesk.dto.CustomCurrencyResponse;
import com.coindesk.dto.coindesk.CoindeskResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * API 整合測試
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoindeskApiApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    // 測試報告生成標記，用於確保僅生成一次報告
    private static boolean reportGenerated = false;

    @Test
    @Order(1)
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
    }

    /**
     * 測試幣別 CRUD API
     */
    @Test
    @Order(2)
    void testCurrencyCRUD() {
        System.out.println("\n========== 測試幣別 CRUD API ==========");
        String baseUrl = "http://localhost:" + port + "/api/currencies";
        
        // 測試獲取所有幣別
        System.out.println("測試獲取所有幣別...");
        ResponseEntity<ApiResponse<List<CurrencyDto>>> getAllResponse = restTemplate.exchange(
                baseUrl, 
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<CurrencyDto>>>() {}
        );
        
        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<CurrencyDto>> getAllResult = getAllResponse.getBody();
        if (getAllResult != null) {
            assertThat(getAllResult.getReturnCode()).isEqualTo(200);
            assertThat(getAllResult.getData()).isNotNull();
            assertThat(getAllResult.getData().size()).isGreaterThan(0);
            System.out.println("獲取所有幣別成功: " + getAllResult.getReturnMsg());
            System.out.println("幣別數量: " + getAllResult.getData().size());
            System.out.println("第一個幣別: " + getAllResult.getData().get(0));
        } else {
            fail("Response body should not be null");
        }
        
        // 測試獲取特定幣別
        System.out.println("\n測試獲取特定幣別...");
        String currencyCode = "USD";
        ResponseEntity<ApiResponse<CurrencyDto>> getOneResponse = restTemplate.exchange(
                baseUrl + "/" + currencyCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<CurrencyDto>>() {}
        );
        
        assertThat(getOneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<CurrencyDto> getOneResult = getOneResponse.getBody();
        if (getOneResult != null) {
            assertThat(getOneResult.getReturnCode()).isEqualTo(200);
            assertThat(getOneResult.getData()).isNotNull();
            assertThat(getOneResult.getData().getCode()).isEqualTo(currencyCode);
            System.out.println("獲取幣別 " + currencyCode + " 成功: " + getOneResult.getReturnMsg());
            System.out.println("幣別詳情: " + getOneResult.getData());
        } else {
            fail("Response body should not be null");
        }
        
        // 測試創建新幣別
        System.out.println("\n測試創建新幣別...");
        CurrencyDto newCurrency = new CurrencyDto("TWD", "新臺幣", "New Taiwan Dollar");
        ResponseEntity<ApiResponse<CurrencyDto>> createResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(newCurrency),
                new ParameterizedTypeReference<ApiResponse<CurrencyDto>>() {}
        );
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<CurrencyDto> createResult = createResponse.getBody();
        if (createResult != null) {
            assertThat(createResult.getReturnCode()).isEqualTo(200);
            assertThat(createResult.getData()).isNotNull();
            assertThat(createResult.getData().getCode()).isEqualTo("TWD");
            System.out.println("創建幣別成功: " + createResult.getReturnMsg());
            System.out.println("新幣別詳情: " + createResult.getData());
        } else {
            fail("Response body should not be null");
        }
        
        // 測試更新幣別
        System.out.println("\n測試更新幣別...");
        newCurrency.setChineseName("臺幣");
        ResponseEntity<ApiResponse<CurrencyDto>> updateResponse = restTemplate.exchange(
                baseUrl + "/TWD",
                HttpMethod.PUT,
                new HttpEntity<>(newCurrency),
                new ParameterizedTypeReference<ApiResponse<CurrencyDto>>() {}
        );
        
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<CurrencyDto> updateResult = updateResponse.getBody();
        if (updateResult != null) {
            assertThat(updateResult.getReturnCode()).isEqualTo(200);
            assertThat(updateResult.getData()).isNotNull();
            assertThat(updateResult.getData().getChineseName()).isEqualTo("臺幣");
            System.out.println("更新幣別成功: " + updateResult.getReturnMsg());
            System.out.println("更新後幣別詳情: " + updateResult.getData());
        } else {
            fail("Response body should not be null");
        }
        
        // 測試刪除幣別
        System.out.println("\n測試刪除幣別...");
        ResponseEntity<ApiResponse<Void>> deleteResponse = restTemplate.exchange(
                baseUrl + "/TWD",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<ApiResponse<Void>>() {}
        );
        
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<Void> deleteResult = deleteResponse.getBody();
        if (deleteResult != null) {
            assertThat(deleteResult.getReturnCode()).isEqualTo(200);
            System.out.println("刪除幣別成功: " + deleteResult.getReturnMsg());
        } else {
            fail("Response body should not be null");
        }
    }
    
    /**
     * 測試 Coindesk API 轉換
     */
    @Test
    @Order(3)
    void testCoindeskApi() {
        System.out.println("\n========== 測試 Coindesk API 轉換 ==========");
        
        // 測試獲取轉換後的 Coindesk 數據
        System.out.println("測試獲取轉換後的 Coindesk 數據...");
        String transformUrl = "http://localhost:" + port + "/api/coindesk/transform";
        ResponseEntity<ApiResponse<CustomCurrencyResponse>> transformResponse = restTemplate.exchange(
                transformUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<CustomCurrencyResponse>>() {}
        );
        
        assertThat(transformResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<CustomCurrencyResponse> transformResult = transformResponse.getBody();
        if (transformResult != null) {
            assertThat(transformResult.getReturnCode()).isEqualTo(200);
            assertThat(transformResult.getData()).isNotNull();
            assertThat(transformResult.getData().getUpdateTime()).isNotNull();
            assertThat(transformResult.getData().getCurrencies()).isNotNull();
            assertThat(transformResult.getData().getCurrencies().size()).isGreaterThan(0);
            
            System.out.println("獲取轉換數據成功: " + transformResult.getReturnMsg());
            System.out.println("更新時間: " + transformResult.getData().getUpdateTime());
            System.out.println("幣別數量: " + transformResult.getData().getCurrencies().size());
            System.out.println("幣別列表: " + transformResult.getData().getCurrencies());
        } else {
            fail("Response body should not be null");
        }
    }
    
    /**
     * 測試輸入 Coindesk 數據並檢查轉換
     */
    @Test
    @Order(4)
    void testInputCoindeskData() {
        System.out.println("\n========== 測試輸入 Coindesk 數據並檢查轉換 ==========");
        
        String inputUrl = "http://localhost:" + port + "/api/coindesk/input";
        
        // 創建一個測試用的 CoindeskResponse 對象
        System.out.println("創建測試數據...");
        CoindeskResponse testData = createTestCoindeskData();
        
        // 發送 POST 請求，輸入數據
        System.out.println("發送數據到 Coindesk API...");
        ResponseEntity<ApiResponse<Void>> inputResponse = restTemplate.exchange(
                inputUrl,
                HttpMethod.POST,
                new HttpEntity<>(testData),
                new ParameterizedTypeReference<ApiResponse<Void>>() {}
        );
        
        assertThat(inputResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<Void> inputResult = inputResponse.getBody();
        if (inputResult != null) {
            assertThat(inputResult.getReturnCode()).isEqualTo(200);
            System.out.println("輸入數據成功: " + inputResult.getReturnMsg());
        } else {
            fail("Response body should not be null");
        }
        
        // 檢查轉換後的數據
        System.out.println("\n檢查轉換後的數據...");
        String transformUrl = "http://localhost:" + port + "/api/coindesk/transform";
        ResponseEntity<ApiResponse<CustomCurrencyResponse>> transformResponse = restTemplate.exchange(
                transformUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<CustomCurrencyResponse>>() {}
        );
        
        assertThat(transformResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<CustomCurrencyResponse> transformResult = transformResponse.getBody();
        if (transformResult != null) {
            assertThat(transformResult.getReturnCode()).isEqualTo(200);
            assertThat(transformResult.getData()).isNotNull();
            assertThat(transformResult.getData().getCurrencies().size()).isEqualTo(testData.getBpi().size());
            
            System.out.println("獲取轉換數據成功: " + transformResult.getReturnMsg());
            System.out.println("幣別數量與測試數據相符: " + transformResult.getData().getCurrencies().size());
            System.out.println("幣別列表: " + transformResult.getData().getCurrencies());
        } else {
            fail("Response body should not be null");
        }
    }
    
    /**
     * 所有測試完成後生成報告
     * 使用AfterAll確保僅在所有測試都完成後執行一次
     */
    @AfterAll
    public static void generateReport() {
        // 確保報告只生成一次
        if (!reportGenerated) {
            reportGenerated = true;
            try {
                System.out.println("\n========== 生成測試報告 ==========");
                TestReportGenerator.generateTestReport();
                System.out.println("測試報告生成完成");
            } catch (IOException e) {
                System.err.println("生成測試報告時發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 創建測試用的 Coindesk 數據
     */
    private CoindeskResponse createTestCoindeskData() {
        CoindeskResponse response = new CoindeskResponse();
        
        // 設置基本信息
        response.setChartName("Test Bitcoin Chart");
        response.setDisclaimer("This is a test disclaimer");
        
        // 設置時間信息
        CoindeskResponse.TimeInfo timeInfo = new CoindeskResponse.TimeInfo();
        timeInfo.setUpdated("Oct 8, 2024 10:10:10 UTC");
        timeInfo.setUpdatedISO("2024-10-08T10:10:10+00:00");
        timeInfo.setUpdateduk("Oct 8, 2024 at 11:10 BST");
        response.setTime(timeInfo);
        
        // 設置幣別信息
        Map<String, CoindeskResponse.CurrencyInfo> bpi = new HashMap<>();
        
        // 添加 USD
        CoindeskResponse.CurrencyInfo usd = new CoindeskResponse.CurrencyInfo();
        usd.setCode("USD");
        usd.setSymbol("&#36;");
        usd.setRate("60,000.00");
        usd.setDescription("United States Dollar");
        usd.setRateFloat(60000.00);
        bpi.put("USD", usd);
        
        // 添加 GBP
        CoindeskResponse.CurrencyInfo gbp = new CoindeskResponse.CurrencyInfo();
        gbp.setCode("GBP");
        gbp.setSymbol("&pound;");
        gbp.setRate("45,000.00");
        gbp.setDescription("British Pound Sterling");
        gbp.setRateFloat(45000.00);
        bpi.put("GBP", gbp);
        
        // 添加 EUR
        CoindeskResponse.CurrencyInfo eur = new CoindeskResponse.CurrencyInfo();
        eur.setCode("EUR");
        eur.setSymbol("&euro;");
        eur.setRate("53,000.00");
        eur.setDescription("Euro");
        eur.setRateFloat(53000.00);
        bpi.put("EUR", eur);
        
        // 添加 JPY (額外幣別)
        CoindeskResponse.CurrencyInfo jpy = new CoindeskResponse.CurrencyInfo();
        jpy.setCode("JPY");
        jpy.setSymbol("&yen;");
        jpy.setRate("9,000,000.00");
        jpy.setDescription("Japanese Yen");
        jpy.setRateFloat(9000000.00);
        bpi.put("JPY", jpy);
        
        response.setBpi(bpi);
        
        return response;
    }
} 