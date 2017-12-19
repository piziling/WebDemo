package com.shilec.manager;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/WebSocket")
public class WebSocketDemo {
	//private static CopyOnWriteArraySet<WebSocketDemo> webSocketSet = new CopyOnWriteArraySet<WebSocketDemo>();
	@OnOpen
	public void onOpenSocket() {
		System.out.println("WebSocket opened!");
		//webSocketSet.add(this);
	}
	
	@OnClose
	public void onSocketClose() {
		System.out.println("WebSocket closed1");
	}
	
	@OnMessage
	public void onMessage(String msg,Session session) {
		System.out.println("WebSocket msg:" + msg);
		try {
			session.getBasicRemote().sendText("服务器收到:" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

 class MsgHandler implements MessageHandler {
	
}
