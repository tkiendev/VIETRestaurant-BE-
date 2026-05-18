// package com.example.demo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.socket.config.annotation.EnableWebSocket;
// import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
// import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
// import org.springframework.web.socket.handler.TextWebSocketHandler;
// import com.example.demo.websocket.RestaurantWebSocketHandler;

// @Configuration
// @EnableWebSocket
// public class WebSocketConfig implements WebSocketConfigurer {

//     @Override
//     public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//         registry.addHandler(restaurantWebSocketHandler(), "/ws/restaurant")
//                 .setAllowedOrigins("*");
//     }

//     @Bean
//     public TextWebSocketHandler restaurantWebSocketHandler() {
//         return new RestaurantWebSocketHandler();
//     }
// }
