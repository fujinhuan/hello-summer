//package com.b2c.oms.controller.tao;
//
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.datacenter.DcTmallOrderEntity;
//import com.b2c.entity.datacenter.DcTmallOrderItemEntity;
//import com.b2c.entity.datacenter.DcTmallOrderRefundEntity;
//import com.b2c.enums.third.EnumTmallOrderStatus;
//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.RefundsReceiveGetRequest;
//import com.taobao.api.request.TradesSoldGetRequest;
//import com.taobao.api.request.TradesSoldIncrementGetRequest;
//import com.taobao.api.response.RefundsReceiveGetResponse;
//import com.taobao.api.response.TradesSoldGetResponse;
//import com.taobao.api.response.TradesSoldIncrementGetResponse;
//import org.springframework.util.StringUtils;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class TaoBaoOpenOrderUpdHelper {
//    /**
//     * 更新订单（循环分页）
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param sessionKey
//     * @return
//     */
//    public static TaoBaoOpenOrderUpdResult<DcTmallOrderEntity> updTmallOrder(Long pageNo, Long pageSize,String url,String appKey,String appSecret, String sessionKey) throws ApiException {
//        TaobaoClient client = new DefaultTaobaoClient(url, appKey, appSecret);
//
//        TradesSoldGetRequest req = new TradesSoldGetRequest();
//        req.setFields("tid,post_fee,receiver_name,receiver_state,receiver_city,receiver_district,receiver_address,receiver_mobile,receiver_phone,received_payment,num," +
//                "type,status,payment,orders,rx_audit_status,sellerMemo,pay_time,created,buyer_nick");
////        req.setStartCreated(DateUtil.stringtoDate("2019-11-27 00:00:00"));
////        req.setEndCreated(DateUtil.stringtoDate("2019-12-04 23:59:59"));
////        req.setStatus("WAIT_SELLER_SEND_GOODS");
////        req.setBuyerNick("美丽的你美丽的我");
//        req.setType("fixed");//一口价
////        req.setExtType("service");
////        req.setRateStatus("RATE_UNBUYER");
////        req.setTag("time_card");
//        req.setPageNo(pageNo);
//        req.setPageSize(pageSize);
////        req.setUseHasNext(true);
////        req.setBuyerOpenId("AAHm5d-EAAeGwJedwSHpg8bT");
//        TradesSoldGetResponse rsp = client.execute(req, sessionKey);
////        System.out.println(rsp.getBody());
//
//        if (rsp.getTrades() == null) {
//            //接口查询错误
//            return new TaoBaoOpenOrderUpdResult(500, "接口调用错误：" + rsp.getMsg() + rsp.getSubMsg());
//        }
//
//        //组合的订单列表
//        List<DcTmallOrderEntity> orderList = new ArrayList<>();
//
//        //有数据
//        for (var trade : rsp.getTrades()) {
//            try {
////                if(trade.getTid() == 770674433029365349L)
////                {
////                    String id = trade.getTid().toString();
////                }
//                DcTmallOrderEntity order = new DcTmallOrderEntity();
//                order.setId(trade.getTid().toString());
//                order.setCreateTime(trade.getCreated());
//                order.setModifyTime(trade.getModified());
//                order.setPayTime(trade.getPayTime());
//                order.setTotalAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//                order.setShippingFee(BigDecimal.valueOf(Double.parseDouble(trade.getPostFee())));
//                order.setPayAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//                order.setBuyerName(trade.getBuyerNick());
//                order.setSellerMemo(trade.getSellerMemo());
//                order.setProvince(trade.getReceiverState());
//                order.setCity(trade.getReceiverCity());
//                order.setArea(trade.getReceiverDistrict());
//                order.setStatus(EnumTmallOrderStatus.getStatus(trade.getStatus()));
//                order.setStatusStr(trade.getStatus());
//                List<DcTmallOrderItemEntity> items = new ArrayList<>();
//                for (var item : trade.getOrders()) {
//                    DcTmallOrderItemEntity orderItem = new DcTmallOrderItemEntity();
//                    orderItem.setSpecNumber(item.getOuterSkuId());
//                    orderItem.setGoodsNumber(item.getOuterIid());
//                    orderItem.setProductImgUrl(item.getPicPath());
//                    orderItem.setGoodsTitle(item.getTitle());
//                    orderItem.setPrice(BigDecimal.valueOf(Double.parseDouble(item.getPrice())));
//                    orderItem.setQuantity(item.getNum().doubleValue());
//                    orderItem.setSubItemId(item.getOid().toString());
//                    orderItem.setSkuInfo(item.getSkuPropertiesName());
//                    orderItem.setItemAmount(BigDecimal.valueOf(Double.parseDouble(item.getPayment())));
//                    orderItem.setDiscountFee(new BigDecimal(item.getDiscountFee()));
//                    orderItem.setAdjustFee(new BigDecimal(item.getAdjustFee()));
//
//                    orderItem.setRefundStatusStr(item.getRefundStatus());
//                    items.add(orderItem);
//                }
//                order.setItems(items);
//
//                orderList.add(order);
//            } catch (Exception e) {
//            }
//        }
//
//        return new TaoBaoOpenOrderUpdResult(rsp.getTotalResults().intValue(), orderList);
//    }
//
//    /**
//     * 增量获取淘宝开放平台天猫订单
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param sessionKey
//     * @return
//     * @throws ApiException
//     */
//    public static TaoBaoOpenOrderUpdResult<DcTmallOrderEntity> getIncrementTmallOrder(Long pageNo, Long pageSize,Date startTime, Date endTime,String url,String appKey,String appSecret, String sessionKey) throws ApiException {
//
//
//        TaobaoClient client = new DefaultTaobaoClient(url, appKey, appSecret);
//
//        TradesSoldIncrementGetRequest req = new TradesSoldIncrementGetRequest();
//        req.setFields("tid,post_fee,receiver_name,receiver_state,receiver_city,receiver_district,receiver_address,receiver_mobile,receiver_phone,received_payment,num," +
//                "type,status,payment,orders,rx_audit_status,sellerMemo,pay_time,created,buyer_nick");
//        req.setStartModified(startTime);
//        req.setEndModified(endTime);
//
//        req.setType("fixed");//一口价
////        req.setExtType("service");
////        req.setRateStatus("RATE_UNBUYER");
////        req.setTag("time_card");
//        req.setPageNo(pageNo);
//        req.setPageSize(pageSize);
////        req.setUseHasNext(true);
////        req.setBuyerOpenId("AAHm5d-EAAeGwJedwSHpg8bT");
//        TradesSoldIncrementGetResponse rsp = client.execute(req, sessionKey);
//        System.out.println(rsp.getBody());
//
//        if (rsp.getTrades() == null) {
//            if (StringUtils.isEmpty(rsp.getErrorCode()) == false) {
//                //接口查询错误
//                return new TaoBaoOpenOrderUpdResult(500, "接口调用错误：" + rsp.getMsg() + rsp.getSubMsg());
//            }
//            if (rsp.getTotalResults() == 0) {
//                return new TaoBaoOpenOrderUpdResult(rsp.getTotalResults().intValue(), new ArrayList<>());
//            }
//        }
//
//        //组合的订单列表
//        List<DcTmallOrderEntity> orderList = new ArrayList<>();
//
//        //有数据
//        for (var trade : rsp.getTrades()) {
//            try {
//                DcTmallOrderEntity order = new DcTmallOrderEntity();
//                order.setId(trade.getTid().toString());
//                order.setCreateTime(trade.getCreated());
//                order.setPayTime(trade.getPayTime());
//                order.setTotalAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//                order.setShippingFee(BigDecimal.valueOf(Double.parseDouble(trade.getPostFee())));
//                order.setPayAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//                order.setBuyerName(trade.getBuyerNick());
//                order.setSellerMemo(trade.getSellerMemo());
//                order.setProvince(trade.getReceiverState());
//                order.setCity(trade.getReceiverCity());
//                order.setArea(trade.getReceiverDistrict());
//                order.setStatus(EnumTmallOrderStatus.getStatus(trade.getStatus()));
//                order.setStatusStr(trade.getStatus());
//                List<DcTmallOrderItemEntity> items = new ArrayList<>();
//                for (var item : trade.getOrders()) {
//                    DcTmallOrderItemEntity orderItem = new DcTmallOrderItemEntity();
//                    orderItem.setSpecNumber(item.getOuterSkuId());
//                    orderItem.setGoodsNumber(item.getOuterIid());
//                    orderItem.setProductImgUrl(item.getPicPath());
//                    orderItem.setGoodsTitle(item.getTitle());
//                    orderItem.setPrice(BigDecimal.valueOf(Double.parseDouble(item.getPayment())));
//                    orderItem.setQuantity(item.getNum().doubleValue());
//                    orderItem.setSubItemId(item.getOid().toString());
//                    orderItem.setSkuInfo(item.getSkuPropertiesName());
//                    orderItem.setItemAmount(BigDecimal.valueOf(Double.parseDouble(item.getPayment())));
//                    orderItem.setRefundStatusStr(item.getRefundStatus());
//                    items.add(orderItem);
//                }
//                order.setItems(items);
//
//                orderList.add(order);
//            } catch (Exception e) {
//            }
//        }
//
//        return new TaoBaoOpenOrderUpdResult(rsp.getTotalResults().intValue(), orderList);
//    }
//
//    /**
//     * 拉取淘系退货订单
//     * @param pageNo
//     * @param pageSize
//     * @param url
//     * @param appKey
//     * @param appSecret
//     * @param sessionKey
//     * @return
//     * @throws ApiException
//     */
//    public static TaoBaoOpenOrderUpdResult<DcTmallOrderRefundEntity> updTmallRefunOrder(Long pageNo, Long pageSize, String url, String appKey, String appSecret, String sessionKey) throws ApiException {
//        TaobaoClient client = new DefaultTaobaoClient(url, appKey, appSecret);
//        List<DcTmallOrderRefundEntity> list = new ArrayList<>();
//
////        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
////        RefundGetRequest req1 = new RefundGetRequest();
////        req1.setFields("title,address,good_return_time,created");
////        req1.setRefundId(44929186648087305L);
////        RefundGetResponse rsp1 = client.execute(req1, sessionKey);
////        System.out.println(rsp1.getBody());
////        RefundsApplyGetRequest raReq = new RefundsApplyGetRequest();
////        raReq.setFields("refund_id, tid, title, buyer_nick, seller_nick, total_fee, status, created, refund_fee");
////        raReq.setPageNo(1L);
////        raReq.setPageSize(10L);
////////        raReq.setStatus("SUCCESS");
//////
////        RefundsApplyGetResponse raRsp = client.execute(raReq,sessionKey);
//
//        RefundsReceiveGetRequest req = new RefundsReceiveGetRequest();
//        req.setFields("refund_id, tid, title, buyer_nick, seller_nick, total_fee, status, created,num, refund_fee, oid, good_status," +
//                " company_name, sid, payment, reason, desc, has_good_return, modified, order_status,refund_phase,sku");
////        req.setStatus("WAIT_SELLER_AGREE");
////        req.setSellerNick("hz0799");
////        req.setBuyerNick("juan20108810");
////        req.setType("fixed");
////        req.setType("fixed");
//        req.setPageNo(pageNo);
//        req.setPageSize(pageSize);
//        RefundsReceiveGetResponse rsp = client.execute(req, sessionKey);
//        if (rsp.getTotalResults() > 0) {
//            //查到了数据
//            for (var item : rsp.getRefunds()) {
//                //循环插入退货数据
//                DcTmallOrderRefundEntity tmallOrderRefund = new DcTmallOrderRefundEntity();
//                tmallOrderRefund.setBuyer_nick(item.getBuyerNick());
//                tmallOrderRefund.setCreated(DateUtil.dateToStamp(item.getCreated()));
//                tmallOrderRefund.setDesc(item.getDesc());
//                tmallOrderRefund.setGood_status(item.getGoodStatus());
//                tmallOrderRefund.setHas_good_return(item.getHasGoodReturn() == true ? 1 : 0);
//                tmallOrderRefund.setLogisticsCode(item.getSid());
//                tmallOrderRefund.setLogisticsCompany(item.getCompanyName());
//                tmallOrderRefund.setModified(DateUtil.dateToStamp(item.getModified()));
//                tmallOrderRefund.setOid(item.getOid());
//                tmallOrderRefund.setOrder_status(item.getOrderStatus());
//                tmallOrderRefund.setReason(item.getReason());
//                tmallOrderRefund.setRefund_fee(item.getRefundFee());
//                tmallOrderRefund.setRefund_id(item.getRefundId());
//                tmallOrderRefund.setStatus(item.getStatus());
//                tmallOrderRefund.setTid(item.getTid());
//                tmallOrderRefund.setTotal_fee(item.getTotalFee());
//                tmallOrderRefund.setNum(item.getNum());
//                tmallOrderRefund.setRefundPhase(item.getRefundPhase());
//                list.add(tmallOrderRefund);
//            }
//        }
//        return new TaoBaoOpenOrderUpdResult(rsp.getTotalResults().intValue(), list);
//    }
//}
