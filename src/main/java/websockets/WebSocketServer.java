package websockets;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/socket")
public class WebSocketServer extends Endpoint {
    private Map<String, Session> sessions = new HashMap<>();

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // Store the session.
        Logger.getGlobal().warning("Socket opened by: " + session.getUserPrincipal().getName());
        sessions.put(session.getUserPrincipal().getName(), session);

        // Add the message handler.
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                Logger.getGlobal().warning(session.getUserPrincipal().getName() + ": " + s);
                session.getAsyncRemote().sendText(s);
            }
        });
    }

    @Override
    public void onError(Session session, Throwable thr) {
        Logger.getGlobal().warning("ERROR!");
        Logger.getGlobal().log(Level.SEVERE, thr.getMessage(), thr);
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        // Remove the session.
        Logger.getGlobal().warning("Socket closed by: " + session.getUserPrincipal().getName());
        sessions.remove(session.getUserPrincipal().getName());
    }
}
