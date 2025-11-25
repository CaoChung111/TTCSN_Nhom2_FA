package com.ttcsn.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;
import com.ttcsn.service.RoutingService;

public class FireflyAlgorithm {
	private final Random random = new Random();
	private final RoutingService routingService;
	private final List<Firefly> population = new ArrayList<>();

	// --- CẤU HÌNH FORMAT BẢNG (ĐÃ CĂN CHỈNH) ---
	// Gen(6) | Bright(12) | Cost(14) | Time(12) | Dist(12) | Route(102)
	private static final String BORDER = "+------+------------+--------------+------------+------------+------------------------------------------------------------+";
	private static final String TABLE_FORMAT = "| %-4d | %-10.5f | %-12.0f | %-10.2f | %-10.2f | %-58s |%n";
	private static final String HEADER_FORMAT = "| %-4s | %-10s | %-12s | %-10s | %-10s | %-58s |%n";

	private static final String WARNING_FORMAT = "| %-162s |%n";

	public FireflyAlgorithm(RoutingService routingService) {
		this.routingService = routingService;
	}

	public Route run() {
		System.out.println("=== BẮT ĐẦU THUẬT TOÁN FIREFLY ===");
		Set<Route> existRoute = new HashSet<>();
		int attempts = 0, maxAttempts = Constant.POPULATION_SIZE * 4;
		Node start = routingService.getNode(Constant.START_POINT);
		Node end = routingService.getNode(Constant.END_POINT);
		if (start == null || end == null)
			return null;

		// --- 1. KHỞI TẠO QUẦN THỂ ---
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

		// --- 2. IN CHI TIẾT QUẦN THỂ BAN ĐẦU ---
		System.out.println("--- DANH SÁCH QUẦN THỂ BAN ĐẦU ---");
		// Header bảng quần thể ban đầu (Tái sử dụng style border cho đẹp)
		System.out.println(
				"+-----+------------+------------------------------------------------------------------------------------------------------+");
		System.out.printf("| %-3s | %-10s | %-100s |%n", "ID", "ĐỘ SÁNG", "LỘ TRÌNH");
		System.out.println(
				"+-----+------------+------------------------------------------------------------------------------------------------------+");
		for (int i = 0; i < population.size(); i++) {
			Firefly f = population.get(i);
			System.out.printf("| %-3d | %-10.5f | %-100s |%n", (i + 1), f.getBrightness(),
					truncate(f.getRoute().toString(), 100));
		}
		System.out.println(
				"+-----+------------+------------------------------------------------------------------------------------------------------+\n");

		// --- 3. IN HEADER BẢNG TIẾN TRÌNH ---
		System.out.println("TIẾN TRÌNH TỐI ƯU HÓA (BEST OF GEN):");
		System.out.println(BORDER);
		System.out.printf(HEADER_FORMAT, "GEN", "ĐỘ SÁNG", "CHI PHÍ", "T.GIAN", "Q.ĐƯỜNG", "LỘ TRÌNH (TỐI ƯU GEN)");
		System.out.println(BORDER);

		// --- VÒNG LẶP CHÍNH ---
		Firefly best = population.get(0);
		int g = 0;

		// Biến theo dõi sự trì trệ
		int stagnationCount = 0;
		double lastBestBrightness = -1.0;

		while (g < Constant.MAX_GENERATION) {
			for (int i = 0; i < population.size(); i++) {
				for (int j = 0; j < population.size(); j++) {
					Firefly fi = population.get(i);
					Firefly fj = population.get(j);

					if (fj.getBrightness() > fi.getBrightness()) {
						double r = routingService.jaccardDistance(fi.getRoute(), fj.getRoute());
						double beta = routingService.calculateAttractiveness(Constant.BETA_0, Constant.GAMMA, r);

						if (random.nextDouble() < beta) {
							Route newRoute = routingService.crossover(fi.getRoute(), fj.getRoute());
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

			// Sắp xếp lại quần thể
			Collections.sort(population);

			// Cập nhật Best
			Firefly currentBest = population.get(0);
			if (currentBest.getBrightness() > best.getBrightness()) {
				best = currentBest;
			}

			// Kiểm tra Stagnation (Dùng sai số epsilon để so sánh double cho an toàn)
			if (Math.abs(currentBest.getBrightness() - lastBestBrightness) < 0.000001) {
				stagnationCount++;
			} else {
				stagnationCount = 0;
				lastBestBrightness = currentBest.getBrightness();
			}

			// Kích hoạt đột biến khi kẹt
			int stagnationMax = Math.max(15, Constant.POPULATION_SIZE);
			if (stagnationCount > stagnationMax) {
				System.out.printf(WARNING_FORMAT, ">> CẢNH BÁO: Kẹt " + stagnationMax
						+ " Gen liên tiếp! Kích hoạt đột biến diện rộng (Trừ Top 3)...");

				for (int k = 2; k < population.size(); k++) {
					Firefly f = population.get(k);
					Route mutatedRoute = routingService.mutate(f.getRoute());
					f.setRoute(mutatedRoute);
					f.calculateBrightness();
				}
				stagnationCount = 0;
				Collections.sort(population); // Sort lại ngay để đảm bảo gen sau đúng thứ tự
			}

			// --- IN KẾT QUẢ ---
			Route r = best.getRoute();
			System.out.printf(TABLE_FORMAT, g, best.getBrightness(), r.getTotalCost(), r.getTotalTime(),
					r.getTotalDistance(), truncate(r.toString(), 57));

			g++;
		}
		System.out.println(BORDER);

		return best.getRoute();
	}

	private String truncate(String str, int maxWidth) {
		if (str == null)
			return "";
		if (str.length() <= maxWidth)
			return str;
		return str.substring(0, maxWidth - 3) + "...";
	}
}