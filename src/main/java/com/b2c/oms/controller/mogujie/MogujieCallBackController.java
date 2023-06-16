//package com.b2c.oms.controller.mogujie;
//
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.third.ali.AliClient;
//import com.b2c.common.third.ali.vo.TokenVo;
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
//import org.springframework.web.bind.annotation.ResponseBody;
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
//@RequestMapping("/mogujie")
//@Controller
//public class MogujieCallBackController {
//    @Autowired
//    private SystemService systemService;
//    @Autowired
//    private SysThirdSettingService thirdSettingService;
//    private static Logger log = LoggerFactory.getLogger(MogujieCallBackController.class);
//
//    /**
//     * 获取ali token
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @ResponseBody
//    @RequestMapping("/callback")
//    public ApiResult<String> getToken(HttpServletRequest request) throws IOException, InterruptedException {
//        log.info("/**********获取蘑菇街授权token*********/");
//
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "SUCCESS");
//    }
//
//
//
//}
