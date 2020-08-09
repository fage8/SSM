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
 * �û�(����Ա)������
 * @author Administrator
 *
 */
@RequestMapping("/user")
@Controller
public class UserController {
	
	@Autowired 
	public UserService userService;//���������ó�����,���������ݿ��е�
	
	/**
	 * �û�����ҳ���б�
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		//GET�������/user/list,������������ͼ�ַ�����ʾָ����user/user_list.jspҳ��
		model.setViewName("user/user_list");
		return model;
	}
	
	
	/**
	 * ��ȡ�û��б�
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
		//easyui�ĸ�ʽ��rows�����ŵ����û��б�total�����ŵ����ܵļ�¼��
		ret.put("rows", userService.findList(queryMap));
		ret.put("total", userService.getTotal(queryMap));
		return ret;
	}
	
	/**
	 * ����û�����
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(User user){
		//@Componentע���user������ͨ����user_list.jsp�������������ݣ���ΪspringMVC�Զ��������о͸�ֵ
		Map<String, String> ret = new HashMap<String, String>();
		if (user == null) {
			ret.put("type", "error");
			ret.put("msg", "���ݰ󶨴�������ϵ�����ߣ�");
			return ret;
		}
		if (StringUtil.isEmpty(user.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "�û�������Ϊ�գ�");
			return ret;
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ�գ�");
			return ret;
		}
		User exitUserName = userService.findByUserName(user.getUsername());
		if (exitUserName != null) {
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ����ڣ�");
			return ret;
		}
		if (userService.add(user) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "���ʧ�ܣ�");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "��ӳɹ���");		
		return ret;//ע��ResponseBody��ret��json�ַ�����ʽ����
	}
	
	
	/**
	 * �༭�û�����
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(User user){
		//@Componentע���user������ͨ����user_list.jsp�������������ݣ���ΪspringMVC�Զ��������о͸�ֵ
		Map<String, String> ret = new HashMap<String, String>();
		if (user == null) {
			ret.put("type", "error");
			ret.put("msg", "���ݰ󶨴�������ϵ�����ߣ�");
			return ret;
		}
		if (StringUtil.isEmpty(user.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "�û�������Ϊ�գ�");
			return ret;
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ�գ�");
			return ret;
		}
		User exitUserName = userService.findByUserName(user.getUsername());
		if (exitUserName != null) {
			//�Ƚ��޸ĵ��û������ڵ�id�����ݿ��Ѵ��ڵ��û���id�Ƿ���ͬ������ͬ�������޸�
			if (user.getId() != exitUserName.getId()) {
				ret.put("type", "error");
				ret.put("msg", "���û����Ѿ����ڣ�");
				return ret;
			}
		}
		if (userService.edit(user) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "�޸�ʧ�ܣ�");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "�޸ĳɹ���");		
		return ret;//ע��ResponseBody��ret��json�ַ�����ʽ����
	}

	
	/**
	 * ɾ���û�����
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(
			@RequestParam(value = "ids[]", required = true) Long[] ids
			){
		//ids���������ֱ�Ӹ����������ȥ�ã���Ϊû��ע�������⣬����ɾ����ť������ύ�������޷����������ᱨ��ָ���쳣
		//���Բ���request������ȥ����ò���ids�������Ϳ������������ݵ���̨������
		Map<String, String> ret = new HashMap<String, String>();
//		ret.put("ids", ids.toString());
		if (ids == null) {
			ret.put("type", "error");
			ret.put("msg", "��ѡ��Ҫɾ�������ݣ�");
			return ret;
		}
		String idsString = "";
		for (Long id : ids) {
			idsString += id + ",";
		}
		//idsString.substring(0,idsString.length() - 1)ȡǰ��foreach������id���飬ȥ�����Ķ���
		//��������idsת�����ַ���
		idsString = idsString.substring(0,idsString.length() - 1);
		if (userService.delete(idsString) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "ɾ��ʧ�ܣ�");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "ɾ���ɹ���");	
		return ret;
	}
}
