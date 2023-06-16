package com.b2c.oms.controller.mogujie;


import com.b2c.common.api.ApiResult;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.service.mall.SystemService;
import com.b2c.service.oms.SysThirdSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 描述：
 * 阿里授权Controller
 *
 * @author qlp
 * @date 2019-09-12 14:13
 */
@RequestMapping("/mogujie")
@Controller
public class MogujieNotifyController {
    @Autowired
    private SystemService systemService;
    @Autowired
    private SysThirdSettingService thirdSettingService;
    private static Logger log = LoggerFactory.getLogger(MogujieNotifyController.class);

    /**
     * 获取ali token
     *
     * @param request
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @ResponseBody
    @RequestMapping("/notify")
    public NotifyApi getToken(HttpServletRequest request) throws IOException, InterruptedException {
        log.info("/**********蘑菇街通知消息*********/");

        /***
         * 推送事件code - desc
         * return_goods_requested - 买家发起退货,等待卖家同意
         * refund_requested - 买家发起退款，等待卖家同意
         * return_goods_shipped - 买家发货，等待卖家收货
         * modify_ship_to_address - 买家修改收货地址（未发货前可修改一次）
         *
         * 
         * 蘑菇街通知消息：
         * {
         * "sellerId":452336480,
         * "orderId":93182337647084,
         * "event":
         * {
         * "code":"refund_requested",
         * "desc":"买家发起退款，等待卖家同意"
         * },
         * "updated":1589188478,
         * "refundId":291550576,
         * "status":"ORDER_PAID"}
         * */

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.info("读取错误："+ e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        log.info("/**********蘑菇街通知消息："+sb.toString()+"*********/");
        return new NotifyApi("000000", "succeed");
    }



}
