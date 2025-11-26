package edu.uclm.alarcos.qmutator.model;

import java.util.ArrayList;
import java.util.List;

public class KillingMatrix {
	private List<KMRow> rows;
	private int total;
	private double killeds;
	private double ms;
	private double is;
	private double injureds;
	
	public KillingMatrix() {
		this.rows = new ArrayList<>();
	}

	public void add(Mutant mutant) {
		KMRow row = new KMRow();
		row.setMutant(mutant);
		rows.add(row);
	}

	public List<KMRow> getRows() {
		return rows;
	}

	public void setTotal(int mutants) {
		this.total = mutants;
	}

	public void setKilleds(double killeds) {
		this.killeds = killeds;
	}

	public void setMS(double ms) {
		this.ms = ms;
	}
	
	public int getTotal() {
		return total;
	}
	
	public double getKilleds() {
		return killeds;
	}
	
	public double getMs() {
		return ms;
	}

	public void setIS(double is) {
		this.is = is;
	}
	
	public double getIs() {
		return is;
	}

	public void setInjureds(double injureds) {
		this.injureds = injureds;
	}
	
	public double getInjureds() {
		return injureds;
	}
}
