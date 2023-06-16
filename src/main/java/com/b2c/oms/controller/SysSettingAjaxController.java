package com.b2c.oms.controller;

import com.b2c.common.api.ApiResult;
import com.b2c.entity.DataRow;
import com.b2c.entity.datacenter.vo.UserShopIdVo;
import com.b2c.entity.result.ResultVo;
import com.b2c.service.oms.DcLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class SysSettingAjaxController {
    @Autowired
    private DcLoginService loginService;


    /**
     * 删除管理员
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/dc/del_manage_user", method = RequestMethod.POST)
    public ApiResult<Integer> delManageUser(@RequestBody DataRow model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userId = request.getSession().getAttribute("userId");
        if (Integer.parseInt(userId.toString())!=1)  return new ApiResult<>(401, "permission_denied");

        loginService.delUser(model.getInt("userid"));
        return new ApiResult<>(0, "SUCCESS");
    }

    /**
     * 修改添加管理员
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/dc/execute_manage_user", method = RequestMethod.POST)
    public ApiResult<Integer> checkManageUser(@RequestBody UserShopIdVo model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userId = request.getSession().getAttribute("userId");
        if (Integer.parseInt(userId.toString())!=1)  return new ApiResult<>(401, "permission_denied");

        ResultVo<Integer> rs = loginService.updAddUser(model.getId(), model.getUserName(),model.getMobile(),model.getTrueName(), model.getUserPwd(), model.getGroupId(), model.getStatus().toString());
        if (rs.getCode() > 0) {
            return new ApiResult<>(rs.getCode(), rs.getMsg());
        }
        return new ApiResult<>(0, "SUCCESS");
    }
}
