package com.cybozu.garoon3.schedule;

public class Facility {
	private int key = 0;
	private String name = "";
	private String code = "";
	private long version = 0;
	private long order = 0;
	private String description = "";
	private int belongFacilityGroup = 0;

	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public long getOrder() {
		return order;
	}
	public void setOrder(long order) {
		this.order = order;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getBelongFacilityGroup() {
		return belongFacilityGroup;
	}
	public void setBelongFacilityGroup(int belongFacilityGroup) {
		this.belongFacilityGroup = belongFacilityGroup;
	}
}
