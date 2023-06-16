package com.b2c.oms.controller.douyin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b2c.common.api.ApiResult;
import com.b2c.common.utils.DateUtil;
import com.b2c.common.utils.ExpressClient;
import com.b2c.common.utils.MD5Utils;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.service.ShopService;
import com.b2c.service.oms.DouyinOrderService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;
import java.util.*;

import static com.b2c.common.utils.HttpUtil.map2Url;

@RequestMapping("/douyin_notify")
@RestController
public class AjaxDouyinNotifyController {
    private static Logger log = LoggerFactory.getLogger(AjaxDouyinNotifyController.class);
    @Autowired
    private DouyinOrderService douyinOrderService;
    @Autowired
    private ShopService shopService;

    @RequestMapping("/notify")
    public void newOrderNotify(HttpServletRequest request, HttpServletResponse response,BufferedReader br) throws Exception {
        log.info("收到DOUYIN通知--");
/*        Enumeration<?> enum1 = request.getHeaderNames();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            String value = request.getHeader(key);
            System.out.println(key + "\t" + value);
        }*/
        //body部分
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        System.out.println("str:" + str);
        response.getOutputStream().print("{\"code\":0,\"msg\":\"success\"}");

    }

    /**
     * 仓库退款通知处理
     * @param request
     * @param response
     * @param br
     * @throws Exception
     */
    @RequestMapping("/douyin_refund_wms_notify")
    public void douyinOrderNotify(HttpServletRequest request, HttpServletResponse response,BufferedReader br) throws Exception {
        log.info("收到仓库退款通知--");
        //body部分
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            System.out.println("str:" + str);
            var rIdObj = JSONObject.parseObject(str);
            var res =affirmRefund(rIdObj.getLong("rid"));
            log.info(res.getMsg());
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + "仓库同意退款失败："+str+e.getMessage());
        }
        //response.getOutputStream().print("{\"code\":0,\"msg\":\"success\"}");
    }

    public ApiResult<Integer> affirmRefund(Long id) throws Exception{
        Integer shopId=8;
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        String method = "afterSale.returnGoodsToWareHouseSuccess";

        String  sendUrl= "http://openapi.jinritemai.com/afterSale/returnGoodsToWareHouseSuccess";
        var refundOrder= douyinOrderService.getDouYinRefundOrderDetail(id);
        if(StringUtils.isEmpty(refundOrder))return new ApiResult<>(EnumResultVo.Fail.getIndex(), id+"订单不存在");

        if(refundOrder.getAuditStatus()==1)return new ApiResult<>(EnumResultVo.Fail.getIndex(), refundOrder.getOrderId()+"已退款");
        if(StringUtils.isEmpty(refundOrder.getLogisticsCode()))return new ApiResult<>(EnumResultVo.Fail.getIndex(),refundOrder.getOrderId()+"需更新物流");

        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
        jsonMap.put("aftersale_id",refundOrder.getAftersaleId());
        jsonMap.put("op_time",System.currentTimeMillis() / 1000);
        jsonMap.put("tracking_no",refundOrder.getLogisticsCode());
        //jsonMap.put("logistics_company_code",refundOrder.getLogisticsCode());

        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);
        //System.out.println(sign);

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("access_token",accessToken);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "2");
        params.put("sign", sign);

        try {
            HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
            if (response.statusCode() == 200) {
                JSONObject obj = JSONObject.parseObject(response.body());
                if(obj.getString("message").equals("success")){
                    douyinOrderService.updRefundOrderAuditStatus(refundOrder.getId(),1);
                }
                return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), refundOrder.getOrderId()+obj.getString("message"));
            }
        }catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.Fail.getIndex(), "退款失败");
    }
}
