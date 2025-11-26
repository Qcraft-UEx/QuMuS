package edu.uclm.alarcos.qmutator.http;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.mut.AnnOperator;
import edu.uclm.alarcos.qmutator.annealing.mut.AnnTester;
import edu.uclm.alarcos.qmutator.annealing.mut.MutantGenerator;
import edu.uclm.alarcos.qmutator.dao.IDescriptionEnergy;
import edu.uclm.alarcos.qmutator.model.Manager;

@RestController
@RequestMapping("annm")
public class AnnealingMutation {
	
	@GetMapping("/getOperators")
	public List<AnnOperator> getOperators(HttpServletRequest request) throws Exception {
		return MutantGenerator.loadOperators();
	}
	
	@GetMapping("/getOperator/{mutantId}")
	public String getOperator(@PathVariable String mutantId) throws Exception {
		return Manager.get().getAnnMutantRepo().findOperatorByMutantId(mutantId);
	}
	
	@GetMapping("/getMutants/{problemId}")
	public List<String> getMutants(@PathVariable String problemId) throws Exception {
		return Manager.get().getAnnMutantRepo().findIdByProblemId(problemId);
	}
	
	@PostMapping("/generateMutants")
	public List<String> generateMutants(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		String problemId = jso.optString("problemId");
		if (problemId.length()==0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para generar mutantes es necesario guardar el problema");
		
		Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
		JSONObject jsoCF = new JSONObject(problem.getConcreteFunction());

		JSONArray jsaOperators = jso.getJSONArray("operators");
		try {
			CH f = new CH(jsoCF);
			MutantGenerator mg = new MutantGenerator();
			return mg.generateMutants(f, problemId, jsaOperators);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
		
	@PostMapping("/test")
	public Map<String, Double[]> test(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		JSONObject jsoCombination = jso.optJSONObject("combination");
		String problemId = jso.getString("problemId");
		
		try {
			Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
			Combination comb = new Combination(jsoCombination.toString());
			AnnTester annTester = new AnnTester(problem, comb);
			return annTester.test();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/seeOperatorDetails")
	public List<IDescriptionEnergy> seeOperatorDetails(@RequestParam String problemId, @RequestParam String operator) {
		try {
			return Manager.get().getAnnMutantRepo().findAlive(problemId, operator);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
