package com.ttcsn.config;

public class Constant {
	public static final String START_POINT = "A";
	public static final String END_POINT = "T";
	public static final double TIME_START = 7; // 7h

	// Giới hạn và hệ số phạt
	public static final double MAX_COST = 100000.00; // C_max
	public static final double PENALTY_FACTOR = 1; // R

	// Tham số thuật toán
	public static final double BETA_0 = 1.0; // β0 - độ hấp dẫn tối đa
	public static final double GAMMA = 0.5; // γ - hệ số tắt dần ánh sáng
	public static final double ALPHA = 0.4; // a - hệ số đột biến
	public static final int POPULATION_SIZE = 8; // n - số lượng đom đóm
	public static final int MAX_GENERATION = 20; // gen_max - số thế hệ tối đa

	// Các khung giờ cao điểm
	public static final double AM_START = 7.0; // 7:00
	public static final double AM_END = 9.0; // 9:00
	public static final double PM_START = 17; // 17:00
	public static final double PM_END = 19; // 19:00

}
