package com.ttcsn.model;

import java.util.Objects;

public class Node {
	private int id;
	private String name;

	public Node(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	// đảm bảo hoạt động chính xác
	// So sánh 2 Node
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Node node = (Node) o;
		return id == node.id;
	}

	// xác định vị trí lưu trữ
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
