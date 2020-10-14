package com.sprinteins.coronaticker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	public Landkreis getOfficeLandkreis() {
		for (Landkreis landkreis : relevantLandkreise) {
			if (landkreis.isOffice()) {
				return landkreis;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, relevantLandkreise);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Standort other = (Standort) obj;
		return Objects.equals(name, other.name) && Objects.equals(relevantLandkreise, other.relevantLandkreise);
	}

}
