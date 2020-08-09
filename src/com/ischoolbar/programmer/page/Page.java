package com.ischoolbar.programmer.page;

import org.springframework.stereotype.Component;

/**
 * 分页类封装
 * @author Administrator
 *
 */
@Component
public class Page {
	private int page;//当前页面
	private int rows;//每页显示的记录数
	private int offset;//页面起始页码

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getOffset() {
		this.offset = (page - 1) * rows;
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = (page - 1) * rows;//这个赋值没有作用
	}
	
}
