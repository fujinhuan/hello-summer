package com.b2c.oms.request;

import com.b2c.vo.order.OrderImportDaiFaEntity;

import java.util.List;

public class DaiFaOrderImportSubmitReq {

    private List<OrderImportDaiFaEntity> orderList;//导入的订单

    private Integer buyerUserId; //客户id

    public Integer getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Integer buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public List<OrderImportDaiFaEntity> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderImportDaiFaEntity> orderList) {
        this.orderList = orderList;
    }
}
