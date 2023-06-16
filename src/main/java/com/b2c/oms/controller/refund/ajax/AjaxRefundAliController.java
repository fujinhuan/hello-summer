//package com.b2c.oms.controller.refund.ajax;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.DataRow;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.alibaba.CommonUtil;
//import com.b2c.entity.datacenter.DcAliOrderRefundEntity;
//import com.b2c.entity.enums.AliOrderRefundEnums;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.entity.result.EnumResultVo;
//
//import com.b2c.service.erp.ErpSalesOrderService;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequestMapping("/ajax_ali")
//@RestController
//public class AjaxRefundAliController {
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    @Autowired
//    private ErpSalesOrderService salesOrderService;
//    private static Logger log = LoggerFactory.getLogger(AjaxRefundAliController.class);
//
//    /**
//     * 更新订单退货单
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/pull_refund", method = RequestMethod.POST)
//    public ApiResult<String> updAliRefundOrders(Model model, HttpServletRequest request) {
//        log.info("/***********开始更新阿里退货订单***********/");
//        Integer shopId =1;
//        var shop = shopService.getShop(shopId);
//
//        var entity = thirdSettingService.getEntity(shop.getType());
//        //不存在ali token 跳转获取
//        if (StringUtils.isEmpty(entity.getAccess_token())) {
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新获取");
//        }
//        //判断token是否过期
//        Date date = new Date();
//        long time = date.getTime(); //这是毫秒数
//        long expireTime = entity.getAccess_token_begin() * 1000 + entity.getExpires_in() * 1000;
//        if (expireTime < time) {
//            //已过期
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新获取");
//        }
//
////        ApiExecutor apiExecutor = new ApiExecutor("gw.open.1688.com", 80, 443, AliClient.getAppId(), AliClient.getAppSecret());
////        AlibabaTradeRefundQueryOrderRefundListParam param = new AlibabaTradeRefundQueryOrderRefundListParam();
////
////
////        SDKResult<AlibabaTradeRefundQueryOrderRefundListResult> result1 = apiExecutor.execute(param, entity.getAccess_token());
//
//
//        try {
//            String baseUrl = "http://gw.open.1688.com/openapi/";
//            String apiPath = "param2/1/";
//            String nameSpace = "com.alibaba.trade";
//            String apiName = "alibaba.trade.refund.queryOrderRefundList";
//            String urlPath = apiPath + nameSpace + "/" + apiName + "/" + com.b2c.common.third.ali.AliClient.getAppId();
//
//            Map<String, String> params = new HashMap<>();
//            params.put("access_token", entity.getAccess_token());
//            params.put("dipsuteType", "0");
//            //请求签名
//            String sign = CommonUtil.signatureWithParamsAndUrlPath2(urlPath, params, com.b2c.common.third.ali.AliClient.getAppSecret());
//
//            String url11 = baseUrl + urlPath + "?access_token=" + entity.getAccess_token() + "&_aop_signature=" + sign;
//            String postParams = "access_token=" + entity.getAccess_token() + "&dipsuteType=0";
//            HttpClient client1 = HttpClient.newBuilder().build();
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .uri(URI.create(url11))
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .POST(HttpRequest.BodyPublishers.ofString(postParams))//HttpRequest.BodyPublishers.ofString("name1=value1&name2=value2")
//                    .build();
//            HttpResponse<String> response = client1.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//
//            if (response.statusCode() == 200) {
//                int totalSize = JSONObject.parseObject(response.body()).getJSONObject("result").getInteger("totalCount");
//                int currentPageNum = JSONObject.parseObject(response.body()).getJSONObject("result").getInteger("currentPageNum");
//                int totalUpdate = 0;
//                int totalAdd = 0;
//                int totalError = 0;
//
//                JSONArray obj = JSONObject.parseObject(response.body()).getJSONObject("result").getJSONArray("opOrderRefundModels");
////                String listResult = JSONObject.parseObject(response.body()).getString("result");
////                AliRefundOrderResult refundOrderResult = JSON.parseObject(listResult,AliRefundOrderResult.class);
//
//                for (Object result : obj) {
//                    JSONObject order = (JSONObject) JSON.toJSON(result);
//
//                    Long refId = ((JSONObject) JSON.toJSON(result)).getLong("id");
//
//
//                    String refNo = order.getString("refundId");
////                    if(refNo.equals("TQ46520098142434652")){
////                        refNo = "";
////                    }
////                    String s = order.getString("gmtApply");
////                    String gmtApply = s.substring(0, s.length() - 8);
////                    Date gmtApplyD = DateUtil.stringtoDate(gmtApply,"yyyyMMddHHmmss");
//                    //2019-12-06 16:36:14
//                    DcAliOrderRefundEntity refund = new DcAliOrderRefundEntity();//JSON.parseObject(order.toJSONString(), DcAliOrderRefundEntity.class);
//
//                    refund.setRefId(refId);//退款单主键
//
//                    //调用退款订单详情接口
////                    JSONObject detail = getDetail(entity.getAccess_token(), refId);
////                    if (detail != null) {
//                    //买家是否已经发货（如果有退货的流程）
//                    boolean isBuyerSendGoods = order.getBoolean("isBuyerSendGoods");
//                    refund.setIsBuyerSendGoods(order.getIntValue("isBuyerSendGoods"));
//
//                    //是否要求退货
//                    boolean isRefundGoods = order.getBoolean("isRefundGoods");
//                    refund.setIsRefundGoods(order.getIntValue("isRefundGoods"));
//                    //买家是否已收到货
//                    boolean isGoodsReceived = order.getBoolean("isGoodsReceived");
//                    refund.setIsGoodsReceived(order.getIntValue("isGoodsReceived"));
//                    //是否仅退款
//                    boolean isOnlyRefund = order.getBoolean("isOnlyRefund");
//                    refund.setIsOnlyRefund(order.getIntValue("isOnlyRefund"));
//
//                    refund.setBuyerLoginId(order.getString("buyerLoginId"));
//                    refund.setAlipayPaymentId(order.getString("alipayPaymentId"));
//
//                    //买家申请退款金额，单位：分
//                    refund.setApplyPayment(order.getLong("applyPayment"));
//
//                    refund.setApplyReason(order.getString("applyReason"));
//
//                    refund.setApplySubReason(order.getString("applySubReason"));
////                    refund.setApplySubReasonId(order.getInteger("applySubReasonId"));
////                    refund.setBuyerAlipayId(order.getString("buyerAlipayId"));
//
////                    refund.setBuyerLogisticsName(order.getString("buyerLogisticsName"));
//                    refund.setBuyerMemberId(order.getString("buyerMemberId"));
//                    refund.setBuyerUserId(order.getLong("buyerUserId"));
//                    refund.setCanRefundPayment(order.getLong("canRefundPayment"));
////                    refund.setDisburseChannel(order.getString("disburseChannel"));
//                    refund.setDisputeType(order.getInteger("disputeType"));
//
//                    String extInfo = order.getString("extInfo");
//                    if (extInfo != null) {
//                        refund.setExtInfo(extInfo);
//                    }
//
////                    refund.setFreightBill(order.getString("freightBill"));
////                    if (StringUtils.isEmpty(refund.getFreightBill())) {
////                        refund.setFreightBill("");
////                    }
//
//                    refund.setFrozenFund(order.getLong("frozenFund"));
//
//                    //时间处理
//                    String gmtApplyStr = order.getString("gmtApply").substring(0, order.getString("gmtApply").length() - 8);
////                    Date gmtApply = DateUtil.stringtoDate(order.getString("gmtApply").substring(0, order.getString("gmtApply").length() - 8), "yyyyMMddHHmmss");
//                    refund.setGmtApply(DateUtil.aliDateToLong(gmtApplyStr));
//
////                    Date gmtModified = DateUtil.stringtoDate(order.getString("gmtModified").substring(0, order.getString("gmtModified").length() - 8), "yyyyMMddHHmmss");
//                    String gmtModifiedStr = order.getString("gmtModified").substring(0, order.getString("gmtModified").length() - 8);
//                    refund.setGmtModified(DateUtil.aliDateToLong(gmtModifiedStr));
//
////                    Date gmtCreate = DateUtil.stringtoDate(order.getString("gmtCreate").substring(0, order.getString("gmtCreate").length() - 8), "yyyyMMddHHmmss");
//                    String gmtCreateStr = order.getString("gmtCreate").substring(0, order.getString("gmtCreate").length() - 8);
//                    refund.setGmtCreate(DateUtil.aliDateToLong(gmtCreateStr));
//
////                    Date gmtTimeOut = DateUtil.stringtoDate(order.getString("gmtTimeOut").substring(0, order.getString("gmtTimeOut").length() - 8), "yyyyMMddHHmmss");
//                    String gmtTimeOutStr = order.getString("gmtTimeOut").substring(0, order.getString("gmtTimeOut").length() - 8);
//                    refund.setGmtTimeOut(DateUtil.aliDateToLong(gmtTimeOutStr));
//
//                    try {
////                        Date gmtCompleted = DateUtil.stringtoDate(order.getString("gmtCompleted").substring(0, order.getString("gmtCompleted").length() - 8), "yyyyMMddHHmmss");
//                        String gmtCompletedStr = order.getString("gmtCompleted").substring(0, order.getString("gmtCompleted").length() - 8);
//                        refund.setGmtCompleted(DateUtil.aliDateToLong(gmtCompletedStr));
//
////                        Date gmtFreezed = DateUtil.stringtoDate(order.getString("gmtFreezed").substring(0, order.getString("gmtFreezed").length() - 8), "yyyyMMddHHmmss");
//                        String gmtFreezedStr = order.getString("gmtFreezed").substring(0, order.getString("gmtFreezed").length() - 8);
//                        refund.setGmtFreezed(DateUtil.aliDateToLong(gmtFreezedStr));
//
//                    } catch (Exception e) {
//                    }
//
//                    refund.setGoodsStatus(order.getInteger("goodsStatus"));
//
////                    refund.setInstantRefundType(order.getString("instantRefundType"));
//                    refund.setOrderId(order.getLong("orderId"));
//                    refund.setProductName(order.getString("productName"));
//                    refund.setRefundCarriage(order.getLong("refundCarriage"));
//                    refund.setRefundId(order.getString("refundId"));
//                    refund.setRefundPayment(order.getLong("refundPayment"));
//                    refund.setRejectReason(order.getString("rejectReason"));
//                    refund.setRejectTimes(order.getInteger("rejectTimes"));
//
//                    refund.setSellerUserId(order.getLong("sellerUserId"));
//
//                    refund.setStatus(AliOrderRefundEnums.valueOf(order.getString("status")).getIndex());
//
//                    //调用退款订单详情接口
//                    JSONObject detail = getDetail(entity.getAccess_token(), refId);
//                    if (detail != null) {
//                        refund.setOrderEntryIdList(detail.getString("orderEntryIdList"));
//                        refund.setOrderEntryCountMap(detail.getString("orderEntryCountMap"));
//                        refund.setBuyerLogisticsName(detail.getString("buyerLogisticsName"));
//                        refund.setFreightBill(detail.getString("freightBill"));
//                    }
//
//                    int resultInsert = aliOrderService.updateOrderRefund(refund);
//                    if (resultInsert == 1) {
//                        //新增成功
//                        totalAdd++;
//                    } else if (resultInsert == 2) {
//                        totalUpdate++;
//                    } else if (resultInsert == 0) {
//                        totalError++;
//                    }
//                    //更新阿里退货到erp
//                    var erpResult= aliOrderService.editRefundAliOrder(refId,shopId);
//                    log.info("更新Ali退款到erp:"+JSONObject.toJSONString(erpResult));
//                }
//                salesOrderService.addErpSalesPullOrderLog(System.currentTimeMillis() / 1000,System.currentTimeMillis() / 1000,shopId,totalAdd,totalError,totalUpdate,1);
//                return new ApiResult<>(0, "更新成功，总数据：" + totalSize + "，更新：" + totalUpdate + "，新增：" + totalAdd + "，失败：" + totalError);
//            } else {
//                return new ApiResult<>(404, "更新失败，接口参数错误：" + response.body().toString());
//            }
//        } catch (
//                Exception e) {
//            return new ApiResult<>(400, "更新失败");
//        }
//
//    }
//
//    /**
//     * 更新单条退货订单
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/upd_ali_refund_by_refId", method = RequestMethod.POST)
//    public ApiResult<String> updAliRefundOrderById(Model model, @RequestBody DataRow data, HttpServletRequest request) throws IOException, InterruptedException {
//        Long refId = data.getLong("id");
//        if (refId == null || refId <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少id");
//        }
//        log.info("/***********开始更新阿里退货订单***********/");
//        Integer shopTypeId = 1;
//        var entity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=GETORDERLIST";
////        String returnUrl = variable.getValue() + "/order/aliRefundList&state=GETORDERLIST";
//
//        //不存在ali token 跳转获取
//        if (StringUtils.isEmpty(entity.getAccess_token())) {
//
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新获取");
////            return new ApiResult<>(, "没有更新到订单","redirect:" + AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//        //判断token是否过期
//        Date date = new Date();
//        long time = date.getTime(); //这是毫秒数
//        long expireTime = entity.getAccess_token_begin() * 1000 + entity.getExpires_in() * 1000;
//        if (expireTime < time) {
//            //已过期
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新获取");
////            return new ApiResult<>(404, "没有更新到订单","redirect:" + AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//
//        //调用退款订单详情接口
//        JSONObject detail = getDetail(entity.getAccess_token(), refId);
//        if (detail != null) {
//            DcAliOrderRefundEntity refund = new DcAliOrderRefundEntity();
//            refund.setRefId(refId);//退款单主键
//
//            //买家是否已经发货（如果有退货的流程）
//            boolean isBuyerSendGoods = detail.getBoolean("buyerSendGoods");
//            refund.setIsBuyerSendGoods(detail.getIntValue("buyerSendGoods"));
//
//            //是否要求退货
//            boolean isRefundGoods = detail.getBoolean("refundGoods");
//            refund.setIsRefundGoods(detail.getIntValue("refundGoods"));
//            //买家是否已收到货
//            boolean isGoodsReceived = detail.getBoolean("goodsReceived");
//            refund.setIsGoodsReceived(detail.getIntValue("goodsReceived"));
//            //是否仅退款
//            boolean isOnlyRefund = detail.getBoolean("onlyRefund");
//            refund.setIsOnlyRefund(detail.getIntValue("onlyRefund"));
//
//            refund.setBuyerLoginId(detail.getString("buyerLoginId"));
//            refund.setAlipayPaymentId(detail.getString("alipayPaymentId"));
//
//            //买家申请退款金额，单位：分
//            refund.setApplyPayment(detail.getLong("applyPayment"));
//
//            refund.setApplyReason(detail.getString("applyReason"));
//
//            refund.setApplySubReason(detail.getString("applySubReason"));
//
//            refund.setBuyerMemberId(detail.getString("buyerMemberId"));
//            refund.setBuyerUserId(detail.getLong("buyerUserId"));
//            refund.setCanRefundPayment(detail.getLong("canRefundPayment"));
//            refund.setDisputeType(detail.getInteger("disputeType"));
//            refund.setFrozenFund(detail.getLong("frozenFund"));
//
//            //时间处理
//            String gmtApplyStr = detail.getString("gmtApply").substring(0, detail.getString("gmtApply").length() - 8);
////                    Date gmtApply = DateUtil.stringtoDate(order.getString("gmtApply").substring(0, order.getString("gmtApply").length() - 8), "yyyyMMddHHmmss");
//            refund.setGmtApply(DateUtil.aliDateToLong(gmtApplyStr));
//
////                    Date gmtModified = DateUtil.stringtoDate(order.getString("gmtModified").substring(0, order.getString("gmtModified").length() - 8), "yyyyMMddHHmmss");
//            String gmtModifiedStr = detail.getString("gmtModified").substring(0, detail.getString("gmtModified").length() - 8);
//            refund.setGmtModified(DateUtil.aliDateToLong(gmtModifiedStr));
//
////                    Date gmtCreate = DateUtil.stringtoDate(order.getString("gmtCreate").substring(0, order.getString("gmtCreate").length() - 8), "yyyyMMddHHmmss");
//            String gmtCreateStr = detail.getString("gmtCreate").substring(0, detail.getString("gmtCreate").length() - 8);
//            refund.setGmtCreate(DateUtil.aliDateToLong(gmtCreateStr));
//
////                    Date gmtTimeOut = DateUtil.stringtoDate(order.getString("gmtTimeOut").substring(0, order.getString("gmtTimeOut").length() - 8), "yyyyMMddHHmmss");
//            String gmtTimeOutStr = detail.getString("gmtTimeOut").substring(0, detail.getString("gmtTimeOut").length() - 8);
//            refund.setGmtTimeOut(DateUtil.aliDateToLong(gmtTimeOutStr));
//
//            try {
////                        Date gmtCompleted = DateUtil.stringtoDate(order.getString("gmtCompleted").substring(0, order.getString("gmtCompleted").length() - 8), "yyyyMMddHHmmss");
//                String gmtCompletedStr = detail.getString("gmtCompleted").substring(0, detail.getString("gmtCompleted").length() - 8);
//                refund.setGmtCompleted(DateUtil.aliDateToLong(gmtCompletedStr));
//
////                        Date gmtFreezed = DateUtil.stringtoDate(order.getString("gmtFreezed").substring(0, order.getString("gmtFreezed").length() - 8), "yyyyMMddHHmmss");
//                String gmtFreezedStr = detail.getString("gmtFreezed").substring(0, detail.getString("gmtFreezed").length() - 8);
//                refund.setGmtFreezed(DateUtil.aliDateToLong(gmtFreezedStr));
//
//            } catch (Exception e) {
//            }
//
//            refund.setGoodsStatus(detail.getInteger("goodsStatus"));
//
////                    refund.setInstantRefundType(order.getString("instantRefundType"));
//            refund.setOrderId(detail.getLong("orderId"));
//            refund.setProductName(detail.getString("productName"));
//            refund.setRefundCarriage(detail.getLong("refundCarriage"));
//            refund.setRefundId(detail.getString("refundId"));
//            refund.setRefundPayment(detail.getLong("refundPayment"));
//            refund.setRejectReason(detail.getString("rejectReason"));
//            refund.setRejectTimes(detail.getInteger("rejectTimes"));
//
//            refund.setSellerUserId(detail.getLong("sellerUserId"));
//
//            refund.setStatus(AliOrderRefundEnums.valueOf(detail.getString("status")).getIndex());
//
//
//            refund.setOrderEntryIdList(detail.getString("orderEntryIdList"));
//            refund.setOrderEntryCountMap(detail.getString("orderEntryCountMap"));
//            refund.setBuyerLogisticsName(detail.getString("buyerLogisticsName"));
//            refund.setFreightBill(detail.getString("freightBill"));
//
//            int resultInsert = aliOrderService.updateOrderRefund(refund);
//            if (resultInsert == 1) {
//                //新增成功
//                return new ApiResult<>(0, "新增了一条");
//            } else if (resultInsert == 2) {
//                //更新成功
//                return new ApiResult<>(0, "更新成功");
//            } else if (resultInsert == 0) {
//                //错误
//                return new ApiResult<>(505, "数据库错误");
//            }
//
//            return new ApiResult<>(0, "SUCCESS");
//        } else {
//            //接口获取数据错误
//            return new ApiResult<>(500, "接口获取数据错误");
//        }
//
//
//    }
//
//    /**
//     * 获取详情
//     *
//     * @param accessToken
//     * @param refundOrderId
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    private JSONObject getDetail(String accessToken, Long refundOrderId) throws IOException, InterruptedException {
//        String baseUrl = "http://gw.open.1688.com/openapi/";
//        String apiPath = "param2/1/";
//        String nameSpace = "com.alibaba.trade";
//        String apiName = "alibaba.trade.refund.OpQueryOrderRefund";
//        String urlPath = apiPath + nameSpace + "/" + apiName + "/" + com.b2c.common.third.ali.AliClient.getAppId();
//
//        Map<String, String> params1 = new HashMap<>();
//        params1.put("access_token", accessToken);
//        params1.put("refundId", refundOrderId.toString());
////        params1.put("queryType", "3");
//        //请求签名
//        String sign1 = CommonUtil.signatureWithParamsAndUrlPath2(urlPath, params1, com.b2c.common.third.ali.AliClient.getAppSecret());
//
//        String url12 = baseUrl + urlPath + "?access_token=" + accessToken + "&refundId=" + refundOrderId + "&_aop_signature=" + sign1;
//        String postParams2 = "access_token=" + accessToken + "&refundId=" + refundOrderId;
//        HttpClient client2 = HttpClient.newBuilder().build();
//
//        HttpRequest httpRequest2 = HttpRequest.newBuilder()
//                .uri(URI.create(url12))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(HttpRequest.BodyPublishers.ofString(postParams2))//HttpRequest.BodyPublishers.ofString("name1=value1&name2=value2")
//                .build();
//        HttpResponse<String> response2 = client2.send(httpRequest2, HttpResponse.BodyHandlers.ofString());
//
//        if (response2.statusCode() == 200) {
//            JSONObject details = JSONObject.parseObject(response2.body()).getJSONObject("result").getJSONObject("opOrderRefundModelDetail");
//            return details;
////            JSONObject orderEntryCountMap = details.getJSONObject(0).getJSONObject("orderEntryCountMap");
//        } else {
//            return null;
//        }
//    }
//}
