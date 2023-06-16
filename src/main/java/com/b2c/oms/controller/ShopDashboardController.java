package com.b2c.oms.controller;

import com.b2c.common.utils.DateUtil;
import com.b2c.entity.datacenter.DcShopEntity;
import com.b2c.vo.ShopOrderStatisticsVo;
import com.b2c.service.oms.DcManageUserService;
import com.b2c.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ShopDashboardController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private DcManageUserService manageUserService;
    /**
     * 店铺列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/shop/shop_list", method = RequestMethod.GET)
    public String shopList(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());

        //有权限的店铺列表
        List<DcShopEntity> shops = shopService.getShopListByUserId(userId,0);
        model.addAttribute("shops", shops);

        return "v3/shop_list";
    }

    /**
     * 店铺首页
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/shop/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        HttpSession session = request.getSession();
//        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
//        var hasPermission = manageUserService.checkShopPermission(userId,shopId);
//        if(hasPermission==false){
//            model.addAttribute("msg","你没有该店铺的管理权限");
//        }

        model.addAttribute("menuId", "home");
        model.addAttribute("shopId", shopId);
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        //店铺列表
//        List<DcShopEntity> shops = shopService.getShopList();
//        model.addAttribute("shops", shops);
        //店铺统计信息
        var statistics = shopService.shopOrderStatistics(shopId);
        if(statistics!=null)
            model.addAttribute("report", statistics);
        else  model.addAttribute("report",new ShopOrderStatisticsVo());

        model.addAttribute("today", DateUtil.dateToString(new Date(),"yyyy-MM-dd"));

        return "v3/shop_dashboard";
    }

}
