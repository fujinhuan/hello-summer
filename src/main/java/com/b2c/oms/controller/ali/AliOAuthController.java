//package com.b2c.oms.controller.ali;
//
//
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.third.ali.vo.TokenVo;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.enums.EnumShopType;
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
// * 描述：
// * 阿里授权Controller
// *
// * @author qlp
// * @date 2019-09-12 14:13
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
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_MANAGE_INDEX.name());//获取redirectUrl
//        String redirectUrl = variable.getValue() + "/redirect_token";
//        TokenVo tokenVo = AliClient.getTokenVo(code, redirectUrl);
//        HttpSession session = request.getSession();
//
////        Integer shopId = Integer.parseInt(session.getAttribute("shopId").toString());
//
//        thirdSettingService.updateEntity(1, tokenVo.getAccess_token(), tokenVo.getRefresh_token(), tokenVo.getExpires_in(), tokenVo.getRefresh_token_timeout());
//
//        String state = request.getParameter("state");
//        if (state.equalsIgnoreCase("GETORDERLIST")) {
//            //获取订单list
//            return "redirect:/order/aliOrderList?shopId=1";
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
//        Integer shopTypeId = EnumShopType.Ali.getIndex();
//        var entity = thirdSettingService.getEntity(shopTypeId);
//
//        SysVariableEntity variable = systemService.getVariable(VariableEnums.URL_DATACENTER_INDEX.name());
//        String returnUrl = variable.getValue() + "/ali/getToken&state=GETORDERLIST";
//
//        //不存在ali token 跳转获取
////        if (StringUtils.isEmpty(entity.getAccess_token())) {
////
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
////        //判断token是否过期
////        Date date = new Date();
////        long time = date.getTime(); //这是毫秒数
////        long expireTime = entity.getAccess_token_begin() * 1000 + entity.getExpires_in() * 1000;
////        if (expireTime < time) {
////            //已过期
////            return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
////        }
//
//        return "redirect:" + AliClient.getAliAuthorizeUrl(returnUrl);
//    }
//}
