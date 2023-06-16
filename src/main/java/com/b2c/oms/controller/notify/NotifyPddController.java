//package com.b2c.oms.controller.notify;
//
//import com.alibaba.fastjson.JSON;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import com.b2c.service.erp.ErpSalesOrderService;
//import com.pdd.pop.sdk.message.MessageHandler;
//import com.pdd.pop.sdk.message.WsClient;
//import com.pdd.pop.sdk.message.model.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
///**
// * Pdd通知处理Controller
// */
//@RequestMapping("/notify")
//@RestController
//public class NotifyPddController {
//    @Autowired
//    private ErpSalesOrderService salesOrderService;
//
//    private static Logger log = LoggerFactory.getLogger(NotifyPddController.class);
//
//    /**
//     * pdd消息通知处理
//     * @param req
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping("/pdd_new_order")
//    public ApiResult<Integer> newOrderNotify(HttpServletRequest req) throws IOException {
//        log.info("开启拼多多新订单通知：");
//        String clientId = "dc953bcf16d24b27abf3e64a59e1ecd1";
//        String clientSecret = "de296599e194a08cbfbb2b3b340e11fec7a1bacc";
//        Integer shopId = 5;//拼多多店铺id
//        try {
//            WsClient ws = new WsClient("wss://message-api.pinduoduo.com", clientId, clientSecret, new MessageHandler() {
//                public void onMessage(Message message) throws InterruptedException {
//                    log.info("收到拼多多新订单通知："+JSON.toJSONString(message));
//                    salesOrderService.addNotifyMsg(JSON.toJSONString(message),shopId);
///*                    String tranSType=message.getType();
//                    if(tranSType.equals(PddTransType.TradeConfirm)){
//                        JSON json = JSON.parseObject(message.getContent());
//
//                    }*/
//                }
//            });
//            ws.connect();
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            log.info("拼多多开放平台消息订单异常" + e.getMessage());
//        }
//            return new ApiResult<>(ApiResultEnum.SUCCESS, "OK");
//        }
//}
