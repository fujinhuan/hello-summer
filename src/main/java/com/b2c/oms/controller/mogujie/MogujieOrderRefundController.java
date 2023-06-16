//package com.b2c.oms.controller.mogujie;
//
//import com.b2c.common.third.thymeleaf.PagingResponse;
//import com.b2c.common.utils.DateUtil;
//import com.b2c.entity.datacenter.DcTmallOrderRefundEntity;
//import com.b2c.enums.EnumShopType;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.service.oms.DcTmallOrderReturnService;
//import com.b2c.service.oms.ShopService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Optional;
//
///**
// * 淘系退货
// */
//@RequestMapping("/mogujie")
//@Controller
//public class MogujieOrderRefundController {
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private DcTmallOrderReturnService tmallOrderReturnService;
//
//    /**
//     * 天猫退货订单列表
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/refund_list", method = RequestMethod.GET)
//    public String offlineRefundList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//
//        model.addAttribute("shop", shop);
//
//        model.addAttribute("shopName", shop.getName());
//
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        String orderNum = Optional.ofNullable(request.getParameter("order_num")).orElse("");
//
//
//        String page = request.getParameter("page");
//        Integer startTime = 0;
//        Integer endTime = 0;
//        String state = "";
//
//        String returnOrderNum = "";
//
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//        if (!StringUtils.isEmpty(request.getParameter("state"))) {
//            state = request.getParameter("state");
//        }
//
//        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
//            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
//        }
//        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
//            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
//        }
//
//        if (!StringUtils.isEmpty(request.getParameter("returnOrderNum"))) {
//            returnOrderNum = request.getParameter("returnOrderNum");
//            model.addAttribute("returnOrderNum", returnOrderNum);
//        }
//
//        String logisticsCode = "";
//        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))) {
//            logisticsCode = request.getParameter("logisticsCode");
//        }
//
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
////        PagingResponse<DcTmallOrderRefundEntity> result = tmallOrderReturnService.getTmallOrderRefundList(pageIndex, pageSize, shopId, orderNum, returnOrderNum, logisticsCode, startTime, endTime, state);
////        model.addAttribute("totalSize", result.getTotalSize());
////        model.addAttribute("list", result.getList());
//
//
//        model.addAttribute("menuId", "refund_list");
//
//        return "v3/order_refund_list_mogujie";
//    }
//
//}
