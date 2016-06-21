package com.lzstudio.healthy.bean;

public class User {
	/**
	 * 账号
	 */
	private String phoneNumber;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 用户名称
	 */
	private String username;
	/**
	 * 用户年龄
	 */
	private int age;
	/**
	 * 用户性别
	 */
	private String sex;
	/**
	 * 身高
	 */
	private float height;
	/**
	 * 体重
	 */
	private float weight;
	/**
	 * 用户头像
	 */
	private String headImg;
	/**
	 * 积分
	 */
	private int points;
	/**
	 * 步数
	 */
	private long paceCount;

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public long getPaceCount() {
		return paceCount;
	}

	public void setPaceCount(long paceCount) {
		this.paceCount = paceCount;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

}
