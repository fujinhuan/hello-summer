//package com.b2c.oms.controller.order.ali;
//
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.third.ali.vo.TokenVo;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.service.mall.SystemService;
//import com.b2c.service.oms.SysThirdSettingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
///**
// * 阿里获取token
// */
//@RequestMapping("/ali")
//@Controller
//public class AliOAuthController {
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    private static Logger log = LoggerFactory.getLogger(AliOAuthController.class);
//
//    /**
//     * 获取ali token
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @RequestMapping("/getToken")
//    public String getToken(HttpServletRequest request) throws IOException, InterruptedException {
//        log.info("/**********获取阿里授权token*********/");
//        String code = request.getParameter("code");
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());//获取redirectUrl
//        String redirectUrl = variable.getValue() + "/redirect_token";
//        TokenVo tokenVo = AliClient.getTokenVo(code, redirectUrl);
//        HttpSession session = request.getSession();
//
//        thirdSettingService.updateEntity(1, tokenVo.getAccess_token(), tokenVo.getRefresh_token(), tokenVo.getExpires_in(), tokenVo.getRefresh_token_timeout());
//
//        String state = request.getParameter("state");
//        if (state.equalsIgnoreCase("GETORDERLIST")) {
//            //获取订单list
//            return "redirect:/order/pull_shop_order";
//        } else if (state.equalsIgnoreCase("ERPORDERSEND")) {
//            //跳转到仓库系统订单发货列表
//            SysVariableEntity erp = systemService.getVariable(VariableEnums.URL_WMS_INDEX_IN.name());
//            return "redirect:" + erp.getValue() + "/order/order_wait_send_list";
//        } else if (state.equalsIgnoreCase("DCGOODSLIST")) {
//            //商品list
//            return "redirect:/goods/lists";
//        }
//        return "redirect:/";
//    }
//
//
//    @RequestMapping("/ali_oauth")
//    public String aliOAuth(HttpServletRequest request) throws IOException, InterruptedException {
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=GETORDERLIST";
//        return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
//    }
//}
