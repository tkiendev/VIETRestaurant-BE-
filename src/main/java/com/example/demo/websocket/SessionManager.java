package com.example.demo.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SessionManager {

    private final SocketIOServer socketIOServer;

    @Autowired
    public SessionManager(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public void broadcastToRole(String role, Map<String, Object> eventPayload) {
        // Trong hệ thống này, Frontend đang listen các sự kiện trực tiếp theo tên event (VD: 'order_created').
        // Nên ta sẽ trích xuất tên sự kiện và broadcast cho mọi client. Client (React) sẽ tự nhận đúng event.
        if (eventPayload != null && eventPayload.containsKey("event")) {
            String eventName = (String) eventPayload.get("event");
            socketIOServer.getBroadcastOperations().sendEvent(eventName, eventPayload);
            System.out.println("Broadcasted event '" + eventName + "' to role: " + role);
        }
    }

    public void broadcastToAll(Map<String, Object> eventPayload) {
        if (eventPayload != null && eventPayload.containsKey("event")) {
            String eventName = (String) eventPayload.get("event");
            socketIOServer.getBroadcastOperations().sendEvent(eventName, eventPayload);
            System.out.println("Broadcasted event '" + eventName + "' to ALL clients");
        }
    }
}
