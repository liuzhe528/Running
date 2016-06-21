package com.lzstudio.healthy.bean;

public class NewsDetailModle extends BaseModle {

	private static final long serialVersionUID = 1L;
	/**
	 * 新闻标题
	 */
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 新闻内容
	 */
	private String content;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 关键字
	 */
	private String keyword;
	/**
	 * 来源媒体
	 */
	private String mediaName;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		String[] str_time = time.split("\\.");
		this.time = str_time[0];
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

}
