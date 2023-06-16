//package com.b2c.oms.controller.ali;
//
//import com.alibaba.logistics.param.*;
//import com.alibaba.ocean.rawsdk.ApiExecutor;
//import com.alibaba.ocean.rawsdk.common.SDKResult;
//import com.alibaba.trade.param.AlibabaOpenplatformTradeModelTradeInfo;
//import com.alibaba.trade.param.AlibabaTradeGetSellerViewParam;
//import com.alibaba.trade.param.AlibabaTradeGetSellerViewResult;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.utils.JsonUtil;
//import com.b2c.entity.DataRow;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.UserEntity;
//import com.b2c.entity.alibaba.OrderSendsVo;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.entity.erp.ErpGoodsSpecEntity;
//import com.b2c.entity.erp.ErpOrderEntity;
//import com.b2c.entity.erp.vo.ExpressInfoVo;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.enums.EnumShopType;
//import com.b2c.enums.erp.EnumErpOrderSendStatus;
//import com.b2c.service.erp.ErpOrderService;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.OrderYungouService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * 阿里订单AJAX
// */
//@RequestMapping("/ali_ajax")
//@RestController
//public class AjaxAliOrderController {
//    private static Logger log = LoggerFactory.getLogger(AjaxAliOrderController.class);
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    @Autowired
//    private OrderYungouService dcOrderService;
//    @Autowired
//    private ErpOrderService erpOrderService;
//    @Autowired
//    private SystemService systemService;
//
//
//
//
//    /**
//     * 1688订单发货
//     *
//     * @param orderId 订单号
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/order_send", method = RequestMethod.POST)
//    public ApiResult<String> orderSend(@RequestBody Long orderId, HttpServletRequest req) {
//        var dcAliOrder = aliOrderService.getOrderById(orderId);
//        ErpOrderEntity erpOrder = null;
////        OrdersEntity order = null;
//        if (dcAliOrder.getOrderId().intValue() == 0) {
//            erpOrder = erpOrderService.getOrderEntityByNum(dcAliOrder.getId().toString());
//        }
////        else {
////            order = dcOrderService.getOrderEntityById(dcAliOrder.getOrderId());
////            erpOrder = erpOrderService.getOrderEntityByNum(order.getOrderNum());
////        }
//        //阿里发货订单号
//        String sendOrderNum = dcAliOrder.getId().toString();
//
//        if (erpOrder == null) return new ApiResult<>(EnumResultVo.NotFound.getIndex(), "发货订单不存在");
//        if (erpOrder.getStatus() != EnumErpOrderSendStatus.HasSend.getIndex())
//            return new ApiResult<>(EnumResultVo.StateError.getIndex(), "仓库商品未发货，不能发货");
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=ERPORDERSEND";
//
//        //不存在ali token 跳转获取
//        if (StringUtils.isEmpty(settingEntity.getAccess_token())) {
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//        //判断token是否过期
//        Date date = new Date();
//        long time = date.getTime(); //这是毫秒数
//        long expireTime = settingEntity.getAccess_token_begin() * 1000 + settingEntity.getExpires_in() * 1000;
//        if (expireTime < time) {
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//        synchronized (this) {
//            //设置appkey和密钥(seckey)
//            ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//            //阿里发货方法
//            AlibabaLogisticsOpDeliverySendOrderOfflineParam param = new AlibabaLogisticsOpDeliverySendOrderOfflineParam();
//
//            //发货商品 对应order
//            AlibabaLogisticsOpSendGood sendGood = new AlibabaLogisticsOpSendGood();
//            sendGood.setSourceId(sendOrderNum);
//
//            //发货商品明细 对应orderItems
//            List<AlibabaLogisticsOpSendGoodEntry> goodEntries = new ArrayList<>();
//
//            /**采购订单详细信息**/
//            var aliOrderItems = aliOrderService.getItemsByOrderId(dcAliOrder.getId());
//            aliOrderItems.forEach(o -> {
//                if (o.getSubItemID() == null || o.getSubItemID().intValue() == 0) return;
//                AlibabaLogisticsOpSendGoodEntry entry = new AlibabaLogisticsOpSendGoodEntry();
//                entry.setSourceEntryId(o.getSubItemID().toString());
//                entry.setAmount(o.getQuantity().longValue());
//                entry.setWeight(0.2);
//                goodEntries.add(entry);
//            });
//
//            sendGood.setSendGoodEntries(goodEntries.toArray(new AlibabaLogisticsOpSendGoodEntry[goodEntries.size()]));
//
//            OrderSendsVo sendsVo = new OrderSendsVo();
//            sendsVo.setCpCode(erpOrder.getLogisticsCompanyCode());
//            sendsVo.setLogisticsCpName(erpOrder.getLogisticsCompany());
//            sendsVo.setMailNo(erpOrder.getLogisticsCode());
//
//            //发送至阿里地址
//            List<AlibabaLogisticsOpSendGood> goods = new ArrayList<>();
//            goods.add(sendGood);
//
//            param.setSendGoods(goods.toArray(new AlibabaLogisticsOpSendGood[goods.size()]));
//
//            param.setExtBody(JsonUtil.objToString(sendsVo));
//
//            var result = apiExecutor.execute(param, settingEntity.getAccess_token());
//
//            if (result.getResult().getSuccess()) {
//                //var ptResult = orderService.orderSend(id,erpOrder.getLogisticsCompany(),erpOrder.getLogisticsCompanyCode(),erpOrder.getLogisticsCode(),userName);
//                //return new ApiResult<>(ptResult.getCode(), ptResult.getMsg());
//                aliOrderService.updAliOrderSendStatus(orderId, dcAliOrder.getOrderId());
//                return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "发货成功");
//            } else {
//                return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "阿里发货失败：" + result.getResult().getErrorMessage());
//            }
//        }
//    }
//
//    /**
//     * 阿里订单关联平台订单
//     *
//     * @param data
//     * @param req
//     * @return
//     */
////    @RequestMapping(value = "/order_relevance", method = RequestMethod.POST)
////    public synchronized ApiResult<Integer> orderSend(@RequestBody DataRow data, HttpServletRequest req) {
////        if (StringUtils.isEmpty(data.getString("order_id")))
////            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择关联订单");
////        String orderId = data.getString("order_id");
////        String aliOrderId = data.getString("ali_order_id");
////        var result = aliOrderService.orderRelevance(Long.parseLong(aliOrderId), Long.parseLong(orderId));
////        return new ApiResult<>(result.getCode(), result.getMsg());
////
////    }
//
//    /**
//     * 1688物流地址
//     */
//    @RequestMapping(value = "order_logistics", method = RequestMethod.POST)
//    public ApiResult<AlibabaLogisticsOpenPlatformLogisticsTrace[]> getTrcc(@RequestBody Object id) {
//        Integer shopTypeId = 1;
//        var entity = thirdSettingService.getEntity(shopTypeId);
//
//        //设置appkey和密钥(seckey)
//        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//        AlibabaTradeGetSellerViewParam param = new AlibabaTradeGetSellerViewParam();
//        param.setOrderId(Long.parseLong(id.toString()));
//
//        //调用API并获取返回结果
//        SDKResult<AlibabaTradeGetSellerViewResult> result = apiExecutor.execute(param, entity.getAccess_token());
//        if(StringUtils.isEmpty(result.getErrorCode())==false){
//            if(result.getErrorCode().equals("401"))
//                return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token过期");
//            else return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "接口错误："+result.getErrorMessage());
//        }
//        AlibabaOpenplatformTradeModelTradeInfo info = result.getResult().getResult();
//
//        BigDecimal sum = new BigDecimal(0);
//        for (int i = 0; i < info.getProductItems().length; i++) {
//            BigDecimal quantity = info.getProductItems()[i].getQuantity();
//            BigDecimal decimal = info.getProductItems()[i].getPrice();
//            sum = sum.add(quantity.multiply(decimal));
//        }
//
//        AlibabaTradeGetLogisticsTraceInfoSellerViewParam viewParam = new AlibabaTradeGetLogisticsTraceInfoSellerViewParam();
//        viewParam.setOrderId(Long.parseLong(id.toString()));
//        viewParam.setWebSite("1688");
//        SDKResult<AlibabaTradeGetLogisticsTraceInfoSellerViewResult> execute = apiExecutor.execute(viewParam, entity.getAccess_token());
//        if (execute.getResult().getLogisticsTrace() == null) return new ApiResult<>(500, "没有物流信息");
//        AlibabaLogisticsOpenPlatformLogisticsTrace[] trace = execute.getResult().getLogisticsTrace();
//        return new ApiResult<>(0, "", trace);
//    }
//
//    /**
//     * 同意退货
//     *
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/reviewRefund", method = RequestMethod.POST)
//    public ApiResult<String> reviewRefund(@RequestBody DataRow data, HttpServletRequest req) {
//        Long refId = data.getLong("id");
//        if(refId == null || refId<=0){
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少id");
//        }
//
////        if (StringUtils.isEmpty(data.getString("companyCode")))
////            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择快递公司");
////        if (StringUtils.isEmpty(data.getString("code")))
////            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入快递单号");
////        if (StringUtils.isEmpty(data.getString("address")))
////            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入快递单号");
//        ExpressInfoVo express = new ExpressInfoVo();
//        express.setLogisticsCompany(data.getString("company"));
//        express.setLogisticsCode(data.getString("code"));
//        express.setLogisticsCompanyCode(data.getString("companyCode"));
//        express.setAddress(data.getString("address"));
//
//        var result = aliOrderService.reviewRefundAli(refId, express);
//        return new ApiResult<>(result.getCode(), result.getMsg());
//    }
//
//    /**
//     * 修改商品规格
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/upd_order_spec", method = RequestMethod.POST)
//    public ApiResult<Integer> updGoodSepc(@RequestBody DataRow data, HttpServletRequest req) {
//        Integer result = aliOrderService.updAliGoodSpec(data.getInt("itemId"), data.getInt("newSpecId"));
//        if (result < 0) return new ApiResult<>(EnumResultVo.Fail.getIndex(), "修改失败");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "修改成功");
//    }
//
//    /**
//     * 查询商品规格
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/get_order_good_spec", method = RequestMethod.POST)
//    public ApiResult<List<ErpGoodsSpecEntity>> getGoodSpec(@RequestBody DataRow data, HttpServletRequest req) {
//        if (StringUtils.isEmpty(data.get("goodNumber")))
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "商品编码不存在");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", aliOrderService.getGoodSpecByGoodNumber(data.getString("goodNumber")));
//    }
//
//    /**
//     * 查询用户信息
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/get_user_by_order_type", method = RequestMethod.POST)
//    public ApiResult<List<UserEntity>> getUserByType(@RequestBody DataRow data, HttpServletRequest req) {
//        if (StringUtils.isEmpty(data.get("orderType")))
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "未选择订单类型");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", aliOrderService.getUserByType(data.getInt("orderType")));
//    }
//
//
//}
