package com.b2c.oms.controller;

import com.b2c.common.PagingResponse;
import com.b2c.entity.datacenter.DcManageUserEntity;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.oms.DcLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class SysSettingController {
    @Autowired
    private DcLoginService loginService;

    /**用户列表**/
    @RequestMapping(value = "/login/user_list",method = RequestMethod.GET)
    public String uerList(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        Object userId = request.getSession().getAttribute("userId");
        if (Integer.parseInt(userId.toString())!=1) return "/permission_denied";

        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String page = request.getParameter("page");
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        String name = "";
        if (!StringUtils.isEmpty(request.getParameter("name"))) {
            name = request.getParameter("name");
        }
        String mobile = "";
        if (!StringUtils.isEmpty(request.getParameter("mobile"))) {
            mobile = request.getParameter("mobile");
        }



        PagingResponse<DcManageUserEntity> result = loginService.getUserList(pageIndex, pageSize, name, mobile);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());
        model.addAttribute("shopLists", loginService.getDcShopList());
        return "sys_tdgl";
    }


}
