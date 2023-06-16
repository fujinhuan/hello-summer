package com.b2c.oms.controller;

import com.b2c.service.oms.OrderPddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/pdd")
@Controller
public class WaitSendGoodsSpecInfoController {
    @Autowired
    private OrderPddService orderPddService;
    @RequestMapping("/wait_send_goods_info")
    public String orderList(Model model,  HttpServletRequest request){
        var result = orderPddService.getWaitSendGoodsSpecList();

        model.addAttribute("lists", result);
        model.addAttribute("totalSize", result.size());

        model.addAttribute("menuId","order_list");
        return "wait_send_goods_info";
    }
}
