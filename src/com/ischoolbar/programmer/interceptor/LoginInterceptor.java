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
 * ��¼����������
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
		//System.out.println("����������,url��" + requestURI);
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			//��ʾ��ʱ��δ��¼�����¼ʧЧ
			System.out.println("δ��¼���¼ʧЧ,url:" + requestURI);
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				//��ť�ύ��ajax���󣬻ᷢ����¼��ʱ
				Map<String, String> ret = new HashMap<String, String>();
				ret.put("type", "error");
				ret.put("msg", "��¼״̬��ʧЧ�������µ�¼��");
				response.getWriter().write(JSONObject.fromObject(ret).toString());
				return false;//���أ���ʾ��¼��ʱ��Ϣ
			}
			//δ��¼���¼ʧЧ��ֱ�ӽ���ҳת����¼ҳ�����µ�¼��request.getContextPath()�õ���վ�ĸ�Ŀ¼
			response.sendRedirect(request.getContextPath() + "/system/login");
			//������û�����ajax�ύ��¼����ɵĵ�¼ʧЧ������student��¼
			//����ͨ��������ʽ�������Ȼlogin.jspת��index.jsp��ת��login.jsp
			return false;//����,����ʾ/system/indexҳ��
		}
		return true;//������
	}
	
}
