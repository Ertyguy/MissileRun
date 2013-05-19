package com.runtotheshelter;

import com.google.android.gms.maps.model.LatLng;

public class RoutePlan {
	public LatLng startLocation;
	public LatLng destination;
	public String mode;
	
	public RoutePlan(){}
	
	public RoutePlan(LatLng startLocation, LatLng destination, String mode){
		this.startLocation = startLocation;
		this.destination = destination;
		this.mode = mode;	
	}
	
}
