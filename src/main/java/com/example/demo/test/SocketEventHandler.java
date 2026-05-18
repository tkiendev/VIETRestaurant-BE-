package com.example.demo.test;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocketEventHandler {

    private final SocketIOServer server;

    @Autowired
    public SocketEventHandler(SocketIOServer server) {
        this.server = server;

        // 1. Lắng nghe Client kết nối
        server.addConnectListener(client -> {
            System.out.println("🟢 Client kết nối: " + client.getSessionId());
        });

        // 2. Lắng nghe Client ngắt kết nối
        server.addDisconnectListener(client -> {
            System.out.println("🔴 Client ngắt kết nối: " + client.getSessionId());
        });

        // 3. Lắng nghe sự kiện "send_message" từ React
        server.addEventListener("send_message", String.class, (client, data, ackSender) -> {
            System.out.println("Nhận tin nhắn: " + data);
            
            // 4. Phát lại (Broadcast) dữ liệu đó cho tất cả Client bằng sự kiện "receive_message"
            server.getBroadcastOperations().sendEvent("receive_message", data);
        });
    }
}
