//package com.b2c.oms.controller.ali;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.ocean.rawsdk.ApiExecutor;
//import com.alibaba.product.param.AlibabaProductListGetParam;
//import com.alibaba.product.param.AlibabaProductPageResult;
//import com.alibaba.product.param.AlibabaProductProductInfo;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.datacenter.DcAliGoodsEntity;
//import com.b2c.entity.datacenter.DcAliGoodsSkuEntity;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.enums.EnumShopType;
//import com.b2c.oms.reponse.UpdateAliGoodsResult;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.AliGoodsService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * 描述：
// * 阿里店铺商品AJAX
// *
// * @author qlp
// * @date 2019-09-11 15:24
// */
//@RequestMapping("/ali_ajax")
//@RestController
//public class AjaxAliGoodsController {
//
//    private static Logger log = LoggerFactory.getLogger(AjaxAliGoodsController.class);
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private AliGoodsService aliGoodsService;
//
//    /**
//     * 更新阿里商品列表
//     *
//     * @return
//     */
//    @RequestMapping(value = "/upd_ali_goods", method = RequestMethod.POST)
//    public ApiResult<Object> updGoods(HttpServletRequest request) {
//
//        HttpSession session = request.getSession();
//        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
//        if(userId != 1 ) return new ApiResult<>(EnumResultVo.NotLogin.getIndex(), "没有权限");
//
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var settingEntity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
//
//        //不存在ali token 跳转获取
//        if (StringUtils.isEmpty(settingEntity.getAccess_token())) {
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//        //判断token是否过期
//        Date date = new Date();
//        long time = date.getTime(); //这是毫秒数
//        long expireTime = settingEntity.getAccess_token_begin() * 1000 + settingEntity.getExpires_in() * 1000;
//        if (expireTime < time) {
//            //已过期
////                return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
//            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//        }
//
//
//        try {
//
//            int pageSize = 20;
//            int pageIndex = 1;
//            int totalSuccess = 0;
//            int totalError = 0;
//
//
//            //第一次获取
//            UpdateAliGoodsResult upResult = updAliGoods(pageIndex, 20, settingEntity.getAccess_token());
//            if (upResult.getCode().intValue() == 401) {
//                //没有授权；
//                return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
//            }
//            totalSuccess += upResult.getTotalSuccess();
//            totalError += upResult.getTotalError();
//            //计算总页数
//            int totalPage = (upResult.getTotalRecords() % pageSize == 0) ? upResult.getTotalRecords() / pageSize : (upResult.getTotalRecords() / pageSize) + 1;
//            pageIndex++;
//
//            while (pageIndex <= totalPage) {
//
//                UpdateAliGoodsResult upResult1 = updAliGoods(pageIndex, 20, settingEntity.getAccess_token());
//                totalSuccess += upResult1.getTotalSuccess();
//                totalError += upResult1.getTotalError();
//                pageIndex++;
//            }
//
////            Integer pageNo = 1;
////            for (int i = 0; i < 100; i++) {
////                Integer count = updAliGoods(pageNo++, 20, variable.getValue());
////                if (count == 0) break;
////            }
//            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "更新完成，其中成功：" + totalSuccess + "，失败：" + totalError);
//        } catch (Exception e) {
//            return new ApiResult<>(EnumResultVo.DataError.getIndex(), "系统异常,请重新更新");
//        }
//    }
//
//    /**
//     *
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param token
//     * @return
//     */
//    private UpdateAliGoodsResult updAliGoods(Integer pageNo, Integer pageSize, String token) {
//
//
//        Integer totalSuccess = 0;//更新成功数量
//        Integer totalError = 0;//更新失败数量
//
//        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//        AlibabaProductListGetParam productListParam = new AlibabaProductListGetParam();
//        productListParam.setPageNo(pageNo);
//        productListParam.setPageSize(pageSize);
//        String[] statusList = new String[]{"published"};
//        productListParam.setStatusList(statusList);
//
//        var result = apiExecutor.execute(productListParam, token);
//
//        if (StringUtils.isEmpty(result.getErrorCode()) && result.getResult() != null) {
//            AlibabaProductPageResult pageResult = result.getResult().getResult().getPageResult();
//
//            AlibabaProductProductInfo[] productInfoList = pageResult.getResultList();
//
//            //阿里goods数据实体
//            List<DcAliGoodsEntity> goodsList = new ArrayList<>();
//
//            /*******转换成阿里goods实体******/
//            for (var aliProduct : productInfoList) {
//                DcAliGoodsEntity goods = new DcAliGoodsEntity();
//                goods.setProductID(aliProduct.getProductID());
//                goods.setApprovedTime(aliProduct.getApprovedTime());
//                goods.setAttributes(JSONArray.toJSONString(aliProduct.getAttributes()));
//                goods.setBizType(aliProduct.getBizType());
//                goods.setCategoryID(aliProduct.getCategoryID());
//                goods.setCategoryName(aliProduct.getCategoryName());
//                goods.setCreateTime(aliProduct.getCreateTime());
//                goods.setDescription(aliProduct.getDescription());
//                goods.setExpireTime(aliProduct.getExpireTime());
//                goods.setExtendInfos(JSONArray.toJSONString(aliProduct.getExtendInfos()));
//                goods.setGroupID(aliProduct.getGroupID() == null ? "" : JSONArray.toJSONString(aliProduct.getGroupID()));
//                goods.setMainImage((aliProduct.getImage() == null || aliProduct.getImage().getImages().length == 0) ? "" : "https://cbu01.alicdn.com/"+aliProduct.getImage().getImages()[0]);
//                goods.setImage(JSONArray.toJSONString(aliProduct.getImage()));
//                goods.setLanguage(aliProduct.getLanguage());
//                goods.setLastRepostTime(aliProduct.getLastRepostTime());
//                goods.setLastUpdateTime(aliProduct.getLastUpdateTime());
//                goods.setMainVedio(aliProduct.getMainVedio());
//                goods.setModifyTime(aliProduct.getModifyTime());
//                goods.setPeriodOfValidity(aliProduct.getPeriodOfValidity());
//                goods.setPictureAuth(aliProduct.getPictureAuth().toString());
//                goods.setProductCargoNumber(aliProduct.getProductCargoNumber());
//                goods.setProductType(aliProduct.getProductType());
//                goods.setReferencePrice(aliProduct.getReferencePrice());
//                goods.setSaleInfo(JSONArray.toJSONString(aliProduct.getSaleInfo()));
//                goods.setShippingInfo(JSONArray.toJSONString(aliProduct.getShippingInfo()));
//                goods.setStatus(aliProduct.getStatus());
//                goods.setSubject(aliProduct.getSubject());
//                goods.setWebSite("1688");
//
//                //SKU信息
//                List<DcAliGoodsSkuEntity> skuList = new ArrayList<>();
//
//                for (var s : aliProduct.getSkuInfos()) {
//                    DcAliGoodsSkuEntity sku = new DcAliGoodsSkuEntity();
//                    sku.setAmountOnSale(s.getAmountOnSale());
//                    sku.setAttributes(JSONArray.toJSONString(s.getAttributes()));
//                    sku.setCargoNumber(s.getCargoNumber());
//                    sku.setConsignPrice(s.getConsignPrice());
//                    sku.setPrice(s.getPrice());
//                    sku.setProductID(aliProduct.getProductID());
//                    sku.setRetailPrice(s.getRetailPrice());
//                    sku.setSkuId(s.getSkuId());
//                    sku.setSpecId(s.getSpecId());
//                    skuList.add(sku);
//                }
//                goods.setSkus(skuList);
//
//                //更新阿里商品
//                try {
//                    aliGoodsService.updAliGoods(goods);
//                    totalSuccess++;
//                } catch (Exception e) {
//                    log.info("插入阿里商品异常："+e.getMessage());
////                    return -500;//系统异常
//                    totalError++;
//                }
//            }
//
//
//            /********组装完成*******/
//
////            JSONArray list = new JSONArray(Arrays.asList(productInfoList));
//
//
//            return new UpdateAliGoodsResult(0, "", pageResult.getTotalRecords(), totalSuccess, totalError);
////            return pageResult.getTotalRecords();
//        } else if (result.getErrorCode().equalsIgnoreCase("401")) {
//            //没有授权
//            return new UpdateAliGoodsResult(401, "阿里授权到期，请重新授权", 0, totalSuccess, totalError);
//
//        } else {
//            return new UpdateAliGoodsResult(500, "未知错误", 0, totalSuccess, totalError);
//        }
//    }
//
//}
