package edu.uclm.alarcos.qmutator.http;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.alarcos.qmutator.dao.CircuitRepository;
import edu.uclm.alarcos.qmutator.dao.GeneralRepository;
import edu.uclm.alarcos.qmutator.dao.MutantRepository;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.qiskit.QiskitRunner;
import edu.uclm.alarcos.qmutator.qiskit.SimpleFitnesser;

@RestController
@RequestMapping("qiskit")
public class QiskitController {
	@Autowired
	private CircuitRepository circuitRepo;
	@Autowired
	private MutantRepository mutantRepo;
	@Autowired
	private GeneralRepository generalRepo;
	
	@GetMapping(value = "/executeOriginal/{circuitId}")
	public Map<String, Object> execute(HttpSession session, @PathVariable String circuitId, @RequestParam int shots, @RequestParam(required = false) String mutantsToUse,
				@RequestParam(required = false) boolean executeWithAllInputs, @RequestParam(defaultValue = "AAA") String executionAlgorithm,
				@RequestParam(required = false) String outputQubits) throws Exception {
		session.removeAttribute("qa");
		
		Circuit original = circuitRepo.findById(circuitId).get();

		if (mutantsToUse.equals("null")) {
			//String sql = "select mutant_index from mutant where original_circuit_id='" + circuitId;
			mutantsToUse = "";
			List<Mutant> mmii = this.mutantRepo.findByOriginalCircuitId(circuitId);
			for (int i=0; i<mmii.size(); i++)
				mutantsToUse = mutantsToUse + mmii.get(i).getMutantIndex() + ",";
			mutantsToUse = mutantsToUse.substring(0, mutantsToUse.length()-1);
		}
		
		session.setAttribute("mutantsToUse", mutantsToUse.split(","));
		session.setAttribute("executeWithAllInputs", executeWithAllInputs);
		session.setAttribute("executionAlgorithm", executionAlgorithm);
		
		QiskitRunner runner = new QiskitRunner();
		session.setAttribute("runner", runner);
		StringBuilder code = new StringBuilder();
		for (String line : original.getQiskitCode())
			code.append(line + "\n");
		
		JSONObject originalResult;
		if (executeWithAllInputs) {
			mutantsToUse = mutantsToUse.replace("-1,", "");
			session.setAttribute("mutantsToUse", mutantsToUse.split(","));
			String sql = "select mutant_index from mutant where original_circuit_id='" + circuitId + "' and (";
			String[] tokens = mutantsToUse.split(",");
			for (int i=0; i<tokens.length; i++) {
				sql = sql + "col_index=" + tokens[i];
				if (i<tokens.length-1)
					sql = sql + " or ";
			}
			sql = sql + ") order by mutant_index";
			
			int qubits = original.getQubits();
			FileOutputStream fos = new FileOutputStream(runner.getWorkingFolder() + "wholeOriginalResults.txt");
			fos.write("{ \"version\" : \"original\",\n".getBytes());
			fos.write("\"executeWithAllInputs\" : \"true\",\n".getBytes());
			fos.write("\"mutants\" : \n".getBytes());
			JSONArray mutantIndexes = new JSONArray(this.generalRepo.getIntegerList(sql)); 
			fos.write(mutantIndexes.toString().getBytes());
			fos.write(",\n".getBytes());
			fos.write("\"results\" : [".getBytes());
			int iterations = (int) Math.pow(2, qubits);
			for (int i=0; i<iterations; i++) {
				String[] input = buildInput(i, qubits);
				int start = code.indexOf("#INITIALIZATION#");
				int end = code.indexOf("#CODE#");
				code = code.replace(start, end, "#INITIALIZATION#\n#" + input[1] + "\n" +  input[0] + "\n");
				String fileName = "original_" + i + ".py";
				String sCode = code.toString();
				JSONObject executionResult = runner.execute(fileName, sCode, shots);
				executionResult.put("input", input[1]);
				fos.write(executionResult.toString().getBytes());
				if (i<iterations-1)
					fos.write(",\n".getBytes());
			}
			fos.write("]}".getBytes());
			fos.close();
			FileInputStream fis = new FileInputStream(runner.getWorkingFolder() + "wholeOriginalResults.txt");
			byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			originalResult = new JSONObject(new String(b));
			session.setAttribute("originalResult", originalResult);
		} else {
			originalResult = runner.execute("original.py", code.toString(), shots);
			originalResult.put("version", "original");
			originalResult.put("executeWithAllInputs", executeWithAllInputs);
		}
		original.setQiskitResult(originalResult.toString());
		this.circuitRepo.save(original);
		
		return originalResult.toMap();
	}
	
	private String[] buildInput(int index, int qubits) {
		String input = Integer.toBinaryString(index);
		int requiredBits = qubits-input.length();
		for (int i=0; i<requiredBits; i++)
			input = "0" + input;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			if (c=='1')
				sb.append("circuit.x(qreg[" + i + "])\n");
		}
		String[] result = new String[2];
		result[0] = sb.toString();
		result[1] = input;		
		
		return result;
	}

	@GetMapping(value = "/executeMutant/{circuitId}")
	public Map<String, Object> executeMutant(HttpSession session, @PathVariable String circuitId,
			@RequestParam int mutantIndex, @RequestParam int shots, @RequestParam double pValue) throws Exception {
		
		boolean executeWithAllInputs = true;
		
		if (session.getAttribute("executeWithAllInputs")!=null)
			executeWithAllInputs = (boolean) session.getAttribute("executeWithAllInputs");
		
		Mutant mutant = this.mutantRepo.getMutant(circuitId, mutantIndex);
		String[] mutantsToUse;
		
		boolean found = false;
		if (session.getAttribute("mutantsToUse")!=null) {
			mutantsToUse = (String[]) session.getAttribute("mutantsToUse");
	
			for (int i=0; i<mutantsToUse.length; i++) {
				if (Integer.parseInt(mutantsToUse[i])==mutant.getColIndex()) {
					found = true;
					break;
				}
			}
		}
		
		QiskitRunner runner = (QiskitRunner) session.getAttribute("runner");
		if (runner==null)
			runner = new QiskitRunner();
		session.setAttribute("runner", runner);
		JSONObject jsoOriginalResult = (JSONObject) session.getAttribute("originalResult");
		
		if (executeWithAllInputs) {
			if (!found)
				return new JSONObject().put("executeWithAllInputs", executeWithAllInputs).toMap();
			String executionAlgorithm = session.getAttribute("executionAlgorithm").toString();
			Circuit original = circuitRepo.findById(circuitId).get();
			int qubits = original.getQubits();
			int iterations = (int) Math.pow(2, qubits);
			JSONObject mutantResult = new JSONObject();
			mutantResult.put("version", "m" + mutantIndex);
			mutantResult.put("executeWithAllInputs", executeWithAllInputs);
			JSONArray mutantResults = new JSONArray();
			boolean killed = false;
			for (int i=0; i<iterations; i++) {
				String[] input = buildInput(i, qubits);
				String fileName = "m" + mutantIndex + "_" + i + ".py";
				StringBuilder code = new StringBuilder(mutant.getRawQiskitCode());
				
				int start = code.indexOf("#INITIALIZATION#");
				int end = code.indexOf("#CODE#");
				code = code.replace(start, end, "#INITIALIZATION#\n#" + input[1] + "\n" +  input[0] + "\n");
				JSONObject mutantIterationResult = runner.execute(fileName, code.toString(), shots);
				
				if (mutantIterationResult.getInt("returnCode")==0) {
					long timeIni = System.currentTimeMillis();
					mutantIterationResult.put("input", input[1]);
					double error = SimpleFitnesser.calculate(jsoOriginalResult, mutantIterationResult);
					double mutantValue = error;
					mutantValue = Math.round(mutantValue * 1000.0) / 1000.0;
					mutantIterationResult.put("pValue", String.format("%.3f", mutantValue));
					killed = error>pValue;
					mutantIterationResult.put("alive", !killed);					
					mutantIterationResult.put("pValueCalculusTime", System.currentTimeMillis()-timeIni);
					mutantResults.put(mutantIterationResult);
					if (killed && executionAlgorithm.equalsIgnoreCase("OA"))
						break;
				} else {
					mutantResults.put(mutantIterationResult);
				}
			}
			mutantResult.put("results", mutantResults);
			return mutantResult.toMap();
		} else {	
			if (!found)
				return null;
			
			String fileName = "m" + mutantIndex + ".py";
			
			JSONObject mutantResult = runner.execute(fileName, mutant.getRawQiskitCode(), shots);
			mutant.setQiskitResult(mutantResult.toString());
			this.mutantRepo.save(mutant);
			
			mutantResult.put("version", "m" + mutantIndex);
			
			double error = SimpleFitnesser.calculate(jsoOriginalResult, mutantResult);
			
			double mutantValue = error;
			mutantValue = Math.round(mutantValue * 1000.0) / 1000.0;
			mutantResult.put("pValue", String.format("%.3f", mutantValue));
			mutantResult.put("alive", error<=pValue);
		
			mutant.setKilled(!mutantResult.getBoolean("alive"));
			this.mutantRepo.save(mutant);
			mutantResult.put("operator", mutant.getOperatorName());
			mutantResult.put("column", mutant.getColIndex());
			mutantResult.put("row", mutant.getRowIndex());
			mutantResult.put("executeWithAllInputs", executeWithAllInputs);
			return mutantResult.toMap();
		}
	}
	
	@GetMapping(value = "/analyzeMutant/{circuitId}")
	public Map<String, Object> analyzeMutant(HttpSession session, @PathVariable String circuitId, @RequestParam int mutantIndex, @RequestParam double pValue) throws Exception {
		JSONObject originalResult = this.circuitRepo.findById(circuitId).get().getQiskitResult();
		Mutant mutant = this.mutantRepo.getMutant(circuitId, mutantIndex);
		JSONObject mutantResult;
		mutantResult = mutant.getQiskitResult();
		
		JSONObject jsoPValue = new JSONObject(SimpleFitnesser.calculate(originalResult, mutantResult));
		Map<String, Object> result = new HashMap<>();
		if (Double.isNaN(jsoPValue.optDouble("data"))) {
			result.put("pValue", "&infin;");
			result.put("alive", true);
		} else {
			result.put("pValue", jsoPValue.getDouble("data"));
			result.put("alive", jsoPValue.getDouble("data")>pValue);
		}
		result.put("version", mutant.getMutantIndex());
		return result;
	}
	
	@GetMapping(value = "/closeExecution")
	public void closeExecution(HttpSession session) {
		session.removeAttribute("runner");
	}
	
	@GetMapping(value = "/getCode/{circuitId}")
	public String[] getCode(@PathVariable String circuitId) throws Exception {
		Circuit original = circuitRepo.findById(circuitId).get();
		return original.getQiskitCode();
	}
}
