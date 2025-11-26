package edu.uclm.alarcos.qmutator.http;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.dwave.DWaver;

@RestController
@RequestMapping("dw")
public class DwaveController {
	
	@PostMapping("/getCode")
	public String getCode(HttpServletResponse response, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);

		String problemId = jso.optString("problemId");
		if (problemId.length()==0)
			problemId = null;
		jso = jso.optJSONObject("concreteFunction");
		if (jso==null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ha llegado la función que quieres calcular. ¿Le has dado al botón Transform?");

		try {
			CH f = new CH(jso);
			return DWaver.getCode(f);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
