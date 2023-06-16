package com.b2c.oms.controller;

import com.b2c.common.PagingResponse;
import com.b2c.entity.erp.vo.ErpGoodsListVo;
import com.b2c.entity.erp.vo.ErpGoodsSpecListVo;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.oms.DataInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/goods")
@Controller
public class GoodsInventoryController {
    @Autowired
    private DataInvoiceService dataInvoiceService;

    /**
     * 库存商品列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/inventory_list", method = RequestMethod.GET)
    public String getgGoodsList(Model model, HttpServletRequest request) {
        String page = request.getParameter("page");
        Integer pageIndex = 1;

        String goodsNum = "";
        String goodsSpecNum = "";
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("goodsNum"))) {
            goodsNum = request.getParameter("goodsNum");
            model.addAttribute("goodsNum",goodsNum);
        }
        if (!StringUtils.isEmpty(request.getParameter("goodsSpecNum"))) {
            goodsSpecNum = request.getParameter("goodsSpecNum");
            model.addAttribute("goodsSpecNum",goodsSpecNum);
        }

        if (!StringUtils.isEmpty(request.getParameter("shopId"))) {
            model.addAttribute("shopId",request.getParameter("shopId"));
        }

        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        PagingResponse<ErpGoodsSpecListVo> result = dataInvoiceService.getSpecInventoryList(pageIndex,pageSize, goodsNum,goodsSpecNum);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId","goods_stock");

        return "goods_inventory_list";
    }
}
