package com.ttcsn.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;
import com.ttcsn.model.dto.FireflyPoint;
import com.ttcsn.model.dto.GenData; // Import class vừa tạo
import com.ttcsn.service.RoutingService;

public class FireflyAlgorithm {
    private final Random random = new Random();
    private final RoutingService routingService;
    private final List<Firefly> population = new ArrayList<>();

    // List lưu lịch sử để vẽ biểu đồ
    private final List<GenData> bestOfGen = new ArrayList<>();

    // Format bảng console
    private static final String BORDER = "+------+------------+--------------+------------+------------+------------------------------------------------------------+";
    private static final String TABLE_FORMAT = "| %-4d | %-10.5f | %-12.0f | %-10.2f | %-10.2f | %-58s |%n";
    private static final String HEADER_FORMAT = "| %-4s | %-10s | %-12s | %-10s | %-10s | %-58s |%n";
    private static final String WARNING_FORMAT = "| %-162s |%n";

    public FireflyAlgorithm(RoutingService routingService) {
        this.routingService = routingService;
    }

    public Route run() {
        System.out.println("=== BẮT ĐẦU THUẬT TOÁN FIREFLY ===");

        // 1. Reset các biến lưu trữ
        bestOfGen.clear();
        population.clear();

        // Khai báo 2 list để lưu dữ liệu vẽ biểu đồ Scatter
        List<FireflyPoint> initialPoints = new ArrayList<>();
        List<FireflyPoint> finalPoints = new ArrayList<>();

        Set<Route> existRoute = new HashSet<>();
        int attempts = 0, maxAttempts = Constant.POPULATION_SIZE * 4;
        Node start = routingService.getNode(Constant.START_POINT);
        Node end = routingService.getNode(Constant.END_POINT);

        if (start == null || end == null) {
            System.err.println("Không tìm thấy điểm bắt đầu hoặc kết thúc!");
            return null;
        }

        // --- 1. KHỞI TẠO QUẦN THỂ (GEN 0) ---
        System.out.print("Đang khởi tạo quần thể... ");
        while (population.size() < Constant.POPULATION_SIZE && attempts < maxAttempts) {
            attempts++;
            Route route = routingService.generateRandomRoute(start, end);
            if (route != null && (!existRoute.contains(route) || attempts > Constant.POPULATION_SIZE * 3)) {
                Firefly firefly = new Firefly(route);
                firefly.calculateBrightness();
                population.add(firefly);
                existRoute.add(route);
            }
        }
        System.out.println("Hoàn tất (" + population.size() + " cá thể).\n");

        // =========================================================================
        // [QUAN TRỌNG] LƯU DỮ LIỆU GEN 0 TẠI ĐÂY (TRƯỚC KHI TỐI ƯU HÓA)
        // =========================================================================
        for (Firefly f : population) {
            Route r = f.getRoute();
            // Lưu ý: Dùng Constant.MAX_COST_BUDGET (hoặc biến budget tương ứng của bạn)
            initialPoints.add(new FireflyPoint(r.getTotalCost(), r.getTotalTime(), Constant.MAX_COST));
        }
        // =========================================================================

        // --- 2. IN CHI TIẾT QUẦN THỂ BAN ĐẦU ---
        printInitialPopulation();

        // --- 3. IN HEADER BẢNG TIẾN TRÌNH ---
        System.out.println("TIẾN TRÌNH TỐI ƯU HÓA (BEST OF GEN):");
        System.out.println(BORDER);
        System.out.printf(HEADER_FORMAT, "GEN", "ĐỘ SÁNG", "CHI PHÍ", "T.GIAN", "Q.ĐƯỜNG", "LỘ TRÌNH (TỐI ƯU GEN)");
        System.out.println(BORDER);

        // --- VÒNG LẶP CHÍNH (QUÁ TRÌNH TỐI ƯU) ---
        Firefly best = population.get(0);
        int g = 0;
        int stagnationCount = 0;
        double lastBestBrightness = -1.0;

        while (g < Constant.MAX_GENERATION) {
            // ... (Giữ nguyên logic Firefly của bạn: so sánh, di chuyển, đột biến) ...
            if(g<=3){printInitialPopulation();}
            for (int i = 0; i < population.size(); i++) {
                for (int j = 0; j < population.size(); j++) {
                    Firefly fi = population.get(i);
                    Firefly fj = population.get(j);

                    if (fj.getBrightness() > fi.getBrightness()) {
                        double r = routingService.jaccardDistance(fi.getRoute(), fj.getRoute());
                        double beta = routingService.calculateAttractiveness(Constant.BETA_0, Constant.GAMMA, r);

                        if (random.nextDouble() < beta) {
                            Route newRoute = routingService.crossover(fi.getRoute(), fj.getRoute());
                            if (newRoute.equals(fi.getRoute())) {
                                // System.out.println(">> Lai ghép không đổi (Trùng cha), kích hoạt Đột biến cưỡng ép!");
                                newRoute = routingService.mutate(newRoute);
                            }
                            fi.setRoute(newRoute);
                        }
                        if (random.nextDouble() < Constant.ALPHA) {
                            Route mutated = routingService.mutate(fi.getRoute());
                            fi.setRoute(mutated);
                        }
                        fi.calculateBrightness();
                    }
                }
            }

            // Sắp xếp & Cập nhật Best
            Collections.sort(population);
            Firefly currentBest = population.get(0);
            if (currentBest.getBrightness() > best.getBrightness()) {
                best = currentBest;
            }

            // Check Stagnation & Thảm họa
            if (Math.abs(currentBest.getBrightness() - lastBestBrightness) < 0.000001) {
                stagnationCount++;
            } else {
                stagnationCount = 0;
                lastBestBrightness = currentBest.getBrightness();
            }

//            int stagnationMax = Math.max(15, Constant.POPULATION_SIZE);
//            if (stagnationCount > stagnationMax) {
//                for (int k = 2; k < population.size(); k++) {
//                    System.out.println("Thảm họa");
//                    Firefly f = population.get(k);
//                    Route mutatedRoute = routingService.mutate(f.getRoute());
//                    f.setRoute(mutatedRoute);
//                    f.calculateBrightness();
//                }
//                stagnationCount = 0;
//                Collections.sort(population);
//            }

            // Log Console & Lưu Line Chart
            Route r = best.getRoute();
            System.out.printf(TABLE_FORMAT, g, best.getBrightness(), r.getTotalCost(), r.getTotalTime(),
                    r.getTotalDistance(), truncate(r.toString(), 57));

            bestOfGen.add(new GenData(g, best.getBrightness(), r.getTotalCost(), r.getTotalTime(), r.getTotalDistance(), truncate(r.toString(), 100)));

            g++;
        }
        // --- KẾT THÚC VÒNG LẶP WHILE ---

        System.out.println(BORDER);

        // =========================================================================
        // [QUAN TRỌNG] LƯU DỮ LIỆU GEN CUỐI TẠI ĐÂY (SAU KHI ĐÃ TỐI ƯU)
        // =========================================================================
        for (Firefly f : population) {
            Route r = f.getRoute();
            finalPoints.add(new FireflyPoint(r.getTotalCost(), r.getTotalTime(), Constant.MAX_COST));
        }
        // =========================================================================

        // --- XUẤT BIỂU ĐỒ ---
        try {
            // Bước 1: Lưu dữ liệu ra file JSON
            FireflyOutput.saveDataToJson(initialPoints, finalPoints);

            // Bước 2: Đọc file JSON đó và tạo HTML
            FireflyOutput.exportComparisonCharts();

        } catch (IOException e) {
            e.printStackTrace();
        }
        printInitialPopulation();
        return best.getRoute();
    }

    private void printInitialPopulation() {
        System.out.println("--- DANH SÁCH QUẦN THỂ BAN ĐẦU ---");
        System.out.println("+-----+------------+------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-3s | %-10s | %-100s |%n", "ID", "ĐỘ SÁNG", "LỘ TRÌNH");
        System.out.println("+-----+------------+------------------------------------------------------------------------------------------------------+");
        for (int i = 0; i < population.size(); i++) {
            Firefly f = population.get(i);
            System.out.printf("| %-3d | %-10.5f | %-100s |%n", (i + 1), f.getBrightness(), truncate(f.getRoute().toString(), 100));
        }
        System.out.println("+-----+------------+------------------------------------------------------------------------------------------------------+\n");
    }

    private String truncate(String str, int maxWidth) {
        if (str == null) return "";
        if (str.length() <= maxWidth) return str;
        return str.substring(0, maxWidth - 3) + "...";
    }
}