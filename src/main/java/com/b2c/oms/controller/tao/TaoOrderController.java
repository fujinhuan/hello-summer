//package com.b2c.oms.controller.tao;
//
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.UserEntity;
//import com.b2c.entity.datacenter.DcTmallOrderEntity;
//import com.b2c.entity.datacenter.DcTmallOrderItemEntity;
//import com.b2c.entity.pdd.OrderPddEntity;
//import com.b2c.entity.pdd.OrderPddItemEntity;
//import com.b2c.entity.result.ResultVo;
//import com.b2c.enums.EnumShopType;
//import com.b2c.enums.third.EnumTmallOrderStatus;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.oms.request.OrderConfirmReq;
//import com.b2c.repository.utils.OrderNumberUtils;
//import com.b2c.service.erp.ErpGoodsService;
//import com.b2c.service.oms.DcTmallOrderService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.mall.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * 淘系 订单管理
// */
//@RequestMapping("/tao_order")
//@Controller
//public class TaoOrderController {
//    @Autowired
//    private DcTmallOrderService tmallOrderService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private ErpGoodsService erpGoodsService;
//    private static Logger log = LoggerFactory.getLogger(TaoOrderController.class);
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
//        if (shop.getType().intValue() != EnumShopType.Tmall.getIndex()) {
//            //并不是淘宝开放平台店铺
//            return "redirect:/";
//        }
//
//        model.addAttribute("shop", shop);
//        model.addAttribute("menuId", "order_list");
//
//        Integer status = null;
//        if (!StringUtils.isEmpty(request.getParameter("status"))) {
//            status = Integer.parseInt(request.getParameter("status"));
//            model.addAttribute("status", status);
//        }
//        String orderId = request.getParameter("orderId");
//
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//
//        if (!StringUtils.isEmpty(request.getParameter("page"))) {
//            pageIndex = Integer.parseInt(request.getParameter("page"));
//        }
//        String mobile = "";
//        if (!StringUtils.isEmpty(request.getParameter("mobile"))) mobile = request.getParameter("mobile");
//        model.addAttribute("mobile", mobile);
//
//        String startTime = "";
//        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
//            startTime = request.getParameter("startTime");
//            model.addAttribute("startTime", startTime);
//            startTime = startTime + " 00:00:00";
//        }
//        String endTime = "";
//        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
//            endTime = request.getParameter("endTime");
//            model.addAttribute("endTime", endTime);
//            endTime = endTime + " 23:59:59";
//        }
//
//        Integer developerId = 0;
//        if (!StringUtils.isEmpty(request.getParameter("developerId"))) {
//            developerId = Integer.parseInt(request.getParameter("developerId"));
//        }
//
//        //查询订单
//        var result = tmallOrderService.getList(pageIndex, pageSize, orderId, status, shopId, mobile,developerId, startTime, endTime);
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
//
//        //查询业务员
//        List<UserEntity> developerList = userService.getDeveloperList();
//        model.addAttribute("developerList", developerList);
//
//        return "v3/order_list_tao";
//    }
//
//    /**
//     * 订单详情（淘系）
//     *
//     * @param model
//     * @param id
//     * @param shopId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/detail", method = RequestMethod.GET)
//    public String orderDetailTmall(Model model, @RequestParam Long id, @RequestParam Integer shopId, HttpServletRequest request) {
//
//        DcTmallOrderEntity orderDetail = tmallOrderService.getOrderDetailAndItemsById(id);
//
//        model.addAttribute("orderVo", orderDetail);
//
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//        model.addAttribute("menuId", "order_list");
//
//        return "v3/order_detail_tao";
//    }
//
//    /**
//     * 确定订单
//     * @param model
//     * @param orderId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_confirm", method = RequestMethod.GET)
//    public String orderConfirmGet(Model model, @RequestParam Long orderId,HttpServletRequest request) {
//        var order = tmallOrderService.getOrderEntityById(orderId);
//        if (order == null) {
//            model.addAttribute("error", "没有找到订单");
//            model.addAttribute("orderVo", new DcTmallOrderEntity());
//
//        } else {
//            model.addAttribute("orderVo", order);
////            model.addAttribute("clientId", order.getClientUserId() != null ? order.getClientUserId() : 0);
//        }
//
////        model.addAttribute("clientId",181);
//
////        model.addAttribute("orderVo", service.getOrderConfirm(id, ErpOrderSourceEnum.TMALL));
//
//        //查询客户列表
//        List<UserEntity> clientList = userService.getClientUserListByType(null, null);
//        model.addAttribute("clientList", clientList);
//
//        return "v3/order_confirm_tao";
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
//    public ApiResult<String> confirmOrder(@RequestBody OrderConfirmReq req, HttpServletRequest request) {
//        if (req.getOrderId() == null || req.getOrderId() <= 0)
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");
//
////        if (req.getClientId() == null || req.getOrderId() <= 0)
////            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少clientId");
//
//        if (StringUtils.isEmpty(req.getReceiver()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少receiver");
//        if (StringUtils.isEmpty(req.getMobile()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少mobile");
//        if (StringUtils.isEmpty(req.getAddress()))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少address");
//
//
//        //查询订单信息
//        var order = tmallOrderService.getOrderEntityById(req.getOrderId());
//
//        log.info("/**********************订单状态判断" + order.getStatus() + "**********************/");
//
//        if (order == null)
//            return new ApiResult<>(404, "订单不存在");//检查是否已经确认
//        else if (order.getAuditStatus().intValue() > 0)
//            return new ApiResult<>(501, "订单已经确认过了");
//        else if (order.getStatus() != EnumTmallOrderStatus.WAIT_SEND_GOODS.getStatus())
//            return new ApiResult<>(402, "订单不是发货中的状态，不能操作");
//
////        if (StringUtils.isEmpty(order.getContactPerson())) {
////            return new ApiResult<>(408, "订单缺少收货人信息，请补全收货人信息");
////        }
//
//        //查询订单item信息
//        List<DcTmallOrderItemEntity> items = tmallOrderService.getOrderItemsByOrderId(req.getOrderId());
//        for (var item : items) {
//            if (item.getRefundStatusStr().equalsIgnoreCase("NO_REFUND") == false) {
//                return new ApiResult<>(401, "子订单处于退款状态，不能确认");
//            }
//        }
//
//
//        log.info("/**********************开始确认订单" + req.getOrderId() + "**********************/");
//        synchronized (this) {
//            //确认订单，加入到仓库系统待发货订单列表
//            ResultVo<Integer> result = tmallOrderService.orderConfirmAndJoinDeliveryQueueForTmall(req.getOrderId(), req.getClientId(), req.getReceiver(), req.getMobile(), req.getAddress(), req.getSellerMemo());
////            if (result.getCode() == 0 && data.getInt("orderType") > -1 && data.getInt("buyUserId") > 0) {
////                ResultVo<Integer> resultSub = tmallOrderService.addTmallOrderByDeveLoper(order, data.getInt("buyUserId"), data.getInt("orderType"), ErpOrderSourceEnum.TMALL);
////                return new ApiResult<>(resultSub.getCode(), resultSub.getMsg());
////            }
//            log.info("/**********************确认订单完成" + result + "**********************/");
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }
//    }
//
//    /**
//     * 创建订单
//     * @param model
//     * @param shopId
//     * @param request
//     * @return
//     */
//    @RequestMapping("/order_create")
//    public String orderCreate(Model model,@RequestParam Integer shopId, HttpServletRequest request){
//        model.addAttribute("menuId", "order_create");
//        model.addAttribute("shopId", shopId);
//        return "v3/order_create_tao";
//    }
//
//    /**
//     * pdd创建订单
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_create", method = RequestMethod.POST)
//    public String postSystemOrder(Model model, HttpServletRequest request) {
//        model.addAttribute("menuId", "order_create");
//        Integer shopId=Integer.parseInt(request.getParameter("shopId"));
//        /***商品信息****/
//        String[] specNumber = request.getParameterValues("specNumber");//规格编码组合
//        String[] goodsNumber = request.getParameterValues("goodsNumber");//商品编码组合
//        String[] goodsId = request.getParameterValues("goodsId");//商品id组合
//        String[] specsId = request.getParameterValues("specId");//商品规格id组合
//        String[] quantitys = request.getParameterValues("quantity");//数量组合
//        String[] prices = request.getParameterValues("note");//商品价格
//
//        String orderNumber = request.getParameter("orderNumber");
//        //收件人信息
//        String contactMobile = request.getParameter("contactMobile");
//        String contactPerson = request.getParameter("contactPerson");
//        String area = request.getParameter("area");
//        String address = request.getParameter("address");
//        String shippingFee= StringUtils.isEmpty(request.getParameter("shippingFee")) ? "0" :request.getParameter("shippingFee");
//        String sellerMemo = request.getParameter("sellerMemo");
//
//        String[] areaNameArray = area.split(" ");
//
//        String provinceName = "";
//        if (areaNameArray.length > 0) provinceName = areaNameArray[0];
//        String cityName = "";
//        if (areaNameArray.length > 1) cityName = areaNameArray[1];
//        String districtName = "";
//        if (areaNameArray.length > 2) districtName = areaNameArray[2];
//
//        DcTmallOrderEntity order = new DcTmallOrderEntity();
//        List<DcTmallOrderItemEntity> items = new ArrayList<>();
//        double goodsTotalAmount = 0;//商品总价
//        for (int i = 0,n=goodsId.length;i<n;i++) {
//            if(StringUtils.isEmpty(goodsId[i]))continue;
//            DcTmallOrderItemEntity pddItem = new DcTmallOrderItemEntity();
//            Integer specId=Integer.parseInt(specsId[i]);
//            BigDecimal price = new BigDecimal(prices[i]);
//            Integer count =Integer.parseInt(quantitys[i]);
//            var spec = erpGoodsService.getSpecBySpecId(specId);
//
//            goodsTotalAmount +=  price.doubleValue() * count;
//
//            pddItem.setErpGoodsId(spec.getGoodsId());
//            pddItem.setErpGoodsSpecId(spec.getId());
//            pddItem.setProductImgUrl(spec.getColorImage());
//            pddItem.setGoodsTitle(spec.getGoodTitle());
//            pddItem.setGoodsNumber(goodsNumber[i]);
//            pddItem.setPrice(new BigDecimal(price.doubleValue()));
//            pddItem.setSpecNumber(specNumber[i]);
//            pddItem.setQuantity(count.doubleValue());
//            pddItem.setSkuInfo(spec.getColorValue()+","+spec.getSizeValue());
//            pddItem.setSubItemId(orderNumber);
//            pddItem.setRefundStatusStr("NO_REFUND");
//            pddItem.setItemAmount(new BigDecimal(price.doubleValue() * count));
//            items.add(pddItem);
//        }
//        order.setItems(items);
//        double orderTotalAmount=goodsTotalAmount+Double.valueOf(shippingFee);
//        order.setId(orderNumber);
//        order.setCreateTime(new Date());
//        order.setModifyTime(new Date());
//        order.setPayTime(new Date());
//        order.setTotalAmount(new BigDecimal(orderTotalAmount));
//        order.setShippingFee(new BigDecimal(shippingFee));
//        order.setPayAmount(new BigDecimal(orderTotalAmount));
//        order.setBuyerName("");
//        order.setSellerMemo(sellerMemo);
//        order.setProvince(provinceName);
//        order.setCity(cityName);
//        order.setArea(districtName);
//        order.setAddress(new StringBuilder(provinceName).append(cityName).append(districtName).append(address).toString());
//        order.setStatus(EnumTmallOrderStatus.WAIT_SEND_GOODS.getStatus());
//        order.setStatusStr(EnumTmallOrderStatus.WAIT_SEND_GOODS.getName());
//        order.setContactPerson(contactPerson);
//        order.setMobile(contactMobile);
//
//        var result = tmallOrderService.updateTmallOrderForOpenTaobao(shopId, order);
//        if(result.getCode()==0){
//            return "redirect:/tao_order/list?shopId="+shopId;
//        }
//        return "redirect:/tao_order/order_create?shopId=" + shopId;
//
//    }
//
//    /**
//     * 退货单详情
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/refund_apply", method = RequestMethod.GET)
//    public String refundApply(Model model, @RequestParam Integer shopId, @RequestParam Long id, HttpServletRequest request) {
//        //查询店铺信息
//        DcTmallOrderEntity orderDetail = tmallOrderService.getOrderDetailAndItemsById(id);
//
//        model.addAttribute("orderVo", orderDetail);
//
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//        model.addAttribute("menuId", "order_list");
//
//        return "refund/order_refund_apply_tao";
//    }
//
//
//
//}
