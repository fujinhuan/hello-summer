package com.b2c.oms.controller;

import com.b2c.common.api.ApiResult;
import com.b2c.common.PagingResponse;
import com.b2c.oms.entity.AfterSalesEntity;
import com.b2c.oms.service.AfterSalesSerice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/after_sales")
public class AfterSalesController {

    @Autowired
    private AfterSalesSerice salesSerice;

    /**
     * 客服记录list
     * @param request
     * @param shopId
     * @param model
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.GET)
    public String getList(HttpServletRequest request , @RequestParam Integer shopId, Model model){
        Integer pageIndex = 1, pageSize = 10;
        String page = request.getParameter("page");
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        String no = "";
        if (!StringUtils.isEmpty(request.getParameter("no"))) {
            no = request.getParameter("no");
        }
        String billonNo = "";
        if (!StringUtils.isEmpty(request.getParameter("billonNo"))) {
            billonNo = request.getParameter("billonNo");
        }
        String mobile = "";
        if (!StringUtils.isEmpty(request.getParameter("mobile"))) {
            mobile = request.getParameter("mobile");
        }

        model.addAttribute("shopId", shopId);

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        PagingResponse<AfterSalesEntity> result = salesSerice.getList(pageIndex, pageSize,shopId, mobile ,billonNo);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        model.addAttribute("menuId","after_sales");
        return "client_after_sales";
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<Integer> add(@RequestBody AfterSalesEntity entity){
        Integer add = salesSerice.add(entity);
        if (add==-1) return new ApiResult<>(-1,"新增失败");
        return new ApiResult<>(0,"SUCCESS");
    }

    @RequestMapping(value = "edit",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<Integer> edit( @RequestBody Object id){
        salesSerice.edit(Long.parseLong(id.toString()));
        return new ApiResult<>(0,"SUCCESS");
    }
}
