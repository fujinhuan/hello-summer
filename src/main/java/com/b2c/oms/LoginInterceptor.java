package com.b2c.oms;

import com.b2c.service.oms.DcManageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession();

//        HttpSession session = SessionHelper.getSession(request);
        /**当前访问的页面*/
        String targetUrl = request.getRequestURL().toString();
        if (StringUtils.isEmpty(request.getQueryString()) == false) {
            targetUrl += "?" + request.getQueryString();
        }
       if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?ref=" + URLEncoder.encode(targetUrl,"utf-8"));
            return false;
        } else {
            return true;
        }
        //return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView mv)
            throws Exception {

        HttpSession session = request.getSession();

        if (session.getAttribute("userId") != null) {

            if(StringUtils.isEmpty(request.getParameter("shopId"))==false) {
                //有带店铺id参数，判断是否有店铺权限
                Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
                String shopList = session.getAttribute("shopList").toString();
                Integer shopId = Integer.parseInt(request.getParameter("shopId"));
                mv.addObject("shopId",shopId);

                //判断shopId是否存在
                if(shopList.indexOf("["+shopId+"]")>-1){
                    //存在，有店铺权限
//                    System.out.println("用户"+userId+"访问的店铺id：" + shopId+"，结果：有权限");
                }else {
                    System.out.println("用户"+userId+"访问的店铺id：" + shopId+"你没有该店铺的管理权限：");
                    mv.setViewName("not_permission");

                }

            }
        }
    }
}
