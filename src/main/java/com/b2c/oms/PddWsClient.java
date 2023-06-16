package com.b2c.oms;

import com.b2c.oms.controller.pdd.PddSocketController;
import com.pdd.pop.sdk.message.MessageHandler;
import com.pdd.pop.sdk.message.WsClient;
import com.pdd.pop.sdk.message.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拼多多消息单例
 */
public class PddWsClient {
    //创建 SingleObject 的一个对象
    private static PddWsClient instance = new PddWsClient();
    private WsClient wsClient = null;
    private static Logger log = LoggerFactory.getLogger(PddWsClient.class);

    public WsClient getWsClient() {
        return wsClient;
    }
    //获取唯一可用的对象
    public static PddWsClient getInstance() {
        return instance;
    }
    public void setWsClient(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    private PddWsClient(){
        try {
            wsClient = new WsClient(
                    "wss://message-api.pinduoduo.com", DataConfigObject.getInstance().getPddClientId(),
                    DataConfigObject.getInstance().getPddClientSecret(), new MessageHandler() {
                @Override
                public void onMessage(Message message) throws InterruptedException {
                    log.info("接收拼多多消息中");
                }
            });

            wsClient.connect();
        }catch (Exception e){
            throw e;
        }
    }
}
