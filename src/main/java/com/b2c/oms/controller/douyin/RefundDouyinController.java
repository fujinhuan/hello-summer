package com.b2c.oms.controller.douyin;

import com.b2c.common.utils.DateUtil;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.ExpressCompanyService;
import com.b2c.service.ShopService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.oms.DouyinOrderService;
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
@RequestMapping("/douyin")
@Controller
public class RefundDouyinController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ErpSalesOrderService salesOrderService;
    @Autowired
    private DouyinOrderService douyinOrderService;

    /**
     * 退货订单
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund_list", method = RequestMethod.GET)
    public String list(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop",shop);
        model.addAttribute("menuId", "refund_list");

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = 20;

        String orderNum = "";
        String logisticsCode = "";//退货物流单号

        Integer startTime = null;
        Integer endTime = null;

        Integer status = null;

        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }

        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
            model.addAttribute("orderNum", orderNum);
        }


        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))) {
            logisticsCode = request.getParameter("logisticsCode");
            model.addAttribute("logisticsCode", logisticsCode);
        }

        if (!StringUtils.isEmpty(request.getParameter("status"))) {
            status = Integer.parseInt(request.getParameter("status"));
        }
        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }


        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        var result = douyinOrderService.getDouyinRefundOrders(shop.getId(),pageIndex,pageSize,orderNum,logisticsCode,startTime,endTime,status,0);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("list", result.getList());

        return "v3/refund_list_douyin";
    }

    @RequestMapping(value = "/refund_detail", method = RequestMethod.GET)
    public String refundDetail(Model model, @RequestParam Long refundId,@RequestParam Integer shopId,HttpServletRequest request) {
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop",shop);
        model.addAttribute("menuId", "refund_list");
        model.addAttribute("orderVo",douyinOrderService.getDouYinRefundOrderDetail(refundId));
        return "v3/refund_detail_douyin";
    }

    @RequestMapping(value = "/order_refund_confirm", method = RequestMethod.GET)
    public String orderRefundConfirm(Model model, @RequestParam Long refundId,@RequestParam Integer shopId, HttpServletRequest request) {
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop",shop);
        model.addAttribute("menuId", "refund_list");
        model.addAttribute("orderVo",douyinOrderService.getDouYinRefundOrderDetail(refundId));
        return "order/douyin/order_refund_confirm";
    }


}
