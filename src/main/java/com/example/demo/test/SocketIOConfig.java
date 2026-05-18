package com.example.demo.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

@Configuration
public class SocketIOConfig {

    @Value("${socket.host:localhost}")
    private String socketHost;

    @Value("${socket.port:8081}")
    private int socketPort;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort); // Chạy riêng trên Port cấu hình
        
        // Cấu hình CORS để React gọi sang không bị lỗi
        config.setOrigin("*"); 
        
        return new SocketIOServer(config);
    }
}