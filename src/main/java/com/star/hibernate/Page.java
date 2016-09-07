package com.star.hibernate;

import java.io.Serializable;
import java.util.List;

/**
 * 分页帮助类
 * 
 * @author starhq
 *
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 每页显示条数
	 */
	public static final int DEFAUTSIZE = 10;

	/**
	 * 每页记录数，要改成从属性文件中读取
	 */
	private int pageSize = 5;

	/**
	 * 总记录数
	 */
	private long totalCount;

	/**
	 * 当前页
	 */
	private int pageNum = 1;

	/**
	 * 总页数
	 */
	private int pageCount;

	/**
	 * 记录
	 */
	private List<?> data;

	/**
	 * 返回每页记录数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页记录数
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 获得总记录数
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数
	 */
	public void setTotalCount(final Long totalCount) {
		if (totalCount <= 0) {
			this.totalCount = 0;
		} else {
			this.totalCount = totalCount;
			if (this.totalCount % pageSize == 0) {
				setPageCount((int) this.totalCount / pageSize);
			} else {
				setPageCount((int) (this.totalCount / pageSize + 1));
			}
		}
	}

	/**
	 * 当前页
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * 设置当前页
	 * 
	 */
	public void setPageNum(final int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * 总页数
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 设置总页数
	 */
	public void setPageCount(final int pageCount) {
		if (pageCount <= 0) {
			this.pageCount = 0;
		} else {
			this.pageCount = pageCount;
		}
	}

	/**
	 * 开始位置
	 */
	public int getBeginIndex() {
		if (pageNum < 1) {
			return 0;
		} else if (pageNum > pageCount) {
			return (pageCount - 1) * pageSize;
		} else {
			return (pageNum - 1) * pageSize;
		}
	}

	/**
	 * 结束位置
	 */
	public int getEndIndex() {
		return getBeginIndex() + pageSize;
	}

	/**
	 * 获得结果集
	 */
	public List<?> getData() {
		return data;
	}

	/**
	 * 设置结果集
	 */
	public void setData(final List<?> data) {
		this.data = data;
	}
}
