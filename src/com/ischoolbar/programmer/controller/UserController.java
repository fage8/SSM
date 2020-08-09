package com.ischoolbar.programmer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.UserService;

/**
 * 用户(管理员)控制器
 * @author Administrator
 *
 */
@RequestMapping("/user")
@Controller
public class UserController {
	
	@Autowired 
	public UserService userService;//从容器中拿出对象,来自于数据库中的
	
	/**
	 * 用户管理页面列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		//GET传入参数/user/list,控制器则让视图分发器显示指定的user/user_list.jsp页面
		model.setViewName("user/user_list");
		return model;
	}
	
	
	/**
	 * 获取用户列表
	 * @param username
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/get_list", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getList(
			@RequestParam(value = "username", required = false, defaultValue = "") String username,
			Page page
			){
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("username", "%"+username+"%");
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		//easyui的格式，rows里面存放的是用户列表，total里面存放的是总的记录数
		ret.put("rows", userService.findList(queryMap));
		ret.put("total", userService.getTotal(queryMap));
		return ret;
	}
	
	/**
	 * 添加用户操作
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(User user){
		//@Component注解的user对象是通过在user_list.jsp输入框输入的数据，因为springMVC自动检查如果有就赋值
		Map<String, String> ret = new HashMap<String, String>();
		if (user == null) {
			ret.put("type", "error");
			ret.put("msg", "数据绑定错误，请联系开发者！");
			return ret;
		}
		if (StringUtil.isEmpty(user.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			return ret;
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			return ret;
		}
		User exitUserName = userService.findByUserName(user.getUsername());
		if (exitUserName != null) {
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在！");
			return ret;
		}
		if (userService.add(user) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "添加失败！");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "添加成功！");		
		return ret;//注解ResponseBody，ret以json字符串格式返回
	}
	
	
	/**
	 * 编辑用户操作
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(User user){
		//@Component注解的user对象是通过在user_list.jsp输入框输入的数据，因为springMVC自动检查如果有就赋值
		Map<String, String> ret = new HashMap<String, String>();
		if (user == null) {
			ret.put("type", "error");
			ret.put("msg", "数据绑定错误，请联系开发者！");
			return ret;
		}
		if (StringUtil.isEmpty(user.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			return ret;
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			return ret;
		}
		User exitUserName = userService.findByUserName(user.getUsername());
		if (exitUserName != null) {
			//比较修改的用户名所在的id与数据库已存在的用户名id是否相同，若相同不允许修改
			if (user.getId() != exitUserName.getId()) {
				ret.put("type", "error");
				ret.put("msg", "该用户名已经存在！");
				return ret;
			}
		}
		if (userService.edit(user) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "修改失败！");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "修改成功！");		
		return ret;//注解ResponseBody，ret以json字符串格式返回
	}

	
	/**
	 * 删除用户操作
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(
			@RequestParam(value = "ids[]", required = true) Long[] ids
			){
		//ids参数如果是直接给，会从容器去拿，因为没有注解会出问题，导致删除按钮请求表单提交的数据无法传过来，会报空指针异常
		//可以采用request请求中去定义该参数ids，这样就可以正常传数据到后台方法中
		Map<String, String> ret = new HashMap<String, String>();
//		ret.put("ids", ids.toString());
		if (ids == null) {
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的数据！");
			return ret;
		}
		String idsString = "";
		for (Long id : ids) {
			idsString += id + ",";
		}
		//idsString.substring(0,idsString.length() - 1)取前面foreach遍历的id数组，去掉最后的逗号
		//将长整型ids转化成字符串
		idsString = idsString.substring(0,idsString.length() - 1);
		if (userService.delete(idsString) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "删除失败！");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "删除成功！");	
		return ret;
	}
}
