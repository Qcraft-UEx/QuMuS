package edu.uclm.alarcos.qmutator.qiskit;

import org.json.JSONObject;

public interface IFitnesser {
	void setOriginalResult(JSONObject originalResult);
	void setMutantResult(JSONObject mutantResult);
	String calculate();
}
