package com.ttcsn.service;

import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.ttcsn.model.Edge;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.dto.EdgeData;
import com.ttcsn.model.dto.GraphData;

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
}
