//package com.b2c.oms.controller.ali;
//
//import com.alibaba.ocean.rawsdk.ApiExecutor;
//import com.alibaba.product.param.AlibabaProductListGetParam;
//import com.alibaba.product.param.AlibabaProductPageResult;
//import com.alibaba.product.param.AlibabaProductProductInfo;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.third.thymeleaf.PagingResponse;
//import com.b2c.entity.datacenter.DcAliGoodsEntity;
//import com.b2c.enums.EnumShopType;
//import com.b2c.oms.DataConfigObject;
//import com.b2c.service.erp.ErpGoodsService;
//import com.b2c.service.oms.AliGoodsService;
//import com.b2c.service.oms.ShopService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 描述：
// * 阿里商品Controller
// *
// * @author qlp
// * @date 2019-09-19 08:44
// */
//@RequestMapping("/goods")
//@Controller
//public class GoodsAliController {
//
//    @Autowired
//    private AliGoodsService goodsService;
//    @Autowired
//    private ErpGoodsService erpGoodsService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//
//    @RequestMapping(value = "/list_ali", method = RequestMethod.GET)
//    public String list(Model model, HttpServletRequest request) {
//        Integer shopId = 1;
//        //查询店铺信息
//        var shop = shopService.getShop(shopId);
//        model.addAttribute("shop", shop);
//
//
//        String page = request.getParameter("page");
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        ;
//        String title = "";
//
//        Long goodsId = 0l;
//
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//        if (!StringUtils.isEmpty(request.getParameter("goodsname"))) {
//            title = request.getParameter("goodsname");
//        }
//        if (!StringUtils.isEmpty(request.getParameter("goodsId"))) {
//            goodsId = Long.parseLong(request.getParameter("goodsId"));
//        }
//
//
//        model.addAttribute("pageIndex", pageIndex);
//        model.addAttribute("pageSize", pageSize);
//
//        PagingResponse<DcAliGoodsEntity> result = goodsService.getGoodsList(pageIndex, pageSize, title, goodsId);
//
//
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
//
//        model.addAttribute("menuId", shop.getEname());
//
//        return "goods/list_ali";
//    }
//
//    /**
//     * 关联阿里商品
//     * @param model
//     * @param id
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/ali_online_product_lists", method = RequestMethod.GET)
//    public String aliProductList(Model model, @RequestParam Integer id, HttpServletRequest request) {
//        String page = request.getParameter("page");
//        Integer pageIndex = 1, pageSize = 20;
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//
//        //关联阿里商品的erpGoodsId
//        var erpGoods = erpGoodsService.getById(id);
//        if (erpGoods == null) {
//            model.addAttribute("msg", "仓库中不存在该商品");
//        } else {
//
//            model.addAttribute("erpGoods",erpGoods);
//
//            var settingEntity = thirdSettingService.getEntity(EnumShopType.Ali.getIndex());
//
//            ApiExecutor apiExecutor = new ApiExecutor(AliClient.getAppId(), AliClient.getAppSecret());
//            AlibabaProductListGetParam productListParam = new AlibabaProductListGetParam();
//            productListParam.setPageNo(pageIndex);
//            productListParam.setPageSize(pageSize);
//            String[] statusList = new String[]{"published"};
//            productListParam.setStatusList(statusList);
//
//            productListParam.setCargoNumber(erpGoods.getNumber());
//
//
//            var result = apiExecutor.execute(productListParam, settingEntity.getAccess_token());
//
//            if (StringUtils.isEmpty(result.getErrorCode()) && result.getResult() != null) {
//                AlibabaProductPageResult pageResult = result.getResult().getResult().getPageResult();
//
//                AlibabaProductProductInfo[] productInfoList = pageResult.getResultList();
//                if (productInfoList.length > 0) {
//
//                    //阿里goods数据实体
//                    List<DcAliGoodsEntity> goodsList = new ArrayList<>();
//
//                    /*******转换成阿里goods实体******/
//                    for (var aliProduct : productInfoList) {
//                        DcAliGoodsEntity goods = new DcAliGoodsEntity();
//
//                        goods.setProductID(aliProduct.getProductID());
//                        goods.setMainImage((aliProduct.getImage() == null || aliProduct.getImage().getImages().length == 0) ? "" : "https://cbu01.alicdn.com/" + aliProduct.getImage().getImages()[0]);
//                        goods.setProductCargoNumber(aliProduct.getProductCargoNumber());
//                        goods.setStatus(aliProduct.getStatus());
//                        goods.setSubject(aliProduct.getSubject());
//
//                        //更新阿里商品
//                        goodsList.add(goods);
//                    }
//
//                    model.addAttribute("goodsList", goodsList);
//                } else {
//                    model.addAttribute("msg", "阿里店铺还没有上架" + erpGoods.getNumber() + "商品");
//                }
//            }
//        }
//        return "goods/ali_online_product_lists";
//    }
//}
