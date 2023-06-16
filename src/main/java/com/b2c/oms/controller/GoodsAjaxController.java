package com.b2c.oms.controller;



import com.b2c.service.GoodsCenterService;
import com.b2c.service.erp.ErpGoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * 商品管理AJAX
 *
 * @author ly
 * @date 2019-1-17 15:50
 */
@RequestMapping(value = "/goods_ajax")
@RestController
public class GoodsAjaxController {

    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private GoodsCenterService goodsCenterService;


//    /**
//     * 一键发布到云购
//     *
//     * @return
//     */
//    @RequestMapping(value = "/publish_to_yg", method = RequestMethod.POST)
//    public ApiResult<Object> publishToYG(@RequestBody Integer id, HttpServletRequest request) {
//        //查询商品
//        var erpGoods = erpGoodsService.getById(id);
//        if (erpGoods == null) return new ApiResult<>(EnumResultVo.DataError.getIndex(), "数据错误，不存在商品信息");
//
//        //查询阿里goods
//        var aliGoods = aliGoodsService.getGoodsByAliErpGoodsId(erpGoods.getId());
//        //如果阿里存在，使用阿里基本信息填充
//
//        GoodsAddVo addVo = new GoodsAddVo();
//        addVo.setCostPrice(BigDecimal.valueOf(0.0));
//        addVo.setDeputyTitle(aliGoods != null ? aliGoods.getSubject() : erpGoods.getName());
//        addVo.setGoodsNumber(erpGoods.getNumber());
//        addVo.setImage(aliGoods != null ? aliGoods.getMainImage() : "");
//        addVo.setmDetail(aliGoods != null ? aliGoods.getDescription() : "");
//        addVo.setPrice(BigDecimal.valueOf(0.0));
//        addVo.setSaleNumShow(0);
//        addVo.setTitle(aliGoods != null ? aliGoods.getSubject() : erpGoods.getName());
//        addVo.setCategoryId(0);
//        addVo.setCommisionRate(BigDecimal.valueOf(0.30));
//        addVo.setTime(2);//下架在库状态
//        addVo.setKeyword("");
//        addVo.setFreightTemplate(0);
//
//        addVo.setErpGoodsId(erpGoods.getId());
//
//        //处理图片
//        if (aliGoods != null && StringUtils.isEmpty(aliGoods.getImage()) == false) {
//            var jo = JSONObject.parseObject(aliGoods.getImage());
//            List<String> array = JSONArray.parseArray(jo.getString("images"), String.class);
//            for (var arr : array) {
//                arr = "https://cbu01.alicdn.com/" + arr;
//            }
//            addVo.setImages(array.toArray(new String[array.size()]));
//        }
//
//        //商品规格
//        List<GoodsSpecVo> goodsSpecList = new ArrayList<>();
//        //组合商品规格
//        var erpGoodsSpecList = erpGoodsService.getSpecListByGoodsId(erpGoods.getId());
//        if (erpGoodsSpecList != null && erpGoodsSpecList.size() > 0) {
//
//            for (var sp : erpGoodsSpecList) {
//                GoodsSpecVo goodsSpec = new GoodsSpecVo();
//                goodsSpec.setColorId(sp.getColorId());
//                goodsSpec.setColor(sp.getColorValue());
//                goodsSpec.setImage(aliGoods != null ? aliGoods.getMainImage() : "");
//                goodsSpec.setSizeId(sp.getSizeId());
//                goodsSpec.setSize(sp.getSizeValue());
//                goodsSpec.setStyleId(sp.getStyleId());
//                goodsSpec.setStyle(sp.getStyleValue());
//                goodsSpec.setPrice(BigDecimal.valueOf(0.0));
//                goodsSpec.setCode(sp.getSpecNumber());
//                goodsSpecList.add(goodsSpec);
//            }
//        }
//        addVo.setGoodsSpec(goodsSpecList);
//        Integer goodsId = goodsService.addGoods(addVo, "Sys");
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "发布成功，云购商品ID:" + goodsId);
//    }

//    /**
//     * ajax获取商品规格list
//     *
//     * @param req
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/link_yg", method = RequestMethod.POST)
//    public ApiResult<Integer> getGoodsSpecList(@RequestBody ErpGoodsLinkYgGoodsReq req, HttpServletRequest request) {
//        if (req == null || req.getErpGoodsId() == null || req.getYgGoodsId() == null || req.getErpGoodsId().intValue() <= 0 || req.getYgGoodsId().intValue() <= 0)
//            return new ApiResult<>(ApiResultEnum.ParamsError, "参数错误");
//
//        //查询erp商品
//        var erpGoods = erpGoodsService.getById(req.getErpGoodsId());
//        if (erpGoods == null) return new ApiResult<>(ApiResultEnum.ParamsError, "参数错误，仓库没找到该商品");
//
//        var ygGoods = goodsService.getGoodsById(req.getYgGoodsId());
//        if (ygGoods == null) return new ApiResult<>(ApiResultEnum.ParamsError, "参数错误，云购没找到该商品");
//
//        //开始关联，将erp_goods_id写入goods表
//        goodsCenterService.linkToYg(req.getErpGoodsId(),req.getYgGoodsId());
//
////        List<GoodsSpecEntity> specList = goodsService.getGoodsSpecByGoodsId(goodsId);
//        return new ApiResult<>(0, "SUCCESS");
//    }


}
