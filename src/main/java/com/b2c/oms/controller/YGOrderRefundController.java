package com.b2c.oms.controller;

import com.b2c.common.PagingResponse;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.datacenter.vo.OfflineOrderListVo;
import com.b2c.entity.enums.OrderTypeEnums;
import com.b2c.vo.order.RefundOrderListVo;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.ExpressCompanyService;
import com.b2c.service.mall.OrderCancelService;
import com.b2c.service.oms.OrderYungouService;
import com.b2c.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-24 16:05
 */
@RequestMapping("/order_yungou")
@Controller
public class YGOrderRefundController {
    @Autowired
    private OrderCancelService orderCancelService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderYungouService orderYungouService;
    @Autowired
    private ExpressCompanyService expressCompanyService;

    /**
     * 云购退货订单
     *
     * @param model
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund_list", method = RequestMethod.GET)
    public String list(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        //订单类型参数
        OrderTypeEnums orderTypeEnums = OrderTypeEnums.PT;

        if (shopId.intValue() == 3) {
            orderTypeEnums = OrderTypeEnums.PT;
        } else if (shopId.intValue() == 4) {
            orderTypeEnums = OrderTypeEnums.PiFa;

        } else if (shopId.intValue() == 11) {
            orderTypeEnums = OrderTypeEnums.DaiFa;
        }

        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        model.addAttribute("shopName", shop.getName());
//        model.addAttribute("menuId", "ref-" + shop.getEname());
        model.addAttribute("menuId", "refund_list");

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String orderNum = "";
        Integer startTime = 0;
        Integer endTime = 0;
        Integer state = -1; //售后状态
        String returnOrderNum = "";

        if (!StringUtils.isEmpty(request.getParameter("state"))) {
            state = Integer.parseInt(request.getParameter("state"));
        }
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
        }

        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }

        if (!StringUtils.isEmpty(request.getParameter("returnOrderNum"))) {
            returnOrderNum = request.getParameter("returnOrderNum");
        }

        String logisticsCode = "";//退货物流单号
        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))) {
            logisticsCode = request.getParameter("logisticsCode");
        }
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
//        PagingResponse<RefundOrderListVo> result = orderCancelService.getAfterOrders(pageIndex, pageSize, orderNum, startTime, endTime, returnOrderNum, state, OrderTypeEnums.DaiFa);
//        PagingResponse<RefundOrderListVo> result = orderCancelService.getAfterOrders(pageIndex, pageSize, orderNum, startTime, endTime, returnOrderNum, state, OrderTypeEnums.PiFa);
        PagingResponse<RefundOrderListVo> result = orderCancelService.getAfterOrders(pageIndex, pageSize, orderNum, startTime, endTime, returnOrderNum, logisticsCode, state, null);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("list", result.getList());
//        if (shopId != 3) {
            model.addAttribute("company", expressCompanyService.getExpressCompany());
//        }


        return "v3/yg/order_refund_list_yg";
    }

    /**
     * 云购退货订单详情
     *
     * @param model
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_detail_refund_yg", method = RequestMethod.GET)
    public String getOrderDetailYg(Model model, @RequestParam Long id, HttpServletRequest request) {
        Integer shopId = 3;
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        model.addAttribute("menuId", "ref-" + shop.getEname());

        model.addAttribute("orderVo", orderCancelService.getRefundOrderById(id));

        return "v3/yg/order_detail_refund_yg";
    }


    /**
     * 云购订单申请退货
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/apply_refund", method = RequestMethod.GET)
    public String getOrderRefundOffline(Model model, @RequestParam Long id, HttpServletRequest request) {
        //查询订单信息
        OfflineOrderListVo orderDetail = orderYungouService.getOrderDetail(id);
        Integer shopId = 3;

//        if (orderDetail.getType().intValue() == OrderTypeEnums.PT.getIndex() || orderDetail.getType().intValue() == OrderTypeEnums.PT_YUSHOU.getIndex()) {
//            //云购零售、云购预售
//            shopId = 3;
//        } else if (orderDetail.getType().intValue() == OrderTypeEnums.PiFa.getIndex()) {
//            //云购批发订单
//            shopId = 4;
//        } else if (orderDetail.getType().intValue() == OrderTypeEnums.DaiFa.getIndex()) {
//            //云购批发订单
//            shopId = 11;
//        }

        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        Integer orderId = Integer.parseInt(request.getParameter("id"));
        model.addAttribute("menuId", "order_list");
        model.addAttribute("items", orderCancelService.getOrderItemByAfter(orderId));
        model.addAttribute("orderId", orderId);
        return "v3/yg/order_apply_refund";
    }
}
