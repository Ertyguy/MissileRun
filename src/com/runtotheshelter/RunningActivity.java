package com.runtotheshelter;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunningActivity extends Activity implements LocationListener, LocationSource{
	GoogleMap mMap;
	GoogleMap missileMap;
	RouteApplication appState;
	
	Location currentLocation;
	
	private LocationManager locationManager;
	private OnLocationChangedListener mListener;
	private Location location;  
	
	
	private Timer missileTimer;
	private Marker missileMarker;
	private Marker shelterMarker;
	private boolean gpsIsEnabled;
	private boolean networkIsEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running);
		
		appState = ((RouteApplication)this.getApplication());
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager != null)
        {
        	gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("locationManager", "Status: GPS "+gpsIsEnabled+",  Provider "+networkIsEnabled);
            if(gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 0, this);
                Log.e("locationManager", "gps enabled");
            }
            else if(networkIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            }
            else
            {
                Log.e("locationManager", "Something is disabled mainActivity");
            }
        } else
        {
        	 Log.e("locationManager", "Manager is null, this is bad");
        }
		

        this.setUpMapsIfNeeded();
        
        
        for(FeatureInfo feature : getPackageManager().getSystemAvailableFeatures()){
    	   if("android.hardware.touchscreen.multitouch".equals(feature.name)){
    		   missileMap.getUiSettings().setZoomControlsEnabled(false);
    		   mMap.getUiSettings().setZoomControlsEnabled(false);
    	   }
        }
        
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            
           
            //Wrap map around location and marker
        	LatLngBounds bounds = new LatLngBounds.Builder()
        			.include(appState.routeInformation.plan.startLocation)
                    .include(appState.routeInformation.plan.destination).build();
        	
        	mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
        			this.getResources().getDisplayMetrics().widthPixels, 
                    this.getResources().getDisplayMetrics().heightPixels, 275));

            mMap.setMyLocationEnabled(true); 
            
            
        	MarkerOptions markerOptions = new MarkerOptions().draggable(true);
            markerOptions.position(appState.routeInformation.plan.destination);
            shelterMarker = mMap.addMarker(markerOptions);
	        // Setting custom icon for the marker either missile or explosion
	        shelterMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shelter));
	        
            PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.argb(98, 123, 104, 238));

        	for(int i = 0 ; i < appState.routeInformation.directionPoint.size() ; i++) {          
        		rectLine.add(appState.routeInformation.directionPoint.get(i));
        	}
        	mMap.addPolyline(rectLine);
        	
        	/* circle = mMap.addCircle(new CircleOptions()
             .center(appState.routeInformation.plan.destination)
             .radius(20)
             .strokeColor(Color.argb(98, 123, 104, 238)));
        	*/
        }

        missileTimer = new Timer(); 
        missileTimer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				if(appState.detonationTime <= 0){
					//missleTimer.cancel();
					//return;
					if(appState.detonationTime < 5)
						PostDetination();
				}else{
				Countdown();
				appState.detonationTime -= 1;
				}

			}
			
		}, 0, 1000);
	}
	
	private void Countdown()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		
		this.runOnUiThread(Timer_Tick);
	}
	
	private void PostDetination(){
		missileTimer.cancel();

		appState.timeRemaining = appState.detonationTime;
		
		Intent intent = new Intent(RunningActivity.this, ScoreActivity.class);
		RunningActivity.this.startActivity(intent);	
	}
	
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
		
			//Set timer text   	       
			TextView text = (TextView) findViewById(R.id.countdowntimer);
			text.setText(appState.detonationTime+" seconds");
			
			//Set Missile Icon to display and move
			//missleMap.clear();

	        //Setting position for the marker
			double lng = appState.routeInformation.plan.destination.longitude-appState.missleLaunchDistance+((appState.missleLaunchDistance/appState.setDetonationTime) *(appState.setDetonationTime-appState.detonationTime));

	        LatLng pos = new LatLng(appState.routeInformation.plan.destination.latitude-1, lng);
	        Log.d("missile",lng+" || "+(double)(appState.setDetonationTime/(appState.setDetonationTime-appState.detonationTime)));
	       
	        if(missileMarker != null) {
	        	missileMarker.setPosition(pos);
	        } else {
	        	MarkerOptions markerOptions = new MarkerOptions();
	            markerOptions.position(pos);
	            missileMarker = missileMap.addMarker(markerOptions);
	        }
	        
	        
	        // Setting custom icon for the marker either missile or explosion
	        if(appState.detonationTime > 0){
	        	missileMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sidemissile));
	        }else{
	        	missileMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.explosion));
	        }
	        missileMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
		}
	};

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running, menu);
		return true;
	}*/
    private void setUpMapsIfNeeded() {
    	
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.runningmap)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        missileMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.missilemap)).getMap();
        missileMap.setMyLocationEnabled(true); 
        missileMap.getUiSettings().setAllGesturesEnabled(false);
        
        if (mMap == null) {
        	mMap = ((MapFragment) getFragmentManager().findFragmentById( R.id.runningmap)).getMap();
        }
        if (mMap != null) {
            	mMap.setMyLocationEnabled(true);
                // Set default zoom
                //mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
            }
            mMap.setLocationSource(this);
        
    }

	@Override
	public void activate(OnLocationChangedListener listener) {
			mListener = listener;

			if(gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
            }
            else if(networkIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            }
            else
            {
                Log.d("locationManager", "Something is disabled mainActivity");
            }
	}

	@Override
	public void deactivate() {
		locationManager.removeUpdates(this);
		mListener = null; 
		
	}

	@Override
	public void onLocationChanged(Location location) {
		
		Log.e("location","made it here");
		if( mListener != null )
	    {
	        mListener.onLocationChanged( location );
	    }
        //Move the camera to the user's location once it's available
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        
        
        LatLng s = new LatLng(location.getLatitude(),location.getLongitude());
        LatLng d = appState.routeInformation.plan.destination;
        float[] results = new float[1];
        Location.distanceBetween(s.latitude, s.longitude, d.latitude, d.longitude, results);
        
        Toast.makeText(this, "results[0] "+results[0], Toast.LENGTH_SHORT).show();
        //location.distanceTo(new Location(this.))
        if(results[0] <= 33){ //a few meters to target is the goal
        	Toast.makeText(this, "You made it in time", Toast.LENGTH_LONG).show();
        	PostDetination();
        }
	   
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "status changed"+status, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    if(gpsIsEnabled)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
        }
        else if(networkIsEnabled)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
        }
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	}

}
