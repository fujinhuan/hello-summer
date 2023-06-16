//package com.b2c.oms.controller.ali;
//
//import com.b2c.common.third.thymeleaf.PagingResponse;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.datacenter.vo.DcAliOrderRefundVo;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.service.ExpressCompanyService;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 描述：
// * 阿里退款订单
// *
// * @author qlp
// * @date 2019-09-18 19:57
// */
//@RequestMapping("/order")
//@Controller
//public class RefundOrderAliController {
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    @Autowired
//    private ExpressCompanyService expressCompanyService;
//
//    /**
//     * 退货列表
//     * @param model
//     * @param shopId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/aliRefundList", method = RequestMethod.GET)
//    public String aliRefundOrders(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
////        Integer shopId= 1;
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        String orderNum = "";
//        String refNum = "";
//        Integer startTime = 0;
//        Integer endTime = 0;
//
//        Integer state = -1;
//        String page = request.getParameter("page");
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//        if (!StringUtils.isEmpty(request.getParameter("state"))) {
//            state = Integer.parseInt(request.getParameter("state"));
//        }
//        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
//            orderNum = request.getParameter("orderNum");
//        }
//        if (!StringUtils.isEmpty(request.getParameter("refNum"))) {
//            refNum = request.getParameter("refNum");
//        }
//        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
//            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
//        }
//        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
//            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
//        }
//
//        String logisticsCode = "";
//        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))) {
//            logisticsCode = request.getParameter("logisticsCode");
//        }
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
//        PagingResponse<DcAliOrderRefundVo> result = aliOrderService.getOrdersRefund(pageIndex, pageSize, orderNum, refNum,logisticsCode, startTime, endTime, state);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("list", result.getList());
//        model.addAttribute("company", expressCompanyService.getExpressCompany());
//
//        model.addAttribute("menuId", "refund_list");
//        return "refund/order_refund_ali";
//    }
//
//    /***
//     * 阿里退货订单详情
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "dc_ali_refund_item", method = RequestMethod.GET)
//    public String getRefundItem(Model model, @RequestParam Long id, HttpServletRequest request) {
//        Integer shopId = 1;
////        Integer shopTypeId = 1;
////        var entity = thirdSettingService.getEntity(shopTypeId);
////
////        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
////        String returnUrl = variable.getValue() + "/order/aliRefundList&state=GETORDERLIST";
////
////        //不存在ali token 跳转获取
////        if(StringUtils.isEmpty( entity.getAccess_token())){
////
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time=date.getTime(); //这是毫秒数
////        long expireTime = entity.getAccess_token_begin()*1000 +entity.getExpires_in()*1000;
////        if( expireTime < time){
////            //已过期
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
////        //设置appkey和密钥(seckey)
////        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//
////        Long id = Long.parseLong(request.getParameter("id"));
//        DcAliOrderRefundVo refundItemByItemId = aliOrderService.getRefundDetailByRefId(id);
//        model.addAttribute("item", refundItemByItemId);
//
////        AlibabaTradeGetLogisticsTraceInfoSellerViewParam viewParam = new AlibabaTradeGetLogisticsTraceInfoSellerViewParam();
////        viewParam.setOrderId(id);
////        viewParam.setWebSite("1688");
////        SDKResult<AlibabaTradeGetLogisticsTraceInfoSellerViewResult> execute = apiExecutor.execute(viewParam, entity.getAccess_token());
////        AlibabaLogisticsOpenPlatformLogisticsTrace[] trace = execute.getResult().getLogisticsTrace();
////        model.addAttribute("trace",trace);
//
//        model.addAttribute("menuId", "refund_list");
//
//        return "refund/order_refund_detail_ali";
//    }
//}
