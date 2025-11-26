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

import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.mathml.Math2Qumu;
import edu.uclm.alarcos.qmutator.annealing.mathml.Qumu2Math;

@RestController
@RequestMapping("annealerMath")
public class AnnealingMath {
	
	@PostMapping("/rule2Q")
	public Map<String, Object> rule2MathML(HttpServletResponse response, @RequestBody String info) {
		String code = "";
		boolean inside = false;
		char c;
		for (int i=0; i<info.length(); i++) {
			c = info.charAt(i);
			if (c=='<')
				inside = true;
			if (c=='>')
				inside = false;
			if (inside)
				code = code + c;
			else if (!Character.isWhitespace(c))
				code = code + c;
		}
		System.out.println(code);
		
		try {
			Math2Qumu parser = new Math2Qumu(code);
			GH h = parser.buildH();
			JSONObject jso = h.getThis().getJSONArray("h").getJSONObject(0);
			return jso.toMap();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/qumu2ML")
	public String qumu2ML(HttpServletResponse response, @RequestBody String info) {
		JSONObject jso = new JSONObject(info);
		System.out.println(jso);
		Qumu2Math parser = new Qumu2Math(jso);
		String s = parser.buildRule();
		return s;
	}
}
