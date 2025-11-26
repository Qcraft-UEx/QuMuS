package edu.uclm.alarcos.qmutator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.json.JSONArray;

@Entity
public class InitColumn {
	@Id @Column(length=36)
	private String id;
	
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<InitialValue> initialValues;
	
	@OneToOne
	private Circuit containerCircuit;
	
	public InitColumn() {
		this.id = UUID.randomUUID().toString();
		this.initialValues = new ArrayList<>();
	}

	public InitColumn(int rows) {
		this();
		for (int i=0; i<rows; i++)
			this.initialValues.add(InitialValue.ZERO);
	}

	public InitColumn copy() {
		InitColumn result = new InitColumn();
		for (InitialValue value : this.initialValues)
			result.initialValues.add(value);
		return result;
	}

	public void load(JSONArray jsa) {
		for (int i=0; i<jsa.length(); i++)
			this.initialValues.add(InitialValue.parse(jsa.get(i)));
	}

	public JSONArray toJSON() {
		JSONArray jsa = new JSONArray();
		for (int i=0; i<this.initialValues.size(); i++)
			jsa.put(toValue(initialValues.get(i)));
		return jsa;
	}

	private Object toValue(InitialValue value) {
		if (value==InitialValue.ZERO)
			return 0;
		if (value==InitialValue.ONE)
			return 1;
		if (value==InitialValue.POS_I)
			return "i";
		if (value==InitialValue.NEG_I)
			return "-i";
		if (value==InitialValue.PLUS)
			return "+";
		return "-";
	}

	public int size() {
		return this.initialValues.size();
	}

	public InitialValue getValue(int wireIndex) {
		return this.initialValues.get(wireIndex);
	}

	public void add(InitialValue value) {
		this.initialValues.add(value);
	}

	public void setValues(List<InitialValue> newValues) {
		this.initialValues = newValues;
	}

	public void setValue(int row, InitialValue value) {
		this.initialValues.set(row, value);
	}

	public String buildQiskitCode() {
		for (int i=0; i<this.size(); i++) {
			InitialValue n = this.initialValues.get(i);
			if (n!=null && n==InitialValue.ONE)
				return "circuit.x(qreg[" + i + "])\n";
		}
		return "";
	}

	public static Integer getNumber(Object object) {
		try {
			return (Integer) object;
		} catch (Exception e) {
		}
		return null;
	}

	public void setContainerCircuit(Circuit containerCircuit) {
		this.containerCircuit = containerCircuit;
	}
	
	public Circuit getContainerCircuit() {
		return containerCircuit;
	}
}
