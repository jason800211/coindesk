package com.coindesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用 API 回應包裝類
 * @param <T> 數據類型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 回應代碼
     * 200: 成功
     * 400: 請求錯誤
     * 404: 資源不存在
     * 500: 伺服器錯誤
     */
    private int returnCode;
    
    /**
     * 回應訊息
     */
    private String returnMsg;
    
    /**
     * 回應數據
     */
    private T data;
    
    /**
     * 創建成功回應
     * @param data 數據
     * @param <T> 數據類型
     * @return API回應
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .returnCode(200)
                .returnMsg("成功")
                .data(data)
                .build();
    }
    
    /**
     * 創建成功回應(含自訂訊息)
     * @param data 數據
     * @param message 成功訊息
     * @param <T> 數據類型
     * @return API回應
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .returnCode(200)
                .returnMsg(message)
                .data(data)
                .build();
    }
    
    /**
     * 創建錯誤回應
     * @param code 錯誤代碼
     * @param message 錯誤訊息
     * @param <T> 數據類型
     * @return API回應
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .returnCode(code)
                .returnMsg(message)
                .build();
    }
} 