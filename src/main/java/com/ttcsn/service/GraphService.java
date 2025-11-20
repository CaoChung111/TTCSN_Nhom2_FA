package com.ttcsn.service;

import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.ttcsn.model.Edge;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.dto.EdgeData;
import com.ttcsn.model.dto.GraphData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GraphService {
	public Graph loadGraphFromJson(String filePath) {
		Gson gson = new Gson();
		Graph graph = new Graph(); // Khởi tạo graph rỗng

		try (Reader reader = new FileReader(filePath)) {

			// 1. GSON đọc file JSON và đổ vào lớp GraphData
			GraphData graphData = gson.fromJson(reader, GraphData.class);

			// 2. Thêm tất cả Node vào Graph
			// (Sử dụng trực tiếp Node từ file JSON)
			for (Node node : graphData.getNodes()) {
				graph.addNode(node);
			}

			// 3. Xử lý logic để thêm các Edge
			for (EdgeData edgeData : graphData.getEdges()) {

				// Lấy các đối tượng Node THẬT từ graph bằng ID
				Node fromNode = graph.getNode(edgeData.getFromId());
				Node toNode = graph.getNode(edgeData.getToId());

				// Kiểm tra xem các Node có tồn tại không
				if (fromNode == null || toNode == null) {
					System.err.println("Lỗi: Không tìm thấy Node cho cạnh từ " + edgeData.getFromId() + " đến "
							+ edgeData.getToId());
					continue;
				}

				// Tạo đối tượng Edge THẬT bằng cách truyền các đối tượng Node
				Edge edge = new Edge(fromNode, toNode, edgeData.getDistance(), edgeData.getSpeedLimit(),
						edgeData.getCost(), edgeData.getTrafficLights(), edgeData.getAvgWaitTime(),
						edgeData.getRushHourFactor());

				// Thêm cạnh này vào đồ thị
				graph.addEdge(edge);
			}

			System.out.println("Tải đồ thị từ " + filePath + " thành công!");

		} catch (Exception e) {
			System.err.println("Lỗi khi đọc file JSON: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return graph;
	}
        public static List<String> generateRandomRoute(String startNode, String endNode, Map<String, List<String>> graph) {
        Random rand = new Random();

        while (true) {
            // Khởi tạo lộ trình với điểm bắt đầu
            List<String> path = new ArrayList<>();
            path.add(startNode);
            String currentNode = startNode;

            while (!currentNode.equals(endNode)) {
                // Bước 1: Lấy danh sách hàng xóm trực tiếp. Đơn giản hơn nhiều!
                List<String> neighbors = graph.get(currentNode);

                // Nếu không có hàng xóm (ngõ cụt), thoát để thử lại
                if (neighbors == null || neighbors.isEmpty()) {
                    break;
                }

                // Bước 2: Lọc ra các hàng xóm hợp lệ (chưa đi qua) bằng vòng lặp for đơn giản
                List<String> validNeighbors = new ArrayList<>();
                for (String neighbor : neighbors) {
                    if (!path.contains(neighbor)) {
                        validNeighbors.add(neighbor);
                    }
                }

                // Nếu không còn hàng xóm hợp lệ nào, thoát để thử lại
                if (validNeighbors.isEmpty()) {
                    break;
                }

                // Chọn ngẫu nhiên một hàng xóm từ danh sách hợp lệ
                String nextNode = validNeighbors.get(rand.nextInt(validNeighbors.size()));
                path.add(nextNode);
                currentNode = nextNode;
            }

            // Nếu đã đến đích, trả về lộ trình
            if (path.get(path.size() - 1).equals(endNode)) {
                return path;
            }
        }
    }

    public static void main(String[] args) {
        // --- CẤU TRÚC DỮ LIỆU MỚI: ĐƠN GIẢN HƠN, KHÔNG CÒN SỐ 1 ---
        // Mỗi đỉnh sẽ tương ứng với một DANH SÁCH các đỉnh kề nó.
        Map<String, List<String>> graph = Map.of(
            "A", List.of("B", "C"),
            "B", List.of("D"),
            "C", List.of("D", "E"),
            "D", List.of("E", "T"),
            "E", List.of("T"),
            "T", List.of() // Đỉnh T không đi đâu cả -> danh sách rỗng
        );

        // Giữ nguyên điểm bắt đầu và kết thúc để ví dụ
        String start = "A";
        String end = "T";

        System.out.println("Đang tao lo trinh " + start + " den " + end + "...");

        // Gọi hàm với cấu trúc dữ liệu mới
        List<String> randomRoute = generateRandomRoute(start, end, graph);

        System.out.println("Lo trinh mơi la: " + String.join(" -> ", randomRoute));
    }
}
