package com.b2c.oms.controller;

import com.b2c.common.utils.DateUtil;
import com.b2c.entity.UserEntity;
import com.b2c.entity.erp.ErpSalesOrderItemEntity;
import com.b2c.enums.erp.EnumErpOrderStatus;
import com.b2c.enums.erp.EnumSalesOrderPayStatus;
import com.b2c.repository.utils.OrderNumberUtils;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.erp.ErpSalesUserService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/sales")
public class SystemOrderController {
    @Autowired
    private ErpSalesOrderService erpSalesOrderService;
    @Autowired
    private ErpGoodsService erpGoodsService;
//    @Autowired
//    private UserService userService;

    /**
     * 系统下单
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/system_order", method = RequestMethod.GET)
    public String login(Model model, @RequestParam Integer shopId, HttpServletRequest request) {

        //查询客户列表
        List<UserEntity> clientList = new ArrayList<>();//userService.getClientUserListByType(null, null);
        model.addAttribute("clientList", clientList);

        if(StringUtils.isEmpty(request.getParameter("shopId"))==false){
            shopId = Integer.parseInt(request.getParameter("shopId"));
        }

        model.addAttribute("menuId", "order_create");
        return "system_order_v3";

    }

    @RequestMapping(value = "/system_order", method = RequestMethod.POST)
    public String postSystemOrder(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userId = request.getSession().getAttribute("userId");
        //return "/permission_denied";
        model.addAttribute("menuId", "system_order");
        /***商品信息****/
        String[] specNumber = request.getParameterValues("specNumber");//规格编码组合
        String[] goodsNumber = request.getParameterValues("goodsNumber");//商品编码组合
        String[] goodsId = request.getParameterValues("goodsId");//商品id组合
        String[] specsId = request.getParameterValues("specId");//商品规格id组合
        String[] quantitys = request.getParameterValues("quantity");//数量组合
        String[] prices = request.getParameterValues("note");//商品价格
        //收件人信息
        String contactMobile = request.getParameter("contactMobile");
        String contactPerson = request.getParameter("contactPerson");
        String area = request.getParameter("area");
        String address = request.getParameter("address");
        String saleType = request.getParameter("saleType");
        String shippingFee= StringUtils.isEmpty(request.getParameter("shippingFee")) ? "0" :request.getParameter("shippingFee");
        String sellerMemo = request.getParameter("sellerMemo");
        Integer payStatus = Integer.parseInt(request.getParameter("payStatus"));
        Integer payMethod = 0;
        if(StringUtils.isEmpty(request.getParameter("payMethod"))==false){
            payMethod = Integer.parseInt(request.getParameter("payMethod"));
        }

        String[] areaNameArray = area.split(" ");

        String provinceName = "";
        if (areaNameArray.length > 0) provinceName = areaNameArray[0];
        String cityName = "";
        if (areaNameArray.length > 1) cityName = areaNameArray[1];
        String districtName = "";
        if (areaNameArray.length > 2) districtName = areaNameArray[2];

        //下单人信息
        String buyerUserId = request.getParameter("buyerUserId");
        ErpSalesOrderDetailVo salesOrder= new ErpSalesOrderDetailVo();
        List<ErpSalesOrderItemEntity> items=new ArrayList<>();
        Long goodsCount=0L;//商品数量
        Long goodsSpecCount=0L;//商品规格数量
        double goodsTotalAmount = 0;//商品总价
        for (int i = 0,n=goodsId.length;i<n;i++) {
            if(StringUtils.isEmpty(goodsId[i]))continue;
            ErpSalesOrderItemEntity item= new ErpSalesOrderItemEntity();
            Integer specId=Integer.parseInt(specsId[i]);
            BigDecimal price = new BigDecimal(prices[i]);
            Integer count =Integer.parseInt(quantitys[i]);
            var spec = erpGoodsService.getSpecBySpecId(specId);
            //if(count.intValue()>spec.getCurrentQty())continue;
            goodsCount+=count;
            goodsSpecCount++;
            goodsTotalAmount +=  price.doubleValue() * count;
            item.setGoodsId(Integer.parseInt(goodsId[i]));
            item.setSpecId(specId);
            item.setSpecNumber(specNumber[i]);
            item.setGoodsNumber(goodsNumber[i]);
            item.setQuantity(count);
            item.setPrice(price);
            item.setDiscountPrice(price);
            item.setGoodsTitle(spec.getGoodTitle());
            item.setGoodsImage(spec.getColorImage());
            item.setColor(spec.getColorValue());
            item.setSize(spec.getSizeValue());
            item.setSkuInfo(item.getColor()+" "+item.getSize());
            items.add(item);
        }
        UserEntity client=null;
        if(StringUtils.isEmpty(buyerUserId)==false) {
//            client = userService.getUserById(Integer.parseInt(buyerUserId));//用户信息
        }
        salesOrder.setGoodsCount(goodsCount);
        salesOrder.setGoodsSpecCount(goodsSpecCount);
        salesOrder.setGoodsTotalAmount(new BigDecimal(goodsTotalAmount));
        salesOrder.setContactMobile(contactMobile);
        salesOrder.setContactPerson(contactPerson);
        salesOrder.setProvince(provinceName);
        salesOrder.setCity(cityName);
        salesOrder.setArea(districtName);
        salesOrder.setAddress(provinceName+cityName+districtName+address);
        salesOrder.setItems(items);
        salesOrder.setSaleType(Integer.parseInt(saleType));
        salesOrder.setShippingFee(new BigDecimal(0));
        if(client != null) {
            salesOrder.setDeveloperId(client.getDeveloperId());
            salesOrder.setBuyerUserId(Integer.parseInt(buyerUserId));
            salesOrder.setBuyerName(client.getUserName());
        }else {
            salesOrder.setDeveloperId(0);
            salesOrder.setBuyerUserId(0);
            salesOrder.setBuyerName(contactPerson);
        }

        salesOrder.setShopId(99);

        salesOrder.setOrderNum(OrderNumberUtils.getOrderIdByTime());
        salesOrder.setTotalAmount(new BigDecimal(goodsTotalAmount).add(new BigDecimal(shippingFee)));
        salesOrder.setOrderTime(System.currentTimeMillis() / 1000);

        if(payStatus.intValue() == 0){
            //未付款订单
            salesOrder.setStatus(EnumErpOrderStatus.WaitSend.getIndex());
            salesOrder.setPayAmount(new BigDecimal(0));//付款金额
            salesOrder.setPayTime(0L);
            salesOrder.setPayStatus(EnumSalesOrderPayStatus.NotPay.getIndex());
        }else if(payStatus.intValue() == 2){
            //已付款订单
            salesOrder.setStatus(EnumErpOrderStatus.WaitSend.getIndex());
            salesOrder.setPayAmount(new BigDecimal(goodsTotalAmount).add(new BigDecimal(shippingFee)));//付款金额
            salesOrder.setPayTime(System.currentTimeMillis() / 1000 );
            salesOrder.setPayStatus(EnumSalesOrderPayStatus.CompletePay.getIndex());
        }

        salesOrder.setPayMethod(payMethod);


        salesOrder.setOrderDate(DateUtil.getCurrentDate());
        salesOrder.setShippingFee(new BigDecimal(shippingFee));
        salesOrder.setSellerMemo(sellerMemo);
        salesOrder.setCreateOn(System.currentTimeMillis() / 1000);
        salesOrder.setAuditStatus(0);

        if(goodsCount>0)erpSalesOrderService.editSalesOrder(salesOrder);
        return "redirect:/order/list?shopId=99";
    }
}
