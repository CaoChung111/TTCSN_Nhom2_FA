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
		Graph graph = new Graph();

		try (Reader reader = new FileReader(filePath)) {

			// GSON đọc file JSON và đổ vào lớp GraphData
			GraphData graphData = gson.fromJson(reader, GraphData.class);

			// Thêm tất cả Node vào Graph
			for (Node node : graphData.getNodes()) {
				graph.addNode(node);
			}

			// Thêm các Edge
			for (EdgeData edgeData : graphData.getEdges()) {
				Node fromNode = graph.getNode(edgeData.getFromId());
				Node toNode = graph.getNode(edgeData.getToId());
				if (fromNode == null || toNode == null) {
					System.err.println("Lỗi: Không tìm thấy Node cho cạnh từ " + edgeData.getFromId() + " đến "
							+ edgeData.getToId());
					continue;
				}

				// Tạo đối tượng Edge
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
