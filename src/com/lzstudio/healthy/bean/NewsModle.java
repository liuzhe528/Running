package com.lzstudio.healthy.bean;

public class NewsModle extends BaseModle {

	private static final long serialVersionUID = 1L;
	/**
	 * 发表时间
	 */
	private String time;
	/**
	 * 新闻id
	 */
	private String id;
	/**
	 * 摘要
	 */
	private String intro;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 是否已读
	 */
	private boolean hasRead;

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		String[] str_time = time.split("\\.");
		this.time = str_time[0];
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
