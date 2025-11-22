package com.ttcsn.config;

public class Constant {
	public static final String START_POINT = "A";
	public static final String END_POINT = "M (Hub 5)";
	public static final double TIME_START = 8.5; // 8:30

	// Giới hạn và hệ số phạt
	public static final double MAX_COST = 80000.00; // C_max
	public static final double PENALTY_FACTOR = 2.0; // R

	// Tham số thuật toán
	public static final double BETA_0 = 1.0; // β0 - độ hấp dẫn tối đa
	public static final double GAMMA = 0.5; // γ - hệ số tắt dần ánh sáng
	public static final int POPULATION_SIZE = 10; // n - số lượng đom đóm
	public static final int MAX_GENERATION = 30; // gen_max - số thế hệ tối đa

	// Các khung giờ cao điểm
	public static final double AM_START = 7.0; // 7:00
	public static final double AM_END = 9.0; // 9:00
	public static final double PM_START = 16.5; // 16:30
	public static final double PM_END = 18.5; // 18:30

	private Constant() {
	}

}
