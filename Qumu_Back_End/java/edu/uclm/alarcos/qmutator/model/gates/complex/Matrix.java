package edu.uclm.alarcos.qmutator.model.gates.complex;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class Matrix extends UserDefinedGate {
	private String matrix;
	private int qubits;
	
	public Matrix() {
		super();
		this.matrix = "";
	}
	
	public void loadMatrix(String matrix) {
		int posLeft = matrix.indexOf('{', 1);
		int posRight = matrix.indexOf('}', posLeft+1);
		String data = matrix.substring(posLeft+1, posRight);
		this.qubits = (int) Math.sqrt(data.split(",").length);
		
		this.matrix = matrix.replace("√½", "" + Math.sqrt(2));
	}
	
	public void setMatrix(String matrix) {
		this.matrix = matrix;
	}
	
	public String getMatrix() {
		return matrix;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("id", this.quirkId);
		jso.put("name", this.name);
		jso.put("matrix", this.matrix);
		return jso;
	}

	@Override
	public String toString() {
		return this.toJSON().toString();
	}

	@Override
	public Gate concreteCopy() {
		Matrix result = new Matrix();
		result.setMatrix(matrix);
		return result;
	}
	
	@Override
	public String getDeclaration() {
		return this.matrix.replace("{", "").replace("}", "");
	}

	@Override
	public String getQiskitCode() {
		String matrix = this.matrix.replace("{", "").replace("}", "").trim();
		double angle = this.getAngle(matrix);
		String r = "";
		if (Double.isNaN(angle)) {
			r = "# Ojo: el ángulo de la puerta matriz " + this.name + " no se ha calculado bien\n";
			r = "circuit." + this.name.toLowerCase() + "(180, " + "qreg[" + this.qubit + "])\n";
		} else {
			r = "circuit." + this.name.toLowerCase() + "(" + angle + ", " + "qreg[" + this.qubit + "])\n";
		}
		return r;
	}
	
	private double getAngle(String matrix) {
		String sTheta = matrix.split(",")[3].trim();
		double theta = Double.parseDouble(sTheta);
		return -2 * Math.asin(theta);
	}
	
	public int getQubits() {
		return qubits;
	}

	public void setQubits(int qubits) {
		this.qubits = qubits;
	}
}
