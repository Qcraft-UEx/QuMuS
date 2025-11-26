package edu.uclm.alarcos.qmutator.annealing.mathml;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.model.Manager;

public class LanzadoraMLParser {

	public static void main(String[] args) throws Exception {
		String code = Manager.get().readFileAsString("constraint1.json.txt");
		System.out.println(code);
		
		JSONObject jso = new JSONObject(code);
		Qumu2Math parser = new Qumu2Math(jso);
		String rule = parser.buildRule();
		
		System.out.println(rule);
	}

}
