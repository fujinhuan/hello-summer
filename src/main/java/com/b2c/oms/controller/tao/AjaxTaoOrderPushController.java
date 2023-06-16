//package com.b2c.oms.controller.tao;
//
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.enums.EnumShopType;
//import com.b2c.service.oms.DcTmallOrderService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.LogisticsOfflineSendRequest;
//import com.taobao.api.response.LogisticsOfflineSendResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 淘系订单状态推送
// */
//@RestController
//public class AjaxTaoOrderPushController {
//    private static Logger log = LoggerFactory.getLogger(AjaxTaoOrderPushController.class);
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private DcTmallOrderService tmallOrderService;
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//
//    /**
//     * 订单物流信息推送
//     *
//     * @param req
//     * @param model
//     * @return
//     */
//    @RequestMapping("/tao_ajax/push_order_logistics")
//    public ApiResult<String> orderPull(@RequestBody TaoRequest req, Model model) {
//        if (req.getOrderId() == null || req.getOrderId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少orderId");
//        }
//        if (req.getShopId() == null || req.getShopId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少shopId");
//        }
//
//        var shop = shopService.getShop(req.getShopId());
//
//        if (shop == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有找到店铺");
//        else if (shop.getType().intValue() != EnumShopType.Tmall.getIndex())
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，店铺不是淘系店铺");
//        else if (StringUtils.isEmpty(shop.getSessionKey()))
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新授权");
//
//
//        var thirdConfig = thirdSettingService.getEntity(shop.getType());
//        if (thirdConfig == null) return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，没有找到第三方平台的配置信息");
//        else if (StringUtils.isEmpty(thirdConfig.getAppKey()))
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少appkey");
//        else if (StringUtils.isEmpty(thirdConfig.getAppSecret()))
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少appSecret");
//        else if (StringUtils.isEmpty(thirdConfig.getRequest_url()))
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少request_url");
//
//        //查询订单信息
//        var order = tmallOrderService.getOrderEntityById(req.getOrderId());
//        if(order == null ) return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，订单不存在");
//        else if(StringUtils.isEmpty(order.getLogisticsCode()))  return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，没有查到物流信息");
//
//        String sessionKey = shop.getSessionKey();
//        String url = thirdConfig.getRequest_url();
//        String appkey = thirdConfig.getAppKey();
//        String secret = thirdConfig.getAppSecret();
//
//        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//        LogisticsOfflineSendRequest request = new LogisticsOfflineSendRequest();
//
//        request.setTid(req.getOrderId());
//        request.setIsSplit(0L);
//        request.setOutSid(order.getLogisticsCode());
//        request.setCompanyCode(order.getLogisticsCompanyCode().toUpperCase());
////        request.setSenderId(123456L);
////        request.setCancelId(123456L);
////        request.setFeature("identCode=tid:aaa,bbb;machineCode=tid2:aaa;retailStoreId=12345;retailStoreType=STORE");
////        request.setSellerIp("192.168.1.10");
//        LogisticsOfflineSendResponse rsp = null;
//        try {
//            rsp = client.execute(request, sessionKey);
//            if(StringUtils.isEmpty(rsp.getErrorCode())==false){
//                return new ApiResult<>(EnumResultVo.SystemException.getIndex(), rsp.getSubMsg());
//            }
//
//        } catch (ApiException e) {
//            log.info("/****************tao发货错误：" + e.getErrMsg() + "*******************/");
//            e.printStackTrace();
//        }
//        System.out.println(rsp.getBody());
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
//    }
//}
