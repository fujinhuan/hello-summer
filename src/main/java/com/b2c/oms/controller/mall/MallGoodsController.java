package com.b2c.oms.controller.mall;

import com.b2c.oms.DataConfigObject;
import com.b2c.service.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mall")
public class MallGoodsController {
    @Autowired
    private MallGoodsService mallGoodsService;

    /**
     * 店铺商品
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/goods", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request) {

        String page = request.getParameter("page");
        Integer pageIndex = 1;
        String number = "";
        String title = "";
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        if (!StringUtils.isEmpty(page)) pageIndex = Integer.parseInt(page);

        if (!StringUtils.isEmpty(request.getParameter("number")))number = request.getParameter("number");

        if (!StringUtils.isEmpty(request.getParameter("title"))) title = request.getParameter("title");

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        var result = mallGoodsService.getGoodsList(pageIndex,pageSize,title,number);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId", "goods");
        return "goods/goods_third_list";
    }
}
