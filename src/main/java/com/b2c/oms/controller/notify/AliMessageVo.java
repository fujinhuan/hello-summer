package com.b2c.oms.controller.notify;

public class AliMessageVo<T> {

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

    private String bizKey;
    private Long gmtBorn;
    private String msgId;
    private String type;
    private String userInfo;
    private String data;
    private T dataObj;

    public T getDataObj() {
        return dataObj;
    }

    public void setDataObj(T dataObj) {
        this.dataObj = dataObj;
    }

    public String getBizKey() {
        return bizKey;
    }

    public void setBizKey(String bizKey) {
        this.bizKey = bizKey;
    }

    public Long getGmtBorn() {
        return gmtBorn;
    }

    public void setGmtBorn(Long gmtBorn) {
        this.gmtBorn = gmtBorn;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
