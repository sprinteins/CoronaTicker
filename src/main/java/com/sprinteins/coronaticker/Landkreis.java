package com.sprinteins.coronaticker;

import java.math.BigDecimal;
import java.util.Objects;

public class Landkreis {

	private String displayName;
	private String apiName;
	private BigDecimal cases;
	private boolean office;

	public Landkreis(String name) {
		this(name, name, false);
	}

	public Landkreis(String displayName, String apiName) {
		this(displayName, apiName, false);
	}

	public Landkreis(String name, boolean officeLockdownRelevant) {
		this(name, name, officeLockdownRelevant);
	}

	public Landkreis(String displayName, String apiName, boolean officeLockdownRelevant) {
		this.displayName = displayName;
		this.apiName = apiName;
		this.office = officeLockdownRelevant;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getApiName() {
		return apiName;
	}

	public BigDecimal getCases() {
		return cases;
	}

	public void setCases(BigDecimal cases) {
		this.cases = cases;
	}

	public boolean isOffice() {
		return office;
	}

	public Risk evaluateRisk() {

		// High
		if (getCases().intValue() >= 35) {
			return Risk.HIGH;
		}

		// Medium
		if (getCases().intValue() >= 5) {
			return Risk.MEDIUM;
		}

		// Low
		return Risk.LOW;

	}

	@Override
	public int hashCode() {
		return Objects.hash(apiName, cases, displayName, office);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Landkreis other = (Landkreis) obj;
		return Objects.equals(apiName, other.apiName) && Objects.equals(cases, other.cases)
				&& Objects.equals(displayName, other.displayName) && office == other.office;
	}

}
