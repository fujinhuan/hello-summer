//package com.b2c.oms.controller.ali;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.ocean.rawsdk.ApiExecutor;
//import com.alibaba.ocean.rawsdk.common.SDKResult;
//import com.alibaba.trade.param.AlibabaOpenplatformTradeModelTradeInfo;
//import com.alibaba.trade.param.AlibabaTradeGetSellerOrderListParam;
//import com.alibaba.trade.param.AlibabaTradeGetSellerOrderListResult;
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
//import com.b2c.oms.controller.tao.TaoRequest;
//import com.b2c.oms.reponse.UpdateAliGoodsResult;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * 阿里订单更新
// */
//@RequestMapping("/ali_ajax")
//@RestController
//public class AjaxAliOrderPullController {
//    private static Logger log = LoggerFactory.getLogger(AjaxAliOrderPullController.class);
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    @Autowired
//    private SystemService systemService;
//
//    /**
//     * 获取订单列表
//     */
//    @RequestMapping("/pull_order")
//    public ApiResult<Object> orderPull(Model model, HttpServletRequest req) throws IOException, InterruptedException {
////        String code = request.getParameter("code");
////        SysVariableEntity variable = systemRepository.getVariable(VariableEnums.URL_MANAGE_INDEX.name());//获取redirectUrl
////        String redirectUrl = variable.getValue() + "/redirect_token";
////        TokenVo tokenVo = AliClient.getTokenVo(code, redirectUrl);
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
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
//            //已过期
////                return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//
//        try {
//            int pageSize = 20;
//            int pageIndex = 1;
//            int totalSuccess = 0;
//            int totalError = 0;
//
//            //第一次获取
//            UpdateAliGoodsResult upResult = updAliOrder(pageIndex, 20, settingEntity.getAccess_token());
//            if (upResult.getCode().intValue() == 401) {
//                //没有授权；
//                return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//            }
//            totalSuccess += upResult.getTotalSuccess();
//            totalError += upResult.getTotalError();
//            //计算总页数
//            int totalPage = (upResult.getTotalRecords() % pageSize == 0) ? upResult.getTotalRecords() / pageSize : (upResult.getTotalRecords() / pageSize) + 1;
//            pageIndex++;
//
//            while (pageIndex <= totalPage) {
//
//                UpdateAliGoodsResult upResult1 = updAliOrder(pageIndex, 20, settingEntity.getAccess_token());
//                totalSuccess += upResult1.getTotalSuccess();
//                totalError += upResult1.getTotalError();
//                pageIndex++;
//            }
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功，总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + totalSuccess + "条，修改：" + totalError + "条");
//        } catch (Exception e) {
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统异常" + e.getMessage());
//        }
//
//
//    }
//
//    /**
//     * 更新阿里订单（循环分页）
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param token
//     * @return
//     */
//    private UpdateAliGoodsResult updAliOrder(Integer pageNo, Integer pageSize, String token) {
//
//        Integer totalSuccess = 0;//更新成功数量
//        Integer totalError = 0;//更新失败数量
//
//
//        ApiExecutor apiExecutor = new ApiExecutor("gw.open.1688.com", 80, 443, AliClient.getAppId(), AliClient.getAppSecret());
//
//        AlibabaTradeGetSellerOrderListParam param = new AlibabaTradeGetSellerOrderListParam();
//
//        //订单修改时间（默认30天前到今天）
//        Date modifyEndTime = new Date();//结束时间
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(modifyEndTime);
//        calendar.add(Calendar.DATE, -50);
////        Date modifyStartTime = calendar.getTime();//开始时间
////
////        calendar.add(Calendar.DATE, 1);
////        Date modifyEndTime1  =  calendar.getTime();
//        //设置阿里参数
////        param.setModifyStartTime(modifyStartTime);
////        param.setModifyEndTime(modifyEndTime1);
////        param.setCreateStartTime(modifyStartTime);
////        param.setCreateEndTime(modifyEndTime1);
////        param.setBuyerMemberId("wntqrsc44");
//
//        param.setPage(pageNo);
//        param.setPageSize(pageSize);
//        param.setNeedBuyerAddressAndPhone(true);
//        param.setNeedMemoInfo(true);
//
//        SDKResult<AlibabaTradeGetSellerOrderListResult> result = apiExecutor.execute(param, token);
//        if (StringUtils.isEmpty(result.getErrorCode()) == false) {
//            //有错误
//            if (result.getErrorCode().equalsIgnoreCase("401")) {
//                //没有授权
//                return new UpdateAliGoodsResult(401, "阿里授权到期，请重新授权", 0, totalSuccess, totalError);
//
//            } else {
//                return new UpdateAliGoodsResult(500, "未知错误", 0, totalSuccess, totalError);
//            }
//        }
//
//        if (result.getResult() == null) {
//            return new UpdateAliGoodsResult(500, "没有获取到数据", 0, totalSuccess, totalError);
//        }
//
//        //有订单,循环处理订单
//        for (AlibabaOpenplatformTradeModelTradeInfo tradeInfo : result.getResult().getResult()) {
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
//            /********阿里订单数据转换End********/
//
//            //根据订单号查询数据库
//            DcAliOrderEntity aliOrder = aliOrderService.getOrderById(tradeInfo.getBaseInfo().getId());
//            if (aliOrder == null) {
//                //不存在，新增
//                aliOrderService.insert(orderEntity, orderUser, orderAddress, orderItems);
//                totalSuccess++;
//            } else {
//                //存在，修改
//                aliOrderService.update(orderEntity, orderUser, orderAddress, orderItems);
//                totalError++;
//            }
//
////            aliOrderService.editErpSalesOrder(orderEntity, orderUser, orderAddress, orderItems);
//        }
//
//        return new UpdateAliGoodsResult(0, "", result.getResult().getTotalRecord().intValue(), totalSuccess, totalError);
//    }
//
//    /**
//     * 单条更新订单
//     *
//     * @param req
//     * @param model
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @RequestMapping("/pull_order_by_id")
//    public ApiResult<Object> orderUpdate(@RequestBody TaoRequest req, Model model) throws IOException, InterruptedException {
//        if (req.getOrderId() == null || req.getOrderId() <= 0)
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少orderId");
////        String code = request.getParameter("code");
////        SysVariableEntity variable = systemRepository.getVariable(VariableEnums.URL_MANAGE_INDEX.name());//获取redirectUrl
////        String redirectUrl = variable.getValue() + "/redirect_token";
////        TokenVo tokenVo = AliClient.getTokenVo(code, redirectUrl);
//
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
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
//            //已过期
////                return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//        log.info("/**********************开始确认订单" + req.getOrderId() + "**********************/");
//        /**更新阿里订单最新信息到平台**/
//
//        try {
////            ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//            ApiExecutor apiExecutor = new ApiExecutor("gw.open.1688.com", 80, 443, AliClient.getAppId(), AliClient.getAppSecret());
//            AlibabaTradeGetSellerViewParam param = new AlibabaTradeGetSellerViewParam();
//            param.setOrderId(req.getOrderId());
//            var result = apiExecutor.execute(param, settingEntity.getAccess_token());
//
//            log.info("获取阿里接口订单详情：" + result.getResult().getSuccess());
//
//            AlibabaOpenplatformTradeModelTradeInfo tradeInfo = result.getResult().getResult();
//
//            log.info("/**********************更新阿里订单详情数据" + tradeInfo.getBaseInfo().getId() + "**********************/");
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
//
//            log.info("/**********************更新阿里订单成功**********************/");
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
//        } catch (Exception e) {
//            log.info("/**********************更新阿里订单异常：" + e.getMessage() + "**********************/");
//            return new ApiResult<>(EnumResultVo.DataError.getIndex(), "更新阿里订单异常" + e.getMessage());
//        }
//
//    }
//}
