package com.lzstudio.healthy.bean;

import java.io.Serializable;

/**
 * 约跑记录信息
 * 
 * @author Administrator
 * 
 */
public class Run implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 记录编号
	 */
	private String id;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 约定时间
	 */
	private String yuedingtime;
	/**
	 * 发布时间
	 */
	private String fabutime;
	/**
	 * 发布者
	 */
	private String owner;
	/**
	 * 约定地点
	 */
	private String address;
	/**
	 * 描述
	 */
	private String describe;
	/**
	 * 是否阅读了
	 */
	private boolean hasRead;

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYuedingtime() {
		return yuedingtime;
	}

	public void setYuedingtime(String yuedingtime) {
		this.yuedingtime = yuedingtime;
	}

	public String getFabutime() {
		return fabutime;
	}

	public void setFabutime(String fabutime) {
		this.fabutime = fabutime;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
