package com.runtotheshelter;

import com.google.android.gms.maps.model.LatLng;

import android.app.Application;
import android.text.format.Time;

public class RouteApplication extends Application{
	public RouteInformation routeInformation = new RouteInformation();
	
	public int detonationTime; //Countdown time
	public double setDetonationTime; //Total time
	public double missleLaunchDistance = 80; //Max distance
	
	public int timeRemaining;
	
	public void reset(){
		this.routeInformation = new RouteInformation();
		this.detonationTime = 0;
		this.setDetonationTime = 0;
		this.missleLaunchDistance = 0;
		this.timeRemaining = 0;
	}
}
