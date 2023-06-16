package com.b2c.oms.controller.pipi;

import com.b2c.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/pipi")
@Controller
public class PiPiOrderImportController {


    /**
     * excel导入订单预览页面
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_excel_import_review", method = RequestMethod.GET)
    public String pipi_excel_import_review(Model model, @RequestParam Integer shopId, HttpServletRequest request) {

        model.addAttribute("menuId", "order_excel_import");
        model.addAttribute("orderCount", 0);
        //查询客户列表
//        List<UserEntity> clientList = userService.getClientUserListByType(null, null);
//        model.addAttribute("clientList", clientList);

        return "order_excel_import_review_pipi";
    }
}
