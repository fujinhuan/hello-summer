package com.b2c.oms.controller.yg;


import com.b2c.common.api.ApiResult;
import com.b2c.entity.DataRow;
import com.b2c.entity.enums.OrderCancelStateEnums;
import com.b2c.entity.erp.vo.ExpressInfoVo;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.entity.result.ResultVo;

import com.b2c.service.mall.OrderCancelService;
import com.b2c.service.oms.OrderYungouService;
import com.b2c.vo.oms.OrderRefundApplyItemVo;
import com.b2c.vo.oms.OrderRefundApplyVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/order_yungou")
@RestController
public class AjaxYGOrderRefundController {
    @Autowired
    private OrderCancelService orderCancelService;
    @Autowired
    private OrderYungouService orderYungouService;

    private static Logger log = LoggerFactory.getLogger(AjaxYGOrderRefundController.class);


    /**
     * 用户提交售后单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund_apply_submit", method = RequestMethod.POST)
    public ApiResult<Integer> addOrderCancel(HttpServletRequest request) {

        log.info("申请退货");
        synchronized (this) {
            Long orderId = Long.parseLong(request.getParameter("order_id"));
            OrderRefundApplyVo applyVo = new OrderRefundApplyVo();
            applyVo.setOrderId(orderId);
            List<OrderRefundApplyItemVo> itemVoList = new ArrayList<>();

//            String[] itemIdArr = request.getParameterValues("items");
            String[] idDataArr = request.getParameterValues("item[]");
            String[] nums = request.getParameterValues("count[]");
            //循环查找选中的item
            for (int i = 0; i < idDataArr.length; i++) {
                String[] idData = idDataArr[i].split(":");
                OrderRefundApplyItemVo itemVo = new OrderRefundApplyItemVo();
                itemVo.setOrderItemId(Long.parseLong(idData[1]));
                itemVo.setReturnCount(Integer.parseInt(nums[Integer.parseInt(idData[0])]));
                itemVoList.add(itemVo);
            }

            applyVo.setReturnItems(itemVoList);

            var res = orderCancelService.addBuyerOrderCancel(applyVo);
            return new ApiResult<>(res.getCode(), res.getMsg());
        }
    }


    /**
     * 同意退货（批发单、直播单）
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/reviewRefund_offline", method = RequestMethod.POST)
    public ApiResult<String> reviewRefundOffline(@RequestBody DataRow data, HttpServletRequest req) {
        if (StringUtils.isEmpty(data.getString("companyCode")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择快递公司");
        if (StringUtils.isEmpty(data.getString("code")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入快递单号");
        if (StringUtils.isEmpty(data.getString("address")))
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入快递单号");
        ExpressInfoVo express = new ExpressInfoVo();
        express.setLogisticsCompany(data.getString("company"));
        express.setLogisticsCode(data.getString("code"));
        express.setLogisticsCompanyCode(data.getString("companyCode"));
        express.setAddress(data.getString("address"));

        //同意退货并推送到仓库
        ResultVo result = orderYungouService.reviewRefundOffline(data.getLong("id"), OrderCancelStateEnums.Delivered.getIndex(), express);

        return new ApiResult<>(result.getCode(), result.getMsg());
    }

    /**
     * 退款审核通过（云购零售单）
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/reviewRefund", method = RequestMethod.POST)
    public ApiResult<String> reviewRefund(@RequestBody DataRow data, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String userNm = session.getAttribute("userName").toString();

        ResultVo<Integer> resultVo = orderYungouService.reviewRefund(data.getLong("id"), OrderCancelStateEnums.Agree.getIndex(), userNm, "售后退款审核通过");
        return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());
    }

    @RequestMapping(value = "/refund_notify_wms", method = RequestMethod.POST)
    public ApiResult<String> wmsNotify(@RequestBody DataRow data, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String userNm = session.getAttribute("userName").toString();
        ResultVo<Integer> resultVo = null;//dcOrderService.refundNotifyWms(data.getLong("refundId"));
        return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());
    }
}
