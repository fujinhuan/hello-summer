//package com.b2c.oms;
//
//
//import com.b2c.oms.utils.MyHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.*;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(myHandler(),"/websocket");
//    }
//
//    @Bean
//    public MyHandler myHandler() {
//        return new MyHandler();
//    }
//    /**
//     * 注入一个ServerEndpointExporter,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
//     */
////    @Bean
////    public ServerEndpointExporter serverEndpointExporter() {
////        return new ServerEndpointExporter();
////    }
//}
