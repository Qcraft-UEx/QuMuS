package edu.uclm.alarcos.qmutator.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketsConfigurer implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.
			addHandler(new WebSocketAnnealing(), "/wsAnnealing").
			setAllowedOrigins("*").
			addInterceptors(new HttpSessionHandshakeInterceptor()).
			
			addHandler(new WebSocketFiles(), "/wsFiles").
			setAllowedOrigins("*").
			addInterceptors(new HttpSessionHandshakeInterceptor()).
			
			addHandler(new WebSocketAnnealingExec(), "/wsAnnealingExec").
			setAllowedOrigins("*").
			addInterceptors(new HttpSessionHandshakeInterceptor());
	}
}
