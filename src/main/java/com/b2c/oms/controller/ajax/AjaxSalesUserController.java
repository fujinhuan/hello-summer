package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.entity.BaseAreaEntity;
import com.b2c.entity.DataRow;
import com.b2c.entity.UserEntity;
import com.b2c.entity.result.ResultVo;
import com.b2c.oms.reponse.AreaApiResult;
import com.b2c.service.BaseAreaService;
import com.b2c.service.erp.ErpSalesUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/ajax_user")
public class AjaxSalesUserController {
    @Autowired
    private ErpSalesUserService userService;
    @Autowired
    private BaseAreaService areaService;

    @RequestMapping(value = "/user_list_by_developerId", method = RequestMethod.POST)
    public ApiResult<List<UserEntity>> searchGoodsDetail(@RequestBody DataRow req) {
        var result = userService.getUserListByDeveloperId(req.getInt("developerId"));
        return new ApiResult<>(0, "SUCCESS", result);
    }

    /**
     * 地址数据初始化
     *
     * @param
     * @return
     */
    @RequestMapping("/address_data")
    public AreaApiResult get(@RequestParam String code) {
        AreaApiResult areaApiResult = new AreaApiResult();
        if (StringUtils.isEmpty(code)) {
            areaApiResult.setStatus(-1);
            return areaApiResult;
        }
        ResultVo<List<BaseAreaEntity>> resultVo = areaService.getListByParent(code);
        if (resultVo.getCode() == 0) {
            //成功
            if (resultVo.getData() != null && resultVo.getData().size() > 0) {
                areaApiResult.setStatus(1);
                for (BaseAreaEntity area : resultVo.getData()) {
                    areaApiResult.addData(area.getCode(), area.getName());
                }
            } else {
                areaApiResult.setStatus(0);
                areaApiResult.setData(null);
                return areaApiResult;
            }
        } else {
            areaApiResult.setStatus(0);
            areaApiResult.setData(null);
            return areaApiResult;
        }
        return areaApiResult;
    }
}
