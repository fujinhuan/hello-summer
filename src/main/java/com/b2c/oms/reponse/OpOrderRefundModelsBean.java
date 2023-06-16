package com.b2c.oms.reponse;

import java.util.Date;
import java.util.List;

public class OpOrderRefundModelsBean {
    /**
     * orderId : 745458336336170963
     * disputeRequest : 1
     * disputeType : 1
     * tradeTypeStr : 50060
     * isInsufficientAccount : false
     * gmtTimeOut : 20191211163615000+0800
     * productName : 2019年秋冬新款纯色弹力修身字母高领打底针织毛衣
     * buyerLoginId : 秦小磊19881104
     * refundCarriage : 450
     * isBuyerSendGoods : false
     * rejectReasonId : 0
     * isRefundGoods : false
     * applyPayment : 2640
     * gmtApply : 20191206163614000+0800
     * id : 44620450469176309
     * buyerMemberId : b2b-1075176309
     * frozenFund : -1
     * isOnlyRefund : true
     * sellerLoginId : 华衣云购
     * sellerMemberId : b2b-220122606146708908
     * rejectTimes : 0
     * isTimeOutFreeze : false
     * canRefundPayment : 2640
     * success : true
     * buyerUserId : 1075176309
     * refundPayment : 2640
     * status : refundsuccess
     * gmtModified : 20191207142547000+0800
     * alipayPaymentId : 11130401103191204086733146309
     * refundOperationList : []
     * applyReasonId : 20028
     * applyCarriage : 450
     * extInfo : {"enfunddetail":"1","ee_trace_id":"0b105aa215756999466144999e4fc5","pay_lock":"seller","bizCode":"cbu.general.refund","lastOrder":"450","newRefund":"rp2","tod":"431999883","opRole":"user","7d":"1","b2b_buyer_mId":"b2b-1075176309","refundPostFee":"450","apply_init_refund_fee":"3090","itemBuyAmount":"0","apply_text_id":"null","interceptStatus":"0","seller_batch":"true","restartForXiaoer":"0","bs_sync":"false","tos":"1","ol_tf":"2640","sgr":"1","bgmtc":"2019-12-04 13:17:12","appName":"disputecenter","payMode":"alipay","sellerDoRefundNick":"华衣云购","workflowName":"cbu_refund","ttid":"2","sync":"0","old_reason_id":"20028","abnormal_dispute_status":"0","seller_audit":"0","b2b_bt":"sp","stopAgree":"0","disputeTradeStatus":"6","b2b_exchange":"0","itemPrice":"0","isVirtual":"0","EXmrf":"3090","b2b_seller_mId":"b2b-220122606146708908"}
     * isInsufficientBail : false
     * isSupportNewSteppay : true
     * applySubReasonId : 0
     * applyReason : 不想买了，已与卖家协商一致
     * sellerUserId : 2201226061467
     * gmtCreate : 20191206163614000+0800
     * isAftersaleAgreeTimeout : false
     * gmtCompleted : 20191207142547000+0800
     * isAftersaleAutoDisburse : false
     * goodsStatus : 2
     * isGoodsReceived : true
     * isSellerDelayDisburse : false
     * isNewRefundReturn : false
     * isCrmModifyRefund : false
     * refundId : TQ44620450469176309
     * rejectReason : 货物已在物流运输中
     */

    private long orderId;
    private int disputeRequest;
    private int disputeType;
    private String tradeTypeStr;
    private boolean isInsufficientAccount;
    private Date gmtTimeOut;
    private String productName;
    private String buyerLoginId;
    private int refundCarriage;
    private boolean isBuyerSendGoods;
    private int rejectReasonId;
    private boolean isRefundGoods;
    private int applyPayment;
    private String gmtApply;
    private long id;
    private String buyerMemberId;
    private int frozenFund;
    private boolean isOnlyRefund;
    private String sellerLoginId;
    private String sellerMemberId;
    private int rejectTimes;
    private boolean isTimeOutFreeze;
    private int canRefundPayment;
    private boolean success;
    private String buyerUserId;
    private int refundPayment;
    private String status;
    private Date gmtModified;
    private String alipayPaymentId;
    private int applyReasonId;
    private int applyCarriage;
    private boolean isInsufficientBail;
    private boolean isSupportNewSteppay;
    private int applySubReasonId;
    private String applyReason;
    private long sellerUserId;
    private Date gmtCreate;
    private boolean isAftersaleAgreeTimeout;
    private String gmtCompleted;
    private boolean isAftersaleAutoDisburse;
    private int goodsStatus;
    private boolean isGoodsReceived;
    private boolean isSellerDelayDisburse;
    private boolean isNewRefundReturn;
    private boolean isCrmModifyRefund;
    private String refundId;
    private String rejectReason;
//    private List<?> refundOperationList;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getDisputeRequest() {
        return disputeRequest;
    }

    public void setDisputeRequest(int disputeRequest) {
        this.disputeRequest = disputeRequest;
    }

    public int getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(int disputeType) {
        this.disputeType = disputeType;
    }

    public String getTradeTypeStr() {
        return tradeTypeStr;
    }

    public void setTradeTypeStr(String tradeTypeStr) {
        this.tradeTypeStr = tradeTypeStr;
    }

    public boolean isIsInsufficientAccount() {
        return isInsufficientAccount;
    }

    public void setIsInsufficientAccount(boolean isInsufficientAccount) {
        this.isInsufficientAccount = isInsufficientAccount;
    }

    public Date getGmtTimeOut() {
        return gmtTimeOut;
    }

    public void setGmtTimeOut(Date gmtTimeOut) {
        this.gmtTimeOut = gmtTimeOut;
    }

    public boolean isBuyerSendGoods() {
        return isBuyerSendGoods;
    }

    public void setBuyerSendGoods(boolean buyerSendGoods) {
        isBuyerSendGoods = buyerSendGoods;
    }

    public boolean isRefundGoods() {
        return isRefundGoods;
    }

    public void setRefundGoods(boolean refundGoods) {
        isRefundGoods = refundGoods;
    }

    public boolean isOnlyRefund() {
        return isOnlyRefund;
    }

    public void setOnlyRefund(boolean onlyRefund) {
        isOnlyRefund = onlyRefund;
    }

    public boolean isTimeOutFreeze() {
        return isTimeOutFreeze;
    }

    public void setTimeOutFreeze(boolean timeOutFreeze) {
        isTimeOutFreeze = timeOutFreeze;
    }

    public boolean isInsufficientBail() {
        return isInsufficientBail;
    }

    public void setInsufficientBail(boolean insufficientBail) {
        isInsufficientBail = insufficientBail;
    }

    public boolean isSupportNewSteppay() {
        return isSupportNewSteppay;
    }

    public void setSupportNewSteppay(boolean supportNewSteppay) {
        isSupportNewSteppay = supportNewSteppay;
    }

    public boolean isAftersaleAgreeTimeout() {
        return isAftersaleAgreeTimeout;
    }

    public void setAftersaleAgreeTimeout(boolean aftersaleAgreeTimeout) {
        isAftersaleAgreeTimeout = aftersaleAgreeTimeout;
    }

    public boolean isAftersaleAutoDisburse() {
        return isAftersaleAutoDisburse;
    }

    public void setAftersaleAutoDisburse(boolean aftersaleAutoDisburse) {
        isAftersaleAutoDisburse = aftersaleAutoDisburse;
    }

    public boolean isGoodsReceived() {
        return isGoodsReceived;
    }

    public void setGoodsReceived(boolean goodsReceived) {
        isGoodsReceived = goodsReceived;
    }

    public boolean isSellerDelayDisburse() {
        return isSellerDelayDisburse;
    }

    public void setSellerDelayDisburse(boolean sellerDelayDisburse) {
        isSellerDelayDisburse = sellerDelayDisburse;
    }

    public boolean isNewRefundReturn() {
        return isNewRefundReturn;
    }

    public void setNewRefundReturn(boolean newRefundReturn) {
        isNewRefundReturn = newRefundReturn;
    }

    public boolean isCrmModifyRefund() {
        return isCrmModifyRefund;
    }

    public void setCrmModifyRefund(boolean crmModifyRefund) {
        isCrmModifyRefund = crmModifyRefund;
    }

    public boolean isInsufficientAccount() {
        return isInsufficientAccount;
    }

    public void setInsufficientAccount(boolean insufficientAccount) {
        isInsufficientAccount = insufficientAccount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBuyerLoginId() {
        return buyerLoginId;
    }

    public void setBuyerLoginId(String buyerLoginId) {
        this.buyerLoginId = buyerLoginId;
    }

    public int getRefundCarriage() {
        return refundCarriage;
    }

    public void setRefundCarriage(int refundCarriage) {
        this.refundCarriage = refundCarriage;
    }

    public boolean isIsBuyerSendGoods() {
        return isBuyerSendGoods;
    }

    public void setIsBuyerSendGoods(boolean isBuyerSendGoods) {
        this.isBuyerSendGoods = isBuyerSendGoods;
    }

    public int getRejectReasonId() {
        return rejectReasonId;
    }

    public void setRejectReasonId(int rejectReasonId) {
        this.rejectReasonId = rejectReasonId;
    }

    public boolean isIsRefundGoods() {
        return isRefundGoods;
    }

    public void setIsRefundGoods(boolean isRefundGoods) {
        this.isRefundGoods = isRefundGoods;
    }

    public int getApplyPayment() {
        return applyPayment;
    }

    public void setApplyPayment(int applyPayment) {
        this.applyPayment = applyPayment;
    }

    public String getGmtApply() {
        return gmtApply;
    }

    public void setGmtApply(String gmtApply) {
        this.gmtApply = gmtApply;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuyerMemberId() {
        return buyerMemberId;
    }

    public void setBuyerMemberId(String buyerMemberId) {
        this.buyerMemberId = buyerMemberId;
    }

    public int getFrozenFund() {
        return frozenFund;
    }

    public void setFrozenFund(int frozenFund) {
        this.frozenFund = frozenFund;
    }

    public boolean isIsOnlyRefund() {
        return isOnlyRefund;
    }

    public void setIsOnlyRefund(boolean isOnlyRefund) {
        this.isOnlyRefund = isOnlyRefund;
    }

    public String getSellerLoginId() {
        return sellerLoginId;
    }

    public void setSellerLoginId(String sellerLoginId) {
        this.sellerLoginId = sellerLoginId;
    }

    public String getSellerMemberId() {
        return sellerMemberId;
    }

    public void setSellerMemberId(String sellerMemberId) {
        this.sellerMemberId = sellerMemberId;
    }

    public int getRejectTimes() {
        return rejectTimes;
    }

    public void setRejectTimes(int rejectTimes) {
        this.rejectTimes = rejectTimes;
    }

    public boolean isIsTimeOutFreeze() {
        return isTimeOutFreeze;
    }

    public void setIsTimeOutFreeze(boolean isTimeOutFreeze) {
        this.isTimeOutFreeze = isTimeOutFreeze;
    }

    public int getCanRefundPayment() {
        return canRefundPayment;
    }

    public void setCanRefundPayment(int canRefundPayment) {
        this.canRefundPayment = canRefundPayment;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public int getRefundPayment() {
        return refundPayment;
    }

    public void setRefundPayment(int refundPayment) {
        this.refundPayment = refundPayment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getAlipayPaymentId() {
        return alipayPaymentId;
    }

    public void setAlipayPaymentId(String alipayPaymentId) {
        this.alipayPaymentId = alipayPaymentId;
    }

    public int getApplyReasonId() {
        return applyReasonId;
    }

    public void setApplyReasonId(int applyReasonId) {
        this.applyReasonId = applyReasonId;
    }

    public int getApplyCarriage() {
        return applyCarriage;
    }

    public void setApplyCarriage(int applyCarriage) {
        this.applyCarriage = applyCarriage;
    }


    public boolean isIsInsufficientBail() {
        return isInsufficientBail;
    }

    public void setIsInsufficientBail(boolean isInsufficientBail) {
        this.isInsufficientBail = isInsufficientBail;
    }

    public boolean isIsSupportNewSteppay() {
        return isSupportNewSteppay;
    }

    public void setIsSupportNewSteppay(boolean isSupportNewSteppay) {
        this.isSupportNewSteppay = isSupportNewSteppay;
    }

    public int getApplySubReasonId() {
        return applySubReasonId;
    }

    public void setApplySubReasonId(int applySubReasonId) {
        this.applySubReasonId = applySubReasonId;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(long sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public boolean isIsAftersaleAgreeTimeout() {
        return isAftersaleAgreeTimeout;
    }

    public void setIsAftersaleAgreeTimeout(boolean isAftersaleAgreeTimeout) {
        this.isAftersaleAgreeTimeout = isAftersaleAgreeTimeout;
    }

    public String getGmtCompleted() {
        return gmtCompleted;
    }

    public void setGmtCompleted(String gmtCompleted) {
        this.gmtCompleted = gmtCompleted;
    }

    public boolean isIsAftersaleAutoDisburse() {
        return isAftersaleAutoDisburse;
    }

    public void setIsAftersaleAutoDisburse(boolean isAftersaleAutoDisburse) {
        this.isAftersaleAutoDisburse = isAftersaleAutoDisburse;
    }

    public int getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(int goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public boolean isIsGoodsReceived() {
        return isGoodsReceived;
    }

    public void setIsGoodsReceived(boolean isGoodsReceived) {
        this.isGoodsReceived = isGoodsReceived;
    }

    public boolean isIsSellerDelayDisburse() {
        return isSellerDelayDisburse;
    }

    public void setIsSellerDelayDisburse(boolean isSellerDelayDisburse) {
        this.isSellerDelayDisburse = isSellerDelayDisburse;
    }

    public boolean isIsNewRefundReturn() {
        return isNewRefundReturn;
    }

    public void setIsNewRefundReturn(boolean isNewRefundReturn) {
        this.isNewRefundReturn = isNewRefundReturn;
    }

    public boolean isIsCrmModifyRefund() {
        return isCrmModifyRefund;
    }

    public void setIsCrmModifyRefund(boolean isCrmModifyRefund) {
        this.isCrmModifyRefund = isCrmModifyRefund;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }


}