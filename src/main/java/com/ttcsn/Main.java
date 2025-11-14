package com.ttcsn;

import com.ttcsn.algorithm.FireflyAlgorithm;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;
import com.ttcsn.service.GraphService;

public class Main {
	public static void main(String[] args) {
		System.out.println("--- BẮT ĐẦU TEST HÀM MUTATE (ĐỘT BIẾN) ---");

		try {
			// 1. Tải đồ thị (Graph)
			GraphService loader = new GraphService();
			String filePath = "src/main/resources/graph_data.json";
			Graph graph = loader.loadGraphFromJson(filePath);
			System.out.println(">>> Đã tải Graph thành công.");

			// 2. Lấy điểm đầu (A) và cuối (T)
			Node start = graph.getNode(1);
			Node end = graph.getNode(8);
			if (start == null || end == null) {
				System.err.println("Lỗi: Không tìm thấy A hoặc T.");
				return;
			}

			// 3. Khởi tạo FireflyAlgorithm (truyền graph vào)
			FireflyAlgorithm fa = new FireflyAlgorithm(graph);

			// 4. Tạo 1 lộ trình GỐC
			Route originalRoute = fa.generateRandomRoute(start, end);
			System.out.println("\n--- LỘ TRÌNH GỐC ---");
			System.out.println(originalRoute); // In lộ trình gốc

			// 5. Test hàm mutate 5 lần
			System.out.println("\n--- BẮT ĐẦU ĐỘT BIẾN 5 LẦN ---");
			for (int i = 1; i <= 10; i++) {
				System.out.println("Lần đột biến " + i);
				Route mutatedRoute = fa.mutate(originalRoute);
				System.out.println("/n/n ");
			}

		} catch (Exception e) {
			System.err.println("!!! LỖI TRONG QUÁ TRÌNH TEST:");
			e.printStackTrace(); // Bắt các lỗi NullPointerException
		}
	}
}
