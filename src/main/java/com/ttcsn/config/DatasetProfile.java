package com.ttcsn.config;

public class DatasetProfile {
	public String name;
	public String filePath;

	public int populationSize;
	public int maxGeneration;
	public double maxCostBudget;
	public double alpha;
	public double gamma;

	public String defaultStart;
	public String defaultEnd;
	public double defaultStartTime;

	public DatasetProfile(String name, String filePath, int pop, int gen, double budget, double alpha, double gamma,
			String defStart, String defEnd, double defTime) {
		this.name = name;
		this.filePath = filePath;
		this.populationSize = pop;
		this.maxGeneration = gen;
		this.maxCostBudget = budget;
		this.alpha = alpha;
		this.gamma = gamma;
		this.defaultStart = defStart;
		this.defaultEnd = defEnd;
		this.defaultStartTime = defTime;
	}
}