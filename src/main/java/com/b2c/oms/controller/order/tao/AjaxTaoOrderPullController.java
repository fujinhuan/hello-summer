//package com.b2c.oms.controller.order.tao;
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.datacenter.DcSysThirdSettingEntity;
//import com.b2c.entity.datacenter.DcTmallOrderEntity;
//import com.b2c.entity.datacenter.DcTmallOrderItemEntity;
//import com.b2c.entity.datacenter.DcTmallOrderRefundEntity;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.enums.third.EnumTmallOrderStatus;
//import com.b2c.service.erp.ErpSalesOrderService;
//import com.b2c.service.oms.DcTmallOrderReturnService;
//import com.b2c.service.oms.DcTmallOrderService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.RefundGetRequest;
//import com.taobao.api.request.TradeFullinfoGetRequest;
//import com.taobao.api.request.UserSellerGetRequest;
//import com.taobao.api.response.RefundGetResponse;
//import com.taobao.api.response.TradeFullinfoGetResponse;
//import com.taobao.api.response.UserSellerGetResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 淘系订单更新
// */
//@RestController
//public class AjaxTaoOrderPullController {
//    private static Logger log = LoggerFactory.getLogger(AjaxTaoOrderPullController.class);
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private DcTmallOrderService tmallOrderService;
//    @Autowired
//    private DcTmallOrderReturnService tmallOrderReturnService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private ErpSalesOrderService salesOrderService;
//
//    /**
//     * 更新前的检查
//     *
//     * @param shopId
//     * @return
//     * @throws ApiException
//     */
//    public ApiResult<DcSysThirdSettingEntity> checkBefore(Integer shopId) throws ApiException {
//        log.info("/**************主动更新tao 参数检查****************/");
//        var shop = shopService.getShop(shopId);
//
//        if (shop == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有找到店铺");
//        else if (shop.getType().intValue() != 4)//EnumShopType.Tmall.getIndex()
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，店铺不是淘系店铺");
//        else if (StringUtils.isEmpty(shop.getSessionKey()))
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新授权");
//
//        String sessionKey = shop.getSessionKey();
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
//        thirdConfig.setAccess_token(sessionKey);
//
//        String url = thirdConfig.getRequest_url();
//        String appkey = thirdConfig.getAppKey();
//        String secret = thirdConfig.getAppSecret();
//
//        /****************先查询卖家对不对***************/
//        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//        UserSellerGetRequest reqSeller = new UserSellerGetRequest();
//        reqSeller.setFields("nick,user_id");
//        UserSellerGetResponse rsp = client.execute(reqSeller, sessionKey);
////        System.out.println(rsp.getBody());
//        if (shop.getSellerUserId().longValue() != rsp.getUser().getUserId().longValue()) {
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "当前用户是：" + rsp.getUser().getNick() + "，请重新授权");
//        }
//
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", thirdConfig);
//    }
//
//    /**
//     * 拉取天猫订单
//     *
//     * @param model
//     * @param request
//     * @return
//     * @throws ApiException
//     */
//    @RequestMapping("/ajax_tao/pull_order")
//    @ResponseBody
//    public ApiResult<String> orderPull(@RequestBody TaoRequest req, Model model, HttpServletRequest request) throws Exception {
//        log.info("/**************主动更新tao订单****************/");
//        if (req.getShopId() == null || req.getShopId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有店铺Id");
//        }
//        var checkResult = this.checkBefore(req.getShopId());
//        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
//            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
//        }
//        String sessionKey = checkResult.getData().getAccess_token();
//        String url = checkResult.getData().getRequest_url();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//
//        //查询上次更新信息,默认查 23小时内
//
//        Long startTime = System.currentTimeMillis() / 1000 - (60 * 60 * 24 * 1);//1天前
//        Long endTime = System.currentTimeMillis() / 1000;
//
//        var orderLog = salesOrderService.getErpOrderPullLogByShopId(req.getShopId(),0);
//        if(orderLog != null){
//            startTime = orderLog.getEndTime() -  60 * 60;
//            endTime = startTime + (60 * 60 * 24 * 1) - 60*10;//结束时间+24小时
//        }
//
//
//        //更新类型
//
//
//        log.info("/**************主动更新tao订单，条件判断完成，开始更新。。。。。。****************/");
//        Long pageSize = 50l;
//        Long pageIndex = 1l;
//
//        TaoBaoOpenOrderUpdResult<DcTmallOrderEntity> upResult=null;//更新结果
//        //第一次获取
//        if(req.getUpdType()==null || req.getUpdType().intValue() == 0) {
//            //拉取24小时内的新订单
//            upResult = TaoBaoOpenOrderUpdHelper.getIncrementTmallOrder(pageIndex, pageSize,DateUtil.unixTimeToDate(startTime),DateUtil.unixTimeToDate(endTime), url, appKey, appSecret, sessionKey);
//        }
//        else{
//            //更新30天内的订单（主要用于订单状态更新）
//            upResult = TaoBaoOpenOrderUpdHelper.updTmallOrder(pageIndex, pageSize, url, appKey, appSecret, sessionKey);
//        }
//
//
//        if (upResult.getCode().intValue() != 0) {
//            log.info("/**************主动更新tao订单：第一次获取结果失败：" + upResult.getMsg() + "****************/");
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), upResult.getMsg());
//        }
//
//        log.info("/**************主动更新tao订单：第一次获取结果：总记录数" + upResult.getTotalRecords() + "****************/");
//        int insertSuccess = 0;//新增成功的订单
//        int totalError = 0;
//        int hasExistOrder = 0;//已存在的订单数
//
//        //循环插入订单数据到数据库
//        for (var order : upResult.getList()) {
//
//            //插入订单数据
//            var result = tmallOrderService.updateTmallOrderForOpenTaobao(req.getShopId(), order);
//            if (result.getCode() == EnumResultVo.DataExist.getIndex()) {
//                //已经存在
//                log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "存在、更新****************/");
//                hasExistOrder++;
//            } else if (result.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//                log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "不存在、新增****************/");
//                insertSuccess++;
//            } else {
//                log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "报错****************/");
//                totalError++;
//            }
//        }
//
//        //计算总页数
//        int totalPage = (upResult.getTotalRecords() % pageSize == 0) ? upResult.getTotalRecords() / pageSize.intValue() : (upResult.getTotalRecords() / pageSize.intValue()) + 1;
//        pageIndex++;
//
//        while (pageIndex <= totalPage) {
//
//            TaoBaoOpenOrderUpdResult<DcTmallOrderEntity> upResult1 = null;// TaoBaoOpenOrderUpdHelper.getIncrementTmallOrder(pageIndex, pageSize,DateUtil.unixTimeToDate(startTime),DateUtil.unixTimeToDate(endTime), url, appKey, appSecret, sessionKey);
//
//            if(req.getUpdType()==null || req.getUpdType().intValue() == 0) {
//                //拉取24小时内的新订单
//                upResult1 = TaoBaoOpenOrderUpdHelper.getIncrementTmallOrder(pageIndex, pageSize,DateUtil.unixTimeToDate(startTime),DateUtil.unixTimeToDate(endTime), url, appKey, appSecret, sessionKey);
//            }
//            else{
//
//                //更新30天内的订单（主要用于订单状态更新）
//                upResult1 = TaoBaoOpenOrderUpdHelper.updTmallOrder(pageIndex, pageSize, url, appKey, appSecret, sessionKey);
//            }
//            //循环插入订单数据到数据库
//            for (var order : upResult1.getList()) {
//                //插入订单数据
//                var result = tmallOrderService.updateTmallOrderForOpenTaobao(req.getShopId(), order);
//                if (result.getCode() == EnumResultVo.DataExist.getIndex()) {
//                    //已经存在
//                    log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "存在、更新****************/");
//                    hasExistOrder++;
//                } else if (result.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//                    log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "不存在、新增****************/");
//                    insertSuccess++;
//                } else {
//                    log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "报错****************/");
//                    totalError++;
//                }
//            }
//            pageIndex++;
//        }
//
//        /*********记录更新**********/
//        salesOrderService.addErpSalesPullOrderLog(startTime,endTime,req.getShopId(),insertSuccess,totalError,hasExistOrder,0);
//
//        String msg = "成功，总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
//        log.info("/**************主动更新tao订单：END：" + msg + "****************/");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), msg);
//
//    }
//
//    /**
//     * 更新单个订单
//     *
//     * @param taoRequest
//     * @param model
//     * @param request
//     * @return
//     * @throws ApiException
//     */
//    @RequestMapping("/tao_ajax/pull_order_by_num")
//    @ResponseBody
//    public ApiResult<String> getOrderPullByNum(@RequestBody TaoRequest taoRequest, Model model, HttpServletRequest request) throws ApiException {
//        log.info("/**************主动更新tao订单by number****************/");
//        if (taoRequest.getShopId() == null || taoRequest.getShopId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有店铺Id");
//        }
//        if (taoRequest.getOrderId() == null || taoRequest.getOrderId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少orderId");
//        }
//
//        Integer shopId = taoRequest.getShopId();
//        var checkResult = this.checkBefore(shopId);
//
//        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
//            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
//        }
//
//        String sessionKey = checkResult.getData().getAccess_token();
//        String url = checkResult.getData().getRequest_url();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//
//        TaobaoClient client = new DefaultTaobaoClient(url, appKey, appSecret);
//
//        TradeFullinfoGetRequest req = new TradeFullinfoGetRequest();
////        req.setFields("tid,type,status,payment,orders,promotion_details,post_fee");
//        req.setFields("tid,post_fee,receiver_name,receiver_state,receiver_city,receiver_district,receiver_address,receiver_mobile,receiver_phone,received_payment,num," +
//                "type,status,payment,orders,rx_audit_status,sellerMemo,pay_time,created,buyer_nick");
//        req.setTid(taoRequest.getOrderId());
//        TradeFullinfoGetResponse rsp = client.execute(req, sessionKey);
//        System.out.println(rsp.getBody());
//
//        var trade = rsp.getTrade();
//        if (trade == null) {
//            //没有找到退款单
//            log.info("/**************主动更新tao订单：END： 没有找到退单****************/");
//            return new ApiResult<>(EnumResultVo.DataError.getIndex(), "没有找到退单" + taoRequest.getOrderId());
//        }
//
//        //TODO:开始更新
//        DcTmallOrderEntity order = new DcTmallOrderEntity();
//        order.setId(trade.getTid().toString());
//        order.setCreateTime(trade.getCreated());
//        order.setModifyTime(trade.getModified());
//        order.setPayTime(trade.getPayTime());
//        order.setTotalAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//        order.setShippingFee(BigDecimal.valueOf(Double.parseDouble(trade.getPostFee())));
//        order.setPayAmount(BigDecimal.valueOf(Double.parseDouble(trade.getPayment())));
//        order.setBuyerName(trade.getBuyerNick());
//        order.setSellerMemo(trade.getSellerMemo());
//        order.setProvince(trade.getReceiverState());
//        order.setCity(trade.getReceiverCity());
//        order.setArea(trade.getReceiverDistrict());
//        order.setStatus(EnumTmallOrderStatus.getStatus(trade.getStatus()));
//        order.setStatusStr(trade.getStatus());
//        List<DcTmallOrderItemEntity> items = new ArrayList<>();
//        for (var item : trade.getOrders()) {
//            DcTmallOrderItemEntity orderItem = new DcTmallOrderItemEntity();
//            orderItem.setSpecNumber(item.getOuterSkuId());
//            orderItem.setGoodsNumber(item.getOuterIid());
//            orderItem.setProductImgUrl(item.getPicPath());
//            orderItem.setGoodsTitle(item.getTitle());
//            orderItem.setPrice(BigDecimal.valueOf(Double.parseDouble(item.getPrice())));
//            orderItem.setQuantity(item.getNum().doubleValue());
//            orderItem.setSubItemId(item.getOid());
//            orderItem.setSkuInfo(item.getSkuPropertiesName());
//            orderItem.setItemAmount(BigDecimal.valueOf(Double.parseDouble(item.getPayment())));
//            orderItem.setDiscountFee(new BigDecimal(item.getDiscountFee()));
//            orderItem.setAdjustFee(new BigDecimal(item.getAdjustFee()));
//
//            orderItem.setRefundStatusStr(item.getRefundStatus());
//            items.add(orderItem);
//        }
//        order.setItems(items);
//
//        var result = tmallOrderService.updateTmallOrderForOpenTaobao(taoRequest.getShopId(), order);
//        if (result.getCode() == EnumResultVo.DataExist.getIndex()) {
//            //已经存在
//            log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "存在、更新****************/");
//        } else if (result.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//            log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "不存在、新增****************/");
//        } else {
//            log.info("/**************主动更新tao订单：开始更新数据库：" + order.getId() + "报错****************/");
//        }
//        String msg = "";
//
//        log.info("/**************主动更新tao订单：END：" + msg + "****************/");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), msg);
//    }
//
//    /**
//     * 更新退货订单
//     *
//     * @param model
//     * @param request
//     * @return
//     * @throws ApiException
//     */
//    @RequestMapping("/ajax_tao/pull_refund")
//    @ResponseBody
//    public ApiResult<String> refundOrderPull(@RequestBody TaoRequest taoRequest, Model model, HttpServletRequest request) throws ApiException {
//        log.info("/**************主动更新tao退货订单****************/");
//        if (taoRequest.getShopId() == null || taoRequest.getShopId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有店铺Id");
//        }
//        Integer shopId = taoRequest.getShopId();
//        var checkResult = this.checkBefore(shopId);
//
//        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
//            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
//        }
//
//        String sessionKey = checkResult.getData().getAccess_token();
//        String url = checkResult.getData().getRequest_url();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//
//
//        Long pageSize = 50l;
//        Long pageIndex = 1l;
//
//        //第一次获取
//        TaoBaoOpenOrderUpdResult<DcTmallOrderRefundEntity> upResult = TaoBaoOpenOrderUpdHelper.updTmallRefunOrder(pageIndex, pageSize, url, appKey, appSecret, sessionKey);
//
//        if (upResult.getCode().intValue() != 0) {
//            log.info("/**************主动更新tao退货订单：第一次获取结果失败：" + upResult.getMsg() + "****************/");
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), upResult.getMsg());
//        }
//
//        log.info("/**************主动更新tao退货订单：第一次获取结果：总记录数" + upResult.getTotalRecords() + "****************/");
//        int insertSuccess = 0;//新增成功的订单
//        int totalError = 0;
//        int hasExistOrder = 0;//已存在的订单数
//
//        //循环插入订单数据到数据库
//        for (var order : upResult.getList()) {
//
//            //插入订单数据
//            var result = tmallOrderReturnService.updOrderRefund(shopId, order);
//            if (result == EnumResultVo.DataExist.getIndex()) {
//                //已经存在
//                log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "存在、更新****************/");
//                hasExistOrder++;
//            } else if (result == EnumResultVo.SUCCESS.getIndex()) {
//                log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "不存在、新增****************/");
//                insertSuccess++;
//            } else {
//                log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "报错****************/");
//                totalError++;
//            }
//        }
//
//
//        //计算总页数
//        int totalPage = (upResult.getTotalRecords() % pageSize == 0) ? upResult.getTotalRecords() / pageSize.intValue() : (upResult.getTotalRecords() / pageSize.intValue()) + 1;
//        pageIndex++;
//
//        while (pageIndex <= totalPage) {
//            TaoBaoOpenOrderUpdResult<DcTmallOrderRefundEntity> upResult1 = TaoBaoOpenOrderUpdHelper.updTmallRefunOrder(pageIndex, pageSize, url, appKey, appSecret, sessionKey);
//            //循环插入订单数据到数据库
//            for (var order : upResult1.getList()) {
//
//                //插入订单数据
//                var result1 = tmallOrderReturnService.updOrderRefund(shopId, order);
//                if (result1 == EnumResultVo.DataExist.getIndex()) {
//                    //已经存在
//                    log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "存在、更新****************/");
//                    hasExistOrder++;
//                } else if (result1 == EnumResultVo.SUCCESS.getIndex()) {
//                    log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "不存在、新增****************/");
//                    insertSuccess++;
//                } else {
//                    log.info("/**************主动更新tao退货订单：开始更新数据库：" + order.getRefund_id() + "报错****************/");
//                    totalError++;
//                }
//            }
//            pageIndex++;
//        }
//
//        String msg = "成功，总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
//        log.info("/**************主动更新tao订单：END：" + msg + "****************/");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), msg);
//    }
//
//    /**
//     * 更新单条退货单
//     *
//     * @param taoRequest
//     * @param model
//     * @param request
//     * @return
//     * @throws ApiException
//     */
//    @RequestMapping("/tao_ajax/pull_refund_order_by_num")
//    @ResponseBody
//    public ApiResult<String> refundOrderPullByNum(@RequestBody TaoRequest taoRequest, Model model, HttpServletRequest request) throws ApiException {
//        log.info("/**************主动更新tao退货订单by number****************/");
//        if (taoRequest.getShopId() == null || taoRequest.getShopId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有店铺Id");
//        }
//        if (taoRequest.getOrderId() == null || taoRequest.getOrderId() <= 0) {
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少退款单号");
//        }
//
//        Integer shopId = taoRequest.getShopId();
//        var checkResult = this.checkBefore(shopId);
//
//        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
//            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
//        }
//
//        String sessionKey = checkResult.getData().getAccess_token();
//        String url = checkResult.getData().getRequest_url();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//
//        TaobaoClient client = new DefaultTaobaoClient(url, appKey, appSecret);
//
//        RefundGetRequest req1 = new RefundGetRequest();
//        req1.setFields("refund_id, alipay_no, tid, oid, buyer_nick, seller_nick, total_fee, status, created, refund_fee, good_status, " +
//                "has_good_return, payment, reason, desc,order_status, num_iid, title, price, num, good_return_time, company_name, sid, address, " +
//                "shipping_type, refund_remind_timeout, refund_phase, refund_version, operation_contraint, attribute, outer_id, sku");
//        req1.setRefundId(taoRequest.getOrderId());
//        RefundGetResponse rsp1 = client.execute(req1, sessionKey);
//        System.out.println(rsp1.getBody());
//
//        var item = rsp1.getRefund();
//        if (item == null) {
//            //没有找到退款单
//            log.info("/**************主动更新tao订单：END： 没有找到退单****************/");
//            return new ApiResult<>(EnumResultVo.DataError.getIndex(), "没有找到退单" + taoRequest.getOrderId());
//        }
//
//        if (item.getHasGoodReturn()) {
//            log.info("/**************主动更新tao退货订单：买家需要退货，处理快递公司和快递单号处理*********************/");
//            //买家需要退货，处理快递公司和快递单号处理
//            if (StringUtils.isEmpty(item.getCompanyName())) {
//                log.info("/**************主动更新tao退货订单：买家需要退货，处理快递公司和快递单号处理，主数据中没有快递公司和单号*********************/");
//                String companyName = "";
//                if (rsp1.getRefund().getAttribute().indexOf("logisticsCompanyName:") > -1) {
//                    Integer companyNameStart = rsp1.getRefund().getAttribute().indexOf("logisticsCompanyName:") + 21;
//                    companyName = rsp1.getRefund().getAttribute().substring(companyNameStart, companyNameStart + 4);
//                }
//                String logisticsCode = "";
//                if (rsp1.getRefund().getAttribute().indexOf("logisticsOrderCode:") > -1) {
//                    Integer logisticsCodeStart = rsp1.getRefund().getAttribute().indexOf("logisticsOrderCode:") + 19;
//                    logisticsCode = rsp1.getRefund().getAttribute().substring(logisticsCodeStart, logisticsCodeStart + 15);
//                }
//                if (StringUtils.isEmpty(companyName) == false) {
//                    item.setCompanyName(companyName);
//                }
//
//                if (StringUtils.isEmpty(logisticsCode) == false) {
//                    if (logisticsCode.indexOf(";") >= 0) {
//                        logisticsCode = logisticsCode.substring(0, logisticsCode.indexOf(";"));
//                    }
//                    item.setSid(logisticsCode);
//                }
//            }
//        }
//        DcTmallOrderRefundEntity tmallOrderRefund = new DcTmallOrderRefundEntity();
//        tmallOrderRefund.setBuyer_nick(item.getBuyerNick());
//        tmallOrderRefund.setCreated(DateUtil.dateToStamp(item.getCreated()));
//        tmallOrderRefund.setDesc(item.getDesc());
//        tmallOrderRefund.setGood_status(item.getGoodStatus());
//        tmallOrderRefund.setHas_good_return(item.getHasGoodReturn() == true ? 1 : 0);
//        tmallOrderRefund.setLogisticsCode(item.getSid());
//        tmallOrderRefund.setLogisticsCompany(item.getCompanyName());
//        tmallOrderRefund.setModified(DateUtil.dateToStamp(item.getModified()));
//        tmallOrderRefund.setOid(item.getOid());
//        tmallOrderRefund.setOrder_status(item.getOrderStatus());
//        tmallOrderRefund.setReason(item.getReason());
//        tmallOrderRefund.setRefund_fee(item.getRefundFee());
//        tmallOrderRefund.setRefund_id(item.getRefundId());
//        tmallOrderRefund.setStatus(item.getStatus());
//        tmallOrderRefund.setTid(item.getTid());
//        tmallOrderRefund.setTotal_fee(item.getTotalFee());
//        tmallOrderRefund.setNum(item.getNum());
//        tmallOrderRefund.setRefundPhase(item.getRefundPhase());
//
//        //插入订单数据
//        var result1 = tmallOrderReturnService.updOrderRefund(shopId, tmallOrderRefund);
//        String msg = "SUCCESS";
//        if (result1 == EnumResultVo.DataExist.getIndex()) {
//            //已经存在
//            log.info("/**************主动更新tao退货订单：开始更新数据库：" + tmallOrderRefund.getRefund_id() + "存在、更新****************/");
//            msg = "更新成功";
//        } else if (result1 == EnumResultVo.SUCCESS.getIndex()) {
//            log.info("/**************主动更新tao退货订单：开始更新数据库：" + tmallOrderRefund.getRefund_id() + "不存在、新增****************/");
//            msg = "新增成功";
//        } else {
//            log.info("/**************主动更新tao退货订单：开始更新数据库：" + tmallOrderRefund.getRefund_id() + "报错****************/");
//            msg = "更新报错";
//        }
//
//
//        log.info("/**************主动更新tao订单：END：" + msg + "****************/");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), msg);
//    }
//
//
//}