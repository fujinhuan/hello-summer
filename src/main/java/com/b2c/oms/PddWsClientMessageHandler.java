//package com.b2c.oms;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.b2c.common.jdbc.DbUtil;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.pdd.RefundPddEntity;
//import com.b2c.service.oms.OrderPddService;
//import com.pdd.pop.sdk.message.MessageHandler;
//import com.pdd.pop.sdk.message.model.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//public class PddWsClientMessageHandler implements MessageHandler {
//
//    private static Logger log = LoggerFactory.getLogger(PddWsClientMessageHandler.class);
//
//    @Override
//    public void onMessage(Message message) {
//        try {
//
//            log.info("拼多多新消息-------------------------------" );
//            log.info(JSON.toJSONString(message));
//            if (message.getType().equals("pdd_refund_RefundCreated")) {
//                //新退货订单,插入退货表
//                log.info("接收到拼多多新退款，插入退货表：" );
//                Integer shopId = 0;
//                if(message.getMallID().longValue() == 171504183L){
//                    shopId = 18;
//                }
//
//                JSONObject jsonObject = JSON.parseObject(message.getContent());
//                Long refundId = jsonObject.getLong("refund_id");
//                Integer billType = jsonObject.getInteger("bill_type");
//                Long refundFee = jsonObject.getLong("refund_fee");
//                Integer operation=jsonObject.getInteger("operation");
//                String orderSn = jsonObject.getString("tid");
//                Long modified = jsonObject.getLong("modified");
//
//                RefundPddEntity refundPdd = new RefundPddEntity();
//                refundPdd.setId(refundId);
//                refundPdd.setShopId(shopId);
//                refundPdd.setAfter_sales_type(billType+1);
//                refundPdd.setAfter_sales_status(2);
//                refundPdd.setAfter_sale_reason(operation+"");
//                refundPdd.setAuditStatus(-9);//带拉取状态
//                refundPdd.setConfirm_time(0L);
//                refundPdd.setCreated_time(modified);
//                refundPdd.setDescribe("");
//                refundPdd.setDiscount_amount(0.0);
//                refundPdd.setGoods_id(0);
//                refundPdd.setGoods_image("");
//                refundPdd.setGoods_name("");
//                refundPdd.setGoods_number("");
//                refundPdd.setGoods_price(0.0);
//                refundPdd.setOrder_amount(0.0);
//                refundPdd.setOrder_sn(orderSn);
//                refundPdd.setQuantity(0);
//                refundPdd.setRefund_amount(refundFee.doubleValue() / 100);
//                refundPdd.setShippingStatus(0);
//                refundPdd.setSkuId(0);
//                refundPdd.setSkuInfo("");
//                refundPdd.setSkuNumber("");
//                refundPdd.setTracking_company("");
//                refundPdd.setTracking_number("");
//                refundPdd.setUpdated_time(DateUtil.getCurrentDateTime());
//                var result = orderPddService.editRefundPddOrder(refundPdd);
//                log.info("开始处理退货单消息：{refundId:"+refundId+",status:"+operation+"}，处理结果："+result.getMsg());
//
//            }else if (message.getType().equals("pdd_trade_TradeConfirmed")){
//                log.info("接收到拼多多新订单，插入订单表：" );
//                Integer shopId = 0;
//                if(message.getMallID().longValue() == 171504183L){
//                    shopId = 18;
//                }
//                //{"content":"{\"mall_id\":171504183,\"tid\":\"210822-038440825871540\"}","mallID":171504183,"type":"pdd_trade_TradeConfirmed"}
//                JSONObject jsonObject = JSON.parseObject(message.getContent());
//                String tid = jsonObject.getString("tid");
//                log.info("组合数据{shopId:"+shopId+",tid:"+tid+"}");
//                // 开始写入数据库
//                var result = orderPddService.insertOrderForMessage( shopId,tid);
//                log.info("开始处理新订单消息：{tid:"+tid+",id:"+result.getData()+"}，处理结果："+result.getMsg());
//            }else if (message.getType().equals("pdd_refund_RefundBuyerReturnGoods")) {
//                log.info("----------------------------------------------------------");
//                log.info("接收到拼多多退货订单买家发货消息"+JSON.toJSONString(message));
//            }
//        }catch (Exception e){
//            log.info("拼多多消息接收异常："+e.getLocalizedMessage());
//        }
//
//    }
//}
