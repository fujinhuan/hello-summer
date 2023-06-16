//package com.b2c.oms.controller.mogujie;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.express.KuaiDi100ExpressClient;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.common.utils.MD5Utils;
//import com.b2c.entity.DataRow;
//import com.b2c.entity.erp.vo.ErpSalesPullCountResp;
//import com.b2c.entity.mogujie.DcOrderMogujieStatusEnum;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.oms.controller.mogujie.pojo.ItemOrderInfo;
//import com.b2c.oms.controller.mogujie.pojo.OpenApiOrderDetailResDto;
//import com.b2c.service.erp.ErpSalesOrderService;
//import com.b2c.service.oms.OrderMogujieService;
//import com.b2c.service.oms.OrderPddService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import com.b2c.vo.OmsCreateOrderItemVo;
//import com.b2c.vo.OmsCreateOrderVo;
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
//import java.net.http.HttpResponse;
//import java.util.*;
//
//import static com.b2c.common.utils.HttpUtil.map2Url;
//
//@RequestMapping("/ajax_mogujie")
//@RestController
//public class AjaxMogujieOrderPullController {
//    private static Logger log = LoggerFactory.getLogger(AjaxMogujieOrderPullController.class);
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private OrderMogujieService orderService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private ErpSalesOrderService salesOrderService;
//
//    /**
//     * 接口拉取订单
//     * @param reqData
//     * @param req
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/pull_order", method = RequestMethod.POST)
//    public ApiResult<ErpSalesPullCountResp> getOrderList(@RequestBody DataRow reqData, HttpServletRequest req) throws Exception {
//        Integer updType = reqData.getInt("updType");//更新类型0拉取新订单1更新订单
//
//        String startDate = reqData.getString("startTime");
//        String endDate = reqData.getString("endTime");
//
//        Integer shopId = 12;//蘑菇街shopId
//
//        ApiResult<ErpSalesPullCountResp> result = null;//返回结果
//
//        Long startTime = 0L;//订单更新开始时间
//        Long endTime = System.currentTimeMillis() / 1000;//订单更新结束时间
//
//        if (StringUtils.isEmpty(startDate)) {
//            //没有选择开始日期
//            var pullOrderLog = salesOrderService.getErpOrderPullLogByShopId(shopId, 1);
//            if (pullOrderLog != null) {
//                startTime = pullOrderLog.getEndTime() - 60 * 10;
////                endTime = startTime + 60 * 30;
////                if (endTime > System.currentTimeMillis() / 1000) {
////                    //如果结束时间大于当前时间，那么将开始时间和结算时间改成当前24小时内
////                    endTime = System.currentTimeMillis() / 1000;
////                    startTime = endTime - 60 * 60 * 24;
////                }
//            } else {
//                startTime = endTime - 60*60*24*7;//七天前
//
//            }
//        } else {
//            //选择了开始日期,从开始时间直接循环更新到结束时间
//            startTime = DateUtil.dateToStamp(startDate).longValue();
//
//            if (StringUtils.isEmpty(endDate)) endTime = System.currentTimeMillis() / 1000;
//            else endTime = DateUtil.dateTimeToStamp(endDate + " 23:59:00").longValue();
//        }
//
//
//
//        log.info("开始循环更新蘑菇街订单。开始时间：" + DateUtil.unixTimeStampToDate(startTime) + "结束时间：" + DateUtil.unixTimeStampToDate(endTime)  );
//        int updCount = 0;
//        int insertCount = 0;
//        int failCount = 0;
//
//        result = this.orderPull(startTime, endTime, shopId);
//
////        ErpSalesPullCountResp resp = new ErpSalesPullCountResp();//返回结果
////        resp.setStartTime(DateUtil.unixTimeStampToDate(startTime));
////        resp.setEndTime(DateUtil.unixTimeStampToDate(endTime));
////        resp.setAddCount(insertCount);
////        resp.setFailCount(failCount);
////        resp.setUpdCount(updCount);
//        try {
//            //添加更新日志
//            salesOrderService.addErpSalesPullOrderLog(startTime, endTime, shopId, result.getData().getAddCount(), result.getData().getFailCount(), result.getData().getUpdCount(), updType);
//        } catch (Exception e) {
//            log.info("添加更新日志错误");
//        }
//        if (result.getCode() == 0)
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS", result.getData());
//        else return new ApiResult<>(result.getCode(), result.getMsg(),result.getData());
//
//    }
//
//    /**
//     * 使用接口拉取订单
//     * @param startTime
//     * @param endTime
//     * @param shopId
//     * @return
//     * @throws Exception
//     */
//    private ApiResult<ErpSalesPullCountResp> orderPull(Long startTime, Long endTime, Integer shopId) throws Exception{
//
//        var shop = shopService.getShop(shopId);
//        //Integer shopTypeId = EnumShopType.MoGuJie.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shop.getType());
//        String appSercet = settingEntity.getAppSecret();
//        String appKey = settingEntity.getAppKey();
//        String accessToken = "";//settingEntity.getAccess_token();
//        String method = "xiaodian.trade.sold.get";
//
//        String requestUrl = settingEntity.getRequest_url();
//
//        //API参数组合
//        LinkedHashMap<String, Object> apiParams =new LinkedHashMap<>();
////
//        apiParams.put("endCreated","");
//        apiParams.put("endUpdated",DateUtil.unixTimeStampToDate2(endTime,"yyyy-MM-dd HH:mm:ss"));//截至时间
//        apiParams.put("page","1");//查询的第几页,默认值为1, 第一页下标从1开始
//        apiParams.put("pageSize","30");//每页大小 默认为10, 最大30
//        apiParams.put("startCreated","");
//        apiParams.put("startUpdated",DateUtil.unixTimeStampToDate2(startTime,"yyyy-MM-dd HH:mm:ss"));//截至时间
//
//        JSONObject jsonObject = new JSONObject(true);
//        jsonObject.putAll(apiParams);
//
//        String paramJson =jsonObject.toJSONString();
//
//        Long timestamp = System.currentTimeMillis() / 1000;
//
//        String signStr = "Params"+paramJson+"access_token"+accessToken+"app_key"+appKey+"format"+"json"+"method"+method+"sign_method"+"md5"+"timestamp"+timestamp+"version1.0";
//
//        signStr = appSercet+signStr+appSercet;
//        String sign = MD5Utils.MD5Encode(signStr).toUpperCase();
//
//        //系统参数组合
//        Map<String, String> params = new HashMap<>();
//        params.put("access_token", accessToken);
//        params.put("app_key", appKey);
//        params.put("format", "json");
//        params.put("method", method);
//        params.put("Params",paramJson);
//        params.put("sign_method", "md5");
//        params.put("timestamp", timestamp.toString());
//        params.put("version", "1.0");
//        params.put("sign", sign);
//
//        //请求结果计算
//        int updCount =0;
//        int addCount =0;
//        int failCount = 0;
//
//        try {
//            HttpResponse<String> response = ExpressClient.doPost(requestUrl, map2Url(params));
//            if (response.statusCode() == 200) {
//                String resultStr = response.body();
//                MogujieResponse res = JSONObject.parseObject(resultStr,MogujieResponse.class);
//               if(res.getStatus().getCode().equals("0000011") || res.getStatus().getCode().equals("0000001")){
//                   return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "返回结果：" + res.getStatus().getMsg().toString());
//               }else if(res.getStatus().getCode().equals("0000000")==false) {
//                   return new ApiResult<>(EnumResultVo.Fail.getIndex(), "返回结果：" + response.body().toString());
//               }
//
//               /**************数据处理**************/
//                MogujieOrderResult orderResult = JSONObject.parseObject( res.getResult().getString("data"),MogujieOrderResult.class);
//                if(orderResult.getTotalNum() >0 ) {
//
//                    for (var dto:orderResult.getOpenApiOrderDetailResDtos()) {
//                        OmsCreateOrderVo orderVo = new OmsCreateOrderVo();
//                        orderVo.setOrderItemNumber(dto.getNumber());
//                        orderVo.setStatusStr(dto.getOrderStatus());
//                        orderVo.setStatus(DcOrderMogujieStatusEnum.getIndex(dto.getOrderStatus()));
//                        orderVo.setBuyerName(dto.getBuyerName());
//                        orderVo.setCreatedStr(dto.getCreatedStr());
//                        orderVo.setCreated(DateUtil.strToLongGo(dto.getCreatedStr()));
//                        orderVo.setPayTimeStr(dto.getPayTimeStr());
//                        orderVo.setShipTimeStr(dto.getShipTimeStr());
//                        orderVo.setReceiveTimeStr(dto.getReceiveTimeStr());
//
//                        double totalAmount = dto.getShopOrderPrice().doubleValue() / 100;
//                        orderVo.setTotalAmount(totalAmount);
//
//                        double shippingFee = dto.getShipExpense().doubleValue() / 100;
//                        orderVo.setShippingFee(shippingFee);
//
//                        double platformPromotionAmount = dto.getPlatformPromotionAmount().doubleValue() / 100;
//                        orderVo.setPlatformPromotionAmount(platformPromotionAmount);
//
//                        double promotionAmount = dto.getPromotionAmount().doubleValue() / 100;
//                        orderVo.setPromotionAmount(promotionAmount);
//
//                        double shopPromotionAmount = dto.getShopPromotionAmount().doubleValue() / 100;
//                        orderVo.setShopPromotionAmount(shopPromotionAmount);
//
//                        orderVo.setShopId(12);
//                        orderVo.setSaleType(0);
//                        orderVo.setReceiverMobile(dto.getReceiverMobile());
//                        orderVo.setReceiverName(dto.getReceiverName());
//                        orderVo.setAddress(dto.getReceiverAddress());
//                        orderVo.setProvince(dto.getReceiverProvince());
//                        orderVo.setCity(dto.getReceiverCity());
//                        orderVo.setTown(dto.getReceiverArea());
//                        orderVo.setOrderNum(dto.getShopOrderId()+"");
//
//                        /***组合子订单***/
//                        List<OmsCreateOrderItemVo> orderItems = new ArrayList<>();
//                        for (var item:dto.getItemOrderInfos()) {
//                            OmsCreateOrderItemVo orderItem = new OmsCreateOrderItemVo();
//                            orderItem.setItemOrderId(item.getItemOrderId());
//                            orderItem.setGoodsImg(item.getImgUrl());
//                            orderItem.setGoodsName(item.getTitle());
//                            orderItem.setGoodsNum(item.getItemCode());
//                            orderItem.setGoodsSpec(item.getSkuAttributes());
//                            orderItem.setGoodsSpecNum(item.getSkuCode());
//                            orderItem.setQuantity(item.getNumber());
//                            double price = item.getPrice().doubleValue() / 100.0;
//                            double nowPrice = item.getNowPrice().doubleValue() / 100.0;
//                            orderItem.setPrice(price);
//                            orderItem.setNowPrice(nowPrice);
//                            orderItem.setOrderStatus(item.getOrderStatus());
//                            orderItem.setRefundStatus(item.getRefundStatus());
//                            orderItems.add(orderItem);
//                        }
//                        orderVo.setItems(orderItems);
//
//                        //插入数据库
//                        var result =  orderService.updOrderForApi(orderVo);
//                        if(result.getCode() == EnumResultVo.DataExist.getIndex()) updCount++;
//                        else if(result.getCode() == EnumResultVo.Fail.getIndex()) failCount++;
//                        else if(result.getCode() == EnumResultVo.SUCCESS.getIndex()) addCount++;
//                    }
//
//
//                }
//
//
//            }else{
//                //请求失败
//            }
//
//            ErpSalesPullCountResp resp = new ErpSalesPullCountResp();//返回结果
//            resp.setStartTime(DateUtil.unixTimeStampToDate(startTime));
//            resp.setEndTime(DateUtil.unixTimeStampToDate(endTime));
//            resp.setAddCount(addCount);
//            resp.setFailCount(failCount);
//            resp.setUpdCount(updCount);
//
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS",resp);
//
//
//        } catch (Exception e) {
//            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常：" + e.getMessage());
//        }
//
//
//    }
//
//    /**
//     * 拼多多订单发货推送
//     * @param orderId
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/order_send", method = RequestMethod.POST)
//    public ApiResult<String> orderSend(@RequestBody Long orderId, HttpServletRequest req) {
//        return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "未实现拼多多发货推送");
//    }
//}
