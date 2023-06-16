package com.b2c.oms.controller;

import com.b2c.oms.DataConfigObject;
import com.b2c.service.GoodsCenterService;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：
 * 商品管理Controller
 *
 * @author qlp
 * @date 2018-12-20 11:18 AM
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private GoodsCenterService goodsCenterService;
    @Autowired
    private ErpGoodsService erpGoodsService;

    /**
     * 店铺商品
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/lists", method = RequestMethod.GET)
    public String list(Model model,@RequestParam Integer shopId,  HttpServletRequest request) {

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        String number = "";
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        Integer goodsId = 0;
        if (!StringUtils.isEmpty(request.getParameter("shopId"))) {
            model.addAttribute("shopId",request.getParameter("shopId"));
        }
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
        }
//        if (!StringUtils.isEmpty(request.getParameter("goodsId"))) {
//            goodsId = Integer.parseInt(request.getParameter("goodsId"));
//        }


        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);

//        PagingResponse<DcAliGoodsEntity> result = goodsService.getGoodsList(pageIndex, pageSize, title, goodsId,null);
//        PagingResponse<GoodsListVo> result = goodsService.getGoodsHtLists(pageIndex, pageSize, null, goodsNumber, goodsId, 0);
        var result = goodsCenterService.getList(pageIndex, pageSize, number);

        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId", "goods");
        return "goods_list";
    }

//    /**
//     * 关联云购商品
//     *
//     * @param model
//     * @param id
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/yg_product_lists", method = RequestMethod.GET)
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
//            model.addAttribute("erpGoods", erpGoods);
//
//            //查询云购商品
//            List<GoodsEntity> goodsList = ygGoodsService.getGoodsByNumber(erpGoods.getNumber());
//
//
//            if (goodsList.size() > 0) {
//                model.addAttribute("goodsList", goodsList);
//            } else {
//                model.addAttribute("msg", "云购还没有上架" + erpGoods.getNumber() + "商品");
//            }
//
//        }
//        return "goods/yg_product_lists";
//    }


    @RequestMapping(value = "/sku_list", method = RequestMethod.GET)
    public String skuList(Model model, HttpServletRequest request) {
        String page = request.getParameter("page");
        Integer pageIndex = 1;
        String number = "";
        Integer pageSize = DataConfigObject.getInstance().getPageSize();

        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
        }

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);

        var result = erpGoodsService.getSpecByGoodsNumber(pageIndex, pageSize, number);

        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId", "goods");
        return "goods/erp_goods_sku_list";
    }



}
