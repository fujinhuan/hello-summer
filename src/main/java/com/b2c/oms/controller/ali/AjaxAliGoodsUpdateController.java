//package com.b2c.oms.controller.ali;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 描述：
// * 更新阿里商品到华衣云购
// *
// * @author qlp
// * @date 2019-09-24 14:39
// */
//@RequestMapping("/goods")
//@RestController
//public class AjaxAliGoodsUpdateController {
//
//    private static Logger log = LoggerFactory.getLogger(AjaxAliGoodsUpdateController.class);
//
//
////    /**
////     * 更新阿里商品列表
////     *
////     * @return
////     */
////    @RequestMapping(value = "/upd_ali_goods_to_yungou", method = RequestMethod.POST)
////    public ApiResult<Object> updGoods() {
////        log.info("更新1688商品到云购平台");
////
////        Integer shopTypeId = EnumShopType.Ali.getIndex();
////        var settingEntity = thirdSettingService.getEntity(shopTypeId);
////
////        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
////        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
////
////        //不存在ali token 跳转获取
////        if (StringUtils.isEmpty(settingEntity.getAccess_token())) {
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time = date.getTime(); //这是毫秒数
////        long expireTime = settingEntity.getAccess_token_begin() * 1000 + settingEntity.getExpires_in() * 1000;
////        if (expireTime < time) {
////            //已过期
//////                return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////
////
////        try {
////
////            int pageSize = 20;
////            int pageIndex = 1;
////            int totalSuccess = 0;
////            int totalError = 0;
////
////
////            //第一次获取
////            UpdateAliGoodsResult upResult = updAliGoods(pageIndex, 20, settingEntity.getAccess_token());
////            if (upResult.getCode().intValue() == 401) {
////                //没有授权；
////                return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////            }
////            totalSuccess += upResult.getTotalSuccess();
////            totalError += upResult.getTotalError();
////            //计算总页数
////            int totalPage = (upResult.getTotalRecords() % pageSize == 0) ? upResult.getTotalRecords() / pageSize : (upResult.getTotalRecords() / pageSize) + 1;
////            pageIndex++;
////
////            while (pageIndex <= totalPage) {
////
////                UpdateAliGoodsResult upResult1 = updAliGoods(pageIndex, 20, settingEntity.getAccess_token());
////                totalSuccess += upResult1.getTotalSuccess();
////                totalError += upResult1.getTotalError();
////                pageIndex++;
////            }
////
//////            Integer pageNo = 1;
//////            for (int i = 0; i < 100; i++) {
//////                Integer count = updAliGoods(pageNo++, 20, variable.getValue());
//////                if (count == 0) break;
//////            }
////            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "更新完成，其中成功：" + totalSuccess + "，忽略：" + totalError);
////        } catch (Exception e) {
////            return new ApiResult<>(EnumResultVo.DataError.getIndex(), e.getMessage());
////        }
////    }
//
////    /**
////     * 商品发布到1688
////     *
////     * @return
////     */
////    @RequestMapping(value = "/upd_ali_goods_to_1688", method = RequestMethod.POST)
////    public ApiResult<Object> updGoodsTo1688(@RequestBody Integer id) {
////        log.info("商品发布1688");
////
////        Integer shopTypeId = EnumShopType.Ali.getIndex();
////        var settingEntity = thirdSettingService.getEntity(shopTypeId);
////
////        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
////        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
////
////        //不存在ali token 跳转获取
////        if (StringUtils.isEmpty(settingEntity.getAccess_token())) {
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time = date.getTime(); //这是毫秒数
////        long expireTime = settingEntity.getAccess_token_begin() * 1000 + settingEntity.getExpires_in() * 1000;
////        if (expireTime < time) {
////            //已过期
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////
////        try {
////            ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
////
////            AlibabaProductAddParam param = new AlibabaProductAddParam();
////            param.setProductType("wholesale");
////            GoodsEntity goods = goodsService.getGoodsById(id);
////
////            param.setCategoryID(1031912L);
////            param.setSubject(goods.getTitle());
////            param.setLanguage("CHINESE");
////
////            List<GoodsSpecEntity> goodsSpecByGoodsId = goodsService.getGoodsSpecByGoodsId(id);
////            AlibabaProductProductAttribute[] bute = new AlibabaProductProductAttribute[goodsSpecByGoodsId.size()];
////            int num = 0;
////            for (int i = 0; i < goodsSpecByGoodsId.size(); i++) {
////                AlibabaProductProductAttribute attributes = new AlibabaProductProductAttribute();
////                attributes.setAttributeID(goodsSpecByGoodsId.get(i).getId());
////                attributes.setAttributeName(goodsSpecByGoodsId.get(i).getColorName());
////                attributes.setValueID(goodsSpecByGoodsId.get(i).getColorId());
////                attributes.setValue(goodsSpecByGoodsId.get(i).getColorValue());
////                attributes.setIsCustom(true);
////                bute[i] = attributes;
////            }
////            param.setAttributes(bute);
////
////            List<GoodsAttachmentEntity> goodsImagesById = goodsService.getGoodsImagesById(id);
////            AlibabaProductProductImageInfo imageInfo = new AlibabaProductProductImageInfo();
////            String[] str = new String[goodsImagesById.size()];
////            for (int i = 0; i < goodsImagesById.size(); i++) {
////                str[i] = goodsImagesById.get(i).getUrl();
////            }
////            imageInfo.setImages(str);
////
////            param.setImage(imageInfo);
////            param.setWebSite("1688");
////            param.setDescription("dhiuwghisd");
////            SDKResult<AlibabaProductAddResult> result = apiExecutor.execute(param, settingEntity.getAccess_token());
////            if (result.getResult().getProductID() == null) {
////                return new ApiResult<>(EnumResultVo.Fail.getIndex(), EnumResultVo.Fail.getName());
////            }
////            return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), result.getResult().toString());
////        } catch (Exception e) {
////            return new ApiResult<>(EnumResultVo.DataError.getIndex(), e.getMessage());
////        }
////    }
////
////    /**
////     * 返货totalSize
////     *
////     * @param pageNo
////     * @param pageSize
////     * @param token
////     * @return
////     */
////    private UpdateAliGoodsResult updAliGoods(Integer pageNo, Integer pageSize, String token) {
////
////
////        Integer totalSuccess = 0;//更新成功数量
////        Integer totalError = 0;//更新失败数量
////
////        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
////        AlibabaProductListGetParam productListParam = new AlibabaProductListGetParam();
////        productListParam.setPageNo(pageNo);
////        productListParam.setPageSize(pageSize);
////        String[] statusList = new String[]{"published"};
////        productListParam.setStatusList(statusList);
////
////        var result = apiExecutor.execute(productListParam, token);
////
////        if (StringUtils.isEmpty(result.getErrorCode()) && result.getResult() != null) {
////            AlibabaProductPageResult pageResult = result.getResult().getResult().getPageResult();
////
////            AlibabaProductProductInfo[] productInfoList = pageResult.getResultList();
////
////            //阿里goods数据实体
//////            List<AliGoodsChangeGoodsVo> goodsList = new ArrayList<>();
////
////            /*******转换成 云购平台 goods实体******/
////            for (var aliProduct : productInfoList) {
////                AliGoodsChangeGoodsVo goods = new AliGoodsChangeGoodsVo();
////
////                goods.setAliProductId(aliProduct.getProductID());
////                goods.setImage((aliProduct.getImage() == null || aliProduct.getImage().getImages().length == 0) ? "" : "https://cbu01.alicdn.com/" + aliProduct.getImage().getImages()[0]);
////                goods.setMainVedio(aliProduct.getMainVedio());
////                goods.setDetail(aliProduct.getDescription());
////                goods.setDetailVedio(aliProduct.getDetailVedio());
////                goods.setGoodsNumber(aliProduct.getProductCargoNumber());
////                goods.setTitle(aliProduct.getSubject());
////                goods.setImages(aliProduct.getImage().getImages());
////
////                //阿里价格不是这么取的
//////                if (aliProduct.getSkuInfos() != null && aliProduct.getSkuInfos().length > 0) {
//////                    try {
//////                        goods.setSalePrice(BigDecimal.valueOf(aliProduct.getSkuInfos()[0].getPrice()));
//////                    }catch (Exception e){}
//////                }
//////
////                //SKU信息
////                List<GoodsSpecEntity> skuList = new ArrayList<>();
////
////                for (var s : aliProduct.getSkuInfos()) {
////                    GoodsSpecEntity sku = new GoodsSpecEntity();
////                    sku.setSpecNumber(s.getCargoNumber());
//////                    sku.setPrice(s.getPrice());//价格不是这么取的
////                    sku.setOutSkuId(s.getSkuId());
////                    //设置SKU 属性
////                    if (s.getAttributes() != null) {
////                        if (s.getAttributes().length > 0) {
////                            sku.setColorName("颜色");
////                            sku.setColorValue(s.getAttributes()[0].getAttributeValue());
////                            sku.setColorId(s.getAttributes()[0].getAttributeID());
////                        }
////                        if (s.getAttributes().length > 1) {
////                            sku.setSizeName("尺码");
////                            sku.setSizeValue(s.getAttributes()[1].getAttributeValue());
////                            sku.setSizeId(s.getAttributes()[1].getAttributeID());
////                        }
////                        if (s.getAttributes().length > 2) {
////                            sku.setStyleName("款式");
////                            sku.setStyleValue(s.getAttributes()[2].getAttributeValue());
////                            sku.setStyleId(s.getAttributes()[2].getAttributeID());
////                        }
////                    }
////
////                    skuList.add(sku);
////                }
////                goods.setSkus(skuList);
////
////                //更新阿里商品
//////                try {
//////                    Integer result1 = aliGoodsService.addAliGoods(goods);
//////                    if (result1 == -404) {
//////                        //商品存在，不更新
//////                        totalError++;
//////                    } else {
//////                        totalSuccess++;
//////                    }
//////                } catch (Exception e) {
//////                    throw e;
////////                    return -500;//系统异常
////////                    totalError++;
//////                }
////            }
////
////
////            /********组装完成*******/
////
//////            JSONArray list = new JSONArray(Arrays.asList(productInfoList));
////
////
////            return new UpdateAliGoodsResult(0, "", pageResult.getTotalRecords(), totalSuccess, totalError);
//////            return pageResult.getTotalRecords();
////        } else if (result.getErrorCode().equalsIgnoreCase("401")) {
////            //没有授权
////            return new UpdateAliGoodsResult(401, "阿里授权到期，请重新授权", 0, totalSuccess, totalError);
////
////        } else {
////            return new UpdateAliGoodsResult(500, "未知错误", 0, totalSuccess, totalError);
////        }
////    }
//
//
//
//
////    /**
////     * 同步阿里商品主图及详情到云购
////     *
////     * @return
////     */
////    @RequestMapping(value = "/upd_ali_detail_to_yg", method = RequestMethod.POST)
////    public ApiResult<Object> updAliDetailToYG(@RequestBody DataRow model, HttpServletRequest request) {
////        var settingEntity = thirdSettingService.getEntity(EnumShopType.Ali.getIndex());
////
////        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
////        String returnUrl = variable.getValue() + "/ali/getToken&state=DCGOODSLIST";
////
////        //不存在ali token 跳转获取
////        if (StringUtils.isEmpty(settingEntity.getAccess_token())) {
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time = date.getTime(); //这是毫秒数
////        long expireTime = settingEntity.getAccess_token_begin() * 1000 + settingEntity.getExpires_in() * 1000;
////        if (expireTime < time) {
////            //已过期
////            return new ApiResult<>(EnumResultVo.TokenFail.getIndex(), "Ali授权过期，请重新授权", AliClient.getAliAuthorizeUrl(returnUrl));
////        }
////
////        Long aliProductId = model.getLong("aliProductId");
////        Integer ygProductId = model.getInt("ygProductId");
////
////        //调用阿里接口
////        ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
////        AlibabaProductGetParam param = new AlibabaProductGetParam();
////        param.setProductID(aliProductId);
////
////        var result = apiExecutor.execute(param, settingEntity.getAccess_token());
////        if (StringUtils.isEmpty(result.getErrorCode()) && result.getResult() != null) {
////            if (StringUtils.isEmpty(result.getResult().getErrMsg()) == false) {
////                return new ApiResult<>(EnumResultVo.DataError.getIndex(), result.getResult().getErrMsg());
////            }
////            //处理阿里信息，更新云购商品详情
////            var aliProduct = result.getResult().getProductInfo();
////
////            String mainImage = (aliProduct.getImage() == null || aliProduct.getImage().getImages().length == 0) ? "" : "https://cbu01.alicdn.com/" + aliProduct.getImage().getImages()[0];
////
////            goodsCenterService.updateAliDetailToYunGou(ygProductId, aliProductId, mainImage, aliProduct.getImage().getImages(), aliProduct.getMainVedio(), aliProduct.getDescription());
////        } else {
////            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), "接口调用错误");
////        }
////
////        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "更新成功");
////    }
//
//}
