package com.b2c.oms.controller.mogujie;

import com.b2c.oms.controller.mogujie.pojo.OpenApiOrderDetailResDto;

import java.util.List;

public class MogujieOrderResult {
    private Integer totalNum;
    private Boolean hasNext;
    private List<OpenApiOrderDetailResDto> openApiOrderDetailResDtos;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<OpenApiOrderDetailResDto> getOpenApiOrderDetailResDtos() {
        return openApiOrderDetailResDtos;
    }

    public void setOpenApiOrderDetailResDtos(List<OpenApiOrderDetailResDto> openApiOrderDetailResDtos) {
        this.openApiOrderDetailResDtos = openApiOrderDetailResDtos;
    }
}
