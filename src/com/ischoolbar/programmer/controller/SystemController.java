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
 * ϵͳ��ҳ������
 * @author Administrator
 *
 */

@RequestMapping("/system")
@Controller
public class SystemController {
	
	@Autowired
	private UserService UserService;//���������ó�����,���������ݿ��е�
	
	@RequestMapping(value = "/index",method = RequestMethod.GET)
//	public String index() {
//		return "hello world";
//	}
	public ModelAndView index(ModelAndView model) {
		//model�����ֱ�������ͼ�ַ���������ʾ��jsp�ļ�����Ӧ����Ȼ�޷���ʾ
		model.setViewName("system/index");
//		model.addObject("user", "����ţ��");
		return model;
	}
	
	/**
	 * ��ʾ��¼ҳ��
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		//GET�������/system/login,������������ͼ�ַ�����ʾָ����system/login.jspҳ��
		model.setViewName("system/login");
		return model;
	}
	
	/**
	 * ��¼���ύ
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
			ret.put("msg", "�û�������Ϊ�գ�");
			return ret;
		}
		if(StringUtil.isEmpty(password)) {
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ�գ�");
			return ret;
		}
		if(StringUtil.isEmpty(vcode)) {
			ret.put("type", "error");
			ret.put("msg", "��֤�벻��Ϊ�գ�");
			return ret;
		}
		String vcodeString = (String) request.getSession().getAttribute("loginCpacha");
		if (StringUtil.isEmpty(vcodeString)) {
			ret.put("type", "error");
			ret.put("msg", "��ʱ��δ�������Ự��ʧЧ����ˢ�º����ԣ�");
			return ret;
		}
		if (!vcode.toUpperCase().equals(vcodeString.toUpperCase())) {
			ret.put("type", "error");
			ret.put("msg", "������������֤�룡");
			return ret;
		}
		//���ջỰ�е���֤�룬��Ϊ��
		request.getSession().setAttribute("loginCpacha", null);
		//�����ݿ��в����û�
		if (type == 1) {
			//����Ա��¼
			User user = UserService.findByUserName(username);
			if (user == null) {
				ret.put("type", "error");
				ret.put("msg", "�����ڸù���Ա��");
				return ret;
			}
			if (!password.equals(user.getPassword())) {
				ret.put("type", "error");
				ret.put("msg", "�������");
				return ret;
			}
			request.getSession().setAttribute("user", user);
		}
		if (type == 2) {
			//ѧ����¼
			
		}
		ret.put("type", "success");
		ret.put("msg", "��¼�ɹ���");
		return ret;//ע��ResponseBody��ret��json�ַ�����ʽ����
	}
	
	
	/**
	 * ��ʾ��֤��
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


