package edu.uclm.alarcos.qmutator.model;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class WGate {
	@Id @Column(length = 36)
	private String id;
	@ManyToOne(cascade = CascadeType.ALL)
	private Circuit originalCircuit;
	private String gateName;
	private int colIndex;
	private int rowIndex;
	
	public WGate() {
		this.id = UUID.randomUUID().toString();
	}
	
	public void setOriginalCircuit(Circuit originalCircuit) {
		this.originalCircuit = originalCircuit;
	}
	
	public Circuit getOriginalCircuit() {
		return originalCircuit;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	
	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
