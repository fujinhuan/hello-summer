package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.common.api.ApiResultEnum;
import com.b2c.entity.DataRow;
import com.b2c.entity.erp.vo.ExpressInfoVo;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.vo.oms.OrderRefundApplyItemVo;
import com.b2c.vo.oms.OrderRefundApplyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/ajax_refund")
@RestController
public class AjaxOrderRefundController {
    @Autowired
    private ErpSalesOrderService salesOrderService;
    /**
     * 用户提交售后单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund_apply_submit", method = RequestMethod.POST)
    public ApiResult<Integer> addOrderCancel(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getParameter("order_id")))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少order_id");
        if (StringUtils.isEmpty(request.getParameterValues("item[]")))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少item[]");

        synchronized (this) {
            Long orderId = Long.parseLong(request.getParameter("order_id"));

            var order = salesOrderService.getDetailById(orderId);
            if (order == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单不存在");

            //组合退货参数
            OrderRefundApplyVo applyVo = new OrderRefundApplyVo();
            applyVo.setOrderId(orderId);

            //退货商品list
            List<OrderRefundApplyItemVo> itemVoList = new ArrayList<>();

//            String[] itemIdArr = request.getParameterValues("items");
            String[] idDataArr = request.getParameterValues("item[]");
            String[] nums = request.getParameterValues("count[]");

            Integer totalRefund = 0;
            //循环查找选中的item
            for (int i = 0; i < idDataArr.length; i++) {
                String[] idData = idDataArr[i].split(":");
                Long orderItemId = Long.parseLong(idData[1]);
                Integer quantity = Integer.parseInt(nums[Integer.parseInt(idData[0])]);
                //查找出orderItem并赋新SKUiD值
                var orderItem = order.getItems().stream().filter(it -> it.getId().longValue() == orderItemId.longValue()).findFirst().get();
                if (orderItem == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单orderItem不存在");

                //判断可退货数量
                if (quantity > (orderItem.getQuantity() - orderItem.getRefundCount()))
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), orderItem.getSpecNumber() + "可退货数量不足");
                //组合退货item数据
                OrderRefundApplyItemVo itemVo = new OrderRefundApplyItemVo();
                itemVo.setOrderItemId(orderItemId);
                itemVo.setReturnCount(quantity);

                totalRefund += itemVo.getReturnCount();
                itemVoList.add(itemVo);
            }

            applyVo.setReturnItems(itemVoList);

            if (totalRefund <= 0) {
                return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "没有填写退货数量");
            }
            var res = salesOrderService.refundApply(applyVo);
            return new ApiResult<>(res.getCode(), res.getMsg());
        }
    }

    /**
     * 同意退货（并将退货信息推送到仓库）
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/refund_agree_submit", method = RequestMethod.POST)
    public ApiResult<String> reviewRefundOffline(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.getString("id")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：缺少退货id");

        if (StringUtils.isEmpty(data.getString("companyCode")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择快递公司");
        if (StringUtils.isEmpty(data.getString("code")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入快递单号");

        ExpressInfoVo express = new ExpressInfoVo();
        express.setLogisticsCompany(data.getString("company"));
        express.setLogisticsCode(data.getString("code"));
        express.setLogisticsCompanyCode(data.getString("companyCode"));
        express.setAddress(data.getString("address"));

        //同意退货并推送到仓库
        var result = salesOrderService.refundAgree(data.getLong("id"), express);

        return new ApiResult<>(result.getCode(), result.getMsg());
    }

    /**
     * 退款拒绝
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/refund_refuse_submit", method = RequestMethod.POST)
    public ApiResult<String> refuseRefund(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.getString("id")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：缺少退货id");

        var result = salesOrderService.refundRefuse(data.getLong("id"));
        return new ApiResult<>(result.getCode(), result.getMsg());
    }
}
