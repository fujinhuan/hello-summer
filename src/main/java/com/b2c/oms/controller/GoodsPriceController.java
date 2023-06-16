package com.b2c.oms.controller;

import com.b2c.common.utils.DateUtil;
import com.b2c.service.ShopService;
import com.b2c.service.erp.ErpGoodsService;
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
 * 商品价格管理
 *
 * @author qlp
 * @date 2018-12-20 11:18 AM
 */
@RequestMapping("/goods")
@Controller
public class GoodsPriceController {

    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private ShopService shopService;

    /**
     * 商品列表（以仓库的商品为主体）
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/price_list", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request,@RequestParam Integer shopId) {
        var shop = shopService.getShop(shopId);
        String page = request.getParameter("page");
        Integer pageIndex = 1;
        String number = "";
        Integer pageSize = 50;

        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("number"))) {
            number = request.getParameter("number");
        }
        Integer startTime=null;
        if (!StringUtils.isEmpty(request.getParameter("startTime"))){
            startTime=DateUtil.dateTimeToStamp(request.getParameter("startTime")+" 00:00:00");
            model.addAttribute("startTime",request.getParameter("startTime"));
        }
        Integer endTime=null;
        if (!StringUtils.isEmpty(request.getParameter("endTime"))){
            String endTimes=request.getParameter("endTime")+" 23:59:59";
            endTime = DateUtil.dateTimeToStamp(endTimes);
            model.addAttribute("endTime",request.getParameter("endTime"));
        }

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("shopId",shop.getId());

        var result = erpGoodsService.getDySalesList(shop.getSellerUserId(),pageIndex, pageSize, number,startTime,endTime);

        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId", "goods_price");

        return "goods_price_list";
    }

}
