package com.b2c.oms.controller.notify;
import com.alibaba.fastjson.JSONObject;
import com.b2c.common.api.ApiResult;
import com.b2c.common.api.ApiResultEnum;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.erp.ErpSalesOrderItemEntity;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.mall.OrderService;
import com.b2c.vo.erp.ErpSalesOrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 云购网通知处理Controller
 */
@RequestMapping("/notify")
@RestController
public class NotifyYunGouController {
    private static Logger log = LoggerFactory.getLogger(NotifyYunGouController.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private ErpSalesOrderService erpSalesOrderService;
    @Autowired
    private ErpGoodsService erpGoodsService;
    /**
     * 云购网新订单通知
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/yungou_new_order")
    public ApiResult<Integer> newOrderNotify(HttpServletRequest request) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
        if (StringUtils.isEmpty(jsonObject.getString("token"))) {
            return new ApiResult<>(ApiResultEnum.ParamsError, "没有token");
        }else if(jsonObject.getString("token").equals("N38Dd039d")==false){
            return new ApiResult<>(ApiResultEnum.ParamsError, "token错误");
        }
        try {
            Integer type = jsonObject.getInteger("type");
            if (type == 1) {
                //有新订单，开始拉取该订单到数据库
                Long orderId = jsonObject.getLong("orderId");
                log.info("YUNGOU更新ERP订单:" + orderId);
//                ErpSalesOrderDetailVo salesOrder= new ErpSalesOrderDetailVo();
//
//                List<ErpSalesOrderItemEntity> salesitems=new ArrayList<>();
//
//                var orderItems = orderService.getOrderItems(orderId);
//
//                if(orderItems != null && orderItems.size()>0){
//                    for(var item:orderItems){
//                        var spec = erpGoodsService.getSpecByNumber(item.getSpecNumber());
//                        ErpSalesOrderItemEntity salesItem= new ErpSalesOrderItemEntity();
//                        if(spec!=null){
//                            salesItem.setGoodsId(spec.getGoodsId());
//                            salesItem.setSpecId(spec.getId());
//                        }else{
//                            salesItem.setGoodsId(0);
//                            salesItem.setSpecId(0);
//                        }
//                        salesItem.setOrderId(item.getOrderId());
//                        salesItem.setSpecNumber(item.getSpecNumber());
//                        salesItem.setGoodsNumber(item.getGoodsNumber());
//                        salesItem.setQuantity(item.getCount());
//                        salesItem.setPrice(item.getPrice());
//                        salesItem.setDiscountPrice(item.getDiscountPrice());
//                        salesItem.setGoodsTitle(item.getTitle());
//                        salesItem.setGoodsImage(item.getImage());
//                        salesItem.setColor(item.getColor());
//                        salesItem.setSize(item.getSize());
//                        salesItem.setSkuInfo(item.getColor()+" "+item.getSize());
//                        salesitems.add(salesItem);
//                    }
//                }
//                var order = orderService.getOrderEntity(orderId);
//                if(order!=null){
//                    salesOrder.setGoodsCount(Long.parseLong(String.valueOf(order.getGoodsNum())));
//                    salesOrder.setGoodsSpecCount(Long.parseLong(String.valueOf(orderItems.size())));
//                    salesOrder.setGoodsTotalAmount(order.getGoodsTotalPrice());
//                    salesOrder.setContactMobile(order.getConsigneeMobile());
//                    salesOrder.setContactPerson(order.getConsignee());
//                    salesOrder.setProvince(order.getProvince());
//                    salesOrder.setCity(order.getCity());
//                    salesOrder.setArea(order.getDistrict());
//                    salesOrder.setAddress(order.getAddress());
//                    salesOrder.setItems(salesitems);
//                    salesOrder.setSaleType(order.getBuyerOrderType());
//                    salesOrder.setShippingFee(order.getFreight());
//                    salesOrder.setDeveloperId(order.getDeveloperId());
//                    salesOrder.setBuyerUserId(order.getUserId());
//                    salesOrder.setShopId(order.getShopId());
//                    salesOrder.setStatus(order.getState());
//                    salesOrder.setOrderNum(order.getOrderNum());
//                    salesOrder.setTotalAmount(order.getOrderTotalPrice());
//                    salesOrder.setOrderTime(order.getCreateOn());
//                    salesOrder.setPayAmount(order.getPaymentPrice());
//                    salesOrder.setPayMethod(order.getPaymentMethod());
//                    salesOrder.setPayTime(StringUtils.isEmpty(order.getPaymentResultTime()) ? 0L : order.getPaymentResultTime());
//                    salesOrder.setPayStatus(2);
//                    salesOrder.setOrderDate(DateUtil.unixTimeStampToDate(order.getCreateOn()));
//                    salesOrder.setCreateOn(order.getCreateOn());
//                    salesOrder.setBuyerFeedback(order.getComment());//买家备注
//                    salesOrder.setSellerMemo(order.getSellerMemo());//卖家备注
//                    salesOrder.setOrderTime(order.getCreateOn());
//                    /*****开始插入订单数据到erp_sales_order*****/
//                    //var result= erpSalesOrderService.editSalesOrder(salesOrder);
//                    if(result.getCode()>0)log.info("YUNGOU订单更新到ERP失败："+result.getMsg());
//                }
            }
        }catch (Exception e){
            log.error("PDD订单更新到ERP失败："+e.getMessage());
            return new ApiResult<>(ApiResultEnum.ParamsError, "收到的消息格式错误");
        }
        return new ApiResult<>(ApiResultEnum.SUCCESS);
    }
}
