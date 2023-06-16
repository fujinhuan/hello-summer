package com.b2c.oms.controller;


import com.b2c.service.oms.OrderYungouService;
import com.b2c.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：
 * 订单管理Controller
 *
 * @author ly
 * @date 2019-1-9 13:58
 */
@Controller
public class ShopOrderController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderYungouService dcOrderService;

    /**
     * 订单列表
     *
     * @param model
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/lists", method = RequestMethod.GET)
    public String list(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        model.addAttribute("menuId", "order_list");
        String status = request.getParameter("status");

        if (shopId.intValue() == 1) {
            //1688店铺
            return "redirect:/ali_order/list?shopId=" + shopId;
        } else if (shopId.intValue() == 10) {
            //有赞店铺
            return "redirect:/order/yzOrderList?shopId=" + shopId;
        } else if (shopId.intValue() == 2 || shopId.intValue() == 6 || shopId.intValue() == 7) {
            //淘系
            return "redirect:/tao_order/list?shopId=" + shopId;

        } else if (shopId.intValue() == 3 || shopId.intValue() == 4 || shopId.intValue() == 11) {
            //云购平台
            return "redirect:/order_yungou/list?shopId=" + shopId;
        }
//        else if (shopId.intValue() == 4) {
//            //批发系统
//            return "redirect:/pifa/orderList?shopId=" + shopId+ "&status=" + status;
//        } else if (shopId.intValue() == 11) {
//            //直播机构
//            return "redirect:/daifa/orderList?shopId=" + shopId+ "&status=" + status;
//        }
        else if (shopId.intValue() == 5 || shopId.intValue() == 14 || shopId==18) {
            //拼多多
            return "redirect:/pdd/orderList?shopId=" + shopId+"&status="+status;
        }else if(shopId.intValue() == 8 || shopId==19){
            //抖音
            return "redirect:/douyin/order_list?shopId="+shopId;
        }
        else if (shopId.intValue() == 9) {
            //批批网
            return "redirect:/pipi/order_list?shopId=" + shopId;
        }else if (shopId.intValue() == 12) {
            //蘑菇街
            return "redirect:/mogujie/order_list?shopId=" + shopId;
        }else if (shopId.intValue() == 13) {
            //快手
            return "redirect:/kwai/order_list?shopId=" + shopId;
        }
        else if (shopId.intValue() == 99) {
            //ERP系统订单
            return "redirect:/order/list?shopId=" + shopId;
        }


//        var shop = shopService.getShop(shopId);
//        String status = "";
//        if (!StringUtils.isEmpty(request.getParameter("status"))) status = request.getParameter("status");
//        if (shop != null) {
//            if (shop.getType().intValue() == EnumShopType.Ali.getIndex()) {
//                //1688店铺
//                return "redirect:/order/aliOrderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.YouZan.getIndex()) {
//                //有赞店铺
//                return "redirect:/order/yzOrderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.TaoBao.getIndex()) {
//                //淘宝店铺
//                return "redirect:/order_taobao/order_list?shopId=" + shopId + "&status=" + status;
//
//            } else if (shop.getType().intValue() == EnumShopType.Tmall.getIndex()) {
//                //天猫店铺
//                return "redirect:/order/tmallOrderList?shopId=" + shopId + "&status=" + status;
//
//            } else if (shop.getType().intValue() == EnumShopType.YunGou.getIndex()) {
//                //云购平台
//                status=status.equals("4") ? "2" : "";
//                return "redirect:/order_yungou/list?shopId=" + shopId+"&status=" + status;
//            }else if (shop.getType().intValue() == EnumShopType.PiFa.getIndex()) {
//                //批发系统
//                return "redirect:/pifa/orderList?shopId=" + shopId + "&status=" + status;
//            }else if (shop.getType().intValue() == EnumShopType.Pdd.getIndex()) {
//                //拼多多
//                return "redirect:/pdd/orderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.DaiFa.getIndex()) {
//                //直播机构
//                return "redirect:/daifa/orderList?shopId=" + shopId + "&status=" + status;
//            }
//        }
        return "redirect:/";
    }

    /**
     * 手动下单
     * @param model
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/create", method = RequestMethod.GET)
    public String create(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        model.addAttribute("menuId", "order_create");

        if (shopId.intValue() == 1) {
            //1688店铺
            return "redirect:/ali_order/list?shopId=" + shopId;
        } else if (shopId.intValue() == 10) {
            //有赞店铺
            return "redirect:/order/yzOrderList?shopId=" + shopId;
        } else if (shopId.intValue() == 2 || shopId.intValue() == 6 ) {
            //淘系
            return "redirect:/tao_order/list?shopId=" + shopId;

        } else if (shopId.intValue() == 3 || shopId.intValue() == 4 || shopId.intValue() == 11) {
            //云购平台
            return "redirect:/order_yungou/list?shopId=" + shopId;
        }
        else if (shopId.intValue() == 5) {
            //拼多多
            return "redirect:/pdd/order_create?shopId=" + shopId;
        }else if(shopId.intValue() == 8){
            return "redirect:/douyin/order_create?shopId="+shopId;
        }
        else if (shopId.intValue() == 9) {
            //批批网
            return "redirect:/pipi/order_list?shopId=" + shopId;
        }else if (shopId.intValue() == 12) {
            //蘑菇街
            return "redirect:/mogujie/order_create?shopId=" + shopId;
        } else if (shopId.intValue() == 99) {
            //ERP系统订单
            return "redirect:/sales/system_order?shopId=" + shopId;
        } else if (shopId.intValue() == 7) {
            //ERP系统订单
            return "redirect:/tao_order/order_create?shopId=" + shopId;
        }

        return "redirect:/";
    }


    /**
     * 退货管理
     *
     * @param model
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping(value = "/refund/lists", method = RequestMethod.GET)
    public String refundList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        String status = "";
        if (!StringUtils.isEmpty(request.getParameter("status"))) status = request.getParameter("status");
        String type = "";
        if (!StringUtils.isEmpty(request.getParameter("type"))) type = request.getParameter("type");
        String auditStatus = "";
        if (!StringUtils.isEmpty(request.getParameter("auditStatus"))) auditStatus = request.getParameter("auditStatus");
        if (shopId.intValue() == 1) {
            //1688店铺
            return "redirect:/order/aliRefundList?shopId=" + shopId + "&status=" + status;
        } else if (shopId.intValue() == 10) {
            //有赞店铺
            return "redirect:/order/yzOrderList?shopId=" + shopId + "&status=" + status;
        } else if (shopId.intValue() == 2 || shopId.intValue() == 6 || shopId.intValue() == 7) {
            //淘系
            return "redirect:/tao_order/refund_list?shopId=" + shopId + "&status=" + status;

        } else if (shopId.intValue() == 3 || shopId.intValue() == 4 || shopId.intValue() == 11) {
            //云购平台
            return "redirect:/order_yungou/refund_list?shopId=" + shopId + "&status=" + status;
        }
//        else if (shopId.intValue() == 4) {
//            //批发系统
//            return "redirect:/pifa/pifa_refund_list?shopId=" + shopId+ "&status=" + status;
//        } else if (shopId.intValue() == 11) {
//            //直播机构
//            return "redirect:/daifa/daifa_refund_list?shopId=" + shopId+ "&status=" + status;
//        }
        else if (shopId.intValue() == 5 || shopId.intValue() == 18 || shopId.intValue() == 14) {
            //拼多多
            return "redirect:/pdd/refund_list?shopId=" + shopId + "&status=" + status+"&type="+type+"&auditStatus="+auditStatus;
        }else if(shopId.intValue() == 8 || shopId.intValue()==19){
            //抖音
            return "redirect:/douyin/refund_list?shopId="+shopId;
        }else if (shopId.intValue() == 9) {
            //批批网
            return "redirect:/pipi/refund_list?shopId=" + shopId + "&status=" + status;
        }
        else if (shopId.intValue() == 12) {
            //蘑菇街
            return "redirect:/mogujie/refund_list?shopId=" + shopId;
        }

        else if (shopId.intValue() == 99) {
            //ERP系统订单
            return "redirect:/refund/list?shopId=" + shopId;
        }


//        var shop = shopService.getShop(shopId);
//        String status = "";
//        if (!StringUtils.isEmpty(request.getParameter("status"))) status = request.getParameter("status");
//        if (shop != null) {
//            if (shop.getType().intValue() == EnumShopType.Ali.getIndex()) {
//                //1688店铺
//                return "redirect:/order/aliOrderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.YouZan.getIndex()) {
//                //有赞店铺
//                return "redirect:/order/yzOrderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.TaoBao.getIndex()) {
//                //淘宝店铺
//                return "redirect:/order_taobao/order_list?shopId=" + shopId + "&status=" + status;
//
//            } else if (shop.getType().intValue() == EnumShopType.Tmall.getIndex()) {
//                //天猫店铺
//                return "redirect:/order/tmallOrderList?shopId=" + shopId + "&status=" + status;
//
//            } else if (shop.getType().intValue() == EnumShopType.YunGou.getIndex()) {
//                //云购平台
//                status=status.equals("4") ? "2" : "";
//                return "redirect:/order_yungou/list?shopId=" + shopId+"&status=" + status;
//            }else if (shop.getType().intValue() == EnumShopType.PiFa.getIndex()) {
//                //批发系统
//                return "redirect:/pifa/orderList?shopId=" + shopId + "&status=" + status;
//            }else if (shop.getType().intValue() == EnumShopType.Pdd.getIndex()) {
//                //拼多多
//                return "redirect:/pdd/orderList?shopId=" + shopId + "&status=" + status;
//            } else if (shop.getType().intValue() == EnumShopType.DaiFa.getIndex()) {
//                //直播机构
//                return "redirect:/daifa/orderList?shopId=" + shopId + "&status=" + status;
//            }
//        }
        return "redirect:/";
    }

}
