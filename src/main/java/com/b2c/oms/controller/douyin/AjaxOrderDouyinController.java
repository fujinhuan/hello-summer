package com.b2c.oms.controller.douyin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b2c.common.api.ApiResult;
import com.b2c.common.api.ApiResultEnum;
import com.b2c.common.utils.*;
import com.b2c.entity.DataRow;
import com.b2c.entity.datacenter.DcShopEntity;
import com.b2c.entity.douyin.*;
import com.b2c.entity.pdd.EnumPddPrintCompany;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.enums.EnumZhuBo;
import com.b2c.enums.third.EnumDouYinOrderRefundTypeStatus;
import com.b2c.enums.third.EnumDouYinOrderStatus;
import com.b2c.oms.request.OrderConfirmReq;
import com.b2c.service.ShopService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.oms.DouyinOrderService;
import com.b2c.vo.order.OrderImportDaiFaEntity;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.*;

import static com.b2c.common.utils.HttpUtil.map2Url;

@RequestMapping("/ajax_douyin")
@RestController
public class AjaxOrderDouyinController {
    private static Logger log = LoggerFactory.getLogger(AjaxOrderDouyinController.class);
    @Autowired
    private DouyinOrderService douyinOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ErpSalesOrderService salesOrderService;

    @RequestMapping(value = "/pull_order", method = RequestMethod.POST)
    public ApiResult<Long> getOrderList(@RequestBody DataRow reqData, HttpServletRequest req) throws Exception {
        String startDate = reqData.getString("startTime");
        String endDate = reqData.getString("endTime");

        Integer shopId =reqData.getInt("shopId");//抖音shopId

        var shop = shopService.getShop(shopId);

        Long endTime = System.currentTimeMillis() / 1000;//订单更新结束时间
        Long startTime = endTime-(60 * 60 * 24 * 1);//订单更新开始时间

        if(!StringUtils.isEmpty(startDate))startTime = DateUtil.dateTimeToStamp(startDate+ " 00:00:00").longValue();

        if (!StringUtils.isEmpty(endDate)) endTime = DateUtil.dateTimeToStamp(endDate + " 23:59:00").longValue();

        Integer pageIndex = 0;
        Integer pageSize=100;
        ApiResult<Long> result=null;

         result = this.editDouyinOrder(startTime,endTime,shop,pageIndex,pageSize);

         if(result.getCode()>0) return new ApiResult<>(result.getCode(), result.getMsg());
        //计算总页数
        int totalPage = (result.getData().intValue() % pageSize == 0) ? result.getData().intValue() / pageSize : (result.getData().intValue() / pageSize) + 1;

        while (pageIndex < totalPage) {
            pageIndex++;
            result = this.editDouyinOrder(startTime,endTime,shop,pageIndex,pageSize);
        }
        return result;
    }

    private ApiResult<Long> editDouyinOrder(Long startTime, Long endTime, DcShopEntity shop, Integer pageIndex, Integer pageSize) throws Exception{
        try {
            String appKey = shop.getAppkey();
            String appSercet = shop.getAppSercet();

            String method = "order.searchList";

            String  sendUrl= "http://openapi.jinritemai.com/order/searchList";

            LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();

            jsonMap.put("create_time_end",endTime);//截至时间
            jsonMap.put("create_time_start",startTime);//开始时间trade_type
            jsonMap.put("page",pageIndex);//查询的第几页,默认值为0, 第一页下标从0开始
            jsonMap.put("size",pageSize);//每页大小 默认为10, 最大100


            JSONObject jsonObject = new JSONObject(true);
            jsonObject.putAll(jsonMap);

            String paramJson =jsonObject.toJSONString();
            String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
            String accessToken =shop.getSessionKey();
            String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";
            signStr = appSercet+signStr+appSercet;


            String sign = MD5Utils.stringToMD5(signStr);


            Map<String, String> params = new HashMap<>();
            params.put("app_key", appKey);
            params.put("access_token",accessToken);
            params.put("method", method);
            params.put("param_json",paramJson);
            params.put("timestamp", timestamp);
            params.put("v", "2");
            params.put("sign", sign);
            HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
            JSONObject obj = JSONObject.parseObject(response.body());
            if(!obj.getString("err_no").equals("0")) return new ApiResult<>(EnumResultVo.DataError.getIndex(), obj.getString("message"));
            var resObj = obj.getJSONObject("data");
            if(resObj.getLong("total").intValue()==0)return new ApiResult<>(EnumResultVo.DataError.getIndex(),"无订单可以更新");
            var jsonArray= resObj.getJSONArray("shop_order_list");
            for(var json:jsonArray){
                var jsonTemp=(JSONObject)json;

                DcDouyinOrdersEntity douYinOrder= JsonUtil.strToObject(jsonTemp.toJSONString(),DcDouyinOrdersEntity.class);
                //queryOrderDetail(douYinOrder.getOrderId());
                if(jsonTemp.getJSONArray("logistics_info").size()>0){
                    var objTemp = (JSONObject)jsonTemp.getJSONArray("logistics_info").get(0);
                    douYinOrder.setLogisticsCode(objTemp.getString("tracking_no"));
                    douYinOrder.setLogisticsCompany(objTemp.getString("company_name"));
                }
                var address = JsonUtil.strToObject(douYinOrder.getPostAddr(),DcDouyinAddressVo.class);
                douYinOrder.setProvince(address.getProvince().getName());
                douYinOrder.setCity(address.getCity().getName());
                douYinOrder.setTown(address.getTown().getName());
                douYinOrder.setStreet(address.getStreet().getName());
                douYinOrder.setEncryptDetail(address.getEncryptDetail());
                if(douYinOrder.getOrderStatus()==2){
                    String phoneKey=douYinOrder.getEncrypt_post_tel().substring(0,douYinOrder.getEncrypt_post_tel().indexOf("$",douYinOrder.getEncrypt_post_tel().indexOf("$")+1));
                    douYinOrder.setPhoneKey(phoneKey+"$");
                    String addressKey=address.getEncryptDetail().substring(0,address.getEncryptDetail().indexOf("#",address.getEncryptDetail().indexOf("#")+1));
                    douYinOrder.setAddressKey(addressKey+"#");
                }
                var res = douyinOrderService.editDouYinOrder(douYinOrder,0);
                if(res.getCode()>0)log.info(douYinOrder.getOrderId()+"更新异常"+res.getMsg());
            }
            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS",resObj.getLong("total"));
        } catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
    }

    /**
     * 获取电子面单
     * @param reqData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/douyin_order_print_code", method = RequestMethod.POST)
    public ApiResult<String> douyin_order_print_code(@RequestBody DataRow reqData) throws Exception {
        var shop = shopService.getShop(reqData.getInt("shopId"));
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        synchronized (this) {
            var orders = (ArrayList) reqData.getObject("orders");
            String company=reqData.getString("company");
            Integer ishb=reqData.getInt("isHebing");
            Integer print=reqData.getInt("print");

            for (var orderSn : orders) {
                String method = "logistics.newCreateOrder";

                String orderSn_=String.valueOf(orderSn);

                Long orderId=Long.parseLong(orderSn_);

                //检查库存
                var checkOrder = douyinOrderService.getPrintOrderList(orderId,ishb,print);

                if(checkOrder.getCode()>0){
                    log.info("电子面单取号错误"+orderId+"--"+checkOrder.getMsg());
                    //return new ApiResult<>(EnumResultVo.DataError.getIndex(), checkOrder.getMsg());
                    continue;
                }

                var ordersCode = checkOrder.getData();

/*                if(order.getOrderStatus()!=2){
                    log.info("电子面单取号错误"+orderId+"订单状态不是备货中不能取号");
                    continue;
                }*/

                var order=ordersCode.get(0);

                //if(order.getPrintStatus().intValue()>0)continue;//return new ApiResult<>(EnumResultVo.DataError.getIndex(),"无法取号");

                List<LinkedHashMap<String, Object>> goodItems=new ArrayList<>();
                LinkedHashMap<String, Object> item =new LinkedHashMap<>();
                item.put("item_count",order.getComboNum());
                item.put("item_name",order.getProductName());
                goodItems.add(item);


                LinkedHashMap<String, Object> receiver_address =new LinkedHashMap<>();
                receiver_address.put("city_name",order.getCity());
                receiver_address.put("country_code","CHN");
                receiver_address.put("detail_address",order.getEncryptDetail());
                receiver_address.put("district_name",order.getTown());
                receiver_address.put("province_name",order.getProvince());
                receiver_address.put("street_name",order.getStreet());

                LinkedHashMap<String, Object> receiver_contact =new LinkedHashMap<>();
                receiver_contact.put("mobile",order.getEncryptPostTel());
                receiver_contact.put("name",order.getEncryptPostReceiver());


                LinkedHashMap<String, Object> sender_address =new LinkedHashMap<>();
                sender_address.put("city_name","东莞市");
                sender_address.put("country_code","CHN");
                sender_address.put("detail_address","聚新二路42号华衣云商");
                sender_address.put("district_name","大朗镇");
                sender_address.put("province_name","广东省");
                //sender_address.put("street_name","");

                LinkedHashMap<String, Object> sender_contact =new LinkedHashMap<>();
                sender_contact.put("mobile","13018605585");
                sender_contact.put("name","杨辉");

                //String str="{\"delivery_req\":{},\"logistics_code\":\"yunda\",\"order_infos\":[{\"items\":[{\"item_count\":10,\"item_name\":\"dd\"}],\"order_id\":\"13136156\",\"receiver_info\":{\"address\":{\"city_name\":\"dd\",\"country_code\":\"dfd\",\"detail_address\":\"dd\",\"district_name\":\"dd\",\"province_name\":\"dd\"},\"contact\":{\"mobile\":\"dd\",\"name\":\"dd\",\"phone\":\"dd\"}},\"warehouse\":{\"is_sum_up\":\"true\"}}],\"sender_info\":{\"address\":{\"city_name\":\"dd\",\"country_code\":\"dd\",\"detail_address\":\"dd\",\"district_name\":\"dd\",\"province_name\":\"dd\"},\"contact\":{\"mobile\":\"13077847784\",\"name\":\"dd\",\"phone\":\"\"}}}";

                StringBuilder builder=new StringBuilder("{");
                //builder.append("\"logistics_code\":\"").append(reqData.getString("company")).append(",");
                builder.append("\"logistics_code\":\"").append(reqData.getString("company")).append("\",");
                builder.append("\"order_infos\":[{\"items\":").append(JSON.toJSONString(goodItems));//商品列表
                //builder.append(",\"net_info\":{\"net_code\":\"511716\"}");
                builder.append(",\"order_id\":").append(order.getOrderId()).append(",");
                builder.append("\"receiver_info\":{\"address\":");
                builder.append(JSON.toJSONString(receiver_address)).append(",");
                builder.append("\"contact\":").append(JSON.toJSONString(receiver_contact)).append("}");
                builder.append("}],");
                //builder.append(",\"warehouse\":{\"is_sum_up\":\"true\"}}],");
                builder.append("\"sender_info\":{\"address\":");
                builder.append(JSON.toJSONString(sender_address)).append(",");
                builder.append("\"contact\":").append(JSON.toJSONString(sender_contact)).append("}}");

                String paramJson =builder.toString();
                //System.out.println(paramJson);

                String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");
                String accessToken =shop.getSessionKey();
                String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";
                signStr = appSercet+signStr+appSercet;

                String sign = MD5Utils.MD5Encode(signStr);

                String  sendUrl= "http://openapi.jinritemai.com/logistics/newCreateOrder";

                Map<String, String> params = new HashMap<>();
                params.put("app_key", appKey);
                params.put("access_token",accessToken);
                params.put("method", method);
                params.put("param_json",paramJson);
                params.put("timestamp", timestamp);
                params.put("v", "2");
                params.put("sign", sign);

                HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
                if (response.statusCode() == 200) {
                    try {
                        JSONObject resObj = JSONObject.parseObject(response.body());
                        if(resObj.getString("err_no").equals("0")){
                            for(var billInfo : resObj.getJSONObject("data").getJSONArray("ebill_infos")){
                                var billObj=(JSONObject)billInfo;
                                for (var o : ordersCode) {
                                    douyinOrderService.updOrderLogisticsCode(Long.parseLong(o.getOrderId()),company,billObj.getString("track_no"),1);
                                    log.info(o.getOrderId()+":"+billObj.getString("track_no"));
                                }
                            }
                            for(var billInfo : resObj.getJSONObject("data").getJSONArray("err_infos")){
                                var billObj=(JSONObject)billInfo;
                                for (var o : ordersCode) {
                                    douyinOrderService.updOrderPrint(Long.parseLong(o.getOrderId()),billObj.getString("err_msg"),order.getOrderStatus(),0);
                                    log.info(o.getOrderId()+":"+billObj.getString("err_msg"));
                                }
                            }
                        }else {
                            log.info(orderSn_+"取号异常"+resObj.getString("message"));
                        }
                    }catch (Exception ex){
                        log.info(orderSn_+"取号异常ex"+ex.getMessage());
                        continue;
                    }


                }
            }
            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "取号成功");
        }
    }


    @RequestMapping(value = "/douyin_cancel_print_code", method = RequestMethod.POST)
    public ApiResult<Integer> douyinCancelCode(@RequestBody DataRow reqData) throws Exception{
        var shop = shopService.getShop(reqData.getInt("shopId"));
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        String method = "logistics.cancelOrder";

        String  sendUrl= "http://openapi.jinritemai.com/logistics/cancelOrder";

        var order = douyinOrderService.getOderDetailByOrderId(reqData.getLong("orderId"));

        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();

        jsonMap.put("logistics_code",order.getLogisticsId());//查询的第几页,默认值为0, 第一页下标从0开始
        jsonMap.put("track_no",order.getLogisticsCode());//每页大小 默认为10, 最大100

        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("access_token",accessToken);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "2");
        params.put("sign", sign);
        HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
        JSONObject obj = JSONObject.parseObject(response.body());
        log.info(order.getOrderId()+":"+obj.toJSONString());
        String msg="";
        if(!obj.getString("err_no").equals("0")){
            msg=obj.getString("message");
        }else {
            msg="取消成功";
        }
        var result = douyinOrderService.cancelOrderPrint(reqData.getLong("orderId"));

        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"仓库取消"+result.getMsg()+",抖店取消"+msg);
    }

    @RequestMapping(value = "/douyin_order_print", method = RequestMethod.POST)
    public ApiResult<String>  wayBillApply(@RequestBody DataRow reqData) {
        var shop = shopService.getShop(reqData.getInt("shopId"));
        String appKey = shop.getAppkey();

        synchronized (this) {
            try {
                var order = douyinOrderService.getOderDetailByPrint(reqData.getString("code"),1);

                if(StringUtils.isEmpty(order))return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"订单不存在");

                //if(order.getPrintStatus().intValue()==2)return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),"已打印");

                var printResp = getPrint(shop,order.get(0).getLogisticsId(),order.get(0).getLogisticsCode());
                if (printResp.statusCode() == 200) {
                    var  printObj = JSONObject.parseObject(printResp.body());
                    if(printObj.getString("err_no").equals("30002"))return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),printObj.getString("message"));
                    var billInfo = printObj.getJSONObject("data").getJSONArray("waybill_infos").getJSONObject(0);

                    StringBuilder desc = new StringBuilder("");
                    Integer count=0;
                    for (var item : order) {
                        count=count+item.getComboNum();
                        desc.append(item.getCode());
                        desc.append("(" +item.getCouponInfo()+ item.getSpecDesc() + ")");
                        desc.append(item.getComboNum()).append("件");
                        desc.append(item.getLocationNumber()).append("\\n");
                    }
                    desc.append("共：").append(count).append("件");
                    desc.append(order.get(0).getBuyerWords()+" "+order.get(0).getSellerWords());
                    LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();

                    JSONObject jsonObject = new JSONObject(true);
                    jsonObject.putAll(jsonMap);

                    String paramJson =jsonObject.toJSONString();


                    String method = "logistics.getShopKey";
                    String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
                    String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";
                    signStr = shop.getAppSercet()+signStr+shop.getAppSercet();
                    String sign = MD5Utils.MD5Encode(signStr);

                    Map<String, String> params = new HashMap<>();
                    params.put("app_key", appKey);
                    params.put("access_token",shop.getSessionKey());
                    params.put("method", method);
                    params.put("param_json",paramJson);
                    params.put("timestamp", timestamp);
                    params.put("v", "2");
                    params.put("sign", sign);

                    //String yundaTempate="https://cloudprint.douyinec.com/api/logistics/davinci/template/xml/fPU3Mttc4rRb28tbiGUP4yGSgEVzvDc2FLxGo4snRcMoZIqGQXxbE5QNbEbKUbKIMm3eUqPJGr9ZxdgQ6GtVAHa3YichvOAyZ3uy";
                    String template="https://sf3-ttcdn-tos.pstatp.com/obj/logistics-davinci/template/template_76.xml";

                    String taskId = billInfo.getString("track_no") + "_" + OrderNumberUtils.getOrderIdByTime();
                    StringBuilder sb =new StringBuilder("{\n" +
                            "\t\"cmd\": \"print\",\n" +
                            "\t\"requestID\": \"123458976\",\n" +
                            "\t\"version\": \"1.0\",\n" +
                            "\t\"task\": {\n" +
                            "\t\t\"taskID\":").append("\"").append(taskId).append("\",\n");
                    sb.append("\"printer\":").append("\"").append(reqData.getString("printName")).append("\",");
                    sb.append( "\t\t\"preview\": false,\n" +
                            "\t\t\"documents\": [{\n");
                    sb.append("\"documentID\":").append("\"").append(taskId).append("\",");
                    sb.append("\t\t\t\"contents\": [{\n");
                    sb.append("\t\t\t\t\"params\":" ).append("\"").append(map2Url(params)).append("\",\n" );
                    sb.append("\t\t\t\t\"signature\":").append("\"").append(billInfo.getString("sign")).append("\",\n");
                    sb.append("\t\t\t\t\"encryptedData\":").append("\"").append(billInfo.getString("print_data")).append("\",\n");
                    sb.append("\t\t\t\t\"templateURL\":" ).append("\"").append(template).append("\",\n" );
                    sb.append("\t\t\t\t\"addData\": {\n" +
                            "\t\t\t\t\t\"senderInfo\": {\n" +
                            "\t\t\t\t\t\t\"address\": {\n" +
                            "\t\t\t\t\t\t\t\"cityName\": \"东莞市\",\n" +
                            "\t\t\t\t\t\t\t\"countryCode\": \"CHN\",\n" +
                            "\t\t\t\t\t\t\t\"detailAddress\": \"聚新二路42号华衣云商\",\n" +
                            "\t\t\t\t\t\t\t\"districtName\": \"大朗镇\",\n" +
                            "\t\t\t\t\t\t\t\"provinceName\": \"广东省\"" +
                            "\t\t\t\t\t\t},\n" +
                            "\t\t\t\t\t\t\"contact\": {\n" +
                            "\t\t\t\t\t\t\t\"mobile\": \"13018605585\",\n" +
                            "\t\t\t\t\t\t\t\"name\": \"杨生\"\n" +
                            "\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t}\n" +
                            "\t\t\t\t}\n" +
                            "\t\t\t}, {\n" +
                            "\t\t\t\t\"data\": {\n");
                    sb.append("\t\t\t\t\t\"abc\":").append("\"").append(desc.toString()).append("\"\n");
                    sb.append("\t\t\t\t},\n" +
                            "\t\t\t\t\"templateURL\": \"https://cloudprint.douyinec.com/api/logistics/davinci/template/xml/fPU3Mttc4rRb28tbiGUP4yGSgEVzvDc2FLxGo4snRcMoZIqGQXxbE5QNbEbKUbKIMm3eUqPJGr9ZxdgQ6GtVAHa3YichvOAyZ3uy\"\n" +
                            "\t\t\t}]\n" +
                            "\t\t}]\n" +
                            "\t}\n" +
                            "}");
                    douyinOrderService.updOrderPrintByCode(order.get(0).getLogisticsCode(),2,"已打印",2);
                    return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功",sb.toString());
                }
            }catch (Exception e){
                return new ApiResult<>(EnumResultVo.Fail.getIndex(), "失败");
            }
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "失败");
        }
    }


    @RequestMapping(value = "/douyin_order_repeat_print", method = RequestMethod.POST)
    public ApiResult<String>  douyin_order_repeat_print(@RequestBody DataRow reqData) throws Exception{
        var shop = shopService.getShop(reqData.getInt("shopId"));
        String appKey = shop.getAppkey();

        var order = douyinOrderService.getOderDetailByPrint(reqData.getString("code"),2);

        var printResp = getPrint(shop,order.get(0).getLogisticsId(),order.get(0).getLogisticsCode());
        if (printResp.statusCode() == 200) {
            var  printObj = JSONObject.parseObject(printResp.body());
            if(printObj.getString("err_no").equals("30002"))return new ApiResult<>(EnumResultVo.ParamsError.getIndex(),printObj.getString("message"));
            var billInfo = printObj.getJSONObject("data").getJSONArray("waybill_infos").getJSONObject(0);
            StringBuilder desc = new StringBuilder("");
            Integer count=0;
            for (var item : order) {
                count=count+item.getComboNum();
                desc.append(item.getCode());
                desc.append("(" +item.getCouponInfo()+item.getSpecDesc() + ")");
                desc.append(item.getComboNum()).append("件");
                desc.append(item.getLocationNumber()).append("\\n");
            }
            desc.append("共：").append(count).append("件");
            desc.append(order.get(0).getBuyerWords()+" "+order.get(0).getSellerWords());
            LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();


            JSONObject jsonObject = new JSONObject(true);
            jsonObject.putAll(jsonMap);

            String paramJson =jsonObject.toJSONString();


            String method = "logistics.getShopKey";
            String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
            String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";
            signStr = shop.getAppSercet()+signStr+shop.getAppSercet();
            String sign = MD5Utils.MD5Encode(signStr);

            Map<String, String> params = new HashMap<>();
            params.put("app_key", appKey);
            params.put("access_token",shop.getSessionKey());
            params.put("method", method);
            params.put("param_json",paramJson);
            params.put("timestamp", timestamp);
            params.put("v", "2");
            params.put("sign", sign);

            //String yundaTempate="https://cloudprint.douyinec.com/api/logistics/davinci/template/xml/fPU3Mttc4rRb28tbiGUP4yGSgEVzvDc2FLxGo4snRcMoZIqGQXxbE5QNbEbKUbKIMm3eUqPJGr9ZxdgQ6GtVAHa3YichvOAyZ3uy";
            String template="https://sf3-ttcdn-tos.pstatp.com/obj/logistics-davinci/template/template_76.xml";

            String taskId = billInfo.getString("track_no") + "_" + OrderNumberUtils.getOrderIdByTime();
            StringBuilder sb =new StringBuilder("{\n" +
                    "\t\"cmd\": \"print\",\n" +
                    "\t\"requestID\": \"123458976\",\n" +
                    "\t\"version\": \"1.0\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"taskID\":").append("\"").append(taskId).append("\",\n");
            sb.append("\"printer\":").append("\"").append(reqData.getString("printName")).append("\",");
            sb.append( "\t\t\"preview\": false,\n" +
                    "\t\t\"documents\": [{\n");
            sb.append("\"documentID\":").append("\"").append(taskId).append("\",");
            sb.append("\t\t\t\"contents\": [{\n");
            sb.append("\t\t\t\t\"params\":" ).append("\"").append(map2Url(params)).append("\",\n" );
            sb.append("\t\t\t\t\"signature\":").append("\"").append(billInfo.getString("sign")).append("\",\n");
            sb.append("\t\t\t\t\"encryptedData\":").append("\"").append(billInfo.getString("print_data")).append("\",\n");
            sb.append("\t\t\t\t\"templateURL\":" ).append("\"").append(template).append("\",\n" );
            sb.append("\t\t\t\t\"addData\": {\n" +
                    "\t\t\t\t\t\"senderInfo\": {\n" +
                    "\t\t\t\t\t\t\"address\": {\n" +
                    "\t\t\t\t\t\t\t\"cityName\": \"东莞市\",\n" +
                    "\t\t\t\t\t\t\t\"countryCode\": \"CHN\",\n" +
                    "\t\t\t\t\t\t\t\"detailAddress\": \"聚新二路42号华衣云商\",\n" +
                    "\t\t\t\t\t\t\t\"districtName\": \"大朗镇\",\n" +
                    "\t\t\t\t\t\t\t\"provinceName\": \"广东省\"" +
                    "\t\t\t\t\t\t},\n" +
                    "\t\t\t\t\t\t\"contact\": {\n" +
                    "\t\t\t\t\t\t\t\"mobile\": \"13018605585\",\n" +
                    "\t\t\t\t\t\t\t\"name\": \"杨生\"\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t}, {\n" +
                    "\t\t\t\t\"data\": {\n");
            sb.append("\t\t\t\t\t\"abc\":").append("\"").append(desc.toString()).append("\"\n");
            sb.append("\t\t\t\t},\n" +
                    "\t\t\t\t\"templateURL\": \"https://cloudprint.douyinec.com/api/logistics/davinci/template/xml/fPU3Mttc4rRb28tbiGUP4yGSgEVzvDc2FLxGo4snRcMoZIqGQXxbE5QNbEbKUbKIMm3eUqPJGr9ZxdgQ6GtVAHa3YichvOAyZ3uy\"\n" +
                    "\t\t\t}]\n" +
                    "\t\t}]\n" +
                    "\t}\n" +
                    "}");
            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS",sb.toString());
        }
        return new ApiResult<>(EnumResultVo.Fail.getIndex(), "失败");
    }



    public HttpResponse<String> getPrint(DcShopEntity shop,String codeId,String code) throws Exception{
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method = "logistics.waybillApply";

        String  sendUrl= "http://openapi.jinritemai.com/logistics/waybillApply";


        List<LinkedHashMap<String, Object>> waybill_applies=new ArrayList<>();
        LinkedHashMap<String, Object> waybill_applie =new LinkedHashMap<>();
        waybill_applie.put("logistics_code",codeId);
        waybill_applie.put("track_no",code);
        waybill_applies.add(waybill_applie);

        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();

        jsonMap.put("waybill_applies",waybill_applies);//开始时间trade_type

        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("access_token",accessToken);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "2");
        params.put("sign", sign);
        HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
        return  response;
        //return JSONObject.parseObject(response.body());
    }
    /**
     * 订单确认
     * @return
     */
    @RequestMapping(value = "/affirm_order", method = RequestMethod.POST)
    public ApiResult<Integer> orderAffirm(@RequestBody OrderConfirmReq req){
/*        if (req.getOrderId() == null || req.getOrderId() <= 0)
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");*/

        if (StringUtils.isEmpty(req.getClientId()))req.setClientId(0);

/*        if (StringUtils.isEmpty(req.getReceiver()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少receiver");
        if (StringUtils.isEmpty(req.getMobile()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少mobile");
        if (StringUtils.isEmpty(req.getAddress()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少address");*/
        var result = douyinOrderService.douyinOrderAffirm(req.getOrderId(),0,"", "", "", "");
        return new ApiResult<>(result.getCode(), result.getMsg());
    }
    /**
     * 订单发货
     * @return
     */
    @RequestMapping(value = "/douyin_send_order", method = RequestMethod.POST)
    public ApiResult<Integer> orderSend(@RequestBody DataRow reqData) throws Exception{
        Integer shopId=reqData.getInt("shopId");
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        var orders = (ArrayList) reqData.getObject("orders");
        if (orders.isEmpty()) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择需要发货的订单");

        for (var orderNo : orders) {
            String orderSn=String.valueOf(orderNo);
            try {
                var order = douyinOrderService.getOderDetailByOrderId(Long.parseLong(orderSn));
                String method = "order.logisticsAdd";
                LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
                jsonMap.put("company_code",order.getLogisticsId());//快递公司id
                jsonMap.put("logistics_code",order.getLogisticsCode());
                jsonMap.put("order_id",order.getOrderId());//订单id


                JSONObject jsonObject = new JSONObject(true);
                jsonObject.putAll(jsonMap);

                String paramJson =jsonObject.toJSONString();
                //System.out.println(paramJson);
                String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

                String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";
                signStr = appSercet+signStr+appSercet;

                String sign = MD5Utils.stringToMD5(signStr);

                String  sendUrl= "https://openapi-fxg.jinritemai.com/order/logisticsAdd";

                Map<String, String> params = new HashMap<>();
                params.put("app_key", appKey);
                params.put("access_token",shop.getSessionKey());
                params.put("method", method);
                params.put("param_json",paramJson);
                params.put("timestamp", timestamp);
                params.put("v", "2");
                params.put("sign", sign);


                HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
                if (response.statusCode() == 200) {
                    JSONObject obj = JSONObject.parseObject(response.body());
                    if(obj.getString("message").equals("success")){
                        var resObj = douyinOrderService.orderSend(order);
                        if(resObj.getCode()>0){
                            log.info(orderSn+"发货异常"+resObj.getMsg());
                            //return new ApiResult<>(EnumResultVo.DataError.getIndex(),"平台"+resObj.getMsg());
                        }
                    }else {
                        douyinOrderService.updOrderPrint(Long.parseLong(orderSn),obj.getString("message"),order.getOrderStatus(),2);
                        //return new ApiResult<>(EnumResultVo.DataError.getIndex(), obj.getString("message"));
                    }
                }
            }catch (Exception e){
                log.info(orderSn+"发货异常"+e.getMessage());
                //return new ApiResult<>(EnumResultVo.DataError.getIndex(), "抖音异常");
                continue;
            }
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功");
    }


    @RequestMapping(value = "/douyin_order_status_by_orders", method = RequestMethod.POST)
    public ApiResult<Integer> douyin_order_status_by_orders(@RequestBody DataRow reqData) throws Exception{
        Integer shopId=reqData.getInt("shopId");
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method = "order.orderDetail";

        String  sendUrl= "http://openapi.jinritemai.com/order/orderDetail";
        var orders = (ArrayList) reqData.getObject("orders");
        if (orders.isEmpty()) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择需要更新的订单");

        for (var orderNo : orders) {
            String orderSn=String.valueOf(orderNo);
            try {
                LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
                jsonMap.put("shop_order_id",orderSn);//查询的第几页,默认值为0, 第一页下标从0开始

                JSONObject jsonObject = new JSONObject(true);
                jsonObject.putAll(jsonMap);

                String paramJson =jsonObject.toJSONString();
                String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
                String accessToken =shop.getSessionKey();
                String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

                String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);


                Map<String, String> params = new HashMap<>();
                params.put("app_key", appKey);
                params.put("access_token",accessToken);
                params.put("method", method);
                params.put("param_json",paramJson);
                params.put("timestamp", timestamp);
                params.put("v", "2");
                params.put("sign", sign);

                HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
                if (response.statusCode() == 200) {
                    Integer orderStatus = JSONObject.parseObject(response.body()).getJSONObject("data").getJSONObject("shop_order_detail").getInteger("order_status");
                    douyinOrderService.updDouyinOrderStatus(orderSn,orderStatus);
                }
            }catch (Exception e){
                log.info(orderSn+"更新打单失败订单异常"+e.getMessage());
                //return new ApiResult<>(EnumResultVo.DataError.getIndex(), "抖音异常");
                continue;
            }
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功");
    }

    /**
     * api查询订单详情
     * @param orderId
     * @throws Exception
     */
    public String queryOrderTrackingNo(DcShopEntity shop,String orderId) throws Exception{
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method = "order.orderDetail";

        String  sendUrl= "http://openapi.jinritemai.com/order/orderDetail";

        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
        jsonMap.put("shop_order_id",orderId);//查询的第几页,默认值为0, 第一页下标从0开始


        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);


        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("access_token",accessToken);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "2");
        params.put("sign", sign);

        HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
        //return JSONObject.parseObject(response.body()).getJSONObject("data").getJSONObject("shop_order_detail").getInteger("order_status");

        var res = JSONObject.parseObject(response.body()).getJSONObject("data").getJSONObject("shop_order_detail").getJSONArray("logistics_info");
         var code =(JSONObject)res.get(0);
        //System.out.println(code.getString("tracking_no"));
        return code.getString("tracking_no");

    }

    /**
     * 更新退货订单列表
     * @param reqData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_refund_order", method = RequestMethod.POST)
    public ApiResult<Long> pull_refund_order1(@RequestBody DataRow reqData) throws Exception{
        Integer shopId=reqData.getInt("shopId");
        var shop = shopService.getShop(shopId);

        String startDate = reqData.getString("startTime");
        String endDate = reqData.getString("endTime");

        String orderId = reqData.getString("orderNum");

        Long endTime = System.currentTimeMillis() / 1000;//订单更新结束时间
        Long startTime = endTime-(60 * 60 * 24 * 5);//订单更新开始时间

        if(!StringUtils.isEmpty(startDate))startTime = DateUtil.dateTimeToStamp(startDate+ " 00:00:00").longValue();

        if (!StringUtils.isEmpty(endDate)) endTime = DateUtil.dateTimeToStamp(endDate + " 23:59:00").longValue();

        Integer pageIndex = 0;
        Integer pageSize=100;
        ApiResult<Long> result=null;

        result = this.pullRefundOrder(shop,pageIndex,pageSize,startTime,endTime,orderId);

        if(result.getCode()>0) return new ApiResult<>(result.getCode(), result.getMsg());
        //计算总页数
        if(StringUtils.isEmpty(orderId)){
            int totalPage = (result.getData().intValue() % pageSize == 0) ? result.getData().intValue() / pageSize : (result.getData().intValue() / pageSize) + 1;

            while (pageIndex < totalPage) {
                pageIndex++;
                result = this.pullRefundOrder(shop,pageIndex,pageSize,startTime,endTime,orderId);
            }
        }
        return result;

    }

    public ApiResult<Long> pullRefundOrder(DcShopEntity shop,Integer page,Integer size,Long startTime,Long endTime,String orderId){

        String method = "afterSale.List";
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        String  sendUrl= "http://openapi.jinritemai.com/afterSale/List";

        try {
            LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
            if(StringUtils.isEmpty(orderId)) jsonMap.put("end_time",endTime);
            //jsonMap.put("order_by","apply_time");
            if(!StringUtils.isEmpty(orderId)){
                jsonMap.put("order_id",orderId);
            }

            jsonMap.put("page",page);//查询的第几页,默认值为0, 第一页下标从0开始
            jsonMap.put("size",size);//每页大小 默认为10, 最大100
            if(StringUtils.isEmpty(orderId)) jsonMap.put("start_time",startTime);

            JSONObject jsonObject = new JSONObject(true);
            jsonObject.putAll(jsonMap);

            String paramJson =jsonObject.toJSONString();
            String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
            String accessToken =shop.getSessionKey();
            String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

            String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);


            Map<String, String> params = new HashMap<>();
            params.put("app_key", appKey);
            params.put("access_token",accessToken);
            params.put("method", method);
            params.put("param_json",paramJson);
            params.put("timestamp", timestamp);
            params.put("v", "2");
            params.put("sign", sign);
            HttpResponse<String> response = ExpressClient.doPost(sendUrl, map2Url(params));
            JSONObject obj = JSONObject.parseObject(response.body());
            if(!obj.getString("err_no").equals("0")) return new ApiResult<>(EnumResultVo.DataError.getIndex(), obj.getString("message"));
            var resObj = obj.getJSONObject("data");
            if(resObj.getLong("total").intValue()==0)return new ApiResult<>(EnumResultVo.DataError.getIndex(),"无售后单可以更新");
            var jsonArray= resObj.getJSONArray("items");
            for(var json:jsonArray){
                var tempObj = (JSONObject)json;
                //System.out.println(tempObj);
                DcDouyinOrdersRefundEntity refundEntity=new DcDouyinOrdersRefundEntity();
                refundEntity.setShopId(shop.getId());
                refundEntity.setAftersaleId(tempObj.getJSONObject("aftersale_info").getLong("aftersale_id"));
                refundEntity.setApplyTime(tempObj.getJSONObject("aftersale_info").getLong("apply_time"));
                refundEntity.setRefundStatus(tempObj.getJSONObject("aftersale_info").getInteger("aftersale_status"));
                refundEntity.setAftersaleType(tempObj.getJSONObject("aftersale_info").getInteger("aftersale_type"));
                refundEntity.setTotalAmount(tempObj.getJSONObject("aftersale_info").getDouble("refund_amount"));
                refundEntity.setComboNum(tempObj.getJSONObject("aftersale_info").getInteger("aftersale_num"));
                refundEntity.setOrderId(tempObj.getJSONObject("order_info").getString("shop_order_id"));
                refundEntity.setQuestionDesc(tempObj.getJSONObject("text_part").getString("reason_text"));
                refundEntity.setLogisticsCode(tempObj.getJSONObject("aftersale_info").getString("return_logistics_code"));
                refundEntity.setLogisticsCompany(tempObj.getJSONObject("aftersale_info").getString("return_logistics_company_name"));

                refundEntity.setItems(JsonUtil.strToList(tempObj.getJSONObject("order_info").getJSONArray("related_order_info").toJSONString(),DcDouyinOrdersRefundItemEntity.class));
                if(refundEntity.getAftersaleType() == EnumDouYinOrderRefundTypeStatus.FHTK.getIndex()){
                    refundEntity.setLogisticsCode(this.queryOrderTrackingNo(shop,refundEntity.getOrderId()));
                    refundEntity.setBuyerWords("拦截退货");
                }
                var res = douyinOrderService.editDouYinRefundOrder(refundEntity);

                log.info(refundEntity.getOrderId()+":"+res.getMsg());
            }
            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS",resObj.getLong("total"));

        }catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
    }


    /**
     * 查询子订单退款信息
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/refundProcessDetail", method = RequestMethod.POST)
    public ApiResult<Integer> pull_refund_order_detail(@RequestBody DataRow req) throws Exception{
        Integer shopId=8;
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        String method = "afterSale.refundProcessDetail";

        String  sendUrl= "http://openapi.jinritemai.com/afterSale/refundProcessDetail";


        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
        jsonMap.put("order_id","4842595065265300673");


        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        System.out.println(paramJson);
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);
        System.out.println(sign);



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
                System.out.println(obj.toJSONString());
            }
        }catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
    }

    /**
     * 抖音同意退货退款
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addressAppliedSwitch", method = RequestMethod.POST)
    public ApiResult<Integer> addressAppliedSwitch(@RequestBody DataRow req) throws Exception{
        Integer shopId=8;
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();

        String method = "order.AddressAppliedSwitch";

        String  sendUrl= "http://openapi.jinritemai.com/order/AddressAppliedSwitch";

        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
        jsonMap.put("is_allowed",0);




        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        System.out.println(paramJson);
        String timestamp = DateUtil.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");
        String accessToken =shop.getSessionKey();
        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"2";

        String sign = MD5Utils.stringToMD5(appSercet+signStr+appSercet);
        System.out.println(sign);



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
                if(!obj.getString("err_no").equals("0")) return new ApiResult<>(EnumResultVo.DataError.getIndex(), obj.getString("message"));
                System.out.println(obj.toJSONString());
            }
        }catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
    }

    /**
     * 抖音同意退货退款
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/returnGoodsToWareHouseSuccess", method = RequestMethod.POST)
    public ApiResult<Integer> returnGoodsToWareHouseSuccess(@RequestBody DataRow req) throws Exception{
        var res =affirmRefund(req.getLong("id"));
        return new ApiResult<>(res.getCode(), res.getMsg());
    }

    @RequestMapping(value = "/refund_order_affirm", method = RequestMethod.POST)
    public ApiResult<Integer> affirmRefunds() throws Exception{
        String str="7040048639758057736,7040049527474143503,7040295478004695296,7040295778937553188,7040083657201123584,7039610089178792203,7039610330330251551,7036687489842676000,7040021863124173093,7038911613067460868,7039250941131981094,7037530670679195943,7038589449793487136,7039903577455771915,7040431169824882984,7038520485243715855,7039510031355609378,7038520292292985103,7040034616337580299,7039698107327643916,7040263942547030272,7040357854112465152,7040391711561941288,7040461068392612096,7040461218292842767,7040623726882242827,7039621476764483870,7039251555442311437,7039262632867545357,7039662578984829224,7038966670022476047,7040622210855223563,7038986432957923598,7040377796325097736,7038569770945347848,7038569588031897896,7038549326771011852,7040096770004664589,7040436475976171812,7040681642297475340,7040061064746156326,7040386255397339428,7040681454824767751,7040430836914716931,7038783193710428424,7040413246397956393,7039296751165047075,7039653764650893583,7039892099369615620,7040054564371874083,7040457226800136485,7040368338001789222";
        String strs[] =str.split(",");
        Integer shopId=8;
        var shop = shopService.getShop(shopId);

        Long endTime = System.currentTimeMillis() / 1000;//订单更新结束时间
        Long startTime = endTime-(60 * 60 * 24 * 5);//订单更新开始时间
        for(var rid:strs){
            //批量更新订单
            //var res= this.pullRefundOrder(shop,0,100,startTime,endTime,rid);
            //批量处理售后
            var res =affirmRefund(Long.parseLong(rid));
            if(res.getCode()>0){
                log.info(res.getMsg());
            }
            //获取订单发货物流信息
            //this.queryOrderTrackingNo(shop,rid);
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "成功");
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



    /**
     * 更新退货订单
     * @param req
     * @return
     */
    @RequestMapping(value = "/pull_refund_order_old", method = RequestMethod.POST)
    public ApiResult<Integer> pullRefundOrder(@RequestBody DataRow req){
        Integer shopId=8;
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method = "afterSale.orderList";
        String refundMethod="refund.orderList";
        Long endTime=System.currentTimeMillis() / 1000;//订单更新结束时间
        Long startTime=endTime-(60 * 60 * 24 * 7);//订单更新开始时间
        if(!StringUtils.isEmpty(req.get("startTime"))) startTime=DateUtil.dateTimeToStamp(req.getString("startTime")+" 00:00:00").longValue();
        if(!StringUtils.isEmpty(req.get("endTime")))endTime=DateUtil.dateTimeToStamp(req.getString("endTime")+" 23:59:00").longValue();
        LinkedHashMap<String, Object> jsonMap =new LinkedHashMap<>();
        jsonMap.put("end_time",DateUtil.unixTimeStampToDate2(endTime,"yyyy/MM/dd HH:mm:ss"));//截至时间
        jsonMap.put("order_by","create_time");
        jsonMap.put("page","0");
        jsonMap.put("size","20");
        jsonMap.put("start_time",DateUtil.unixTimeStampToDate2(startTime,"yyyy/MM/dd HH:mm:ss"));//开始时间

        JSONObject jsonObject = new JSONObject(true);
        jsonObject.putAll(jsonMap);

        String paramJson =jsonObject.toJSONString();
        String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"1";
        signStr = appSercet+signStr+appSercet;

        String sign = MD5Utils.MD5Encode(signStr);

        String  sendUrl= "https://openapi.jinritemai.com";

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "1");
        params.put("sign", sign);

        try {
            HttpResponse<String> response = ExpressClient.doPost(sendUrl+"/afterSale/orderList", map2Url(params));
            if (response.statusCode() == 200) {
                JSONObject obj = JSONObject.parseObject(response.body()).getJSONObject("data");
                if(obj.getLong("count").intValue()==0)return new ApiResult<>(EnumResultVo.DataError.getIndex(),"无售后订单可以更新");
                var jsonArray= obj.getJSONArray("list");
                for(var json:jsonArray){
                    DcDouyinOrdersRefundEntity refundOrder= JsonUtil.strToObject(JSON.toJSONString(json),DcDouyinOrdersRefundEntity.class);
                    refundOrder.setLogisticsCode("");
                    var detail = afterSaleOrderDetail(refundOrder.getOrderId());
                    if(detail!=null){
                        refundOrder.setQuestionDesc(detail.getApplyInfo().getQuestionDesc());
                        refundOrder.setApplyTime(DateUtil.dateTimeToStamp(detail.getProcessBar().getApplyTime()));
                        refundOrder.setLogisticsCompany(detail.getStageInfo().getLogisticsInfo().getLogisticsName());
                        refundOrder.setLogisticsCode(detail.getStageInfo().getLogisticsInfo().getLogisticsCode());
                    }
                    douyinOrderService.editDouYinRefundOrder(refundOrder);
                }
            }
            //更新退款订单
            String refundSignStr = "app_key"+appKey+"method"+refundMethod+"param_json"+paramJson+"timestamp"+timestamp+"v"+"1";
            refundSignStr = appSercet+refundSignStr+appSercet;
            String refundSign = MD5Utils.MD5Encode(refundSignStr);
            params.put("method",refundMethod);
            params.put("param_json",paramJson);
            params.put("sign", refundSign);
            HttpResponse<String> refundResponse = ExpressClient.doPost(sendUrl+"/refund/orderList", map2Url(params));
            if (refundResponse.statusCode() == 200) {
                JSONObject obj = JSONObject.parseObject(refundResponse.body()).getJSONObject("data");
                if(obj.getLong("count").intValue()==0)return new ApiResult<>(EnumResultVo.DataError.getIndex(),"无售后订单可以更新");
                var jsonArray= obj.getJSONArray("list");
                for(var json:jsonArray){
                    JSONObject jsonObj=(JSONObject)json;
                    DcDouyinOrdersRefundEntity refundOrder= JsonUtil.strToObject(JSON.toJSONString(json),DcDouyinOrdersRefundEntity.class);
                    JSONObject goodObj = jsonObj.getJSONArray("child").getJSONObject(0);
                    refundOrder.setTotalAmount(jsonObj.getDouble("order_total_amount"));
                    refundOrder.setProductName(goodObj.getString("product_name"));
                    refundOrder.setProductPic(goodObj.getString("product_pic"));
                    refundOrder.setPid(goodObj.getString("pid"));
                    refundOrder.setComboNum(jsonObj.getJSONArray("child").size());
                    refundOrder.setCode(jsonObj.getString("code"));
                    refundOrder.setQuestionDesc(refundOrder.getSellerWords());
                    refundOrder.setApplyTime(refundOrder.getUpdateTime());
                    douyinOrderService.editDouYinRefundOrder(refundOrder);
                }
            }
        } catch (Exception e) {
            return new ApiResult<>(EnumResultVo.Fail.getIndex(), "系统异常："+e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");

    }

    //售后订单详情
    public DcDouyinOrdersRefundDetail afterSaleOrderDetail(String refundOrderId){
        Integer shopId=8;
        var shop = shopService.getShop(shopId);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method = "afterSale.Detail";
        String paramJson ="{\"after_sale_id\":\""+refundOrderId+"\"}";
        String timestamp = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

        String signStr = "app_key"+appKey+"method"+method+"param_json"+paramJson+"timestamp"+timestamp+"v"+"1";
        signStr = appSercet+signStr+appSercet;

        String sign = MD5Utils.MD5Encode(signStr);

        Map<String, String> params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("method", method);
        params.put("param_json",paramJson);
        params.put("timestamp", timestamp);
        params.put("v", "1");
        params.put("sign", sign);
        try {
            String  sendUrl= "https://openapi-fxg.jinritemai.com";
            HttpResponse<String> detailResponse = ExpressClient.doPost(sendUrl+"/afterSale/Detail", map2Url(params));
            JSONObject detailJson = JSONObject.parseObject(detailResponse.body());
            System.out.println(detailJson.toJSONString());
            if(detailJson.getString("message").equals("success")){
                JSONObject datailObj = detailJson.getJSONObject("data").getJSONObject("data");
                return JsonUtil.strToObject(JSON.toJSONString(datailObj),DcDouyinOrdersRefundDetail.class);
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }
    /**
     * 确认订单到仓库
     * @param data
     * @param req
     * @return
     */
    @RequestMapping(value = "/confirm_refund", method = RequestMethod.POST)
    public ApiResult<String> reviewRefund(@RequestBody DataRow data, HttpServletRequest req) {
        Long refId = data.getLong("refundId");
        if(refId == null || refId<=0) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少id");
        if(StringUtils.isEmpty(data.get("logisticsCompany")))return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少物流公司");
        if(StringUtils.isEmpty(data.get("logisticsCode")))return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，缺少物流单号");
        var result = douyinOrderService.confirmRefund(data.getLong("refundId"),data.getString("logisticsCompany"),data.getString("logisticsCode"));
        return new ApiResult<>(result.getCode(), result.getMsg());
    }


    /**
     * 抖音授权
     * @param data
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/oauth", method = RequestMethod.POST)
    public ApiResult<Integer> oauth(@RequestBody DataRow data, HttpServletRequest req) throws Exception {
        var shop = shopService.getShop(data.getInt("shopId"));
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
         LinkedHashMap<String, Object> jsonMap1 =new LinkedHashMap<>();
        jsonMap1.put("code","");//截至时间
        jsonMap1.put("grant_type","authorization_self");
        jsonMap1.put("shop_id", shop.getSellerUserId());

        JSONObject jsonObject1 = new JSONObject(true);
        jsonObject1.putAll(jsonMap1);

        String paramJson1 =jsonObject1.toJSONString();

        String timestamp1 = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

        String signStr1 = "app_key"+appKey+"method"+"token.create"+"param_json"+paramJson1+"timestamp"+timestamp1+"v"+"2";
        signStr1= appSercet+signStr1+appSercet;

        String sign1 = MD5Utils.MD5Encode(signStr1);
        Map<String, String> params1 = new HashMap<>();
        params1.put("app_key", appKey);
        params1.put("method", "token.create");
        params1.put("param_json",paramJson1);
        params1.put("timestamp", timestamp1);
        params1.put("v","2");
        params1.put("sign",sign1);

        HttpResponse<String> response1 = ExpressClient.doPost("https://openapi-fxg.jinritemai.com/token/create", map2Url(params1));

        var resObj = JSONObject.parseObject(response1.body());
        if(!StringUtils.isEmpty(resObj.getJSONObject("data"))){
            String accessToken = resObj.getJSONObject("data").getString("access_token");
            shopService.updateSessionKey(data.getInt("shopId"),shop.getSellerUserId(),accessToken);
            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"授权成功");
        }

        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"授权失败");

    }

    /**
     * 获取商品
     * @param data
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_goods", method = RequestMethod.POST)
    public ApiResult<Integer> pullGoods(@RequestBody DataRow data, HttpServletRequest req) throws Exception {
        var shop = shopService.getShop(8);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        LinkedHashMap<String, Object> jsonMap1 =new LinkedHashMap<>();
        jsonMap1.put("page","1");//截至时间
        jsonMap1.put("size","1");

        JSONObject jsonObject1 = new JSONObject(true);
        jsonObject1.putAll(jsonMap1);

        String paramJson1 =jsonObject1.toJSONString();

        String timestamp1 = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

        String signStr1 = "app_key"+appKey+"method"+"product.listV2"+"param_json"+paramJson1+"timestamp"+timestamp1+"v"+"2";
        signStr1= appSercet+signStr1+appSercet;

        String sign1 = MD5Utils.MD5Encode(signStr1);
        Map<String, String> params1 = new HashMap<>();
        params1.put("app_key", appKey);
        params1.put("access_token",shop.getSessionKey());
        params1.put("method", "product.listV2");
        params1.put("param_json",paramJson1);
        params1.put("timestamp", timestamp1);
        params1.put("v","2");
        params1.put("sign",sign1);

        HttpResponse<String> response1 = ExpressClient.doPost("https://openapi-fxg.jinritemai.com/product/listV2", map2Url(params1));

        var resObj = JSONObject.parseObject(response1.body());
        if(!StringUtils.isEmpty(resObj.getJSONObject("data"))){
            var jsonArray= resObj.getJSONObject("data").getJSONArray("data");
            for(var json:jsonArray){
                JSONObject jsonObj=(JSONObject)json;

                String method="sku.list";
                LinkedHashMap<String, Object> jsonMap2 =new LinkedHashMap<>();
                jsonMap2.put("product_id","3501019669068923416");//截至时间

                JSONObject jsonObject2 = new JSONObject(true);
                jsonObject2.putAll(jsonMap2);

                String paramJson2 =jsonObject2.toJSONString();

                String timestamp2 = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

                String signStr2 = "app_key"+appKey+"method"+method+"param_json"+paramJson2+"timestamp"+timestamp2+"v"+"2";
                signStr2= appSercet+signStr2+appSercet;

                String sign2 = MD5Utils.MD5Encode(signStr2);
                Map<String, String> params2 = new HashMap<>();
                params2.put("app_key", appKey);
                params2.put("access_token",shop.getSessionKey());
                params2.put("method", method);
                params2.put("param_json",paramJson2);
                params2.put("timestamp", timestamp2);
                params2.put("v","2");
                params2.put("sign",sign2);

                HttpResponse<String> response2 = ExpressClient.doPost("https://openapi-fxg.jinritemai.com/sku/list", map2Url(params2));

                var resObj2 = JSONObject.parseObject(response2.body());
                System.out.println(resObj2.toJSONString());

/*                List<DataRow> dataList =new ArrayList<>();
                var skuArray = resObj2.getJSONArray("spec_prices");
                for(var sku:skuArray){
                    var obj=(JSONObject)sku;
                    DataRow skuData =new DataRow();
                    skuData.set("",obj.getString(""));
                }*/
            }
        }

        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"授权失败");

    }

    /**
     * 查询商家和物流商的订购关系以及物流单号使用情况
     * @param data
     * @param req
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listShopNetsite", method = RequestMethod.POST)
    public void listShopNetsite(@RequestBody DataRow data, HttpServletRequest req) throws Exception {
        var shop = shopService.getShop(8);
        String appKey = shop.getAppkey();
        String appSercet = shop.getAppSercet();
        String method="order.logisticsCompanyList";
        LinkedHashMap<String, Object> jsonMap1 =new LinkedHashMap<>();
        //jsonMap1.put("logistics_code","");//截至时间

        JSONObject jsonObject1 = new JSONObject(true);
        jsonObject1.putAll(jsonMap1);

        String paramJson1 =jsonObject1.toJSONString();

        String timestamp1 = DateUtil.dateToString(new Date(),"yyyy/MM/dd HH:mm:ss");

        String signStr1 = "app_key"+appKey+"method"+method+"param_json"+paramJson1+"timestamp"+timestamp1+"v"+"2";
        signStr1= appSercet+signStr1+appSercet;

        String sign1 = MD5Utils.MD5Encode(signStr1);
        Map<String, String> params1 = new HashMap<>();
        params1.put("app_key", appKey);
        params1.put("access_token",shop.getSessionKey());
        params1.put("method", method);
        params1.put("param_json",paramJson1);
        params1.put("timestamp", timestamp1);
        params1.put("v","2");
        params1.put("sign",sign1);

        HttpResponse<String> response1 = ExpressClient.doPost("https://openapi-fxg.jinritemai.com/order/logisticsCompanyList", map2Url(params1));

        var resObj = JSONObject.parseObject(response1.body());
        System.out.println(resObj.toJSONString());
    }

    @RequestMapping(value = "/orders_upd_sku", method = RequestMethod.POST)
    public ApiResult<Integer> ordersUpdSku(@RequestBody DataRow reqData, HttpServletRequest req) throws Exception {
        var orders = (ArrayList) reqData.getObject("orders");
        if (orders.isEmpty()) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择需要批量修改的订单");
        var res = douyinOrderService.ordersUpdSku(orders,reqData.getString("oldSku"),reqData.getString("newSku"));

        return new ApiResult<>(res.getCode(), res.getMsg());
    }

    @RequestMapping(value = "/orders_upd_sku_by_id", method = RequestMethod.POST)
    public ApiResult<Integer> ordersUpdSkuById(@RequestBody DataRow reqData, HttpServletRequest req) throws Exception {
        if (StringUtils.isEmpty(reqData.get("newSku"))) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请输入新SKU");
        var res = douyinOrderService.ordersUpdSkuById(reqData.getLong("id"),reqData.getString("newSku"));

        return new ApiResult<>(res.getCode(), res.getMsg());
    }

    /***
     * 一件代发（直播）订单导入excel数据解析ajax
     * @param file
     * @param req
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @RequestMapping(value = "/zhubo_order_import_excel_settle", method = RequestMethod.POST)
    public ApiResult<Integer> zhubo_order_import_excel_settle(@RequestParam("excel") MultipartFile file, HttpServletRequest req) throws IOException, InvalidFormatException {

        String fileName = file.getOriginalFilename();
        String dir = System.getProperty("user.dir");
        String destFileName = dir + File.separator + "uploadedfiles_" + fileName;
//        System.out.println(destFileName);
        File destFile = new File(destFileName);
        file.transferTo(destFile);
        log.info("/***********导入一件代发订单开始，文件后缀" + destFileName.substring(destFileName.lastIndexOf(".")) + "***********/");

        XSSFSheet sheet;
        InputStream fis = null;

        fis = new FileInputStream(destFileName);

        //订单list
        List<Long> orderList = new ArrayList<>();

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
            log.info("/***********导入一件代发订单***读取excel文件成功***********/");

            sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();//最后一行索引
            XSSFRow row = null;

            //订单实体
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                String orderId = row.getCell(0).getStringCellValue();

                orderList.add(Long.parseLong(orderId));

            }
            douyinOrderService.zhuBoOrderSettle(orderList,1);
        }catch (Exception e){
            log.info("/***********导入一件代发订单***出现异常：" + e.getMessage() + "***********/");
            return new ApiResult<>(500, e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"成功");
    }

    /***
     * 一件代发（直播）订单导入excel数据解析ajax
     * @param file
     * @param req
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @RequestMapping(value = "/zhubo_order_import_excel_test", method = RequestMethod.POST)
    public ApiResult<Integer> zhubo_order_import_excel_test(@RequestParam("excel") MultipartFile file, HttpServletRequest req) throws IOException, InvalidFormatException {

        String fileName = file.getOriginalFilename();
        String dir = System.getProperty("user.dir");
        String destFileName = dir + File.separator + "uploadedfiles_" + fileName;
//        System.out.println(destFileName);
        File destFile = new File(destFileName);
        file.transferTo(destFile);
        log.info("/***********导入一件代发订单开始，文件后缀" + destFileName.substring(destFileName.lastIndexOf(".")) + "***********/");

        XSSFSheet sheet;
        InputStream fis = null;

        fis = new FileInputStream(destFileName);

        //订单list
        List<DataRow> orderList = new ArrayList<>();

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
            log.info("/***********导入一件代发订单***读取excel文件成功***********/");

            sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();//最后一行索引
            XSSFRow row = null;

            //订单实体
            for (int i = 1; i <= lastRowNum; i++) {
                DataRow data= new DataRow();
                row = sheet.getRow(i);
                String zb = row.getCell(2).getStringCellValue();
                data.set("zb",zb);
                data.set("zhibo_date",row.getCell(3).getStringCellValue());
                if(StringUtils.isEmpty(EnumZhuBo.getName(zb)))continue;
                data.set("zhanghao", EnumZhuBo.getName(zb));
                data.set("ljgk",row.getCell(8).getStringCellValue());
                data.set("fs",row.getCell(15).getStringCellValue());
                data.set("dds",row.getCell(23).getStringCellValue());
                data.set("zfje",StringUtil.getCellValue(row.getCell(24)));
                data.set("xdrs",StringUtil.getCellValue(row.getCell(26)));
                data.set("yj",StringUtil.getCellValue(row.getCell(30)));
                data.set("tuije",StringUtil.getCellValue(row.getCell(28)));
                data.set("tuidds",StringUtil.getCellValue(row.getCell(27)));
                data.set("tuirs",StringUtil.getCellValue(row.getCell(29)));

                orderList.add(data);

            }
            douyinOrderService.test(orderList);
        }catch (Exception e){
            log.info("/***********导入一件代发订单***出现异常：" + e.getMessage() + "***********/");
            return new ApiResult<>(500, e.getMessage());
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"成功");
    }


}
