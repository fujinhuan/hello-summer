package com.b2c.oms.controller;

import com.b2c.common.PagingResponse;
import com.b2c.entity.datacenter.DcManageUserEntity;
import com.b2c.entity.result.ResultVo;
import com.b2c.oms.DataConfigObject;
import com.b2c.service.oms.DcLoginService;
import com.b2c.service.oms.DcManageUserService;
import com.b2c.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 描述：
 * 登录Controller
 *
 * @author qlp
 * @date 2018-12-26 5:37 PM
 */
@Controller
public class LoginController {
    @Autowired
    private DcManageUserService manageUserService;
//    @Autowired
//    private SystemRoleService systemRoleService;
    @Autowired
    private DcLoginService loginService;
    @Autowired
    private ShopService shopService;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, HttpServletRequest request) {
        String ref = request.getParameter("ref");
        if (StringUtils.isEmpty(ref) == false && ref.equalsIgnoreCase("NULL")) ref = "";
        model.addAttribute("ref", ref);

        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPost(Model model, HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ref = request.getParameter("ref");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            model.addAttribute("ref", ref);
            model.addAttribute("username", username);
            model.addAttribute("error", "请输入");
            return "login";
        }

        ResultVo<DcManageUserEntity> result = manageUserService.userLogin(username, password);
        if (result.getCode() == 0) {
            //添加session
            HttpSession session = request.getSession();
            session.setAttribute("userId", result.getData().getId());
            session.setAttribute("userName", result.getData().getUserName());
            session.setAttribute("trueName", result.getData().getTrueName());
            var perShopList = manageUserService.getHasPermissionShopList(result.getData().getId().intValue());
            StringBuilder shopList = new StringBuilder();
            for (var item:perShopList) {
                shopList.append("["+item.getId()+"]");
                shopList.append(",");
            }
            session.setAttribute("shopList",shopList.toString());
//            session.setAttribute("menuList", systemRoleService.getMenuByRoleId(result.getData().getGroupId(), 1));
//            systemRoleService.addManageUserLogin(result.getData().getId(), StringUtil.getLoalhostIP());

            //查询店铺List
            var shops = shopService.getShopList(null);
            session.setAttribute("shops", shops);

            System.out.println(ref);

            if (StringUtils.isEmpty(ref)) return "redirect:/";
            else return "redirect:" + ref;
        } else {
            model.addAttribute("ref", ref);
            model.addAttribute("username", username);
            model.addAttribute("error", result.getMsg());
            return "login";
        }

    }

    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public String exit(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userId");
        session.removeAttribute("userName");
        session.removeAttribute("trueName");
        session.removeAttribute("menuList");
        return "redirect:/";
    }


//    /**用户列表**/
//    @RequestMapping(value = "/login/user_list",method = RequestMethod.GET)
//    public String uerList(Model model,HttpServletRequest request){
//        Integer pageIndex = 1;
//        Integer pageSize = DataConfigObject.getInstance().getPageSize();
//        String page = request.getParameter("page");
//        if (!StringUtils.isEmpty(page)) {
//            pageIndex = Integer.parseInt(page);
//        }
//        String name = "";
//        if (!StringUtils.isEmpty(request.getParameter("name"))) {
//            name = request.getParameter("name");
//        }
//        String mobile = "";
//        if (!StringUtils.isEmpty(request.getParameter("mobile"))) {
//            mobile = request.getParameter("mobile");
//        }
//        Object userId = request.getSession().getAttribute("userId");
//        if (Integer.parseInt(userId.toString())!=1) return "/shop/error";
//        PagingResponse<DcManageUserEntity> result = loginService.getUserList(pageIndex, pageSize, name, mobile);
//        model.addAttribute("totalSize", result.getTotalSize());
//        model.addAttribute("lists", result.getList());
//        model.addAttribute("shopLists", loginService.getDcShopList());
//        return "/tdgl";
//    }

}
