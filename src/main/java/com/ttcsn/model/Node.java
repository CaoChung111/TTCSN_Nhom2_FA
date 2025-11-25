package com.ttcsn.model;

import java.util.Objects;

public class Node {
	private final int id;
	private final String name;

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
	@Override
	public String toString() {
	    return String.format("Node(id=%d, name=%s)", id, name);
	}

}
