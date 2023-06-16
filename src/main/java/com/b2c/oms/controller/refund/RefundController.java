package com.b2c.oms.controller.refund;

import com.b2c.common.utils.DateUtil;
import com.b2c.entity.UserEntity;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.ExpressCompanyService;
import com.b2c.service.erp.ErpSalesOrderService;
//import com.b2c.service.mall.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-24 16:05
 */
@RequestMapping("/refund")
@Controller
public class RefundController {
    @Autowired
    private ErpSalesOrderService salesOrderService;
//    @Autowired
//    private UserService userService;
    @Autowired
    private ExpressCompanyService expressCompanyService;

    /**
     * 退货订单
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,@RequestParam Integer shopId, HttpServletRequest request) {
        Integer type = null;
        if (!StringUtils.isEmpty(request.getParameter("type"))) {
            try {
                type = Integer.parseInt(request.getParameter("type"));
            }catch (Exception e){}
        }
        model.addAttribute("menuId", "refund_list");
//        model.addAttribute("type", type);

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();

        String refundNum = "";
        String orderNum = "";
        String logisticsCode = "";//退货物流单号

        Integer startTime = 0;
        Integer endTime = 0;

        Integer buyerUserId = null;
        Integer status = null;

        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }

        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
        }
        model.addAttribute("orderNum", orderNum);

        if (!StringUtils.isEmpty(request.getParameter("refundNum"))) {
            refundNum = request.getParameter("refundNum");
        }
        model.addAttribute("refundNum", refundNum);

        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))) {
            logisticsCode = request.getParameter("logisticsCode");
        }
        model.addAttribute("logisticsCode", logisticsCode);

        if (!StringUtils.isEmpty(request.getParameter("buyerUserId"))) {
            buyerUserId = Integer.parseInt(request.getParameter("buyerUserId"));
            model.addAttribute("buyerUserId", buyerUserId);
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
        var result = salesOrderService.getRefundList(pageIndex, pageSize, refundNum, orderNum, logisticsCode, type, buyerUserId, status, startTime, endTime);
//        PagingResponse<RefundOrderListVo> result = orderCancelService.getAfterOrders(pageIndex, pageSize, orderNum, startTime, endTime, returnOrderNum, logisticsCode, state, orderTypeEnums);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("list", result.getList());

        //查询客户列表
        List<UserEntity> clientList = new ArrayList<>();//userService.getClientUserListByType(null, null);
        model.addAttribute("clientList", clientList);

        //快递公司
        model.addAttribute("company", expressCompanyService.getExpressCompany());


        if(StringUtils.isEmpty(request.getParameter("shopId"))==false){
            shopId = Integer.parseInt(request.getParameter("shopId"));
        }
        model.addAttribute("shopId", shopId);
        
        return "order_refund_list_v3";

    }


    /**
     * 云购订单申请退货
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/refundApply", method = RequestMethod.GET)
    public String getOrderRefundOffline(Model model, @RequestParam Long id, HttpServletRequest request) {
        //查询订单信息
        var orderDetail = salesOrderService.getDetailById(id);

        model.addAttribute("menuId", "refund_list");

        model.addAttribute("items", orderDetail.getItems());
        model.addAttribute("orderId", id);
        model.addAttribute("saleType", orderDetail.getSaleType());
        return "order_refund_apply";
    }

    /**
     * 退货详情
     *
     * @param model
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/refundDetail", method = RequestMethod.GET)
    public String getOrderDetailYg(Model model, @RequestParam Long id, HttpServletRequest request) {
        model.addAttribute("menuId", "refund_list");
        var detail = salesOrderService.getRefundDetailById(id);
        if(detail == null) return "redirect:/refund/list";
        model.addAttribute("orderVo", detail);

        model.addAttribute("saleType", detail.getOrderSaleType());
        model.addAttribute("menuId", "sales" + detail.getOrderSaleType());

        return "order/order_refund_detail_sales";
    }

}
