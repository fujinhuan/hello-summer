//package com.b2c.oms.controller.tao;
//
//
//import com.b2c.common.api.ApiResult;
//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.CainiaoCloudprintStdtemplatesGetRequest;
//import com.taobao.api.request.CainiaoWaybillIiGetRequest;
//import com.taobao.api.response.CainiaoCloudprintStdtemplatesGetResponse;
//import com.taobao.api.response.CainiaoWaybillIiGetResponse;
//import org.java_websocket.drafts.Draft_17;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//public class TaoPrintServerTestController {
//
//    @RequestMapping(value = "/tmall_print_test", method = RequestMethod.GET)
//    public String tmallPrintTest(Model model, HttpServletRequest request) {
//
//        return "tmall_order_print";
//    }
//
//    @RequestMapping(value = "/tmall_print_fahuodan_test", method = RequestMethod.GET)
//    public String tmallPrintTest1(Model model, HttpServletRequest request) throws URISyntaxException {
//
//        return "tmall_order_print_fahuodan";
//    }
//
//
//    @ResponseBody
//    @RequestMapping(value = "/tmall_print", method = RequestMethod.GET)
//    public ApiResult<String> print() throws ApiException {
//        String url = "http://gw.api.taobao.com/router/rest";
//        String appkey = "28181872";
//        String secret = "71815e1aa95b9aac68276aa2873bf634";
//        String sessionKey = "6100c19932f3e3b47a6e27e5bd518e4d5d76263b96170e62206529834322";
//
//
//        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//
//        /*******快递模版*******/
//        CainiaoCloudprintStdtemplatesGetRequest reqTemp = new CainiaoCloudprintStdtemplatesGetRequest();
//        CainiaoCloudprintStdtemplatesGetResponse rspTemp = client.execute(reqTemp);
//        System.out.println(rspTemp.getBody());
//
////        UserSellerGetRequest reqUser = new UserSellerGetRequest();
////        reqUser.setFields("nick,sex,user_id");
////        UserSellerGetResponse rspUser = client.execute(reqUser, sessionKey);
////        System.out.println(rspUser.getBody());
//
//
//        CainiaoWaybillIiGetRequest req = new CainiaoWaybillIiGetRequest();
//
//        CainiaoWaybillIiGetRequest.WaybillCloudPrintApplyNewRequest obj1 = new CainiaoWaybillIiGetRequest.WaybillCloudPrintApplyNewRequest();
//        obj1.setCpCode("YUNDA");
//
//        //发货地址
//        CainiaoWaybillIiGetRequest.UserInfoDto obj2 = new CainiaoWaybillIiGetRequest.UserInfoDto();
//
//        CainiaoWaybillIiGetRequest.AddressDto obj3 = new CainiaoWaybillIiGetRequest.AddressDto();
//        obj3.setCity("东莞市");
//        obj3.setDetail("大朗镇聚新二路42号 广东华衣云商科技有限公司");
//        obj3.setDistrict("");
//        obj3.setProvince("广东省");
//        obj3.setTown("");
//
//        obj2.setAddress(obj3);
//        obj2.setMobile("13430865420");
//        obj2.setName("冯先生");
//        obj2.setPhone("");
//
//        obj1.setSender(obj2);//发货地址
//
//        /***************请求面单信息，数量限制为10******************/
//        List<CainiaoWaybillIiGetRequest.TradeOrderInfoDto> tradeOrderInfoDtoList = new ArrayList<CainiaoWaybillIiGetRequest.TradeOrderInfoDto>();
//
//
//        /*********第一条面单信息**********/
//        CainiaoWaybillIiGetRequest.TradeOrderInfoDto tradeOrderInfoDto = new CainiaoWaybillIiGetRequest.TradeOrderInfoDto();
//
////        obj6.setLogisticsServices("如不需要特殊服务，该值为空");
//        tradeOrderInfoDto.setObjectId("1");
//
//        CainiaoWaybillIiGetRequest.OrderInfoDto orderInfo = new CainiaoWaybillIiGetRequest.OrderInfoDto();
//        orderInfo.setOrderChannelsType("TM");
//        List<String> orderIdList = new ArrayList<>();
//        orderIdList.add("581721356393558111");
//        orderInfo.setTradeOrderList(orderIdList);
//
//        tradeOrderInfoDto.setOrderInfo(orderInfo);
//
//
//        /*********包裹信息***********/
//        CainiaoWaybillIiGetRequest.PackageInfoDto packageInfo = new CainiaoWaybillIiGetRequest.PackageInfoDto();
//        packageInfo.setId("1");
//
//        //包裹信息1
//        List<CainiaoWaybillIiGetRequest.Item> items = new ArrayList<CainiaoWaybillIiGetRequest.Item>();
//
//        CainiaoWaybillIiGetRequest.Item item = new CainiaoWaybillIiGetRequest.Item();
//
//        item.setCount(1L);
//        item.setName("衣服");
//
//        items.add(item);
//
//        packageInfo.setItems(items);
//
//
//        packageInfo.setVolume(1L);
//        packageInfo.setWeight(1L);
////        obj10.setTotalPackagesCount(10L);
////        obj10.setPackagingDescription("5纸3木2拖");
////        obj10.setGoodsDescription("服装");
//
//        tradeOrderInfoDto.setPackageInfo(packageInfo);
//
//
//        /******UserInfoDto 收件人信息 *****/
//
//        CainiaoWaybillIiGetRequest.UserInfoDto userInfoDto = new CainiaoWaybillIiGetRequest.UserInfoDto();
//
//        CainiaoWaybillIiGetRequest.AddressDto addressDto = new CainiaoWaybillIiGetRequest.AddressDto();
//        addressDto.setCity("深圳市");
//        addressDto.setDetail("西乡街道固戍万象新天1期3栋2单元502");
//        addressDto.setDistrict("宝安区");
//        addressDto.setProvince("广东省");
//        addressDto.setTown("西乡街道");
//
//        userInfoDto.setAddress(addressDto);
//        userInfoDto.setMobile("15999600065");
//        userInfoDto.setName("袁润");
//        userInfoDto.setPhone("15999600065");
//
//        tradeOrderInfoDto.setRecipient(userInfoDto);
//
//        tradeOrderInfoDto.setTemplateUrl("http://cloudprint.cainiao.com/template/standard/101");
//        tradeOrderInfoDto.setUserId(2206529834322L);
//
//
//        tradeOrderInfoDtoList.add(tradeOrderInfoDto);
//
//        obj1.setTradeOrderInfoDtos(tradeOrderInfoDtoList);
//
////        obj1.setStoreCode("553323");
////        obj1.setResourceCode("DISTRIBUTOR_978324");
//        obj1.setDmsSorting(false);
//        obj1.setThreePlTiming(false);
//        obj1.setNeedEncrypt(false);
//        req.setParamWaybillCloudPrintApplyNewRequest(obj1);
//        CainiaoWaybillIiGetResponse rsp = client.execute(req, sessionKey);
//        System.out.println(rsp.getBody());
//
//        return new ApiResult<>(500, "");
//    }
//
//
//}
