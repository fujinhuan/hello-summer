package com.b2c.oms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.io.IOException;

public class MyHandler extends TextWebSocketHandler {
    private static Logger log = LoggerFactory.getLogger(MyHandler.class);

    private static WebSocketSession session = null;
    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        this.session = session;
        String payload = message.getPayload();
        System.out.println("我接收到的消息："+payload);

        String rtnMsg = "我回复了";

        for (int i=0;i<10;i++) {
            Thread.sleep(2000);
            session.sendMessage(new TextMessage(rtnMsg+i));
        }
        super.handleTextMessage(session, message);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        log.info("连接开始websocket");
    }
    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        log.info("发送消息到，报文:"+message);
        session.sendMessage(new TextMessage(message));
//        if(!StringUtils.isEmpty(userId)&&webSocketMap.containsKey(userId)){
//            webSocketMap.get(userId).sendMessage(message);
//        }else{
//            log.error("用户"+userId+",不在线！");
//        }
    }
}
