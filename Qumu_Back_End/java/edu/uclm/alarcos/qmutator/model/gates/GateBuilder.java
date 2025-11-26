package edu.uclm.alarcos.qmutator.model.gates;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.complex.CallToUserDefinedGate;
import edu.uclm.alarcos.qmutator.model.gates.complex.UserDefinedGate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.Gate0;
import edu.uclm.alarcos.qmutator.model.gates.primitive.Gate1;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateH;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateI;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateMeasure;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateNoOperation;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateP;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateR;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateRX;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateRY;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateRZ;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateS;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateSdg;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateSwap;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateTdg;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateU;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateX;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateY;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateZ;

public class GateBuilder {
	
	public static Gate build(QColumn column, int qubit, String gateName) throws Exception {
		Gate gate = null;
		String quirkId = gateName;
		
		switch (gateName) {
		case "0" :
			gate = new Gate0(); break;
		case "1" : 
			gate = new Gate1(); break;
		case "X" :
			gate = new GateX(); break;
		case "H" :
			gate = new GateH(); break;
		case "I" :
			gate = new GateI(); break;
		case "S" :
		case "Z^½" :
			gate = new GateS(); break;
		case "Z" :
			gate = new GateZ(); break;
		case "Z^-¼" :
			gate = new GateTdg();
			break;
		case "Z^-½" :
			gate = new GateSdg(); break;
		case "P" :
		case "X^½" :
			gate = new GateP(); break;
		case "RX" :
			gate = new GateRX(); 
			break;
		case "RY" :
		case "Y^½" :
			gate = new GateRY(); 
			break;
		case "RZ" :
		case "Z^¼" :
			gate = new GateRZ(); 
			break;
		case "Y" : 
			gate = new GateY(); break;
		case "U" :
			gate = new GateU(); break;
		case "•":
			gate = new ControlGate();
			column.addControlGate((AControlGate) gate);
			break;
		case "◦" :
			gate = new AntiControlGate();
			column.addControlGate((AControlGate) gate);
			break;
		case "…" :
			gate = new GateNoOperation(); break;
		case "Measure" :
			gate = new GateMeasure(); break;
		case "Swap" :
			gate = new GateSwap(); break;
		case "\\u2026" :
			gate = new GateI(); break;
		}
		
		if (gate==null) {
			Circuit circuit = column.getContainerCircuit();
			UserDefinedGate calledGate = circuit.getGatesById().get(gateName);
			if (calledGate!=null) {
				CallToUserDefinedGate cudg = new CallToUserDefinedGate(calledGate);
				cudg.setName(calledGate.getName().toString());
				gate = cudg;
			}
		}
		if (gate==null)
			throw new Exception("Gate " + gateName + " could not be built");
		
		gate.setColumn(column);
		gate.setQubit(qubit);
		if (gate.getQuirkId()==null)
			gate.setQuirkId(quirkId);
		return gate;
	}

	public static GateR build(String name) throws Exception {
		Class<?> clazz = Class.forName("edu.uclm.alarcos.qmutator.model.gates.primitive." + name);
		GateR gate = (GateR) clazz.newInstance();
		return gate;
	}

}
