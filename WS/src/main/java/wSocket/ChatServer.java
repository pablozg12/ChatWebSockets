/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author pablo
 */
@ServerEndpoint("/chat/{username}")
public class ChatServer {

    private static Map<String, Session> sessions = new ConcurrentHashMap<>();
    private Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        broadcast();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JsonObject json = gson.fromJson(message, JsonObject.class);
        String type = json.get("type").getAsString();

        if ("PUBLIC".equals(type)) {
            enviarMensaje(message);
        } else if ("PRIVATE".equals(type)) {
            String to = json.get("to").getAsString();
            enviarMensajeExclusivo(message, to);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.values().remove(session);
        broadcast();
    }

    private void broadcast() {
        JsonObject listMsg = new JsonObject();
        listMsg.addProperty("type", "USER_LIST");
        listMsg.add("users", gson.toJsonTree(sessions.keySet()));
        enviarMensaje(listMsg.toString());
    }

    private void enviarMensaje(String msg) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText(msg));
    }

    private void enviarMensajeExclusivo(String msg, String to) {
        Session s = sessions.get(to);
        if (s != null && s.isOpen()) {
            s.getAsyncRemote().sendText(msg);
        }
    }
}
