package edu.uclm.alarcos.qmutator.http;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Parser;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ProblemConstraint;
import edu.uclm.alarcos.qmutator.annealing.ResultExprDetails;
import edu.uclm.alarcos.qmutator.model.Manager;

@RestController
@RequestMapping("ann")
public class AnnealingController {
	
	@PostMapping("/getMathML")
	public String getMathML(HttpServletResponse response, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);

		String problemId = jso.optString("problemId");
		if (problemId.length()==0)
			problemId = null;
		jso = jso.optJSONObject("concreteFunction");
		if (jso==null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ha llegado la función que quieres calcular. ¿Le has dado al botón Transform?");

		try {
			CH f = new CH(jso);
			return f.toML();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/calculate")
	public List<Combination> calculate(HttpServletResponse response, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		boolean testing = jso.optBoolean("testing");
		boolean keepBestSolution = jso.optBoolean("keepBestSolution");

		String problemId = jso.optString("problemId");
		if (problemId.length()==0)
			problemId = null;
		jso = jso.optJSONObject("concreteFunction");
		if (jso==null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ha llegado la función que quieres calcular. ¿Le has dado al botón Transform?");

		try {
			CH f = new CH(jso);
			long timeIni = System.currentTimeMillis();
			List<Combination> result = f.calculateAll(problemId, null, testing, keepBestSolution);
			long time = System.currentTimeMillis();
			time = (time-timeIni) / 1000;
			System.out.println("Tiempo seq.: " + time + " segundos");
			return result;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/calculateParallel")
	public List<Combination> calculateParallel(HttpServletResponse response, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		boolean testing = jso.optBoolean("testing");
		boolean keepBestSolution = jso.optBoolean("keepBestSolution");

		String problemId = jso.optString("problemId");
		if (problemId.length()==0)
			problemId = null;
		jso = jso.optJSONObject("concreteFunction");
		if (jso==null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ha llegado la función que quieres calcular. ¿Le has dado al botón Transform?");

		try {
			CH f = new CH(jso);
			long timeIni = System.currentTimeMillis();
			List<Combination> result = f.calculateParallel(problemId, null, testing, keepBestSolution);
			long time = System.currentTimeMillis();
			time = (time-timeIni) / 1000;
			System.out.println("Tiempo paralelo: " + time + " segundos");
			return result;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/seeDetails")
	public List<ResultExprDetails> seeDetails(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		JSONObject jsoCombination = jso.optJSONObject("combination");
		JSONObject jsoConcreteFunction = jso.optJSONObject("concreteFunction");
		
		try {
			CH f = new CH(jsoConcreteFunction);
			return f.getDetails(jsoCombination);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/transform")
	public Map<String, Object> transform(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		String minFunction = jso.optString("minFunction");
		JSONArray constraints = jso.getJSONArray("constraints");
		
		Parser parser = new Parser(minFunction, constraints);
		try {
			JSONObject jsoResult = parser.parse().toJSON();
			Map<String, Object> result = jsoResult.toMap();
			return result;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}		
	}
	
	@PostMapping("/saveProblem")
	public String saveProblem(@RequestBody Problem problem) {
		if (problem.getId()==null)
			problem.setId(UUID.randomUUID().toString());
		List<ProblemConstraint> constraints = problem.getConstraints();
		for (ProblemConstraint constraint : constraints)
			constraint.setProblem(problem);
		Manager.get().getProblemRepo().save(problem);
		return problem.getId();
	}
	
	@GetMapping("/getProblems")
	public List<IProblemDescription> getProblems() {
		return Manager.get().getProblemRepo().getProblems();
	}
	
	@GetMapping("/getProblem")
	public Problem getProblem(@RequestParam String problemId) {
		Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
		problem.getConstraints().sort(new Comparator<ProblemConstraint>() {

			@Override
			public int compare(ProblemConstraint o1, ProblemConstraint o2) {
				return (o1.getPos()-o2.getPos()>0 ? 1 : -1);
			}
		});
		return problem;
	}
	
	@PostMapping("/upload")
    public Map<String, Object> uploadZip(HttpSession session, @RequestParam("file") MultipartFile file) throws Exception {
		byte[] bytes = file.getBytes();
		System.out.println(bytes.length);
		return null;
	}
}
