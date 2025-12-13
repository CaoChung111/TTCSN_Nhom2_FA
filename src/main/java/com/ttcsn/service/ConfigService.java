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
		// --- CẤU HÌNH 10 BỘ DỮ LIỆU

		// 1. Map 11 (Small)
		profiles.add(new DatasetProfile("1. Map 11 (Small)", "src/main/resources/I_11N_22E.json", 10, // Pop
				30, // Gen
				100000, // Budget
				0.2, // Alpha
				0.1, // Gamma
				"A", "T", 7.0));

		// 2. Map 15 (Small)
		profiles.add(new DatasetProfile("2. Map 15 (Small)", "src/main/resources/I_15N_50E.json", 15, 40, 100000, 0.3,
				0.1, "A", "T", 7.0));

		// 3. Map 20 (Small)
		profiles.add(new DatasetProfile("3. Map 20 (Small)", "src/main/resources/I_20N_80E.json", 20, 50, 150000, 0.3,
				0.5, "A", "T", 12));

		// 4. Map 30 (Medium)
		profiles.add(new DatasetProfile("4. Map 30 (Medium)", "src/main/resources/I_30N_180E.json", 50, 80, 150000, 0.4,
				0.5, "Trạm 1", "Trạm 30", 7.5));

		// 5. Map 50 (Medium)
		profiles.add(new DatasetProfile("5. Map 50 (Medium)", "src/main/resources/I_50N_200E.json", 40, 100, 150000,
				0.5, 0.5, "Trạm 1", "Trạm 50", 11.5));

		// 6. Map 50 (Dense - Mật độ cao)
		profiles.add(new DatasetProfile("6. Map 50 (Dense)", "src/main/resources/I_50N_500E.json", 75, 130, 150000, 0.6,
				0.8, "Trạm 1", "Trạm 50", 8.0));

		// 7. Map 75 (Large)
		profiles.add(new DatasetProfile("7. Map 75 (Large)", "src/main/resources/I_75N_350E.json", 60, 120, 150000, 0.5,
				0.8, "Trạm 1", "Trạm 75", 8.0));

		// 8. Map 100 (Large)
		profiles.add(new DatasetProfile("8. Map 100 (Large)", "src/main/resources/I_100N_800E.json", 70, 130, 150000,
				0.5, 1.0, "Trạm 1", "Trạm 100", 9.0));

		// 9. Map 100 (Tight - Ngân sách thấp)
		profiles.add(new DatasetProfile("9. Map 100 (Tight)", "src/main/resources/I_100N_800E_Tight.json", 80, 150,
				80000, 0.7, 1.0, "Trạm 1", "Trạm 100", 17.0));

		// 10. Map 200 (Max)
		profiles.add(new DatasetProfile("10. Map 200 (Max)", "src/main/resources/I_200N_1000E.json", 100, 150, 300000,
				0.6, 1.0, "Trạm 1", "Trạm 200", 6.0));
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

		// Nạp các tham số kỹ thuật mặc định
		applyTechnicalParams(selected);

		System.out.println("\n------------------------------------------");
		System.out.println(">> Đã nạp cấu hình: " + selected.name);
		System.out.println("------------------------------------------");

		// 2. Nhập các thông số người dùng
		System.out.println("CẤU HÌNH YÊU CẦU (Ấn Enter để dùng mặc định):");

		// Nhập
		Constant.START_POINT = inputString("Điểm bắt đầu", selected.defaultStart);
		Constant.END_POINT = inputString("Điểm kết thúc ", selected.defaultEnd);
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
		Constant.GAMMA = p.gamma;

	}

	// --- CÁC HÀM NHẬP LIỆU ---
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