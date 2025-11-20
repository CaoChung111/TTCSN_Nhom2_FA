package com.ttcsn;

import com.ttcsn.algorithm.FireflyAlgorithm;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Route;
import com.ttcsn.service.GraphService;
import com.ttcsn.service.RoutingService;

public class Main {
	public static void main(String[] args) {
		System.out.println("=== KHỞI CHẠY TEST THUẬT TOÁN FIREFLY ===");

		// 1. Load dữ liệu đồ thị từ file JSON
		// Đảm bảo đường dẫn file đúng với nơi bạn lưu file data.json
		String filePath = "src/main/resources/graph_data.json";
		GraphService graphService = new GraphService();
		Graph graph = graphService.loadGraphFromJson(filePath);

		if (graph == null) {
			System.err.println("Không thể tải đồ thị. Dừng chương trình.");
			return;
		}

		// 2. Khởi tạo Service nghiệp vụ
		RoutingService routingService = new RoutingService();
		// Inject graph vào routingService (cần thêm setter như hướng dẫn trên)
		routingService.setGraph(graph);

		// 3. Khởi tạo thuật toán
		// Cần thêm constructor cho FireflyAlgorithm như hướng dẫn trên
		FireflyAlgorithm fa = new FireflyAlgorithm(graph, routingService);

		// 4. Chạy thuật toán
		long startTime = System.currentTimeMillis();
		Route bestRoute = fa.run();
		long endTime = System.currentTimeMillis();

		// 5. In kết quả
		System.out.println("\n=== KẾT QUẢ TỐI ƯU ===");
		if (bestRoute != null && !bestRoute.getNodes().isEmpty()) {
			System.out.println("Lộ trình tốt nhất: " + bestRoute.toString());
			System.out.printf("Tổng thời gian: %.2f giờ\n", bestRoute.getTotalTime());
			System.out.printf("Tổng chi phí: %.2f VNĐ\n", bestRoute.getTotalCost());
			System.out.println("Thời gian chạy thuật toán: " + (endTime - startTime) + "ms");
		} else {
			System.out.println("Không tìm thấy lộ trình nào!");
		}
	}
}
