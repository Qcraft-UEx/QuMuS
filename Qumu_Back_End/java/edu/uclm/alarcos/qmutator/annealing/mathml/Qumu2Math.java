package edu.uclm.alarcos.qmutator.annealing.mathml;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.g.GRule;

public class Qumu2Math {

	private JSONObject jsoRule;

	public Qumu2Math(JSONObject jsoRule) {
		this.jsoRule = jsoRule;
	}

	public String buildRule() {
		GRule rule = new GH().newRule(this.jsoRule, false);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<math xmlns='http://www.w3.org/1998/Math/MathML' display='block'>");
		sb.append(rule.toML());
		sb.append("</math>");
		return sb.toString();
	}

}
