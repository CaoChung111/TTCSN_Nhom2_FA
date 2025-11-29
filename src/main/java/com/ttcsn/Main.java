package com.ttcsn;

import com.ttcsn.algorithm.FireflyAlgorithm;
import com.ttcsn.config.Constant;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Route;
import com.ttcsn.service.ConfigService;
import com.ttcsn.service.GraphService;
import com.ttcsn.service.ReportService;
import com.ttcsn.service.RoutingService;

public class Main {

	public static void main(String[] args) {
		System.out.println(">>> KHỞI ĐỘNG ỨNG DỤNG TÌM ĐƯỜNG FIREFLY...\n");

		try {
			// BƯỚC 1: CẤU HÌNH
			ConfigService config = new ConfigService();
			GraphService loadGraphJson = new GraphService();
			config.runMenu();

			// BƯỚC 2: LOAD DỮ LIỆU ĐỒ THỊ
			System.out.println(">> Đang tải dữ liệu đồ thị từ: " + Constant.GRAPH_FILE_PATH);
			Graph graph = null;
			try {
				graph = loadGraphJson.loadGraphFromJson(Constant.GRAPH_FILE_PATH);
			} catch (Exception e) {
				System.err.println("[LỖI] Không đọc được file dữ liệu!");
				System.err.println("Chi tiết: " + e.getMessage());
				System.out.println("Vui lòng kiểm tra lại đường dẫn file trong DatasetProfile.");
				return;
			}

			if (graph == null) {
				System.err.println("[LỖI] Dữ liệu đồ thị bị rỗng (null).");
				return;
			}
			System.out.println("--> [OK] Tải thành công file dữ liệu\n");

			// BƯỚC 3: KHỞI TẠO
			RoutingService routingService = new RoutingService();
			routingService.setGraph(graph);

			if (routingService.getNode(Constant.START_POINT) == null
					|| routingService.getNode(Constant.END_POINT) == null) {
				System.err.println("[LỖI] Điểm bắt đầu hoặc kết thúc không tồn tại trong bản đồ!");
				System.err.println("Start: " + Constant.START_POINT + " | End: " + Constant.END_POINT);
				return;
			}

			// BƯỚC 4: CHẠY THUẬT TOÁN
			FireflyAlgorithm fa = new FireflyAlgorithm(routingService);
			long startTime = System.currentTimeMillis();
			Route bestRoute = fa.run();
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			System.out.println("\n>>> CHƯƠNG TRÌNH HOÀN TẤT THÀNH CÔNG!");
			System.out.println("    Thời gian chạy: " + duration + " ms");

			// Xuất báo cáo văn bản
			ReportService reportService = new ReportService();
			reportService.saveFinalReport(bestRoute, duration);

		} catch (Exception e) {
			// Catch tất cả các lỗi
			System.err.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println("ĐÃ XẢY RA LỖI KHÔNG MONG MUỐN:");
			e.printStackTrace();
			System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}
}