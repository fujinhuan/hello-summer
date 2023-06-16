package com.b2c.oms;


import com.b2c.oms.utils.MyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocket
public class WebConfig implements WebMvcConfigurer, WebSocketConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns= new ArrayList<>();
        patterns.add("/css/**");
        patterns.add("/js/**");
        patterns.add("/layui/**");
        patterns.add("/login");
        patterns.add("/pddSocket");
        patterns.add("/pddSocketClose");
        patterns.add("/pdd/getToken");
        patterns.add("/pdd/getTokenSuccess");
        patterns.add("/web_socket_test");
        patterns.add("/imserver/**");
        patterns.add("/pdd/wait_send_goods_info");
        patterns.add("/images/**");
        patterns.add("/notify/**");
        patterns.add("/ajax_pdd/pull_order");
        patterns.add("/tao_ajax/pull_order");
        patterns.add("/ajax_pdd/pull_order_test");
        patterns.add("/douyin_notify/douyin_refund_wms_notify");
//        registry.addInterceptor(new LoginInterceptor())
//                .excludePathPatterns(patterns);

    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(),"/websocket");
    }

    @Bean
    public MyHandler myHandler() {
        return new MyHandler();
    }
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
}
