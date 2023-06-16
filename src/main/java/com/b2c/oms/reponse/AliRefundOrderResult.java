package com.b2c.oms.reponse;

import com.b2c.entity.datacenter.DcAliOrderRefundEntity;

import java.util.List;

public class AliRefundOrderResult {

    private int totalCount;
    private int currentPageNum;
//    private List<OpOrderRefundModelsBean> opOrderRefundModels;
    private List<DcAliOrderRefundEntity> opOrderRefundModels;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public List<DcAliOrderRefundEntity> getOpOrderRefundModels() {
        return opOrderRefundModels;
    }

    public void setOpOrderRefundModels(List<DcAliOrderRefundEntity> opOrderRefundModels) {
        this.opOrderRefundModels = opOrderRefundModels;
    }
}
