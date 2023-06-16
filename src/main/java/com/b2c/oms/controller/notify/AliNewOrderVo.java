package com.b2c.oms.controller.notify;

public class AliNewOrderVo {
    /**
     *
     {
     "bizKey": "167539019420540000",
     "data": {
     "buyerMemberId": "b2b-665170100",
     "orderId": 167539019420540000,
     "currentStatus": "waitsellersend",
     "sellerMemberId": "b2b-1676547900b7bb3",
     "msgSendTime": "2018-05-30 19: 34: 27"
     },
     "gmtBorn": 1586227869437,
     "msgId": 7565094247,
     "type": "ORDER_BUYER_VIEW_ORDER_PAY",
     "userInfo": "b2b-1676547900b7bb3"
     }
     *
     */
    private String buyerMemberId;
    private Long orderId;
    private String currentStatus;
    private String sellerMemberId;
    private String msgSendTime;

    public String getBuyerMemberId() {
        return buyerMemberId;
    }

    public void setBuyerMemberId(String buyerMemberId) {
        this.buyerMemberId = buyerMemberId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getSellerMemberId() {
        return sellerMemberId;
    }

    public void setSellerMemberId(String sellerMemberId) {
        this.sellerMemberId = sellerMemberId;
    }

    public String getMsgSendTime() {
        return msgSendTime;
    }

    public void setMsgSendTime(String msgSendTime) {
        this.msgSendTime = msgSendTime;
    }
}
