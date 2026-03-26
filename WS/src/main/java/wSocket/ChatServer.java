package wSocket;

import com.google.gson.Gson;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        // guarda el nombre de usuario en las propiedades de la sesión
        session.getUserProperties().put("usuario", username);
        sessions.put(username, session);
        MensajeDTO mensajeAviso = new MensajeDTO("servidor", "SYSTEM", username + " se ha unido al servidor.");

        broadcast(gson.toJson(mensajeAviso), username);
        enviarListaUsuarios();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        MensajeDTO mensaje = gson.fromJson(message, MensajeDTO.class);

        if ("PUBLIC".equals(mensaje.getTipo())) {
            broadcast(message, (String) session.getUserProperties().get("usuario"));

        } else if ("PRIVATE".equals(mensaje.getTipo())) {

            String to = mensaje.getDestinatario();
            enviarMensajeExclusivo(message, to);
            session.getAsyncRemote();
        }
    }

    @OnClose
    public void onClose(Session session) {
        // obtener el nombre de usuario de la sesión que se desconectó
        String usuarioDesconectado = (String) session.getUserProperties().get("usuario");

        if (usuarioDesconectado != null) {
            sessions.remove(usuarioDesconectado); // elimina por la llave
            MensajeDTO mensajeAviso = new MensajeDTO("servidor", "SYSTEM", usuarioDesconectado + " ha abandonado el servidor.");

            broadcast(gson.toJson(mensajeAviso), usuarioDesconectado);
        }
        enviarListaUsuarios();
    }

    private void broadcast(String msj, String user) {
        // envía a todos los clientes menos al que envía el mensaje
        for (Session s : sessions.values()) {
            String sessionUser = (String) s.getUserProperties().get("usuario");
            if (sessionUser != null && !sessionUser.equals(user) && s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(msj);
                } catch (IOException e) {
                    System.out.println("Error enviando broadcast a " + sessionUser);
                }
            }
        }
    }

    private void enviarListaUsuarios() {
        List<String> lista = new ArrayList<>(sessions.keySet());
        MensajeDTO dto = new MensajeDTO("USERS", lista);
        String json = gson.toJson(dto);

        for (Session s : sessions.values()) {
            s.getAsyncRemote().sendText(json);
        }
    }

    private void enviarMensajeExclusivo(String msg, String to) {
        Session s = sessions.get(to);
        if (s != null && s.isOpen()) {
            try {
            s.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            System.out.println("Error enviando mensaje exclusivo a " + to);
        }
        }
    }
}
