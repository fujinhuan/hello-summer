package com.b2c.oms.controller.order.tao;

import com.b2c.service.ShopService;
import com.b2c.service.oms.SysThirdSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 淘宝回调地址
 */
@Controller
public class OAuthTaoCallbackController {
    @Autowired
    private SysThirdSettingService thirdSettingService;
    @Autowired
    private ShopService shopService;
    private static Logger log = LoggerFactory.getLogger(OAuthTaoCallbackController.class);
    /**
     * 淘宝授权url
     *
     * @param request
     * @param shopId
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @RequestMapping("/tao_oauth")
    public String aliOAuth(HttpServletRequest request, @RequestParam Integer shopId) throws IOException, InterruptedException {
        //查询店铺信息
        var shop = shopService.getShop(shopId);

        var entity = thirdSettingService.getEntity(shop.getType());
        String url = "http://container.open.taobao.com/container?appkey=" + entity.getAppKey() + "&state=" + shopId;
        //https://oauth.taobao.com/authorize?response_type=token&force_auth=true&from_site=fuwu&client_id=28181872
        return "redirect:" + url;
    }

    /**
     * 淘宝授权回调
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/callback/taobao")
    public String callback(Model model, HttpServletRequest request) {
        log.info("淘系店铺授权回调开始");
        String sessionKey = request.getParameter("top_session");
        String state = request.getParameter("state");
        try {
            Integer shopId = Integer.parseInt(state);
            shopService.updateSessionKey(shopId,0l, sessionKey);
            return "redirect:/order/list?shopId=" + shopId;
        } catch (Exception e) {
            return "redirect:/?msg=callback_taobao_error";
        }
    }


}
