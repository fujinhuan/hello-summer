package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.entity.DataRow;
import com.b2c.entity.erp.vo.GoodsSearchShowVo;
import com.b2c.oms.request.GoodsSearchReq;
import com.b2c.service.erp.ErpGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping(value = "/ajax_goods")
public class AjaxGoodsController {
    @Autowired
    private ErpGoodsService goodsService;

    /**
     * 根据商品编码搜索商品
     * @param req
     * @return
     */
    @RequestMapping(value = "/goods_spec_search_by_number", method = RequestMethod.POST)
    public ApiResult<List<GoodsSearchShowVo>> searchGoodsDetail(@RequestBody GoodsSearchReq req) {
        List<GoodsSearchShowVo> goods = new ArrayList<>();//goodsService.getGoodsSpecByNumberForSale(req.getKey(), 10);
        return new ApiResult<>(0, "SUCCESS", goods);
    }
}
