package com.example.demo.test;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

@Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(8081); // Chạy riêng trên Port 8081
        
        // Cấu hình CORS để React gọi sang không bị lỗi
        config.setOrigin("*"); 
        
        return new SocketIOServer(config);
    }
}