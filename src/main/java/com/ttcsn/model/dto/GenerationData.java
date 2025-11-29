package com.ttcsn.model.dto;

import java.util.List;

public class GenerationData {
    private int generation;   // dùng Integer để cho phép null
    private List<FireflyPoint> fireflies;

    public GenerationData(int generation, List<FireflyPoint> fireflies) {
        this.generation = generation;
        this.fireflies = fireflies;
    }

    public int getGeneration() { return generation; }
    public List<FireflyPoint> getFireflies() { return fireflies; }
}
