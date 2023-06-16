package com.b2c.oms.controller.pipi;

import com.b2c.common.PagingResponse;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.UserEntity;
import com.b2c.entity.erp.ErpSalesOrderEntity;
import com.b2c.enums.EnumShopType;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.ShopService;
import com.b2c.vo.erp.ErpSalesOrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-24 16:05
 */
@RequestMapping("/pipi")
@Controller
public class PiPiOrderController {
    private final ErpSalesOrderService salesOrderService;
    @Autowired
    private ShopService shopService;


    public PiPiOrderController(ErpSalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    /**
     * 订单列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_list", method = RequestMethod.GET)
    public String orderList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        model.addAttribute("menuId", "order_list");
        var shop = shopService.getShop(shopId);

        model.addAttribute("shop", shop);

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String orderNum = "";
        String contactMobile = "";
        Integer startTime = 0;
        Integer endTime = 0;

        Integer buyerUserId = null;
        Integer status = null;


        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("pageSize"))) {
            try {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            } catch (Exception e) {
            }
        }
        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
        }
        if (!StringUtils.isEmpty(request.getParameter("contactMobile"))) {
            contactMobile = request.getParameter("contactMobile");
        }

        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }

        if (!StringUtils.isEmpty(request.getParameter("buyerUserId"))) {
            buyerUserId = Integer.parseInt(request.getParameter("buyerUserId"));
            model.addAttribute("buyerUserId", buyerUserId);
        }
        if (!StringUtils.isEmpty(request.getParameter("status"))) {
            status = Integer.parseInt(request.getParameter("status"));
        }

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
//        PagingResponse<ErpSalesOrderEntity> result = salesOrderService.getList(pageIndex, pageSize, orderNum, contactMobile, 1, buyerUserId, status,null, startTime, endTime);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
        model.addAttribute("totalSize", 0);
        model.addAttribute("lists", null);
        //查询业务员
//        List<UserEntity> developerList = userService.getDeveloperList();
//        model.addAttribute("developerList", developerList);



        return "v3/order_list_pipi";
    }

    /**
     * 订单详情
     *
     * @param model
     * @param id      订单Id
     * @param request
     * @return
     */
    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
    public String getOrderDetail(Model model, @RequestParam Long id, HttpServletRequest request) {
        var orderDetail = salesOrderService.getDetailById(id);
        model.addAttribute("order", orderDetail);
        model.addAttribute("menuId", "sales" + orderDetail.getSaleType());

        return "sales/order_detail_sales";
    }

    /**
     * 订单确认
     *
     * @param model
     * @param orderId
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_confirm", method = RequestMethod.GET)
    public String orderConfirmGet(Model model, @RequestParam Long orderId, HttpServletRequest request) {
        var order = salesOrderService.getDetailById(orderId);
        if (order == null) {
            model.addAttribute("error", "没有找到订单");
            model.addAttribute("orderVo", new ErpSalesOrderDetailVo());

        } else {
            model.addAttribute("orderVo", order);
        }

        return "sales/order_confirm_sales";
    }

}
