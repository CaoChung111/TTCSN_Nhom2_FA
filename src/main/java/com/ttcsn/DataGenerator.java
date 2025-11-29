package com.ttcsn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

	// Cấu hình số lượng mong muốn
	private static final int NUM_NODES = 200;
	private static final int NUM_EDGES = 1000;
	private static final String FILE_PATH = "src/main/resources/I_" + NUM_NODES + "N_" + NUM_EDGES + "E.json";

	public static void main(String[] args) {
		System.out.println("Đang tạo dữ liệu giả lập...");
		Random rand = new Random();
		StringBuilder json = new StringBuilder();

		// 1. Mở JSON và tạo Nodes
		json.append("{\n  \"nodes\": [\n");
		for (int i = 1; i <= NUM_NODES; i++) {
			json.append(String.format("    { \"id\": %d, \"name\": \"Trạm %d\" }", i, i));
			if (i < NUM_NODES)
				json.append(",\n");
		}
		json.append("\n  ],\n");

		// 2. Tạo Edges ngẫu nhiên
		json.append("  \"edges\": [\n");
		for (int i = 1; i <= NUM_EDGES; i++) {
			// Random fromId và toId (đảm bảo khác nhau)
			int fromId = rand.nextInt(NUM_NODES) + 1;
			int toId = rand.nextInt(NUM_NODES) + 1;
			while (toId == fromId) {
				toId = rand.nextInt(NUM_NODES) + 1;
			}

			// Random các chỉ số giả lập thực tế
			int distance = 2 + rand.nextInt(48); // 2km - 50km
			int[] speeds = { 30, 40, 50, 60, 80, 100 };
			int speedLimit = speeds[rand.nextInt(speeds.length)];

			// Cost tính sơ bộ theo quãng đường + biến động ngẫu nhiên
			int cost = distance * 1500 + rand.nextInt(5) * 1000;

			int trafficLights = rand.nextInt(6); // 0 - 5 đèn
			int avgWaitTime = trafficLights * (10 + rand.nextInt(20)); // Mỗi đèn chờ 10-30s
			double rushHourFactor = 1.0 + (rand.nextInt(10) / 10.0); // 1.0 - 2.0

			json.append("    {\n");
			json.append(String.format("      \"edgeId\": \"e%d\",\n", i));
			json.append(String.format("      \"fromId\": %d,\n", fromId));
			json.append(String.format("      \"toId\": %d,\n", toId));
			json.append(String.format("      \"distance\": %d,\n", distance));
			json.append(String.format("      \"speedLimit\": %d,\n", speedLimit));
			json.append(String.format("      \"cost\": %d,\n", cost));
			json.append(String.format("      \"trafficLights\": %d,\n", trafficLights));
			json.append(String.format("      \"avgWaitTime\": %d,\n", avgWaitTime));
			json.append(String.format("      \"rushHourFactor\": %.1f\n", rushHourFactor));
			json.append("    }");
			if (i < NUM_EDGES)
				json.append(",\n");
		}
		json.append("\n  ]\n}");

		// 3. Ghi ra file
		try (FileWriter writer = new FileWriter(FILE_PATH)) {
			writer.write(json.toString());
			System.out.println("✅ Đã tạo thành công file: " + FILE_PATH);
			System.out.println("   - Số node: " + NUM_NODES);
			System.out.println("   - Số cạnh: " + NUM_EDGES);
		} catch (IOException e) {
			System.err.println("❌ Lỗi ghi file: " + e.getMessage());
		}
	}
}
