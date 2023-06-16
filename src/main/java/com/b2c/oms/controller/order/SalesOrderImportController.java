package com.b2c.oms.controller.order;

import com.b2c.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/order")
@Controller
public class SalesOrderImportController {


    /**
     * 一件代发（直播）订单excel导入
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/daifa_order_excel_import_review", method = RequestMethod.GET)
    public String daifa_excel_import_review(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        //导入的订单一定是实售订单
        model.addAttribute("menuId", "order_excel_import");

        if (!StringUtils.isEmpty(request.getParameter("shopId"))) {
            shopId = Integer.parseInt(request.getParameter("shopId"));
            model.addAttribute("shopId",shopId);
        }
        //查询客户列表
        List<UserEntity> clientList = new ArrayList<>();//userService.getClientUserListByType(null, null);
        model.addAttribute("clientList", clientList);

        if(shopId!=null && shopId == 99)
            return "order_excel_import_review_daifa_v3";
        else
            return "order_excel_import_review_daifa";
    }

//    /**
//     * excel导入订单预览页面
//     *
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/pipi_order_excel_import_review", method = RequestMethod.GET)
//    public String pipi_excel_import_review(Model model, HttpServletRequest request) {
//        //导入的订单一定是实售订单
//        model.addAttribute("menuId", "order_excel_import");
//        model.addAttribute("orderCount", 0);
//        //查询客户列表
//        List<UserEntity> clientList = userService.getClientUserListByType(null, null);
//        model.addAttribute("clientList", clientList);
//
//        return "order_excel_import_review_pipi";
//    }
}
