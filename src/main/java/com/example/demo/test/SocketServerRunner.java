package com.example.demo.test;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy; // Thay bằng javax.annotation.PreDestroy nếu dùng Spring Boot 2.x

@Component
public class SocketServerRunner implements CommandLineRunner {

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        server.start();
        System.out.println("🚀 Socket.IO Server đã chạy trên port 8081");
    }

    @PreDestroy
    public void stop() {
        server.stop();
        System.out.println("🔴 Socket.IO Server đã dừng");
    }
}