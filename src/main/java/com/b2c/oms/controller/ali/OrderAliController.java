//package com.b2c.oms.controller.ali;
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import com.b2c.entity.UserEntity;
//import com.b2c.entity.alibaba.EnumAliOrderStatus;
//import com.b2c.entity.datacenter.DcAliOrderItemEntity;
//import com.b2c.entity.datacenter.DcAliOrderListVo;
//import com.b2c.entity.result.ResultVo;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.oms.request.OrderConfirmReq;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.mall.UserService;
//import com.b2c.service.oms.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
///**
// * 描述：
// * 阿里店铺订单Controller
// *
// * @author qlp
// * @date 2019-09-12 11:32
// */
//@RequestMapping("/ali_order")
//@Controller
//public class OrderAliController {
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private AliOrderService aliOrderService;
//
//    @Autowired
//    private UserService userService;
//    private static Logger log = LoggerFactory.getLogger(OrderAliController.class);
//    /**
//     * 订单列表
//     *
//     * @param model
//     * @param shopId
//     * @param request
//     * @return
//     */
//    @RequestMapping("/list")
//    public String orderList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//
//        String orderNum = "";
//        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) orderNum = request.getParameter("orderNum");
//        model.addAttribute("orderNum", orderNum);
//
//        String status = "";
//        if (!StringUtils.isEmpty(request.getParameter("state"))) status = request.getParameter("state");
//
//        String startTime = "";
//        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
//            startTime = request.getParameter("startTime");
//            model.addAttribute("startTime", startTime);
//            startTime = startTime + " 00:00:00";
//        }
//
//        String endTime = "";
//        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
//            endTime = request.getParameter("endTime");
//            model.addAttribute("endTime", endTime);
//            endTime = endTime + " 23:59:59";
//        }
//
//        String mobile = "";
//        if (!StringUtils.isEmpty(request.getParameter("mobile"))) mobile = request.getParameter("mobile");
//        model.addAttribute("mobile", mobile);
//
//
//        Integer developerId = 0;
//        if (!StringUtils.isEmpty(request.getParameter("developerId"))) {
//            developerId = Integer.parseInt(request.getParameter("developerId"));
//        }
//
//        Integer pageIndex = 1, pageSize = DataConfigObject.getInstance().getPageSize();
//        if (!StringUtils.isEmpty(request.getParameter("page"))) {
//            pageIndex = Integer.parseInt(request.getParameter("page"));
//        }
//        //查询订单
//        var result = aliOrderService.getOrders(pageIndex, pageSize, orderNum, startTime, endTime, status, shopId, mobile,developerId);
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
//
//        model.addAttribute("menuId", shop.getEname());
//
//        //查询业务员
//        List<UserEntity> developerList = userService.getDeveloperList();
//        model.addAttribute("developerList", developerList);
//        model.addAttribute("menuId", "order_list");
////        model.addAttribute("buyer", buyerOrdersService.getBuyerOrders());
//
//        return "v3/order_list_ali";
//    }
//
//    /**
//     * 订单详情（调用阿里接口获取详情）
//     *
//     * @param model
//     * @param request
//     * @return
//     */
////    @RequestMapping(value = "/aliOrderDetail_1688", method = RequestMethod.GET)
////    public String getOrderDetail(Model model, HttpServletRequest request) {
////
////        Integer shopId = 1;
////        Integer shopTypeId = 1;
////        var entity = thirdSettingService.getEntity(shopTypeId);
////
////        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
////        String returnUrl = variable.getValue() + "/ali/getToken&state=GETORDERLIST";
////
////        //不存在ali token 跳转获取
////        if (StringUtils.isEmpty(entity.getAccess_token())) {
////
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time = date.getTime(); //这是毫秒数
////        long expireTime = entity.getAccess_token_begin() * 1000 + entity.getExpires_in() * 1000;
////        if (expireTime < time) {
////            //已过期
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
////
////        //查询店铺信息
////        var shop = shopService.getShop(shopId);
////        model.addAttribute("shop", shop);
////
////
////        //设置appkey和密钥(seckey)
////        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
////
////        Long id = Long.parseLong(request.getParameter("id"));
////
////        var aliOrder = aliOrderService.getOrderById(id);
////
////        model.addAttribute("id", id);
////        AlibabaTradeGetSellerViewParam param = new AlibabaTradeGetSellerViewParam();
////        param.setOrderId(id);
////
////        //调用API并获取返回结果
////        SDKResult<AlibabaTradeGetSellerViewResult> result = apiExecutor.execute(param, entity.getAccess_token());
////        AlibabaOpenplatformTradeModelTradeInfo info = result.getResult().getResult();
////        model.addAttribute("info", info.getBaseInfo());
////        model.addAttribute("status", EnumAliOrdersPaymentState.valueOf(info.getBaseInfo().getStatus()).getIndex());
////        if (aliOrder.getOrderId().intValue() == 0) {
////            model.addAttribute("item", info.getProductItems());
////        } else {
////            model.addAttribute("item", aliOrderService.getOrderItemByOrderId(aliOrder.getOrderId()));
////        }
////        model.addAttribute("orderId", aliOrder.getOrderId());
////        BigDecimal sum = new BigDecimal(0);
////        for (int i = 0; i < info.getProductItems().length; i++) {
////            BigDecimal quantity = info.getProductItems()[i].getQuantity();
////            BigDecimal decimal = info.getProductItems()[i].getPrice();
////            sum = sum.add(quantity.multiply(decimal));
////        }
////        model.addAttribute("sum", sum);
////        model.addAttribute("native", info.getNativeLogistics());
////
////        AlibabaTradeGetLogisticsTraceInfoSellerViewParam viewParam = new AlibabaTradeGetLogisticsTraceInfoSellerViewParam();
////        viewParam.setOrderId(id);
////        viewParam.setWebSite("1688");
////        SDKResult<AlibabaTradeGetLogisticsTraceInfoSellerViewResult> execute = apiExecutor.execute(viewParam, entity.getAccess_token());
////        AlibabaLogisticsOpenPlatformLogisticsTrace[] trace = execute.getResult().getLogisticsTrace();
////        model.addAttribute("trace", trace);
////
////        model.addAttribute("menuId", shop.getEname());
////
////        return "order/order_detail_ali_1688";
////    }
//
//    /**
//     * 订单详情（本地读取）
//     *
//     * @param model
//     * @param shopId  店铺id
//     * @param id      订单id
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/detail", method = RequestMethod.GET)
//    public String getOrderDetail1(Model model, @RequestParam Long id, HttpServletRequest request) {
//        Integer shopId = 1;
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
////        var aliOrder = aliOrderService.getOrderById(id);
//        var orderDetail = aliOrderService.getOrderDetailAndItems(id);
////
////        model.addAttribute("order", orderDetail);
////
////        model.addAttribute("id", id);
//
////        DcOrderDetailVo orderDetail = dcOrderService.getDcOrderDetail(Long.parseLong(request.getParameter("id")), shopId);
//
////        var shopId1= EnumShopIdType.valueOf(request.getParameter("type"));
////        DcOrderDetailVo orderDetail1 = dcOrderService.getDcOrderDetail(Long.parseLong(request.getParameter("id")),shopId1);
//
//        model.addAttribute("order", orderDetail);
//
//        model.addAttribute("shop", shop);
//        model.addAttribute("menuId", "order_list");
//
////        return "order/order_detail_ali";
//        return "v3/order_detail_ali";
//    }
//
//    /**
//     * 阿里订单确认
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_confirm", method = RequestMethod.GET)
//    public String orderConfirmGet(Model model, @RequestParam Long orderId, HttpServletRequest request) {
//        var order = aliOrderService.getOrderDetailById(orderId);
//        if (order == null) {
//            model.addAttribute("error", "没有找到订单");
//            model.addAttribute("orderVo", new DcAliOrderListVo());
//
//        } else {
//            model.addAttribute("orderVo", order);
//        }
//
//        //查询客户列表
//        List<UserEntity> clientList = userService.getClientUserListByType(null, null);
//        model.addAttribute("clientList", clientList);
//
//        return "v3/order_confirm_ali";
//    }
//
//    /**
//     * 订单确认（进入仓库）
//     *
//     * @param req
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/order_confirm_post", method = RequestMethod.POST)
//    public ApiResult<String> completeOrder(@RequestBody OrderConfirmReq req) {
//        if (req.getOrderId() == null || req.getOrderId() <= 0)
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");
//
//        if (req.getClientId() == null || req.getOrderId() <= 0)
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少clientId");
//
//        if (StringUtils.isEmpty(req.getReceiver()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少receiver");
//        if (StringUtils.isEmpty(req.getMobile()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少mobile");
//        if (StringUtils.isEmpty(req.getAddress()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少address");
//
//        //确认订单，加入到仓库系统待发货订单列表
//        //查询订单信息
//        DcAliOrderListVo order = aliOrderService.getOrderDetailById(req.getOrderId());
//
//
//        if (order == null) return new ApiResult<>(404, "订单不存在");
//
//        //检查是否已经确认
//        if (order.getAuditStatus().intValue() > 0) return new ApiResult<>(501, "订单已经确认过了");
//
//        if (order.getStatus().equals(EnumAliOrderStatus.waitsellersend.getIndex()) == false)
//            return new ApiResult<>(402, "订单状态不能操作");
//
//
//        //查询订单item信息
//        List<DcAliOrderItemEntity> items = aliOrderService.getItemsByOrderId(req.getOrderId());
//        for (var item : items) {
//            if (item.getStatus().equals(EnumAliOrderStatus.waitsellersend.getIndex()) == false)
//                return new ApiResult<>(401, "子订单状态不能操作");
//            if (StringUtils.isEmpty(item.getRefundStatus()) == false) return new ApiResult<>(400, "子订单有退款");
//        }
//        log.info("/**********************开始确认订单" + req.getOrderId() + "**********************/");
//
//        /**非采购订单确认**/
////        if (order.getOrderId().intValue() == 0) {
//        synchronized (this) {
//            //修改地址信息
//            ResultVo<Integer> result = aliOrderService.orderConfirmAndJoinDeliveryQueueForAli(req.getOrderId(), req.getClientId(), req.getReceiver(), req.getMobile(), req.getAddress(), req.getSellerMemo());
//
//            log.info("/**********************确认订单完成" + result.getMsg() + "**********************/");
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }
////        } else {
////            ResultVo<Integer> resultVo = dcOrderService.orderConfirmAndJoinDeliveryQueueForYgPiFa(order.getOrderId());
////            if (resultVo.getCode() == 0 && data.getInt("orderType") > -1 && data.getInt("buyUserId") > 0) {
////                ResultVo<Integer> resultSub = aliOrderService.addAliOrderByOrder(order, data.getInt("buyUserId"), data.getInt("orderType"), ErpOrderSourceEnum.ALIBABA);
////                return new ApiResult<>(resultSub.getCode(), resultSub.getMsg());
////            }
////            return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());
////        }
//    }
//}
