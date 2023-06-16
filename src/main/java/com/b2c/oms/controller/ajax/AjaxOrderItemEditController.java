package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.DataRow;
import com.b2c.entity.erp.ErpGoodsSpecEntity;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.entity.result.ResultVo;
import com.b2c.enums.EnumShopIdType;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;

import com.b2c.service.oms.DcTmallOrderService;
import com.b2c.service.oms.DouyinOrderService;
import com.b2c.service.oms.OrderPddService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 订单item数据修改
 *
 * @author pbd
 */
@RestController
@RequestMapping("/ajax_order_item_edit")
public class AjaxOrderItemEditController {
    @Autowired
    private OrderPddService orderPddService;
    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private ErpSalesOrderService salesOrderService;
    @Autowired
    private DcTmallOrderService tmallOrderService;
//    @Autowired
//    private AliOrderService aliOrderService;
      @Autowired
      private DouyinOrderService douyinOrderService;
//    @Autowired
//    private OrderMogujieService orderMogujieService;
//    @Autowired
//    private DcKwaiOrderService kwaiOrderService;

    private static Logger log = LoggerFactory.getLogger(AjaxOrderItemEditController.class);

    /**
     * 根据商品编码获取SKU
     *
     * @param data
     * @param req
     * @return
     */
    @RequestMapping(value = "/get_good_spec_by_goods_id", method = RequestMethod.POST)
    public ApiResult<List<ErpGoodsSpecEntity>> getGoodSpecByGoodsId(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.get("goodsId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：缺少goodsId");
        Integer goodsId = data.getInt("goodsId");
        var specList = erpGoodsService.getSpecListByGoodsId(goodsId);
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", specList);
    }

    /**
     * 修改sku
     *
     * @param data
     * @param req
     * @return
     */
    @RequestMapping(value = "/upd_good_spec_for_item_id", method = RequestMethod.POST)
    public ApiResult<Integer> updGoodSpec(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.getString("orderId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "缺少orderId");

        if (StringUtils.isEmpty(data.getString("itemId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "缺少itemId");

        if (StringUtils.isEmpty(data.getString("newSpecId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "新规格不存在");

        try {
            //要修改的orderItemId
            Long orderId = data.getLong("orderId");
            Long orderItemId = data.getLong("itemId");
            Integer newSpecId = data.getInt("newSpecId");

            var order = salesOrderService.getDetailById(orderId);
            if (order == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单不存在");

            //查找出orderItem并赋新SKUiD值
            var orderItem = order.getItems().stream().filter(i -> i.getId().longValue() == orderItemId.longValue()).findFirst().get();
            if (orderItem == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单orderItem不存在");

            //查询新的specId是否存在
            var erpGoodsSpec = erpGoodsService.getSpecBySpecId(newSpecId);
            if (erpGoodsSpec == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "新规格不存在");
            //
            if(erpGoodsSpec.getId() == orderItem.getSpecId().intValue()){
                return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "你选的规格和之前的是一样的");
            }
            var erpGoods = erpGoodsService.getById(erpGoodsSpec.getGoodsId());
            if (erpGoods == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "新规格商品不存在");

            orderItem.setSpecId(newSpecId);
            orderItem.setSpecNumber(erpGoodsSpec.getSpecNumber());
            orderItem.setGoodsId(erpGoodsSpec.getGoodsId());
            orderItem.setGoodsTitle(erpGoods.getName());
            orderItem.setGoodsImage(erpGoodsSpec.getColorImage());
            orderItem.setColor(erpGoodsSpec.getColorValue());
            orderItem.setSize(erpGoodsSpec.getSizeValue());
            orderItem.setSkuInfo((StringUtils.isEmpty(erpGoodsSpec.getColorValue()) ? "" : erpGoodsSpec.getColorValue()) + (StringUtils.isEmpty(erpGoodsSpec.getSizeValue()) ? "" : erpGoodsSpec.getSizeValue()));
            if(StringUtils.isEmpty(orderItem.getModifySkuRemark()))
                orderItem.setModifySkuRemark("");
            orderItem.setModifySkuRemark( orderItem.getModifySkuRemark() + "修改新规格，原规格："+orderItem.getSpecNumber()+orderItem.getSkuInfo()+"["+ DateUtil.dateToString(new Date(),"yyyyMMdd HH:mm:ss") +"]");
            //修改sku
            salesOrderService.updateOrderItemSkuByItemId(orderItem);

            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "修改成功");
        } catch (Exception ex) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "失败：" + ex);
        }
    }

    @RequestMapping(value = "/del_good_spec_for_item_id", method = RequestMethod.POST)
    public ApiResult<Integer> delOrderItemSkuByItemId(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.getString("orderId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "缺少orderId");
        if (StringUtils.isEmpty(data.getString("itemId")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "缺少itemId");
        Long orderId = data.getLong("orderId");
        Long orderItemId = data.getLong("itemId");
        salesOrderService.delOrderItemSkuByItemId(orderId,orderItemId);
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "删除成功");
    }



//    /**
//     * 根据id修改商品SKU信息
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/upd_good_spec_by_id", method = RequestMethod.POST)
//    public ApiResult<Integer> updGoodSpec(@RequestBody DataRow data, HttpServletRequest req) {
//        if (StringUtils.isEmpty(data.get("newSpecId")))
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "新规格不存在");
//
//        try {
//            var shop = EnumShopIdType.valueOf(data.getString("orderSource"));
//            //要修改的orderItemId
//            Long orderItemId = data.getLong("itemId");
//            Integer newSpecId = data.getInt("newSpecId");
//
//            //查询新的specId是否存在
//            var erpGoodsSpec = erpGoodsService.getSpecBySpecId(newSpecId);
//            if (erpGoodsSpec == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "新规格不存在");
//
//            //查询订单信息
//            var order = dcOrderService.getDcOrderDetail(data.getLong("orderId"), shop);
//
//            //查找出orderItem并赋新SKUiD值
//            var orderItem = order.getGoods().stream().filter(i -> i.getId().longValue() == orderItemId.longValue()).findFirst().get();
//            if (orderItem != null) {
//                orderItem.setNewSpecId(newSpecId);
//                orderItem.setNewSpecNumber(erpGoodsSpec.getSpecNumber());
//            }
//            var checkResult = dcOrderService.checkDcOrderConfirm(order, shop);
//
//            if (checkResult.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//                dcOrderService.updGoodSpecBySpecId(orderItemId, newSpecId, shop);
//                return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "修改成功");
//            }
//            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
//        } catch (Exception ex) {
//            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "失败：" + ex);
//        }
//    }

    /**
     * 修改关联订单无商品Id
     * @param req
     * @return
     */
    @RequestMapping(value = "/upd_erp_sales_order_spec", method = RequestMethod.POST)
    public ApiResult<Long> updErpSalesOrderSpec(@RequestBody DataRow req) {
        Integer quantity = req.getInt("quantity");
        Long orderItemId = req.getLong("orderItemId");
        Integer erpGoodSpecId = req.getInt("erpGoodSpecId");
        Integer shopId = req.getInt("shopId");

        if(orderItemId==null || orderItemId.longValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少orderItemId");
        }

        if(erpGoodSpecId==null || erpGoodSpecId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少erpGoodSpecId");
        }
        if(quantity==null || quantity.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少quantity");
        }
        if(shopId==null || shopId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少shopId");
        }
        if(shopId == 99) {
            var result = salesOrderService.updErpSalesOrderSpec(orderItemId, erpGoodSpecId, quantity);
            return new ApiResult<>(result.getCode(), result.getMsg(), result.getData());
        }else if(shopId == 5 || shopId == 18 || shopId == 14){
            //拼多多
            var result = orderPddService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }else if(shopId == 2|| shopId == 6|| shopId == 7) {
            //淘系
            var result = tmallOrderService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }else if(shopId==8){
            //抖音
            var result = douyinOrderService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }
//        else if(shopId == 1){
//            //1688
//            var result = aliOrderService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }else if(shopId == 8){
//            //抖音
//            var result = douyinOrderService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }else if(shopId==13){
//            var result = kwaiOrderService.updOrderSpec(orderItemId,erpGoodSpecId,quantity);
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }
        else {
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(),"店铺"+shopId+"，未实现修改商品功能");
        }
    }

    @RequestMapping(value = "/upd_order_refund_spec", method = RequestMethod.POST)
    public ApiResult<Long> upd_order_refund_spec(@RequestBody DataRow req) {
        Integer quantity = req.getInt("quantity");
        Long refundId = req.getLong("refundId");
        Integer erpGoodSpecId = req.getInt("erpGoodSpecId");
        Integer shopId = req.getInt("shopId");

        if(refundId==null || refundId.longValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少refundId");
        }

        if(erpGoodSpecId==null || erpGoodSpecId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少erpGoodSpecId");
        }
        if(quantity==null || quantity.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少quantity");
        }
        if(shopId==null || shopId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少shopId");
        }
        if(shopId == 5 || shopId == 18 || shopId == 14){
            //拼多多
            var result = orderPddService.updOrderRefundSpec(refundId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }else if(shopId == 8) {
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(),"店铺"+shopId+"，未实现修改商品功能");
        }else {
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(),"店铺"+shopId+"，未实现修改商品功能");
        }
    }

    /**
     * 添加订单赠品
     * @param req
     * @return
     */
    @RequestMapping(value = "/add_gift", method = RequestMethod.POST)
    public ApiResult<Long> addGift(@RequestBody DataRow req) {
        Integer quantity = req.getInt("quantity");
        Long orderId = req.getLong("orderId");
        Integer erpGoodSpecId = req.getInt("erpGoodSpecId");
        Integer erpGoodsId = req.getInt("erpGoodsId");
        Integer shopId = req.getInt("shopId");

        if(orderId==null || orderId.longValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少orderId");
        }
        if(erpGoodsId==null || erpGoodsId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少erpGoodsId");
        }
        if(erpGoodSpecId==null || erpGoodSpecId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少erpGoodSpecId");
        }
        if(quantity==null || quantity.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少quantity");
        }
        if(shopId==null || shopId.intValue()<=0){
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"参数错误，缺少shopId");
        }

        if(shopId == 99) {
            salesOrderService.addGift(orderId, erpGoodsId, erpGoodSpecId, quantity);
            return new ApiResult<>(0, "");
        }else if(shopId == 5){
            //拼多多
            var result = orderPddService.addOrderGift(orderId,erpGoodsId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }
        else if(shopId == 8){
            //抖音
           var result = douyinOrderService.addOrderGift(orderId,erpGoodsId,erpGoodSpecId,quantity);
            return new ApiResult<>(result.getCode(), result.getMsg());
        }
//        }else if(shopId == 12){
//            //蘑菇街
//            var result = orderMogujieService.addOrderGift(orderId,erpGoodsId,erpGoodSpecId,quantity);
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }else if(shopId==13){
//            //快手
//            var result =kwaiOrderService.addOrderGift(orderId,erpGoodsId,erpGoodSpecId,quantity);
//            return new ApiResult<>(result.getCode(), result.getMsg());
//        }
        else {
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(),"店铺"+shopId+"，未实现订单赠品功能");
        }
    }
}
