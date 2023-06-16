package com.b2c.oms.reponse;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-19 15:23
 */
public class UpdateAliGoodsResult {
    private Integer code;//代码 0成功其他错误
    private String msg;//错误信息
    private Integer totalRecords;//总记录数
    private Integer totalSuccess;//更新成功数量
    private Integer totalError;//更新失败数量

    public UpdateAliGoodsResult(Integer code, String msg, Integer totalRecords, Integer totalSuccess, Integer totalError) {
        this.code = code;
        this.msg = msg;
        this.totalError = totalError;
        this.totalRecords = totalRecords;
        this.totalSuccess = totalSuccess;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getTotalSuccess() {
        return totalSuccess;
    }

    public void setTotalSuccess(Integer totalSuccess) {
        this.totalSuccess = totalSuccess;
    }

    public Integer getTotalError() {
        return totalError;
    }

    public void setTotalError(Integer totalError) {
        this.totalError = totalError;
    }
}
