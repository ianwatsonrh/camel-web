package org.globex.usecase.service;

public enum GeoEnum {

	NA("NORTH_AMERICA"),SA("SOUTH_AMERICA"),WA("WEST_AMERICA"),EA("EAST_AMERICA"),EU("EUROPE");
	
	private String location;
	
	private GeoEnum(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}
	
}
