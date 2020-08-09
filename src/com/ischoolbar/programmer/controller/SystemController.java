package com.ischoolbar.programmer.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.service.UserService;
import com.ischoolbar.programmer.util.CpachaUtil;

import net.sf.jsqlparser.statement.create.table.Index;

/**
 * 系统主页控制器
 * @author Administrator
 *
 */

@RequestMapping("/system")
@Controller
public class SystemController {
	
	@Autowired
	private UserService UserService;//从容器中拿出对象,来自于数据库中的
	
	@RequestMapping(value = "/index",method = RequestMethod.GET)
//	public String index() {
//		return "hello world";
//	}
	public ModelAndView index(ModelAndView model) {
		//model的名字必须于视图分发器首先显示的jsp文件名对应，不然无法显示
		model.setViewName("system/index");
//		model.addObject("user", "发哥牛逼");
		return model;
	}
	
	/**
	 * 显示登录页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		//GET传入参数/system/login,控制器则让视图分发器显示指定的system/login.jsp页面
		model.setViewName("system/login");
		return model;
	}
	
	/**
	 * 登录表单提交
	 * @return
	 */
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> login(
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "vcode", required = true) String vcode,
			@RequestParam(value = "type", required = true) int type,
			HttpServletRequest request
			) {
		Map<String, String> ret = new HashMap<String, String>();
		if(StringUtil.isEmpty(username)) {
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			return ret;
		}
		if(StringUtil.isEmpty(password)) {
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			return ret;
		}
		if(StringUtil.isEmpty(vcode)) {
			ret.put("type", "error");
			ret.put("msg", "验证码不能为空！");
			return ret;
		}
		String vcodeString = (String) request.getSession().getAttribute("loginCpacha");
		if (StringUtil.isEmpty(vcodeString)) {
			ret.put("type", "error");
			ret.put("msg", "长时间未操作，会话已失效，请刷新后重试！");
			return ret;
		}
		if (!vcode.toUpperCase().equals(vcodeString.toUpperCase())) {
			ret.put("type", "error");
			ret.put("msg", "请重新输入验证码！");
			return ret;
		}
		//回收会话中的验证码，置为空
		request.getSession().setAttribute("loginCpacha", null);
		//从数据库中查找用户
		if (type == 1) {
			//管理员登录
			User user = UserService.findByUserName(username);
			if (user == null) {
				ret.put("type", "error");
				ret.put("msg", "不存在该管理员！");
				return ret;
			}
			if (!password.equals(user.getPassword())) {
				ret.put("type", "error");
				ret.put("msg", "密码错误！");
				return ret;
			}
			request.getSession().setAttribute("user", user);
		}
		if (type == 2) {
			//学生登录
			
		}
		ret.put("type", "success");
		ret.put("msg", "登录成功！");
		return ret;//注解ResponseBody，ret以json字符串格式返回
	}
	
	
	/**
	 * 显示验证码
	 * @param request
	 * @param vl
	 * @param w
	 * @param h
	 * @param response
	 */
	@RequestMapping(value = "/get_cpacha", method = RequestMethod.GET)
	public void getCpacha(HttpServletRequest request,
			@RequestParam(value = "vl", defaultValue = "4", required = false) Integer vl,
			@RequestParam(value = "w", defaultValue = "98", required = false) Integer w,
			@RequestParam(value = "h", defaultValue = "33", required = false) Integer h,
			HttpServletResponse response) {
		CpachaUtil cpachaUtil = new CpachaUtil(vl, w, h);
		String generatorVCode = cpachaUtil.generatorVCode();
		request.getSession().setAttribute("loginCpacha", generatorVCode);
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ImageIO.write(generatorRotateVCodeImage, "gif", response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


