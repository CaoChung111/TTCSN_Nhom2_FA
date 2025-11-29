package com.ttcsn.service;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Route;

public class ReportService {
	private static final String OUTPUT_DIR = "src/main/resources/output/";
	private static final String REPORT_FILE = OUTPUT_DIR + "firefly_final_report.txt";

	public void saveFinalReport(Route bestRoute, long executionTime) {
		// 1. Tạo thư mục nếu chưa có
		new File(OUTPUT_DIR).mkdirs();
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		// --- HEADER ---
		sb.append("==================================================\n");
		sb.append("          BÁO CÁO KẾT QUẢ TỐI ƯU HÓA              \n");
		sb.append("==================================================\n");
		sb.append("Ngày chạy: ").append(dtf.format(LocalDateTime.now())).append("\n\n");

		// --- PHẦN 1: CẤU HÌNH ---
		sb.append("--- 1. CẤU HÌNH ---\n");
		sb.append(String.format("Bộ dữ liệu:    %s\n", Constant.GRAPH_FILE_PATH));
		sb.append(String.format("Lộ trình:      %s -> %s\n", Constant.START_POINT, Constant.END_POINT));
		sb.append(String.format("Giờ khởi hành: %.1fh\n", Constant.TIME_START));
		sb.append(String.format("Ngân sách:     %,.0f VNĐ\n", Constant.MAX_COST));
		sb.append(String.format("Số đom đóm:    %d\n", Constant.POPULATION_SIZE));
		sb.append(String.format("Số thế hệ:     %d\n", Constant.MAX_GENERATION));
		sb.append(String.format("Tham số:       Alpha=%.2f, Gamma=%.2f, Beta0=%.2f\n", Constant.ALPHA, Constant.GAMMA,
				Constant.BETA_0));
		sb.append("\n");

		// --- PHẦN 2: KẾT QUẢ ---
		sb.append("--- 2. KẾT QUẢ TỐI ƯU NHẤT ---\n");
		if (bestRoute != null) {
			sb.append(String.format("Tổng Chi phí:  %,.0f VNĐ\n", bestRoute.getTotalCost()));
			sb.append(String.format("Tổng Thời gian:%.2f giờ\n", bestRoute.getTotalTime()));
			sb.append(String.format("Tổng Quãng đường: %.2f km\n", bestRoute.getTotalDistance()));

			// In chi tiết các trạm đi qua
			sb.append("Chi tiết lộ trình:\n");
			sb.append("  ").append(bestRoute.toString()).append("\n");

			// Kiểm tra trạng thái hợp lệ
			if (bestRoute.getTotalCost() <= Constant.MAX_COST) {
				sb.append(">> TRẠNG THÁI: [HỢP LỆ] (Thỏa mãn ngân sách)\n");
			} else {
				double over = bestRoute.getTotalCost() - Constant.MAX_COST;
				sb.append(String.format(">> TRẠNG THÁI: [VI PHẠM] (Vượt quá ngân sách %,.0f VNĐ)\n", over));
			}
		} else {
			sb.append(">> KẾT QUẢ: KHÔNG TÌM THẤY LỘ TRÌNH NÀO!\n");
		}
		sb.append("\n");

		// --- PHẦN 3: HIỆU NĂNG ---
		sb.append("--- 3. HIỆU NĂNG ---\n");
		sb.append(String.format("Thời gian thực thi: %d ms\n", executionTime));
		sb.append("==================================================\n");

		// --- GHI FILE ---
		try (FileWriter writer = new FileWriter(REPORT_FILE)) {
			writer.write(sb.toString());
			System.out.println("--> [OK] Đã ghi báo cáo chi tiết tại: " + REPORT_FILE);

			// Tự động mở file
			File file = new File(REPORT_FILE);
			if (Desktop.isDesktopSupported() && file.exists()) {
				Desktop.getDesktop().open(file);
			}
		} catch (IOException e) {
			System.err.println("[Lỗi ReportService] Không ghi được file: " + e.getMessage());
		}
	}
}