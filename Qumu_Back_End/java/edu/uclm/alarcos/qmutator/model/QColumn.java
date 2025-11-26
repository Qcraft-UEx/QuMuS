package edu.uclm.alarcos.qmutator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.model.gates.AControlGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.GateBuilder;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;
import edu.uclm.alarcos.qmutator.model.gates.IHasRotationAngle;
import edu.uclm.alarcos.qmutator.model.gates.complex.CallToUserDefinedGate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.Gate1;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateR;
import edu.uclm.alarcos.qmutator.model.gates.primitive.OneQubitGate;

public class QColumn {
	private Integer pos;	
	private Circuit containerCircuit;
	private List<Gate> gates;
	private List<AControlGate> controlGates;
	private int qubits;
	private Gate controlledGate;
	
	public QColumn() {
		this.gates = new ArrayList<>();
		this.controlGates = new ArrayList<>();
	}
	
	public QColumn(QColumn original) {
		this();
		for (int i=0; i<original.qubits; i++) {
			Gate1 gate = new Gate1();
			gate.setColumn(this);
			gate.setQubit(i);
			this.gates.add(gate);
		}
		this.containerCircuit = original.getContainerCircuit();
		this.qubits = original.qubits;
	}
	
	public void add(Gate gate) {
		this.gates.add(gate);
	}
	
	public void load(JSONArray jsaColumn) throws Exception {
		for (int i=0; i<jsaColumn.length(); i++) {
			String gateName = jsaColumn.get(i).toString();
			Gate gate = GateBuilder.build(this, i, gateName);
			this.add(gate);
		}
		
		int ncg = this.controlGates.size();
		
		if (ncg>0) {
			Gate gate;
			this.controlledGate=null;
			for (int i=0; i<this.gates.size(); i++) {
				gate = this.gates.get(i);
				if (!(gate instanceof AControlGate) && !gate.isNumeric()) {
					this.controlledGate = gate;
					break;
				}
			}
			if (this.controlledGate!=null) {
				AControlGate controlGate;
				for (int i=0; i<this.controlGates.size(); i++) {
					controlGate = this.controlGates.get(i);
					this.controlledGate.addControlGate(controlGate);
					controlGate.setControlledGate(this.controlledGate);
				}
			}
			
			if (this.controlledGate==null)
				throw new Exception("Controlled gate not found in column " + this.pos);
			if (!(this.controlledGate instanceof IControlable))
				throw new Exception("The controled gate (" + this.controlledGate.getName() + ")  is not controlable in column " + this.pos);
		}
	}

	public JSONArray toJSON() {
		JSONArray jsa = new JSONArray();
		Gate gate;
		for (int i=0; i<this.gates.size(); i++) {
			gate = this.gates.get(i);
			jsa.put(gate.getQuirkId());
		}
		return jsa;
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}


	public String buildQiskitCode() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		int ncg = this.controlGates.size();
		
		if (ncg == 0) {
			Gate gate;
			for (int i=0; i<this.gates.size(); i++) {
				gate = this.gates.get(i);
				if (!gate.isNumeric())
					sb = sb.append(gate.getQiskitCode());
			}
		} else if (ncg == 1) {
			AControlGate controlGate = this.controlGates.get(0);
			//sb = sb.append(this.combine(controlGate, this.controlledGate));
		} else if (ncg == 2){
			AControlGate controlGate1 = this.controlGates.get(0);
			AControlGate controlGate2 = this.controlGates.get(1);
			sb = sb.append("circuit.ccx(qreg[" + controlGate1.getQubit() + "], qreg[" + controlGate2.getQubit() + "], qreg[" + controlGate1.getControlledGate().getQubit() + "])");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	private String combine(AControlGate controlGate, Gate controlledGate) throws Exception {
		String result = "";
		if (controlledGate instanceof CallToUserDefinedGate) {
			CallToUserDefinedGate ctudg = (CallToUserDefinedGate) controlledGate;
			String ctudgName = ctudg.getName().toString();
			if (ctudgName.equals("RX") || ctudgName.equals("RY") || ctudgName.equals("RZ")) {
				GateR gateR = GateBuilder.build("Gate" + ctudgName);
				String quirkId = gateR.getQuirkId().toString();
				result = "circuit.c" + quirkId.toLowerCase() + 
					"(" + gateR.getAngle() + ", qreg[" + controlGate.getQubit() + "], qreg[" +
					controlledGate.getQubit() + "])\n";
			} else if (ctudgName.equals("U")) {
				result = "circuit.cu" + "(" + ctudg.getCalledGate().getDeclaration() + ", " +
						"qreg[" + controlGate.getQubit() + "], qreg[" +
						controlledGate.getQubit() + "])\n";
			}
		} else if (controlledGate instanceof IHasRotationAngle){
			IHasRotationAngle angleGate = (IHasRotationAngle) controlledGate;
			result = "circuit.c" + controlledGate.getQuirkId().toString().toLowerCase() + 
					"(" + angleGate.getAngle() + ", qreg[" + controlGate.getQubit() + "], " +
					"qreg[" + controlledGate.getQubit() + "])\n";
			return "";
		} else {
			result = "circuit.c" + controlledGate.getQuirkId().toString().toLowerCase() + 
					"(qreg[" + controlGate.getQubit() + "], " +
					"qreg[" + controlledGate.getQubit() + "])\n";
		}
		return result;
	}

	public Gate getControlledGate() {
		return controlledGate;
	}
	
	public void setControlledGate(Gate controlledGate) {
		this.controlledGate = controlledGate;
	}

	public List<Gate> getGates() {
		return this.gates;
	}

	void setGate(int wireIndex, Gate gate) {
		if (wireIndex==this.qubits)
			this.gates.add(gate);
		else if (wireIndex<this.qubits)
			this.gates.set(wireIndex, gate);
		else {
			for (int i=this.qubits; i<=wireIndex; i++) {
				Gate1 newGate = new Gate1(this, i);
				this.gates.add(newGate);
			}
			this.gates.set(wireIndex, gate);
		}
	}

	boolean removeGate(int wireIndex) {
		this.gates.remove(wireIndex);
		if (!this.gates.isEmpty())
			this.gates.add(wireIndex, new Gate1(this, wireIndex));
		this.removeGates1FromEnd();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean containsOnly(Class<? extends Gate>... gateClasses) {
		Gate gate;
		int cont = 0;
		for (int i=0; i<this.gates.size(); i++) {
			gate = this.gates.get(i);
			for (int j=0; j<gateClasses.length; j++) {
				if (gateClasses[j]==gate.getClass())
					cont++;
			}
		}
		return cont==this.gates.size();
	}

	private void removeGates1FromEnd() {
		int last = this.qubits-1;
		Gate gate;
		for (int i=last; i>=0; i--) {
			gate = this.gates.get(i);
			if (gate instanceof OneQubitGate) {
				OneQubitGate oqg = (OneQubitGate) gate;
				if (oqg.getName().equals("1"))
					this.gates.remove(i);
			} else
				return;
		}
	}

	@JsonIgnore
	public ArrayList<AControlGate> getControlGates() {
		Gate gate;
		ArrayList<AControlGate> controlGates = new ArrayList<>();
		for (int i=0; i<this.gates.size(); i++) {
			gate = this.gates.get(i);
			if (gate instanceof AControlGate)
				controlGates.add((AControlGate) gate);
		}
		return controlGates;
	}
	
	public void setContainerCircuit(Circuit containerCircuit) {
		this.containerCircuit = containerCircuit;
	}
	
	@JsonIgnore
	public Circuit getContainerCircuit() {
		return containerCircuit;
	}
	
	public int getPos() {
		return pos;
	}
	
	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setQubits(int qubits) {
		this.qubits = qubits;
	}
	
	public int getQubits() {
		return qubits;
	}

	public void addControlGate(AControlGate gate) {
		this.controlGates.add(gate);
	}

	public boolean recalculateControlGates(AControlGate controlGate) {
		boolean columnIsRemovable = false;
		if (controlGate!=null) {
			this.controlGates.remove(controlGate);
			
			int ncg = this.controlGates.size();
			if (ncg==0) {
				this.controlGates.clear();
				this.controlledGate = null;
			}
		} else {
			int noCg = 0;
			Gate gate;
			for (int i=0; i<this.gates.size(); i++) {
				gate = this.gates.get(i);
				if (!(gate instanceof AControlGate) && (gate instanceof IControlable))
					noCg++;
			}
			columnIsRemovable = noCg==0;
		}
		return columnIsRemovable;
	}
}
