package edu.uclm.alarcos.qmutator.websockets;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ProblemConstraint;
import edu.uclm.alarcos.qmutator.model.Manager;

@Component
public class WebSocketFiles extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		session.setTextMessageSizeLimit(1000*1024*1024);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		buildProblem(payload);
	}

	private void buildProblem(String payload) {
		String[] lines = payload.split("\n");
		
		String name = lines[0];
		String min = lines[2];

		Problem problem = new Problem();
		problem.setName(name);
		if (!min.startsWith("//"))
			problem.setMinFunction(min);

		String line;
		long cont = 1;
		for (int i=4; i<lines.length; i++) {
			line = lines[i].trim();
			if (line.length()>0) {
				ProblemConstraint pc = new ProblemConstraint(new JSONObject().put("constraintText", line).put("lambda", 1));
				pc.setProblem(problem);
				pc.setPos(cont++);
				problem.add(pc);
			}
		}			

		Manager.get().getProblemRepo().save(problem);
	}
		
}
