package com.coindesk;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/**
 * 測試報告生成器
 * 執行所有測試並生成報告
 * 
 * 注意: 此類不再作為測試類運行，而是作為工具類在需要時手動調用
 */
public class TestReportGenerator {

    // 標誌文件路徑，用於防止重複運行
    private static final String LOCK_FILE = "report-generation.lock";
    // 報告輸出目錄
    private static final String REPORTS_DIR = "test-reports";

    /**
     * 生成測試報告的主方法
     * 可以從命令行直接運行此方法
     */
    public static void main(String[] args) {
        try {
            generateTestReport();
        } catch (IOException e) {
            System.err.println("生成測試報告時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成測試報告
     * 使用鎖機制確保同一時間只有一個報告生成進程
     */
    public static void generateTestReport() throws IOException {
        // 檢查是否已經有報告生成進程在運行
        Path lockPath = Paths.get(LOCK_FILE);
        if (Files.exists(lockPath)) {
            System.out.println("另一個報告生成進程已在運行，跳過此次生成");
            return;
        }

        try {
            // 創建鎖文件
            Files.createFile(lockPath);
            
            // 確保報告目錄存在
            Path reportsDir = Paths.get(REPORTS_DIR);
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }
            
            // 創建 Launcher
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectPackage("com.coindesk"))
                    .build();
            Launcher launcher = LauncherFactory.create();
            
            // 添加測試執行摘要監聽器
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            
            // 執行測試
            System.out.println("執行所有測試...");
            launcher.execute(request);
            
            // 獲取測試執行摘要
            TestExecutionSummary summary = listener.getSummary();
            
            // 生成報告文件
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = REPORTS_DIR + "/test-report-" + timeStamp + ".txt";
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                // 報告標題
                writer.println("========================================================");
                writer.println("              Coindesk API 測試報告                     ");
                writer.println("========================================================");
                writer.println("報告生成時間: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
                writer.println();
                
                // 測試統計
                writer.println("測試統計:");
                writer.println("----------------------------------------");
                writer.printf("總測試數: %d%n", summary.getTestsFoundCount());
                writer.printf("成功執行: %d%n", summary.getTestsSucceededCount());
                writer.printf("執行失敗: %d%n", summary.getTestsFailedCount());
                writer.printf("執行跳過: %d%n", summary.getTestsSkippedCount());
                writer.printf("總執行時間: %d ms%n", summary.getTimeFinished() - summary.getTimeStarted());
                writer.println();
                
                // 失敗測試詳情
                if (summary.getTestsFailedCount() > 0) {
                    writer.println("失敗測試詳情:");
                    writer.println("----------------------------------------");
                    AtomicInteger count = new AtomicInteger(1);
                    summary.getFailures().forEach(failure -> {
                        writer.printf("%d. %s%n", count.getAndIncrement(), failure.getTestIdentifier().getDisplayName());
                        writer.printf("   原因: %s%n", failure.getException().getMessage());
                        writer.println();
                    });
                }
                
                // 測試項目總結
                writer.println("測試項目總結:");
                writer.println("----------------------------------------");
                writer.println("1. 資料轉換相關邏輯測試: " + (summary.getTestsFailedCount() == 0 ? "通過" : "失敗"));
                writer.println("2. 幣別對應表資料 CRUD API 測試: " + (summary.getTestsFailedCount() == 0 ? "通過" : "失敗"));
                writer.println("3. Coindesk API 測試: " + (summary.getTestsFailedCount() == 0 ? "通過" : "失敗"));
                writer.println("4. 資料轉換 API 測試: " + (summary.getTestsFailedCount() == 0 ? "通過" : "失敗"));
                writer.println();
                
                // 結論
                writer.println("結論:");
                writer.println("----------------------------------------");
                if (summary.getTestsFailedCount() == 0) {
                    writer.println("所有測試都已通過！應用程序功能正常。");
                } else {
                    writer.println("測試存在失敗，請檢查上述失敗測試詳情並修復相關問題。");
                }
            }
            
            System.out.println("測試報告已生成: " + fileName);
            
        } finally {
            // 移除鎖文件
            Files.deleteIfExists(lockPath);
        }
    }
} 