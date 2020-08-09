package com.ischoolbar.programmer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.GradeService;

@RequestMapping("/grade")
@Controller
public class GradeController {
	
	@Autowired
	public GradeService gradeService;
	/**
	 * �꼶����ҳ���б�
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		//GET�������/user/list,������������ͼ�ַ�����ʾָ����user/user_list.jspҳ��
		model.setViewName("grade/grade_list");
		return model;
	}
	
	@RequestMapping(value = "/get_list", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getList(
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			Page page
			){
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("name", "%"+name+"%");
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		//easyui�ĸ�ʽ��rows�����ŵ����û��б�total�����ŵ����ܵļ�¼��
		ret.put("rows", gradeService.findList(queryMap));
		ret.put("total", gradeService.getTotal(queryMap));
		return ret;
	}
}
