package com.sprinteins.coronaticker;

import java.util.ArrayList;
import java.util.List;

public class Standort {

	private String name;
	private List<Landkreis> relevantLandkreise = new ArrayList<>();

	public Standort(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Landkreis> getRelevantLandkreise() {
		return relevantLandkreise;
	}

	public Risk evaluateRisk() {

		// High
		for (Landkreis landkreis : relevantLandkreise) {
			if (landkreis.getCases().intValue() >= 25) {
				return Risk.HIGH;
			}
		}

		// Medium
		for (Landkreis landkreis : relevantLandkreise) {
			if (landkreis.getCases().intValue() >= 5) {
				return Risk.MEDIUM;
			}
		}

		// Low
		return Risk.LOW;

	}

}
