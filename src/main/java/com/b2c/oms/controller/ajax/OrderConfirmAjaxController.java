package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.common.api.ApiResultEnum;
import com.b2c.entity.result.ResultVo;
import com.b2c.oms.request.OrderConfirmReq;
import com.b2c.service.erp.ErpSalesOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单确认控制器
 */
@RestController
public class OrderConfirmAjaxController {
    private static Logger log = LoggerFactory.getLogger(OrderConfirmAjaxController.class);

    @Autowired
    private ErpSalesOrderService salesOrderService;



    /**
     * 确认订单(外发单)
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/ajax_order_confirm/order_confirm_submit", method = RequestMethod.POST)
    public ApiResult<Integer> confirmOrderPiPi(@RequestBody OrderConfirmReq req) {
        if (req.getOrderId() == null || req.getOrderId() <= 0)
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");
        if (StringUtils.isEmpty(req.getReceiver()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少receiver");
        if (StringUtils.isEmpty(req.getMobile()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少mobile");
        if (StringUtils.isEmpty(req.getAddress()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少address");

        ResultVo<Integer> result = salesOrderService.orderConfirmAndJoinDeliveryQueueForSales(req.getOrderId(), req.getReceiver(), req.getMobile(), req.getAddress(), req.getSellerMemo());

        return new ApiResult<>(result.getCode(), result.getMsg());
    }

    /**
     * 确认订单(外发单)
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/ajax_order_confirm/order_cancel", method = RequestMethod.POST)
    public ApiResult<Integer> cancelOrder(@RequestBody OrderConfirmReq req) {
        if (req.getOrderId() == null || req.getOrderId() <= 0)
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");

       salesOrderService.cancelOrder(req.getOrderId());

        return new ApiResult<>(ApiResultEnum.SUCCESS);
    }
}
