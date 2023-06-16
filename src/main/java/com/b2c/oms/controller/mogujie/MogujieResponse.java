package com.b2c.oms.controller.mogujie;

import com.alibaba.fastjson.JSONObject;

public class MogujieResponse {
    /**
     * {"result":{},"st":1589160174559,"status":{"msg":"AccessToken不存在，请重新授权","code":"0000011"}}
     */
    private JSONObject result;
    private Long st;
    private StatusVo status;

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public Long getSt() {
        return st;
    }

    public void setSt(Long st) {
        this.st = st;
    }

    public StatusVo getStatus() {
        return status;
    }

    public void setStatus(StatusVo status) {
        this.status = status;
    }

    protected class  StatusVo{
       private String code;
       private String msg;

       public String getCode() {
           return code;
       }

       public void setCode(String code) {
           this.code = code;
       }

       public String getMsg() {
           return msg;
       }

       public void setMsg(String msg) {
           this.msg = msg;
       }
   }
}


