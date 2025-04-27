# Coindesk API 整合專案

這是一個 Spring Boot 專案，用於整合 Coindesk API 並提供幣別相關的功能。

## 技術堆疊

- JDK 8
- Spring Boot 2.7.18
- Spring Data JPA
- H2 資料庫
- Maven

## 功能列表

1. 幣別資料表的 CRUD 操作
2. 處理 Coindesk 格式的 JSON 數據
3. 解析 JSON 數據並存入資料庫
4. 轉換數據格式，整合本地幣別中文名稱

## API 端點

### 幣別 CRUD API

- 獲取所有幣別: `GET /api/currencies`
- 獲取特定幣別: `GET /api/currencies/{code}`
- 新增幣別: `POST /api/currencies`
- 更新幣別: `PUT /api/currencies/{code}`
- 刪除幣別: `DELETE /api/currencies/{code}`

### Coindesk API

- 獲取原始 Coindesk 數據: `GET /api/coindesk/original`
- 獲取轉換後的 Coindesk 數據: `GET /api/coindesk/transform`
- 輸入 Coindesk JSON 數據: `POST /api/coindesk/input`

## 如何運行

### 使用 Maven

```bash
# 編譯並打包
mvn clean package

# 運行應用程序
java -jar target/coindesk-api-0.0.1-SNAPSHOT.jar
```

### 使用 IDE

導入專案至 IDE 中，然後運行 `CoindeskApiApplication` 類的 `main` 方法。

## 資料庫

專案使用 H2 內存資料庫，應用程序啟動時會自動創建表結構並初始化測試數據。

- H2 控制台: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:coindesk`
- 用戶名: `sa`
- 密碼: (空白)

## 輸入 JSON 數據

你可以通過 POST 請求向 `/api/coindesk/input` 端點發送 JSON 數據。JSON 結構應如下所示：

```json
{
  "time": {
    "updated": "Sep 2, 2024 07:07:20 UTC",
    "updatedISO": "2024-09-02T07:07:20+00:00",
    "updateduk": "Sep 2, 2024 at 08:07 BST"
  },
  "disclaimer": "just for test",
  "chartName": "Bitcoin",
  "bpi": {
    "USD": {
      "code": "USD",
      "symbol": "&#36;",
      "rate": "57,756.298",
      "description": "United States Dollar",
      "rate_float": 57756.2984
    },
    "GBP": {
      "code": "GBP",
      "symbol": "&pound;",
      "rate": "43,984.02",
      "description": "British Pound Sterling",
      "rate_float": 43984.0203
    },
    "EUR": {
      "code": "EUR",
      "symbol": "&euro;",
      "rate": "52,243.287",
      "description": "Euro",
      "rate_float": 52243.2865
    }
  }
}
```

系統將解析這個 JSON 數據，並將幣別信息保存到數據庫中。

## 測試

專案包含單元測試，用於測試 API 的功能。可以通過以下命令運行測試：

```bash
mvn test
``` 