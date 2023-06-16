//package com.b2c.oms.controller.notify;
//
//import com.alibaba.fastjson.JSONObject;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URLDecoder;
//
///**
// * 阿里通知处理Controller
// */
//@RequestMapping("/notify")
//@RestController
//public class NotifyAlibabController {
//    private static Logger log = LoggerFactory.getLogger(NotifyAlibabController.class);
//
//    @RequestMapping("/ali_new_order")
//    public ApiResult<Integer> newOrderNotify(HttpServletRequest request) throws IOException {
//        /**
//         *
//         {
//         "bizKey": "167539019420540000",
//         "data": {
//         "buyerMemberId": "b2b-665170100",
//         "orderId": 167539019420540000,
//         "currentStatus": "waitsellersend",
//         "sellerMemberId": "b2b-1676547900b7bb3",
//         "msgSendTime": "2018-05-30 19: 34: 27"
//         },
//         "gmtBorn": 1586227869437,
//         "msgId": 7565094247,
//         "type": "ORDER_BUYER_VIEW_ORDER_PAY",
//         "userInfo": "b2b-1676547900b7bb3"
//         }
//         *
//         */
//        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
//        StringBuilder responseStrBuilder = new StringBuilder();
//        String inputStr;
//        while ((inputStr = streamReader.readLine()) != null)
//            responseStrBuilder.append(inputStr);
//        String msg = URLDecoder.decode(responseStrBuilder.toString().replace("message=",""));
//        msg = msg.substring(0,msg.indexOf("&"));
////        JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
//        log.info("获取到1688新付款订单信息："+msg);
//        try {
//            AliMessageVo<AliNewOrderVo> alim = JSONObject.parseObject(msg, AliMessageVo.class);
//            alim.setDataObj(JSONObject.parseObject(alim.getData(),AliNewOrderVo.class));
//            log.info("转换1688订单信息,订单id："+alim.getDataObj().getOrderId());
//        }catch (Exception e){
//            log.info("转换1688订单信息出错："+e.getMessage());
//        }
//        return new ApiResult<>(ApiResultEnum.SUCCESS, "OK");
//    }
//}
