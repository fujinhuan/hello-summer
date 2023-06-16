package com.b2c.oms.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b2c.common.utils.DateUtil;
import com.b2c.common.utils.ExpressClient;
import com.b2c.common.utils.HttpUtil;
import com.b2c.common.utils.MD5Utils;
import com.b2c.entity.douyin.DcDouyinOrdersEntity;
import com.b2c.oms.utils.MyHandler;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddFinanceBalanceDailyBillUrlGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddFinanceBalanceDailyBillUrlGetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
    private static Logger log = LoggerFactory.getLogger(HomeController.class);


    @RequestMapping("/")
    public String home(Model model) throws Exception {
        model.addAttribute("menuId", "home");
        log.info("访问首页，跳转到shop_list页面");
//        messageForTao();
        //messageForPdd();

//        return "home";
//        String clientId = "dc953bcf16d24b27abf3e64a59e1ecd1";
//        String clientSecret = "de296599e194a08cbfbb2b3b340e11fec7a1bacc";
//        String accessToken = "5f3ed4cddf99491e883bc6dfd867bed3b1ec90a4";
//
//        PopClient client = new PopHttpClient(clientId, clientSecret);
//
//        PddFinanceBalanceDailyBillUrlGetRequest request = new PddFinanceBalanceDailyBillUrlGetRequest();
//        request.setBillDate("2021-06-01");
//        PddFinanceBalanceDailyBillUrlGetResponse response = client.syncInvoke(request, accessToken);
//        System.out.println(JsonUtil.transferToJson(response));


        return "redirect:/shop/shop_list";
    }

    @RequestMapping("/web_socket_test")
    public String webSocketTest(Model model){
        return "web_socket_test";
    }








//    private void messageForTao() {
//        try {
//            log.info("开启淘宝开放平台消息长连接");
//            String appKey = "28181872";
//            String appSecret = "71815e1aa95b9aac68276aa2873bf634";
////            String sessionKey = "61003129c973ba045c8017a49c36b177d11f2af1c4e97a32206529834322";
////        String url = "http://gw.api.taobao.com/router/rest";
////        TaobaoClient client1 = new DefaultTaobaoClient(url, appKey, appSecret);
////        TmcUserPermitRequest req = new TmcUserPermitRequest();
////        req.setTopics("taobao_trade_TradeBuyerPay,taobao_trade_TradeCreate");
////        TmcUserPermitResponse rsp = client1.execute(req, sessionKey);
////        System.out.println(rsp.getBody());
//
//
///*            com.taobao.api.internal.tmc.TmcClient client = new com.taobao.api.internal.tmc.TmcClient(appKey, appSecret, "default"); // 关于default参考消息分组说明
//            client.setMessageHandler(new com.taobao.api.internal.tmc.MessageHandler() {
//                public void onMessage(com.taobao.api.internal.tmc.Message message, com.taobao.api.internal.tmc.MessageStatus status) {
//                    try {
//                        log.info("开启淘宝开放平台消息长连接");
//                        System.out.println(message.getContent());
//                        System.out.println(message.getTopic());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        status.fail(); // 消息处理失败回滚，服务端需要重发
//                        // 重试注意：不是所有的异常都需要系统重试。
//                        // 对于字段不全、主键冲突问题，导致写DB异常，不可重试，否则消息会一直重发
//                        // 对于，由于网络问题，权限问题导致的失败，可重试。
//                        // 重试时间 5分钟不等，不要滥用，否则会引起雪崩
//                    }
//                }
//            });
//            client.connect("ws://mc.api.taobao.com"); // 消息环境地址：ws://mc.api.tbsandbox.com/
//            Thread.sleep(1000);*/
//        } catch (Exception e) {
//            log.info("淘宝开放平台消息订单异常" + e.getMessage());
//        }
//    }
//
//    private void messageForPdd() {
//        try {
//            log.info("开启拼多多消息长连接");
//            String clientId = "dc953bcf16d24b27abf3e64a59e1ecd1";
//            String clientSecret = "de296599e194a08cbfbb2b3b340e11fec7a1bacc";
//           String accessToken = "70804fd3362b4985b20ab7f86ac4d55213140eaa";
//        PopClient client = new PopHttpClient(clientId, clientSecret);
////
//        PddPmcUserPermitRequest request = new PddPmcUserPermitRequest();
//////        request.setTopics("pdd_trade_TradeConfirmed");
//        request.setTopics("pdd_trade_TradeSellerShip");
//        PddPmcUserPermitResponse response = client.syncInvoke(request, accessToken);
//        System.out.println(JsonUtil.transferToJson(response));
//
//            com.pdd.pop.sdk.message.WsClient ws = new com.pdd.pop.sdk.message.WsClient(
//                    "wss://message-api.pinduoduo.com", clientId,
//                    clientSecret, new com.pdd.pop.sdk.message.MessageHandler() {
//                @Override
//                public void onMessage(com.pdd.pop.sdk.message.model.Message message) throws InterruptedException {
//                    //业务处理
//                    log.info("接收拼多多消息中");
//                    Long mallId = message.getMallID();
//                    String tid = message.getContent();
//                    log.info("接收到拼多多新订单：" + JSONObject.toJSONString(message));
//                }
//            });
//            ws.connect();
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            log.info("拼多多开放平台消息订单异常" + e.getMessage());
//        }
//
//    }

//    private void messageForAli() {
//        String url = "ws://message.1688.com/websocket";
//        //1688环境为ws://message.1688.com/websocket,
//        TunaClient client = new TunaClient("appkey", "secret", url);
//        TunaMessageHandler mhandler = new TunaMessageHandler() {
//            @Override
//            public boolean onMessage(TunaMessage message) throws Exception {
//                boolean success = true;
//                try {
//                        /*说明，服务端推送的消息分为2种，
//                        业务数据和系统消息，类型分别SERVER_PUSH和SYSTEM,
//                        其中系前者是业务消息，
//                        后者是系统消息，如appkey与secret不匹配等。
//                       */
//
//                    if (TunaMessageType.SERVER_PUSH.name().equals(message.getType())) {//如果是业务数据
//                        //此处填写业务逻辑，数据存储在message.getContent()中
//
//                    }
//                } catch (Exception e) {
//
//                }
//                return success;
//            }
//        };
//        client.setTunaMessageHandler(mhandler);
//        client.connect();
//    }


    //在应用关闭时：执行 client.shutdown();
//}


    @ResponseBody
    @RequestMapping("/test_douyin")
    public String testDouYin() throws IOException {
        String appKey = "3409030645526182640";
        String appSercet = "eb0650f0788c5dfb7ce35201c063f6b3";
        String shopId="2506514458227181";
        String method = "order.list";
        //https://openapi.jinritemai.com/order/list?app_key=your_app_key_here&method=order.list&param_json={"end_time":"2018/05/31 16:01:02","is_desc":"1","page":"0","size":"10","start_time":"2018/04/01 15:03:58"}&timestamp=2018-06-14 16:06:59&v=1&sign=your_sign_here

        String paramJson = "{\"end_time\":\"2020/04/22 16:01:02\",\"is_desc\":\"1\",\"page\":\"0\",\"size\":\"10\",\"start_time\":\"2018/04/01 15:03:58\"}";
        String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"1";
        signStr = appSercet+signStr+appSercet;

        String sign = MD5Utils.MD5Encode(signStr);

//        String url = "https://openapi.jinritemai.com/order/list?app_key="+appKey;
//        url += "&method="+method;
//        url += "&param_json="+paramJson;
//        url += "&timestamp="+timestamp;
//        url += "&v=1";
//        url += "&sign="+sign;

        String  url1= "https://openapi.jinritemai.com/order/list";

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("method", method);
        params.put("param_json", paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "1");
        params.put("sign", sign);

        String result = "";

        try {
            HttpResponse<String> response = ExpressClient.doPost(url1, HttpUtil.map2Url(params));
            if (response.statusCode() == 200) {
                JSONObject obj = JSONObject.parseObject(response.body());
                var jsonArray= obj.getJSONObject("data").getJSONArray("list");
                for(var json:jsonArray){
                    DcDouyinOrdersEntity douYinOrder= com.b2c.common.utils.JsonUtil.strToObject(JSONArray.toJSON(json).toString(),DcDouyinOrdersEntity.class);
                    System.out.println(douYinOrder.getId());
                }
              /*  DcDouyinOrdersEntity douYinOrder= com.b2c.common.utils.JsonUtil.strToObject(,DcDouyinOrdersEntity.class);
                //DcDouyinOrdersEntity douYinOrder= JsonUtil.transferToObj(obj.getJSONObject("data").getString("list"),DcDouyinOrdersEntity.class);
                System.out.println(douYinOrder.getId());*/
            }

//            HttpClient client = HttpClient.newBuilder().build();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url1)).header("Content-Type", "application/json").POST()
//                    .build();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            log.error("ExpressClient doPost exception:" + e);
        }


        return  result;
    }
//
//    @ResponseBody
//    @RequestMapping("/test_kuaishou")
//    public String testKuaiShou() throws IOException {
//        String appKey = "3409030645526182640";
//        String appSercet = "eb0650f0788c5dfb7ce35201c063f6b3";
//        String shopId="2506514458227181";
//        String method = "order.list";
//        //https://openapi.jinritemai.com/order/list?app_key=your_app_key_here&method=order.list&param_json={"end_time":"2018/05/31 16:01:02","is_desc":"1","page":"0","size":"10","start_time":"2018/04/01 15:03:58"}&timestamp=2018-06-14 16:06:59&v=1&sign=your_sign_here
//
//        String paramJson = "{\"end_time\":\"2020/04/22 16:01:02\",\"is_desc\":\"1\",\"page\":\"0\",\"size\":\"10\",\"start_time\":\"2018/04/01 15:03:58\"}";
//        String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");
//        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"1";
//        signStr = appSercet+signStr+appSercet;
//
//        String sign = MD5Utils.MD5Encode(signStr);
//
////        String url = "https://openapi.jinritemai.com/order/list?app_key="+appKey;
////        url += "&method="+method;
////        url += "&param_json="+paramJson;
////        url += "&timestamp="+timestamp;
////        url += "&v=1";
////        url += "&sign="+sign;
//
//        String  url1= "https://openapi.jinritemai.com/order/list";
//
//        Map<String, String> params = new HashMap<>();
//        params.put("app_key", appKey);
//        params.put("method", method);
//        params.put("param_json", paramJson);
//        params.put("timestamp", timestamp);
//        params.put("v", "1");
//        params.put("sign", sign);
//
//        String result = "";
//
//        try {
//            HttpResponse<String> response = ExpressClient.doPost(url1, map2Url(params));
//            if (response.statusCode() == 200) {
//                JSONObject obj = JSONObject.parseObject(response.body());
//                var jsonArray= obj.getJSONObject("data").getJSONArray("list");
//                for(var json:jsonArray){
//                    DcDouyinOrdersEntity douYinOrder= com.b2c.common.utils.JsonUtil.strToObject(JSONArray.toJSON(json).toString(),DcDouyinOrdersEntity.class);
//                    System.out.println(douYinOrder.getId());
//                }
//              /*  DcDouyinOrdersEntity douYinOrder= com.b2c.common.utils.JsonUtil.strToObject(,DcDouyinOrdersEntity.class);
//                //DcDouyinOrdersEntity douYinOrder= JsonUtil.transferToObj(obj.getJSONObject("data").getString("list"),DcDouyinOrdersEntity.class);
//                System.out.println(douYinOrder.getId());*/
//            }
//
////            HttpClient client = HttpClient.newBuilder().build();
////            HttpRequest request = HttpRequest.newBuilder()
////                    .uri(URI.create(url1)).header("Content-Type", "application/json").POST()
////                    .build();
////            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        } catch (Exception e) {
//            log.error("ExpressClient doPost exception:" + e);
//        }
//
//
//        return  result;
//    }
}
