package edu.uclm.alarcos.qmutator.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.alarcos.qmutator.dao.CircuitRepository;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Manager;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.operators.Operator;
import edu.uclm.alarcos.qmutator.services.CircuitService;
import edu.uclm.alarcos.qmutator.services.MutantService;
import edu.uclm.alarcos.qmutator.services.RandomCircuitService;

@RestController
@RequestMapping("circuit")
@SuppressWarnings("unchecked")
public class CircuitController {
	@Autowired
	private CircuitService circuitService;
	
	@Autowired
	private RandomCircuitService randomCircuitService;
	
	@Autowired
	private MutantService mutantService;
	
	@Autowired
	private CircuitRepository circuitRepo;
	
	
	@GetMapping(value = "/getCircuits")
	public List<Circuit> getCircuits() throws Exception {
		return this.circuitService.getCircuits();
	}
	
	@GetMapping(value = "/getCircuit/{circuitId}")
	public Circuit getCircuit(@PathVariable String circuitId) {
		Circuit circuit =  this.circuitService.getCircuit(circuitId);
		return circuit;
	}
	
	@GetMapping(value = "/getOperators")
	public Map<String, List<Operator>> getOperators(HttpServletRequest request) throws Exception {
		return Manager.get().getOperatorsByFamily();
	}
	
	@PostMapping("/generateMutants")
	public void generateMutants(@RequestBody Map<String, Object> info) throws Exception {
		Map<String, Object> circuit = (Map<String, Object>) info.get("circuit");
		List<String> operatorNames = (List<String>) info.get("operatorNames");
		String outputQubits = info.get("outputQubits").toString(); 
		
		this.mutantService.generateMutants(circuit, operatorNames, outputQubits);
	}
	
	@PutMapping("/insert")
	public void insert(@RequestBody Circuit qc) throws Exception {
		this.circuitService.insert(qc);
	}
	
	@GetMapping("/getMutant/{circuitId}/{mutantIndex}")
	public Mutant getMutant(@PathVariable String circuitId, @PathVariable int mutantIndex) throws Exception {
		return this.mutantService.findMutant(circuitId, mutantIndex);
	}
	
	@GetMapping("/getMutantIndexes/{circuitId}")
	public List<Integer> getMutantIndexes(@PathVariable String circuitId, @RequestParam(required=false) String mutantsToUse) throws Exception {
		if (mutantsToUse!=null && mutantsToUse.length()==0)
			mutantsToUse = null;
		return this.mutantService.getMutantIndexes(circuitId, mutantsToUse);
	}
	
	@GetMapping("/createRandomCircuit")
	public Map<String, Object> createRandomCircuit(@RequestParam int qubits, @RequestParam int cols) throws Exception {
		Circuit circuit = this.randomCircuitService.createRandomCircuit(qubits, cols);
		List<String> randoms = this.circuitRepo.selectRandoms();
		int max = 0;
		String name;
		for (int i=0; i<randoms.size(); i++) {
			name = randoms.get(i).substring(6);
			if (Integer.parseInt(name)>max)
				max = Integer.parseInt(name);
		}
		circuit.setName("random" + (max+1));
		
		String outputQubits = "";
		for (int i=0; i<qubits-1; i++)
			outputQubits = outputQubits + i + ",";
		outputQubits = outputQubits + (qubits-1);
		circuit.setOutputQubits(outputQubits);
		this.insert(circuit);
		Map<String, Object> result = new HashMap<>();
		result.put("id", circuit.getId());
		result.put("name", circuit.getName());
		result.put("circuit", circuit.toString());
		return result;
	}
}
