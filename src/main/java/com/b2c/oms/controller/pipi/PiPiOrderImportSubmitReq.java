package com.b2c.oms.controller.pipi;

import com.b2c.vo.order.OrderImportPiPiEntity;

import java.util.List;

public class PiPiOrderImportSubmitReq {

    private Integer buyerUserId;//购买客户id

    private List<OrderImportPiPiEntity> orderList;//导入的订单

    public Integer getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Integer buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public List<OrderImportPiPiEntity> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderImportPiPiEntity> orderList) {
        this.orderList = orderList;
    }
}
