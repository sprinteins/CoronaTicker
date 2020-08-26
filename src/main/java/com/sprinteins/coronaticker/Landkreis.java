package com.sprinteins.coronaticker;

import java.math.BigDecimal;

public class Landkreis {

	private String name;
	private BigDecimal cases;

	public Landkreis(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getCases() {
		return cases;
	}

	public void setCases(BigDecimal cases) {
		this.cases = cases;
	}

}
