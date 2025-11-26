package edu.uclm.alarcos.qmutator.websockets;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;

@Component
public class WebSocketAnnealing extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		session.setTextMessageSizeLimit(1000*1024*1024);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		JSONObject jso = new JSONObject(payload);
		if (jso.getString("type").equals("CALCULATE")) {
			jso = jso.getJSONObject("concreteFunction");
			calculate(session, jso);
		}
		session.sendMessage(new TextMessage(new JSONObject().put("type", "END").toString()));
	}
	
	private void calculate(WebSocketSession session, JSONObject jso) throws IOException {
		CH f = null;
		
		try {
			f = new CH(jso);
		} catch (Exception e) {
			JSONObject jsoError = new JSONObject();
			jsoError.put("type", "ERROR").put("message", e.getMessage());
			session.sendMessage(new TextMessage(jsoError.toString()));
			return;
		}
		int nc = f.getNumberOfCombinations();		
				
		for (int i=0; i<nc; i++) {
			Combination result = f.calculate(i);
			try {
				session.sendMessage(new TextMessage(result.toJSON().toString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
