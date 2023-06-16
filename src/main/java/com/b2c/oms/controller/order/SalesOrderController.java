package com.b2c.oms.controller.order;

import com.b2c.common.PagingResponse;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.UserEntity;
import com.b2c.entity.datacenter.DcShopEntity;
import com.b2c.entity.erp.ErpSalesOrderEntity;
import com.b2c.entity.erp.ErpSalesOrderItemView;
import com.b2c.entity.erp.vo.ErpGoodsSpecListVo;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.erp.ErpGoodsService;
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
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-24 16:05
 */
@RequestMapping("/order")
@Controller
public class SalesOrderController {
    private final ErpSalesOrderService salesOrderService;
    private final ShopService shopService;
    @Autowired
    private ErpGoodsService erpGoodsService;

    public SalesOrderController( ErpSalesOrderService salesOrderService, ShopService shopService) {
        this.salesOrderService = salesOrderService;
        this.shopService = shopService;
    }

    /**
     * 订单列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String orderList(Model model,  HttpServletRequest request) {
        model.addAttribute("menuId", "order_list");


        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String orderNum = "";
        String contactMobile = "";
        Integer startTime = 0;
        Integer endTime = 0;
        Integer saleType = null;
        Integer shopId = null;
        Integer buyerUserId = null;
        Integer status = null;
        Integer auditStatus = null;


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


        if (!StringUtils.isEmpty(request.getParameter("buyerUserId"))) {
            buyerUserId = Integer.parseInt(request.getParameter("buyerUserId"));
            model.addAttribute("buyerUserId", buyerUserId);
        }
        if (!StringUtils.isEmpty(request.getParameter("status"))) {
            status = Integer.parseInt(request.getParameter("status"));
            model.addAttribute("status",status);
        }
        if (!StringUtils.isEmpty(request.getParameter("auditStatus"))) {
            auditStatus = Integer.parseInt(request.getParameter("auditStatus"));
            model.addAttribute("auditStatus",auditStatus);
        }
        if (!StringUtils.isEmpty(request.getParameter("type"))) {
            saleType = Integer.parseInt(request.getParameter("type"));
            model.addAttribute("saleType",saleType);
        }
        if (!StringUtils.isEmpty(request.getParameter("shopId"))) {
            shopId = Integer.parseInt(request.getParameter("shopId"));
            model.addAttribute("shopId",shopId);
        }

        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }


        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        PagingResponse<ErpSalesOrderEntity> result = salesOrderService.getList(pageIndex, pageSize, orderNum, contactMobile, saleType, buyerUserId, status,shopId,auditStatus, startTime, endTime);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        //查询业务员
//        List<UserEntity> developerList = userService.getDeveloperList();
//        model.addAttribute("developerList", developerList);

        //店铺列表
//        List<DcShopEntity> shops = shopService.getShopList(null);
//        model.addAttribute("shops", shops);
        //查询客户列表
        List<UserEntity> clientList = new ArrayList<>();//userService.getClientUserListByType(null, null);
        model.addAttribute("clientList", clientList);



//        if(shopId!=null && shopId == 99)
            return "order_list_v3";
//        else {
//            HttpSession session = request.getSession();
//            Object userId = request.getSession().getAttribute("userId");
//            if (Integer.parseInt(userId.toString())!=1) return "/permission_denied";
//            return "order_list";
//        }
    }

    @RequestMapping(value = "/item_list", method = RequestMethod.GET)
    public String orderItemList(Model model,  HttpServletRequest request) {
        model.addAttribute("menuId", "order_item_list");


        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String orderNum = "";//订单编号
        String sku = "";//sku编码
        Integer startTime = 0;//开始时间
        Integer endTime = 0;//结束时间
        Integer buyerUserId = null;//客户id

        Integer shopId = 99;
        if (!StringUtils.isEmpty(request.getParameter("shopId"))) {
            shopId = Integer.parseInt(request.getParameter("shopId"));
        }
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
            model.addAttribute("orderNum",orderNum);
        }
        if (!StringUtils.isEmpty(request.getParameter("sku"))) {
            sku = request.getParameter("sku");
            model.addAttribute("sku",sku);
        }

        if (!StringUtils.isEmpty(request.getParameter("buyerUserId"))) {
            buyerUserId = Integer.parseInt(request.getParameter("buyerUserId"));
            model.addAttribute("buyerUserId", buyerUserId);
        }

        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }


        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        PagingResponse<ErpSalesOrderItemView> result = salesOrderService.getOrderItemList(pageIndex, pageSize, orderNum, sku, buyerUserId, shopId, startTime, endTime);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());
        model.addAttribute("shopId",shopId);

        //查询业务员
//        List<UserEntity> developerList = userService.getDeveloperList();
//        model.addAttribute("developerList", developerList);

        //店铺列表
//        List<DcShopEntity> shops = shopService.getShopList(null);
//        model.addAttribute("shops", shops);
        //查询客户列表
        List<UserEntity> clientList = new ArrayList<>();//userService.getClientUserListByType(null, null);
        model.addAttribute("clientList", clientList);

        return "order_item_list_erp";
    }

    /**
     * 订单详情
     *
     * @param model
     * @param id      订单Id
     * @param request
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String getOrderDetail(Model model, @RequestParam Long id, HttpServletRequest request) {
        var orderDetail = salesOrderService.getDetailById(id);
        model.addAttribute("order", orderDetail);
        model.addAttribute("shopId", orderDetail.getShopId());
        model.addAttribute("menuId", "order_list");

        return "order_detail";
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

        return "order_confirm";
    }

    /**
     * 修改订单item商品
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/upd_goods", method = RequestMethod.GET)
    public String OrderItemLinkNewGoods(Model model,@RequestParam Long orderItemId,@RequestParam Integer shopId,HttpServletRequest request) {
        /********查询商品********/
        String number = "";
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
            model.addAttribute("number",number);

            PagingResponse<ErpGoodsSpecListVo> result = new PagingResponse<ErpGoodsSpecListVo>(1, 10, 0, null);
            result = erpGoodsService.getSpecByGoodsNumber(1, 10, number);

            model.addAttribute("totalSize", result.getTotalSize());
            model.addAttribute("lists", result.getList());
        }

        model.addAttribute("orderItemId",orderItemId);
        model.addAttribute("shopId",shopId);

        if(shopId == 99){
            //查询订单item
            var orderItem = salesOrderService.getOrderItemByItemId(orderItemId);
            model.addAttribute("orderItemQty",orderItem.getQuantity());
        }else{

        }



        return "goods/erp_goods_sku_list";
    }

    @RequestMapping(value = "/upd_refud_goods", method = RequestMethod.GET)
    public String upd_refud_goods(Model model,@RequestParam Long refundId,@RequestParam Integer shopId,HttpServletRequest request) {
        /********查询商品********/
        String number = "";
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
            model.addAttribute("number",number);

            PagingResponse<ErpGoodsSpecListVo> result = new PagingResponse<ErpGoodsSpecListVo>(1, 10, 0, null);
            result = erpGoodsService.getSpecByGoodsNumber(1, 10, number);

            model.addAttribute("totalSize", result.getTotalSize());
            model.addAttribute("lists", result.getList());
        }

        model.addAttribute("refundId",refundId);
        model.addAttribute("shopId",shopId);

        return "goods/erp_goods_sku_list_refund";
    }

    /**
     * 添加礼品
     * @param model
     * @param orderId
     * @param request
     * @return
     */
    @RequestMapping(value = "/add_gift", method = RequestMethod.GET)
    public String addGift(Model model,@RequestParam Long orderId,@RequestParam Integer shopId,HttpServletRequest request) {
        model.addAttribute("orderId",orderId);
        model.addAttribute("shopId",shopId);

        String number = "";
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
            model.addAttribute("number",number);

            PagingResponse<ErpGoodsSpecListVo> result = new PagingResponse<ErpGoodsSpecListVo>(1, 10, 0, null);
            result = erpGoodsService.getSpecByGoodsNumber(1, 10, number);

            model.addAttribute("totalSize", result.getTotalSize());
            model.addAttribute("lists", result.getList());
        }

        if(shopId == 99) {
            //查询订单
            var order = salesOrderService.getDetailById(orderId);
        }

//        Long orderItemId=0L;
//        if (!StringUtils.isEmpty(request.getParameter("orderItemId"))) {
//            orderItemId = Long.valueOf(request.getParameter("orderItemId"));
//        }


        return "order/order_gift_add";
    }

}
