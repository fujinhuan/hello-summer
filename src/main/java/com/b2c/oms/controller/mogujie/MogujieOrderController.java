//package com.b2c.oms.controller.mogujie;
//
//import com.alibaba.fastjson.JSON;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import com.b2c.common.third.thymeleaf.PagingResponse;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.mogujie.DcOrderMogujieEntity;
//import com.b2c.entity.pdd.EnumPddOrderStatus;
//import com.b2c.entity.pdd.OrderPddEntity;
//import com.b2c.entity.pdd.OrderPddItemEntity;
//import com.b2c.entity.result.ResultVo;
//import com.b2c.enums.erp.EnumErpOrderSendStatus;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.oms.request.OrderConfirmReq;
//import com.b2c.repository.utils.OrderNumberUtils;
//import com.b2c.service.erp.ErpGoodsService;
//import com.b2c.service.erp.ErpSalesOrderService;
//import com.b2c.service.mall.UserService;
//import com.b2c.service.oms.OrderMogujieService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.vo.OmsCreateOrderItemVo;
//import com.b2c.vo.OmsCreateOrderVo;
//import com.b2c.vo.erp.ErpSalesOrderDetailVo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 描述：
// *
// * @author qlp
// * @date 2019-09-24 16:05
// */
//@RequestMapping("/mogujie")
//@Controller
//public class MogujieOrderController {
//    @Autowired
//    private OrderMogujieService orderMogujieService;
//    @Autowired
//    private ErpGoodsService erpGoodsService;
//    @Autowired
//    private ShopService shopService;
//
//
//    /**
//     * 订单列表
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_list", method = RequestMethod.GET)
//    public String orderList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
//        model.addAttribute("menuId", "order_list");
//        var shop = shopService.getShop(shopId);
//
//        model.addAttribute("shop", shop);
//
//        String page = request.getParameter("page");
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        String orderNum = "";
//        String contactMobile = "";
//        Integer startTime = 0;
//        Integer endTime = 0;
//
//        Integer buyerUserId = null;
//        Integer status = null;
//
//
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//        if (!StringUtils.isEmpty(request.getParameter("pageSize"))) {
//            try {
//                pageSize = Integer.parseInt(request.getParameter("pageSize"));
//            } catch (Exception e) {
//            }
//        }
//        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
//            orderNum = request.getParameter("orderNum");
//        }
//        if (!StringUtils.isEmpty(request.getParameter("contactMobile"))) {
//            contactMobile = request.getParameter("contactMobile");
//        }
//
//        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
//            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
//        }
//        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
//            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
//        }
//
//        if (!StringUtils.isEmpty(request.getParameter("buyerUserId"))) {
//            buyerUserId = Integer.parseInt(request.getParameter("buyerUserId"));
//            model.addAttribute("buyerUserId", buyerUserId);
//        }
//        if (!StringUtils.isEmpty(request.getParameter("status"))) {
//            status = Integer.parseInt(request.getParameter("status"));
//        }
//
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
//        PagingResponse<DcOrderMogujieEntity> result = orderMogujieService.getList(pageIndex, pageSize, orderNum,status, shopId, contactMobile, startTime, endTime);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
//
//        return "v3/order_list_mogujie";
//    }
//
//    /**
//     * 订单详情
//     *
//     * @param model
//     * @param id      订单Id
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
//    public String getOrderDetail(Model model, @RequestParam Long id,@RequestParam Integer shopId, HttpServletRequest request) {
//        var orderDetail = orderMogujieService.getOrderDetailAndItem(id);
//        model.addAttribute("orderVo", orderDetail);
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//        model.addAttribute("menuId", "order_list");
//
//        return "v3/order_detail_mogujie";
//    }
//
//    /**
//     * 创建订单
//     * @param model
//     * @param shopId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_create",method = RequestMethod.GET)
//    public String orderCreate(Model model,@RequestParam Integer shopId, HttpServletRequest request){
//        model.addAttribute("orderNumber", OrderNumberUtils.getOrderIdByTime());
//        model.addAttribute("menuId", "order_create");
//        model.addAttribute("shopId", shopId);
//        return "v3/order_create_mogujie";
//    }
//
//    /**
//     * 创建订单post
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_create", method = RequestMethod.POST)
//    public String postSystemOrder(Model model, HttpServletRequest request) {
//        model.addAttribute("menuId", "order_create");
//        Integer shopId = Integer.parseInt(request.getParameter("shopId"));
//        /***商品信息****/
////        String[] specNumber = request.getParameterValues("specNumber");//规格编码组合
////        String[] goodsNumber = request.getParameterValues("goodsNumber");//商品编码组合
////        String[] goodsId = request.getParameterValues("goodsId");//商品id组合
//        String[] specsId = request.getParameterValues("specId");//商品规格id组合
//        String[] quantitys = request.getParameterValues("quantity");//数量组合
//        String[] prices = request.getParameterValues("note");//商品价格
//
//        String orderNumber = request.getParameter("orderNumber");
//        //收件人信息
//        String receiverName = request.getParameter("receiverName");
//        String receiverMobile = request.getParameter("receiverMobile");
//        String area = request.getParameter("area");
//        String address = request.getParameter("address");
//        Integer saleType = Integer.parseInt(request.getParameter("saleType"));
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
//        OmsCreateOrderVo orderVo = new OmsCreateOrderVo();
//
//        List<OmsCreateOrderItemVo> items = new ArrayList<>();
//
//        double goodsTotalAmount = 0;//商品总价
//        for (int i = 0,n=specsId.length;i<n;i++) {
//            if(StringUtils.isEmpty(specsId[i]))continue;
//            Integer quantity =Integer.parseInt(quantitys[i]);
//            //售价
//            Double nowPrice = Double.parseDouble(prices[i]);
//            goodsTotalAmount +=  nowPrice * quantity;
//
//            Integer specId=Integer.parseInt(specsId[i]);
//            //查询商品
//            var spec = erpGoodsService.getSpecBySpecId(specId);
//
//            //组合商品明细
//            OmsCreateOrderItemVo oItem = new OmsCreateOrderItemVo();
//
//            oItem.setErpGoodsId(spec.getGoodsId());
//            oItem.setErpGoodsSpecId(spec.getId());
//            oItem.setGoodsImg(spec.getColorImage());
//            oItem.setGoodsName(spec.getGoodTitle());
//            oItem.setGoodsNum(spec.getGoodsNumber());
//            oItem.setGoodsSpec(spec.getColorValue()+","+spec.getSizeValue());
//            oItem.setGoodsSpecNum(spec.getSpecNumber());
//            oItem.setQuantity(quantity);
//            oItem.setPrice(spec.getSalePrice());
//            oItem.setNowPrice(nowPrice);
//            oItem.setIsGift(saleType.intValue());//1赠品订单0实售订单
//            items.add(oItem);
//        }
//        orderVo.setItems(items);
//
//        double orderTotalAmount = goodsTotalAmount + Double.valueOf(shippingFee);
//
//        orderVo.setOrderNum(orderNumber);
//        orderVo.setReceiverName(receiverName);
//        orderVo.setReceiverMobile(receiverMobile);
//        orderVo.setAddress(new StringBuilder(provinceName).append(cityName).append(districtName).append(address).toString());
//        orderVo.setProvince(provinceName);
//        orderVo.setCity(cityName);
//        orderVo.setTown(districtName);
//        orderVo.setShippingFee(Double.valueOf(shippingFee));
//        orderVo.setShopId(shopId);
//        orderVo.setSaleType(saleType);
//        orderVo.setSellerMemo(sellerMemo);
//        orderVo.setTotalAmount(orderTotalAmount);
//
//         var result = orderMogujieService.createOrder(orderVo);
//        System.out.println(JSON.toJSONString(result));
////        orderPddService.orderCreatePdd(pddEntity);
//        return "redirect:/mogujie/order_list?shopId="+shopId;
//    }
//    /**
//     * 订单确认
//     *
//     * @param model
//     * @param orderId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/order_confirm", method = RequestMethod.GET)
//    public String orderConfirmGet(Model model, @RequestParam Long orderId,@RequestParam Integer shopId, HttpServletRequest request) {
//        var orderDetail = orderMogujieService.getOrderDetailAndItem(orderId);
//        if (orderDetail == null) {
//            model.addAttribute("error", "没有找到订单");
//            model.addAttribute("orderVo", new DcOrderMogujieEntity());
//
//        } else {
//            model.addAttribute("orderVo", orderDetail);
//        }
//
//        return "v3/order_confirm_mogujie";
//    }
//
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
//        var order = orderMogujieService.getOrderDetailAndItem(req.getOrderId());
//
//        if (order == null)
//            return new ApiResult<>(404, "订单不存在");//检查是否已经确认
//        else if (order.getAuditStatus().intValue() > 0)
//            return new ApiResult<>(501, "订单已经确认过了");
//        else if (order.getSendStatus() != EnumErpOrderSendStatus.WaitOut.getIndex())
//            return new ApiResult<>(402, "订单不是发货中的状态，不能操作");
//
//
//        return new ApiResult<>(400, "数据检查通过，操作未实现");
//////        log.info("/**********************开始确认订单" + req.getOrderId() + "**********************/");
////        synchronized (this) {
////            //确认订单，加入到仓库系统待发货订单列表
////            ResultVo<Integer> result = orderMogujieService.orderConfirmAndJoinDeliveryQueueForMogujie(req.getOrderId(), req.getReceiver(), req.getMobile(), req.getAddress(), req.getSellerMemo());
////
//////            log.info("/**********************确认订单完成" + result + "**********************/");
////            return new ApiResult<>(result.getCode(), result.getMsg());
////        }
//
//
//    }
//}
