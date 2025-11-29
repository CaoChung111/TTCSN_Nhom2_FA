package com.ttcsn.algorithm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Theo dõi độ ổn định của thuật toán qua nhiều lần chạy
 */
public class StabilityTracker {
    
    private static final String STABILITY_JSON = "src/main/resources/output/stability_data.json";
    private static final int MAX_RUNS = 10; // Số lần chạy tối đa trước khi reset
    
    /**
     * Dữ liệu một lần chạy
     */
    public static class RunData {
        int runNumber;
        String bestRoute;
        double brightness;
        boolean isCorrect; // true = lộ trình phổ biến, false = lộ trình khác
        
        public RunData(int runNumber, String bestRoute, double brightness) {
            this.runNumber = runNumber;
            this.bestRoute = bestRoute;
            this.brightness = brightness;
            this.isCorrect = false; // Mặc định
        }
    }
    
    /**
     * Dữ liệu thống kê cho biểu đồ
     */
    public static class StabilityStats {
        int runNumber;
        double correctPercent;
        double incorrectPercent;
        
        public StabilityStats(int runNumber, double correctPercent, double incorrectPercent) {
            this.runNumber = runNumber;
            this.correctPercent = correctPercent;
            this.incorrectPercent = incorrectPercent;
        }
    }
    
    /**
     * Thêm dữ liệu lần chạy mới
     */
    public static void addRun(String bestRoute, double brightness) throws IOException {
        List<RunData> runs = loadRuns();
        
        // Kiểm tra nếu đã đủ 10 lần -> Reset
        if (runs.size() >= MAX_RUNS) {
            System.out.println("⚠️ Đã đủ " + MAX_RUNS + " lần chạy. Đang reset dữ liệu...");
            runs.clear();
        }
        
        // Thêm lần chạy mới
        int runNumber = runs.size() + 1;
        RunData newRun = new RunData(runNumber, bestRoute, brightness);
        runs.add(newRun);
        
        // Phân loại đúng/sai dựa trên lộ trình phổ biến nhất
        classifyRuns(runs);
        
        // Lưu lại
        saveRuns(runs);
        
        System.out.println("✅ Đã lưu kết quả lần chạy #" + runNumber);
        
        // Nếu đủ 10 lần, in báo cáo
        if (runs.size() == MAX_RUNS) {
            printSummary(runs);
        }
    }
    
    /**
     * Phân loại các lần chạy thành "đúng" (lộ trình phổ biến) và "sai" (lộ trình khác)
     */
    private static void classifyRuns(List<RunData> runs) {
        if (runs.isEmpty()) return;
        
        // Đếm số lần xuất hiện của mỗi lộ trình
        Map<String, Integer> routeCount = new HashMap<>();
        for (RunData run : runs) {
            routeCount.put(run.bestRoute, routeCount.getOrDefault(run.bestRoute, 0) + 1);
        }
        
        // Tìm lộ trình xuất hiện nhiều nhất
        String mostCommonRoute = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : routeCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonRoute = entry.getKey();
            }
        }
        
        // Phân loại
        for (RunData run : runs) {
            run.isCorrect = run.bestRoute.equals(mostCommonRoute);
        }
    }
    
    /**
     * Tính toán thống kê tích lũy theo từng lần chạy
     */
    public static List<StabilityStats> calculateStats() throws IOException {
        List<RunData> runs = loadRuns();
        List<StabilityStats> stats = new ArrayList<>();
        
        for (int i = 1; i <= runs.size(); i++) {
            List<RunData> subset = runs.subList(0, i);
            long correctCount = subset.stream().filter(r -> r.isCorrect).count();
            long incorrectCount = i - correctCount;
            
            double correctPercent = (correctCount * 100.0) / i;
            double incorrectPercent = (incorrectCount * 100.0) / i;
            
            stats.add(new StabilityStats(i, correctPercent, incorrectPercent));
        }
        
        return stats;
    }
    
    /**
     * Load dữ liệu từ JSON
     */
    private static List<RunData> loadRuns() throws IOException {
        File file = new File(STABILITY_JSON);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<RunData>>(){}.getType();
            List<RunData> runs = gson.fromJson(reader, type);
            return runs != null ? runs : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Lưu dữ liệu vào JSON
     */
    private static void saveRuns(List<RunData> runs) throws IOException {
        new File("src/main/resources/output/").mkdirs();
        
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(STABILITY_JSON)) {
            gson.toJson(runs, writer);
        }
    }
    
    /**
     * In báo cáo tổng kết sau 10 lần chạy
     */
    private static void printSummary(List<RunData> runs) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 BÁO CÁO ĐỘ ỔN ĐỊNH SAU " + MAX_RUNS + " LẦN CHẠY");
        System.out.println("=".repeat(80));
        
        long correctCount = runs.stream().filter(r -> r.isCorrect).count();
        long incorrectCount = runs.size() - correctCount;
        
        System.out.println("✅ Lộ trình ĐÚNG (phổ biến nhất): " + correctCount + " lần (" + 
                          String.format("%.1f%%", correctCount * 100.0 / runs.size()) + ")");
        System.out.println("❌ Lộ trình SAI (khác biệt):      " + incorrectCount + " lần (" + 
                          String.format("%.1f%%", incorrectCount * 100.0 / runs.size()) + ")");
        System.out.println("=".repeat(80) + "\n");
        
        // In chi tiết các lộ trình
        Map<String, Long> routeCounts = new HashMap<>();
        for (RunData run : runs) {
            routeCounts.put(run.bestRoute, routeCounts.getOrDefault(run.bestRoute, 0L) + 1);
        }
        
        System.out.println("Chi tiết các lộ trình:");
        routeCounts.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                System.out.println("  • " + entry.getKey() + " → " + entry.getValue() + " lần");
            });
        System.out.println();
    }
    
    /**
     * Xuất dữ liệu thống kê cho biểu đồ
     */
    public static void exportStatsJson() throws IOException {
        List<StabilityStats> stats = calculateStats();
        
        String statsPath = "src/main/resources/output/stability_stats.json";
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(statsPath)) {
            gson.toJson(stats, writer);
            System.out.println("✅ Đã xuất thống kê độ ổn định tại: " + statsPath);
        }
    }
    
    /**
     * Lấy số lần chạy hiện tại
     */
    public static int getCurrentRunCount() throws IOException {
        return loadRuns().size();
    }
    
    /**
     * Reset thủ công (nếu cần)
     */
    public static void reset() throws IOException {
        saveRuns(new ArrayList<>());
        System.out.println("🔄 Đã reset dữ liệu độ ổn định");
    }
}