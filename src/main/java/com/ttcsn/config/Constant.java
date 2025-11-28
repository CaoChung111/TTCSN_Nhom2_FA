package com.ttcsn.config;

public class Constant {
	public static String GRAPH_FILE_PATH = "src/main/resources/data/I_50N_200E.json";
	public static String START_POINT = "A";
	public static String END_POINT = "T";
	public static double TIME_START = 7; // 7h

	// Giới hạn và hệ số phạt
	public static double MAX_COST = 100000.00; // C_max
	public static final double PENALTY_FACTOR = 1; // R

	// Tham số thuật toán
	public static double BETA_0 = 1.0; // β0 - độ hấp dẫn tối đa
	public static double GAMMA = 0.5; // γ - hệ số tắt dần ánh sáng
	public static double ALPHA = 0.4; // a - hệ số đột biến
	public static int POPULATION_SIZE = 8; // n - số lượng đom đóm
	public static int MAX_GENERATION = 20; // gen_max - số thế hệ tối đa

	// Các khung giờ cao điểm
	public static final double AM_START = 7.0; // 7:00
	public static final double AM_END = 9.0; // 9:00
	public static final double PM_START = 17; // 17:00
	public static final double PM_END = 19; // 19:00

}
