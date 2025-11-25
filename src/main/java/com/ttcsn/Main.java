package com.ttcsn;

import com.ttcsn.algorithm.FireflyAlgorithm;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Route;
import com.ttcsn.service.GraphService;
import com.ttcsn.service.RoutingService;

public class Main {
	public static void main(String[] args) {
		// ... (Phần load graph giữ nguyên) ...
		String filePath = "src/main/resources/I_20N_80E.json";
		GraphService graphService = new GraphService();
		Graph graph = graphService.loadGraphFromJson(filePath);

		if (graph == null)
			return;

		RoutingService routingService = new RoutingService();
		routingService.setGraph(graph);
		FireflyAlgorithm fa = new FireflyAlgorithm(routingService);

		long startTime = System.currentTimeMillis();
		Route bestRoute = fa.run(); // Hàm này giờ sẽ in ra cái bảng tiến trình
		long endTime = System.currentTimeMillis();

		// --- IN KẾT QUẢ TỐI ƯU CUỐI CÙNG ---
		if (bestRoute != null) {
			printFinalResult(bestRoute, endTime - startTime);
		} else {
			System.out.println("❌ Không tìm thấy lộ trình!");
		}
	}

	private static void printFinalResult(Route route, long duration) {
		System.out.println("\n=== TỔNG KẾT KẾT QUẢ ===");
		System.out.println(
				"+----------------------+------------------------------------------------------------------------+");
		System.out.println(
				"| THÔNG SỐ             | GIÁ TRỊ                                                                |");
		System.out.println(
				"+----------------------+------------------------------------------------------------------------+");

		String fmt = "| %-20s | %-70s |%n";

		// Cắt chuỗi lộ trình nếu quá dài để không vỡ bảng
		String routeStr = route.toString();
		if (routeStr.length() > 70)
			routeStr = routeStr.substring(0, 65) + "...";

		System.out.printf(fmt, "Lộ trình tối ưu", routeStr);
		System.out.printf(fmt, "Tổng chi phí", String.format("%,.0f VNĐ", route.getTotalCost()));
		System.out.printf(fmt, "Tổng thời gian", String.format("%.2f giờ", route.getTotalTime()));
		System.out.printf(fmt, "Tổng quãng đường", String.format("%.2f km", route.getTotalDistance()));
		System.out.printf(fmt, "Thời gian thực thi", duration + " ms");

		System.out.println(
				"+----------------------+------------------------------------------------------------------------+");
	}
}