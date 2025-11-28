package com.ttcsn.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ttcsn.config.Constant;
import com.ttcsn.config.DatasetProfile;

public class ConfigService {
	private final Scanner scanner = new Scanner(System.in);
	private final List<DatasetProfile> profiles = new ArrayList<>();

	public ConfigService() {
		// --- CẬP NHẬT LẠI DANH SÁCH PROFILE (Thêm giờ khởi hành vào cuối) ---

		// Bộ 1: 50 đỉnh (Giờ mặc định 7.0)
		profiles.add(new DatasetProfile("Map 11 Đỉnh (I_11N_22E)", "src/main/resources/I_11N_22E.json", 10, 30, 100000,
				0.4, "A", "T", 7.0 // <--- Đi lúc 7h sáng
		));

		// Bộ 2: 100 đỉnh (Giờ mặc định 6.5)
		profiles.add(new DatasetProfile("Map 20 Đỉnh (I_20N_80E)", "src/main/resources/I_20N_80E.json", 20, 40, 200000,
				0.4, "A", "T", 6.5 // <--- Đi sớm hơn lúc 6h30
		));
	}

	public void runMenu() {
		System.out.println("\n==========================================");
		System.out.println("   CHƯƠNG TRÌNH TỐI ƯU HÓA LỘ TRÌNH");
		System.out.println("==========================================");

		System.out.println("Vui lòng chọn bộ dữ liệu kiểm thử:");
		for (int i = 0; i < profiles.size(); i++) {
			System.out.printf("[%d] %s\n", i + 1, profiles.get(i).name);
		}
		System.out.println("==========================================");

		// 1. Chọn Dataset
		int choice = inputInt("Nhập lựa chọn (1-" + profiles.size() + ")", 1);
		if (choice < 1 || choice > profiles.size())
			choice = 1;

		DatasetProfile selected = profiles.get(choice - 1);

		// Nạp các tham số kỹ thuật ngầm (Pop, Gen, Alpha...)
		applyTechnicalParams(selected);

		System.out.println("\n------------------------------------------");
		System.out.println(">> Đã nạp cấu hình: " + selected.name);
		System.out.println("------------------------------------------");

		// 2. Nhập các thông số người dùng (Có thể Enter để Skip)
		System.out.println("CẤU HÌNH YÊU CẦU (Ấn Enter để dùng mặc định):");

		// Nhập Điểm
		Constant.START_POINT = inputString("Điểm bắt đầu", selected.defaultStart);
		Constant.END_POINT = inputString("Điểm kết thúc ", selected.defaultEnd);

		// Nhập Thời gian & Tiền (Mặc định lấy từ Profile)
		Constant.TIME_START = inputDouble("Giờ khởi hành (h)", selected.defaultStartTime);
		Constant.MAX_COST = inputDouble("Ngân sách tối đa (VNĐ)", selected.maxCostBudget);

		System.out.println("\n>> TỔNG HỢP CẤU HÌNH:");
		System.out.println("   - Lộ trình: " + Constant.START_POINT + " -> " + Constant.END_POINT);
		System.out.println("   - Khởi hành: " + Constant.TIME_START + "h");
		System.out.printf("   - Ngân sách: %,.0f VNĐ\n", Constant.MAX_COST);
		System.out.println("==========================================\n");
	}

	private void applyTechnicalParams(DatasetProfile p) {
		Constant.GRAPH_FILE_PATH = p.filePath;
		Constant.POPULATION_SIZE = p.populationSize;
		Constant.MAX_GENERATION = p.maxGeneration;
		Constant.ALPHA = p.alpha;

	}

	// --- CÁC HÀM NHẬP LIỆU (Giữ nguyên logic Enter) ---

	private int inputInt(String label, int defaultValue) {
		System.out.printf("%-30s [%d]: ", label, defaultValue);
		try {
			String line = scanner.nextLine().trim();
			return line.isEmpty() ? defaultValue : Integer.parseInt(line);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private double inputDouble(String label, double defaultValue) {
		String displayValue;

		if (defaultValue == (long) defaultValue) {
			displayValue = String.format("%d", (long) defaultValue);
		} else {
			displayValue = String.format("%s", defaultValue);
		}
		System.out.printf("%-30s [%s]: ", label, displayValue);
		try {
			String line = scanner.nextLine().trim();
			return line.isEmpty() ? defaultValue : Double.parseDouble(line);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private String inputString(String label, String defaultValue) {
		System.out.printf("%-30s [%s]: ", label, defaultValue);
		String line = scanner.nextLine().trim();
		return line.isEmpty() ? defaultValue : line;
	}
}