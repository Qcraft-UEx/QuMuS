package edu.uclm.alarcos.qmutator.websockets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.mut.AnnTester;
import edu.uclm.alarcos.qmutator.model.Manager;

@Component
public class WebSocketAnnealingExec extends TextWebSocketHandler {	
	private Map<String, AnnTester> testers = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		session.setTextMessageSizeLimit(1000*1024*1024);
		this.testers.put(session.getId(), new AnnTester());
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		JSONObject jso = new JSONObject(payload);
		if (jso.getString("type").equals("EXECUTE")) {
			executeMutant(session, jso);
			return;
		}
	}

	private void executeMutant(WebSocketSession session, JSONObject jso) throws Exception, IOException {
		AnnTester tester = this.testers.get(session.getId());
		int pos = jso.getInt("pos");
		String problemId = jso.getString("problemId");
		if (pos==0) {
			Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
			tester.setProblem(problem);
		}
		String mutantId = jso.getString("mutantId");
		List<Combination> combinations = tester.executeMutant(problemId, mutantId);
		JSONArray jsaCombinations = new JSONArray();
		JSONArray jsaEnergies = new JSONArray();
		for (Combination combination : combinations) {
			jsaCombinations.put(combination.getHtml());
			jsaEnergies.put(combination.getValue());
		}
		JSONObject jsoExecuted = new JSONObject().
				put("type", "EXECUTED").
				put("mutantId", mutantId).
				put("combinations", jsaCombinations).
				put("energies", jsaEnergies);
		session.sendMessage(new TextMessage(jsoExecuted.toString()));
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		this.testers.remove(session.getId());
	}
}
