//package com.b2c.oms.controller.ali;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.ocean.rawsdk.ApiExecutor;
//import com.alibaba.trade.param.AlibabaOpenplatformTradeModelTradeInfo;
//import com.alibaba.trade.param.AlibabaTradeGetSellerViewParam;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.datacenter.DcAliOrderAddressEntity;
//import com.b2c.entity.datacenter.DcAliOrderEntity;
//import com.b2c.entity.datacenter.DcAliOrderItemEntity;
//import com.b2c.entity.datacenter.DcAliOrderUserEntity;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.enums.EnumShopType;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Description:更新阿里单个订单 pbd add 2019/10/5 8:32
// */
//@Controller
//public class AliOrderUpdate {
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    private static Logger log = LoggerFactory.getLogger(AliOrderUpdate.class);
//    /**
//     * 获取阿里token
//     *
//     * @return
//     */
//    public ApiResult<String> getAliToken() {
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_WMS_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=GETORDERLIST";
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
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功", settingEntity.getAccess_token());
//    }
//
//    /**
//     * 更新阿里订单信息到平台
//     */
//    @Deprecated
//    public ApiResult<String> updAliOrderById(Long orderId) {
//        var resToken = getAliToken();
//        if (resToken.getCode() > 0)
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), resToken.getMsg(), resToken.getData());
//        try {
////            ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//            ApiExecutor apiExecutor = new ApiExecutor("gw.open.1688.com", 80, 443, AliClient.getAppId(), AliClient.getAppSecret());
//            AlibabaTradeGetSellerViewParam param = new AlibabaTradeGetSellerViewParam();
//            param.setOrderId(orderId);
//            var result = apiExecutor.execute(param, resToken.getData());
//            log.info("更新阿里订单详情："+result.getErrorMessage());
//            AlibabaOpenplatformTradeModelTradeInfo tradeInfo = result.getResult().getResult();
//
//            /********阿里订单数据转换Start********/
//            //订单实体
//            DcAliOrderEntity orderEntity = new DcAliOrderEntity();
//            orderEntity.setAllDeliveredTime(tradeInfo.getBaseInfo().getAllDeliveredTime());
//            orderEntity.setBuyerFeedback(tradeInfo.getBaseInfo().getBuyerFeedback());
//            orderEntity.setCloseReason(tradeInfo.getBaseInfo().getCloseReason());
//            orderEntity.setCompleteTime(tradeInfo.getBaseInfo().getCompleteTime());
//            orderEntity.setConfirmedTime(tradeInfo.getBaseInfo().getConfirmedTime());
//            orderEntity.setCreateTime(tradeInfo.getBaseInfo().getCreateTime());
//            orderEntity.setDiscount(tradeInfo.getBaseInfo().getDiscount());
//            orderEntity.setId(tradeInfo.getBaseInfo().getId());
//            orderEntity.setModifyTime(tradeInfo.getBaseInfo().getModifyTime());
//            orderEntity.setPayTime(tradeInfo.getBaseInfo().getPayTime());
//            orderEntity.setReceivingTime(tradeInfo.getBaseInfo().getReceivingTime());
//            orderEntity.setRefund(tradeInfo.getBaseInfo().getRefund());
//            orderEntity.setRefundId(tradeInfo.getBaseInfo().getRefundId());
//            orderEntity.setRefundPayment(tradeInfo.getBaseInfo().getRefundPayment());
//            orderEntity.setRefundStatus(tradeInfo.getBaseInfo().getRefundStatus());
//            orderEntity.setRefundStatusForAs(tradeInfo.getBaseInfo().getRefundStatusForAs());
//            orderEntity.setRemark(tradeInfo.getBaseInfo().getRemark());
//            orderEntity.setSellerID(tradeInfo.getBaseInfo().getSellerID());
//            orderEntity.setSellerMemo(tradeInfo.getBaseInfo().getSellerMemo());
//            orderEntity.setShippingFee(tradeInfo.getBaseInfo().getShippingFee());
//            orderEntity.setStatus(tradeInfo.getBaseInfo().getStatus());
//            orderEntity.setStepPayAll(tradeInfo.getBaseInfo().getStepPayAll() == true ? 1 : 0);
//            orderEntity.setSumProductPayment(tradeInfo.getBaseInfo().getSumProductPayment());
//            orderEntity.setTotalAmount(tradeInfo.getBaseInfo().getTotalAmount());
//            orderEntity.setTradeType(tradeInfo.getBaseInfo().getTradeType());
//            orderEntity.setBuyerUserId(tradeInfo.getBaseInfo().getBuyerUserId());
//
//
//            //订单用户实体
//            DcAliOrderUserEntity orderUser = new DcAliOrderUserEntity();
//            orderUser.setAliBuyerID(tradeInfo.getBaseInfo().getBuyerID());
//            orderUser.setBuyerLoginId(tradeInfo.getBaseInfo().getBuyerLoginId());
//            orderUser.setBuyerUserId(tradeInfo.getBaseInfo().getBuyerUserId());
//            orderUser.setCompanyName(tradeInfo.getBaseInfo().getBuyerContact().getCompanyName());
//            orderUser.setEmail(tradeInfo.getBaseInfo().getBuyerContact().getEmail());
//            orderUser.setMobile(tradeInfo.getBaseInfo().getBuyerContact().getMobile());
//            orderUser.setName(tradeInfo.getBaseInfo().getBuyerContact().getName());
//            orderUser.setPhone(tradeInfo.getBaseInfo().getBuyerContact().getPhone());
//
//
//            //订单收货地址实体
//            DcAliOrderAddressEntity orderAddress = new DcAliOrderAddressEntity();
//            orderAddress.setAddress(tradeInfo.getBaseInfo().getReceiverInfo().getToArea());
//            try {
//                String[] addresss = tradeInfo.getBaseInfo().getReceiverInfo().getToArea().split(" ");
//                orderAddress.setArea(addresss[2]);
//                orderAddress.setCity(addresss[1]);
//                orderAddress.setProvince(addresss[0]);
//                orderAddress.setTown(addresss[3]);
//            } catch (Exception e) {
//            }
//            orderAddress.setAreaCode(tradeInfo.getBaseInfo().getReceiverInfo().getToDivisionCode());
//            orderAddress.setContactPerson(tradeInfo.getBaseInfo().getReceiverInfo().getToFullName());
//            orderAddress.setMobile(tradeInfo.getBaseInfo().getReceiverInfo().getToMobile());
//            orderAddress.setOrderId(tradeInfo.getBaseInfo().getId());
//            orderAddress.setTownCode(tradeInfo.getBaseInfo().getReceiverInfo().getToTownCode());
//
//            //订单items
//            List<DcAliOrderItemEntity> orderItems = new ArrayList<>();
//            for (var item : tradeInfo.getProductItems()) {
//                DcAliOrderItemEntity itemEntity = new DcAliOrderItemEntity();
//                itemEntity.setSubItemID(item.getSubItemID());
//                itemEntity.setCargoNumber(item.getCargoNumber());
//                itemEntity.setCloseReason(item.getCloseReason());
//                itemEntity.setGmtCompleted(item.getGmtCompleted());
//                itemEntity.setGmtCreate(item.getGmtCreate());
//                itemEntity.setGmtModified(item.getGmtModified());
//                itemEntity.setGmtPayExpireTime(item.getGmtPayExpireTime());
//                itemEntity.setItemAmount(item.getItemAmount());
//                itemEntity.setLogisticsStatus(item.getLogisticsStatus());
//                itemEntity.setName(item.getName());
//                itemEntity.setOrderId(tradeInfo.getBaseInfo().getId());
//                itemEntity.setPrice(item.getPrice());
//                itemEntity.setProductCargoNumber(item.getProductCargoNumber());
//                itemEntity.setProductId(item.getProductID());
//                try {
//                    itemEntity.setProductImgUrl(item.getProductImgUrl()[0]);
//                } catch (Exception e) {
//                }
//                itemEntity.setProductSnapshotUrl(item.getProductSnapshotUrl());
//                itemEntity.setQuantity(item.getQuantity());
//                itemEntity.setRefund(item.getRefund());
//                itemEntity.setRefundId(item.getRefundId());
//                itemEntity.setRefundStatus(item.getRefundStatus());
//                itemEntity.setSkuInfos(JSONArray.toJSONString(item.getSkuInfos()));
//                StringBuilder skuInfo = new StringBuilder();
//                for (var i : item.getSkuInfos()) {
//                    skuInfo.append(i.getName());
//                    skuInfo.append(":");
//                    skuInfo.append(i.getValue());
//                    skuInfo.append(" ");
//                }
//                itemEntity.setSkuInfo(skuInfo.toString());
//                itemEntity.setStatus(item.getStatus());
//                itemEntity.setStatusStr(item.getStatusStr());
//                orderItems.add(itemEntity);
//            }
//            aliOrderService.update(orderEntity, orderUser, orderAddress, orderItems);
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "更新成功");
//        } catch (Exception e) {
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), e.getMessage());
//        }
//    }
//}
