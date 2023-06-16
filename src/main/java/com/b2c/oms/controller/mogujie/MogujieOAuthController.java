//package com.b2c.oms.controller.mogujie;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.third.ali.vo.TokenVo;
//import com.b2c.common.third.express.ExpressClient;
//import com.b2c.entity.SysVariableEntity;
//import com.b2c.entity.enums.VariableEnums;
//import com.b2c.entity.result.EnumResultVo;
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
//import java.net.http.HttpResponse;
//
//import static com.b2c.common.utils.HttpUtil.map2Url;
//
///**
// * 描述：
// * 阿里授权Controller
// *
// * @author qlp
// * @date 2019-09-12 14:13
// */
//@RequestMapping("/mogujie")
//@Controller
//public class MogujieOAuthController {
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    private static Logger log = LoggerFactory.getLogger(MogujieOAuthController.class);
//
//    /**
//     * 获取 token
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @RequestMapping("/getToken")
//    public String getToken(HttpServletRequest request) throws IOException, InterruptedException {
//        log.info("/**********获取蘑菇鸡授权token*********/");
//        String code = request.getParameter("code");
//        String state = request.getParameter("state");
//        log.info("/**********获取蘑菇鸡授权得到的code："+code+"*********/");
//
//        Integer shopTypeId = EnumShopType.MoGuJie.getIndex();
//        var entity = thirdSettingService.getEntity(shopTypeId);
//        String redirect_uri = "http://oms.huayiyungou.com:8080/mogujie/getToken";
//        //获取token
//        String tokeUrl = "https://oauth.mogujie.com/token?code=" +code+
//                "&grant_type=authorization_code" +
//                "&app_key=" +entity.getAppKey()+
//                "&app_secret=" +entity.getAppSecret()+
//                "&redirect_uri="+redirect_uri;
//
//        //http://oms.huayiyungou.com:8080/login?ref=http%3A%2F%2Foms.huayiyungou.com%3A8080%2Fmogujie%2FgetToken%3Fcode%3D0F4437D085CF41D18AFBA8889934CCA6%26state%3DORDER_LIST%26code_expires_in%3D600
//
//        HttpResponse<String> response = ExpressClient.doPost(tokeUrl,"");
//        if (response.statusCode() == 200) {
//           log.info("蘑菇街授权成功");
//            JSONObject jsonObject = JSONObject.parseObject(response.body());
//            thirdSettingService.updateEntity(entity.getId()
//                    ,jsonObject.getString("access_token")
//                    ,jsonObject.getString("refresh_token")
//                    ,jsonObject.getInteger("access_expires_in")
//                    ,"0"
//                    );
//            /***
//             * {"access_expires_in":1590062711,"access_token":"EA9EE6A7C41FAD51F98EEDB6C168A762","errorMsg":"成功！","refresh_expires_in":1591445111,"refresh_token":"C51872BB2F1B355FFD761D8460423FF0","state":"","statusCode":"0000000","token_type":"Bearer","userId":"1eyma60"}
//             */
//           log.info(response.body().toString());
//        }else{
//            //请求失败
//            log.info("蘑菇街授权失败");
//        }
//
////        TokenVo tokenVo = AliClient.getTokenVo(code, redirectUrl);
//        HttpSession session = request.getSession();
//
////        Integer shopId = Integer.parseInt(session.getAttribute("shopId").toString());
//
////        thirdSettingService.updateEntity(1, tokenVo.getAccess_token(), tokenVo.getRefresh_token(), tokenVo.getExpires_in(), tokenVo.getRefresh_token_timeout());
//
//
//        if (state.equalsIgnoreCase("ORDER_LIST")) {
//            //获取订单list
//            return "redirect:/mogujie/order_list?shopId=12";
//        }
//        return "redirect:/";
//    }
//
//    /**
//     * 调取授权页面
//     * @param request
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @RequestMapping("/oauth")
//    public String aliOAuth(HttpServletRequest request) throws IOException, InterruptedException {
//        Integer shopTypeId = EnumShopType.MoGuJie.getIndex();
//        var entity = thirdSettingService.getEntity(shopTypeId);
//        String redirect_uri = "http://oms.huayiyungou.com:8080/mogujie/getToken";
//
//
//        String url = "https://oauth.mogujie.com/authorize?response_type=code&app_key="+entity.getAppKey()+"&redirect_uri="+redirect_uri+"&state=ORDER_LIST";
//
//        return "redirect:" + url;
//    }
//}
