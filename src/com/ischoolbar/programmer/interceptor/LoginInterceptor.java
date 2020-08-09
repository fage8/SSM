package com.ischoolbar.programmer.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.ischoolbar.programmer.entity.User;

import net.sf.json.JSONObject;

/**
 * 登录过滤拦截器
 * @author Administrator
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		String requestURI = request.getRequestURI();
		//System.out.println("进入拦截器,url：" + requestURI);
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			//表示长时间未登录，或登录失效
			System.out.println("未登录或登录失效,url:" + requestURI);
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				//按钮提交的ajax请求，会发生登录超时
				Map<String, String> ret = new HashMap<String, String>();
				ret.put("type", "error");
				ret.put("msg", "登录状态已失效，请重新登录！");
				response.getWriter().write(JSONObject.fromObject(ret).toString());
				return false;//拦截，提示登录超时信息
			}
			//未登录或登录失效，直接将网页转到登录页面重新登录，request.getContextPath()拿到网站的根目录
			response.sendRedirect(request.getContextPath() + "/system/login");
			//如果是用户请求ajax提交登录表单造成的登录失效，比如student登录
			//必须通过其他方式解决，不然login.jsp转入index.jsp又转入login.jsp
			return false;//拦截,不显示/system/index页面
		}
		return true;//不拦截
	}
	
}
