package com.runtotheshelter;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class RouteInformation {

	public RoutePlan plan;
	public ArrayList<LatLng> directionPoint;
	public int duration;
	public int distance;
	public String startAddress;
	public String destinationAddress;
	
	
	public RouteInformation(){}
	
	public RouteInformation(RoutePlan plan, ArrayList<LatLng> directionPoint, int duration, int distance, String startAddress, String destinationAddress){
		this.plan = plan;
		this.directionPoint = directionPoint;
		this.duration = duration;
		this.distance = distance;
		this.startAddress = startAddress;
		this.destinationAddress = destinationAddress;
	}
	
}
