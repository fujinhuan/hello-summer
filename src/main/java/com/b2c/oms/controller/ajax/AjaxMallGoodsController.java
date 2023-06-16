package com.b2c.oms.controller.ajax;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b2c.common.api.ApiResult;
import com.b2c.common.third.express.KuaiDi100ExpressClient;
import com.b2c.common.utils.DateUtil;
import com.b2c.common.utils.HttpUtil;
import com.b2c.common.utils.XmlUtil;
import com.b2c.entity.DataRow;
import com.b2c.entity.datacenter.DcSysThirdSettingEntity;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.vo.goods.GoodsAddVo;
import com.b2c.vo.goods.GoodsSpecVo;
import com.b2c.enums.EnumShopType;
import com.b2c.service.MallGoodsService;
import com.b2c.service.ShopService;
import com.b2c.service.oms.SysThirdSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ajax")
public class AjaxMallGoodsController {
    private static Logger log = LoggerFactory.getLogger(AjaxGoodsController.class);
    @Autowired
    private ShopService shopService;
    @Autowired
    private SysThirdSettingService thirdSettingService;
    @Autowired
    private MallGoodsService mallGoodsService;
    /**
     * 一键发布到云购
     *
     * @return
     */
    @RequestMapping(value = "/publish_to_yg", method = RequestMethod.POST)
    public ApiResult<Object> publishToYG(@RequestBody Long id, HttpServletRequest request) {
        var good =mallGoodsService.getMallGoods(id);
        if(good == null) return new ApiResult<>(EnumResultVo.DataError.getIndex(), "数据错误，不存在商品信息");
        if(StringUtils.isEmpty(good.getNumber()))return new ApiResult<>(EnumResultVo.DataError.getIndex(), "数据错误,商品编码为空");
        GoodsAddVo addVo = new GoodsAddVo();
        addVo.setCostPrice(BigDecimal.valueOf(0.0));
        addVo.setDeputyTitle(good.getTitle());
        addVo.setGoodsNumber(good.getNumber());
        addVo.setImage(good.getImage());
        addVo.setmDetail("");
        addVo.setPrice(good.getPrice());
        addVo.setSaleNumShow(0);
        addVo.setTitle(good.getTitle());
        addVo.setCategoryId(0);
        addVo.setCommisionRate(BigDecimal.valueOf(0));
        addVo.setTime(2);
        addVo.setKeyword("");
        addVo.setFreightTemplate(0);
        addVo.setGoodTotalQty(good.getQuantity());
        List<GoodsSpecVo> goodsSpecList = new ArrayList<>();
        if (good.getGoodItems().size()> 0) {
            for (var sp : good.getGoodItems()) {
                GoodsSpecVo goodsSpec = new GoodsSpecVo();
                goodsSpec.setColorId(0);
                goodsSpec.setColor(sp.getColorValue());
                goodsSpec.setImage(sp.getColorImage());
                goodsSpec.setSizeId(0);
                goodsSpec.setSize(sp.getSizeValue());
                goodsSpec.setStyleId(0);
                goodsSpec.setStyle("");
                goodsSpec.setPrice(sp.getPrice());
                goodsSpec.setCode(sp.getSpecNumber());
                goodsSpec.setStock(sp.getQuantity().intValue());
                goodsSpecList.add(goodsSpec);
            }
        }
        addVo.setGoodsSpec(goodsSpecList);
        Integer goodId = mallGoodsService.addGoods(addVo,"SYS");
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "发布成功，云购商品ID:" +goodId);
    }

    /**
     * 更新前的检查
     *
     * @param shopId
     * @return
     */
    public ApiResult<DcSysThirdSettingEntity> checkBefore(Integer shopId)  {
        var shop = shopService.getShop(shopId);
        if (shop == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，没有找到店铺");
        else if (shop.getType().intValue() != EnumShopType.Tmall.getIndex())
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误，店铺不是淘系店铺");
        else if (StringUtils.isEmpty(shop.getSessionKey()))
            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Token已过期，请重新授权");
        String sessionKey = shop.getSessionKey();
        var thirdConfig = thirdSettingService.getEntity(shop.getType());
        if (thirdConfig == null) return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，没有找到第三方平台的配置信息");
        else if (StringUtils.isEmpty(thirdConfig.getAppKey()))
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少appkey");
        else if (StringUtils.isEmpty(thirdConfig.getAppSecret()))
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少appSecret");
        else if (StringUtils.isEmpty(thirdConfig.getRequest_url()))
            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "系统错误，第三方平台配置信息不完整，缺少request_url");

        thirdConfig.setAccess_token(sessionKey);

        String url = thirdConfig.getRequest_url();
        String appkey = thirdConfig.getAppKey();
        String secret = thirdConfig.getAppSecret();

        /****************先查询卖家对不对***************/
/*        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        UserSellerGetRequest reqSeller = new UserSellerGetRequest();
        reqSeller.setFields("nick,user_id");
        UserSellerGetResponse rsp = client.execute(reqSeller, sessionKey);
        if (shop.getSellerUserId().longValue() != rsp.getUser().getUserId().longValue()) {
            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "当前用户是：" + rsp.getUser().getNick() + "，请重新授权");
        }*/
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", thirdConfig);
    }

    /**
     * 接口拉取订单
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_mall_goods", method = RequestMethod.POST)
    public ApiResult<Integer> getOrderList(@RequestBody DataRow reqData, HttpServletRequest request) throws Exception {
        String startDate = reqData.getString("startTime");
        String endDate = reqData.getString("endTime");
        Integer shopId=6;
        var checkResult = this.checkBefore(shopId);
        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
        }
        Integer pageIndex = 1;
        Integer pageSize = 40;
        Long endTime = System.currentTimeMillis() / 1000;//更新结束时间
        Long startTime = endTime-(60 * 60 * 24 * 7);//更新开始时间
        if(!StringUtils.isEmpty(startDate))startTime = DateUtil.dateToStamp(startDate).longValue();

        if (!StringUtils.isEmpty(endDate)) endTime = DateUtil.dateTimeToStamp(endDate + " 23:59:00").longValue();

        long kaishidaojiesu = endTime - startTime;
        long forSize = (kaishidaojiesu % (60 * 60 * 24 * 7) == 0) ? kaishidaojiesu / (60 * 60 * 24 * 7) : kaishidaojiesu / (60 * 60 * 24 * 7) + 1;//计算需要循环的次数
        for (int i = 0; i < forSize; i++) {
            Long startTime1 = startTime + i * 60 * 60 * 24 * 7;
            Long endTime1 = startTime1 + 60 * 60 * 24 * 7;
            Integer totalCount = pullGoods(checkResult.getData(),pageIndex,pageSize,startTime1,endTime1);
            int totalPage = (totalCount % pageSize == 0) ? totalCount / pageSize : (totalCount / pageSize) + 1;
            while (pageIndex < totalPage) {
                pageIndex++;
                pullGoods(checkResult.getData(),pageIndex,pageSize,startTime1,endTime1);
            }
            pageIndex=1;
        }
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
    }

    public Integer pullGoods(DcSysThirdSettingEntity result,Integer pageNo,Integer pageSize,Long startTime,Long endTime) throws Exception{
        String sendUrl="http://gw.api.taobao.com/router/rest";
        Map<String, String> params = new HashMap<>();
        params.put("method","taobao.items.onsale.get");
        params.put("app_key",result.getAppKey());
        params.put("sign_method","md5");
        params.put("timestamp",String.valueOf(System.currentTimeMillis()));
        params.put("session",result.getAccess_token());
        params.put("v","2.0");
        params.put("fields","approve_status,num_iid,title,pic_url,num,list_time,price,delist_time,outer_id,sold_quantity,modified");
        params.put("nick",result.getName());
        params.put("page_no",String.valueOf(pageNo));
        params.put("page_size",String.valueOf(pageSize));
        params.put("start_modified",DateUtil.stampToDateTime(startTime));
        params.put("end_modified",DateUtil.stampToDateTime(endTime));
        params.put("sign",KuaiDi100ExpressClient.buildSign(params,result.getAppSecret()));
        HttpResponse<String> response = HttpUtil.doPost(sendUrl,HttpUtil.map2Url(params));
        var dd = XmlUtil.xmlToJson(response.body());
        if(dd.getInteger("total_results").intValue()>0){
            var jsonArray= dd.getJSONObject("items").getJSONArray("item");
            jsonArray.forEach(obj->{
                var good = (JSONObject)obj;
                good.put("items",getGoodsSku(result,good.getLong("num_iid")));
            });
            mallGoodsService.addMallGoods(jsonArray);
        }
        return dd.getInteger("total_results");
    }

    public JSONArray getGoodsSku(DcSysThirdSettingEntity result,Long numIid){
        try {
            String sendUrl="http://gw.api.taobao.com/router/rest";
            Map<String, String> params = new HashMap<>();
            params.put("method","taobao.item.skus.get");
            params.put("app_key",result.getAppKey());
            params.put("sign_method","md5");
            params.put("timestamp",String.valueOf(System.currentTimeMillis()));
            params.put("session",result.getAccess_token());
            params.put("v","2.0");
            params.put("nick",result.getName());
            params.put("fields","properties_name,quantity,price,outer_id");
            params.put("num_iids",String.valueOf(numIid));
            params.put("sign",KuaiDi100ExpressClient.buildSign(params,result.getAppSecret()));
            HttpResponse<String> response = HttpUtil.doPost(sendUrl,HttpUtil.map2Url(params));
            var dd = XmlUtil.xmlToJson(response.body());
            return dd.getJSONObject("skus").getJSONArray("sku");
        }catch (Exception e){
            return null;
        }
    }

    @RequestMapping(value = "/query_order", method = RequestMethod.POST)
    public ApiResult<Integer> getOrder(HttpServletRequest request) throws Exception {
        Integer shopId=6;
        var checkResult = this.checkBefore(shopId);
        if (checkResult.getCode() != EnumResultVo.SUCCESS.getIndex()) {
            return new ApiResult<>(checkResult.getCode(), checkResult.getMsg());
        }
       String sendUrl="http://gw.api.taobao.com/router/rest";
        Map<String, String> params = new HashMap<>();
        params.put("method","taobao.trade.fullinfo.get");
        params.put("app_key",checkResult.getData().getAppKey());
        params.put("sign_method","md5");
        params.put("timestamp",String.valueOf(System.currentTimeMillis()));
        params.put("session",checkResult.getData().getAccess_token());
        params.put("v","2.0");
        params.put("fields","receiver_name,receiver_state,receiver_address,receiver_mobile,receiver_phone");
        params.put("tid","1107468576261645808");
        params.put("nick",checkResult.getData().getName());
        params.put("sign",KuaiDi100ExpressClient.buildSign(params,checkResult.getData().getAppSecret()));
        HttpResponse<String> response = HttpUtil.doPost(sendUrl,HttpUtil.map2Url(params));
        var dd = XmlUtil.xmlToJson(response.body());
        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
    }
}
