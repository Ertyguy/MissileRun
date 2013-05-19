package com.runtotheshelter;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
//import android.app.FragmentTransaction;
import android.content.pm.FeatureInfo;


public class DestinationActivity extends Activity implements LocationListener, LocationSource{

	GoogleMap mMap;
    //MapFragment mMapFragment;
    
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    private Location location;    
    
    private LatLng startLocation;
    private LatLng destination;
    private RouteInformation routeInfo;

    private Marker shelterMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_destination);
        //mMapFragment = MapFragment.newInstance();
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*
        if(locationManager != null)
        {
        	boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("locationManager", "Status: GPS "+gpsIsEnabled+",  Provider "+networkIsEnabled);
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
        } else
        {
        	 Log.d("locationManager", "Manager is null, this is bad");
        }
*/
        //setup map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        this.setUpMapIfNeeded();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            
            startLocation = new LatLng(location.getLatitude(),location.getLongitude());
            //Move map to current location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            //Display blue marker
            mMap.setMyLocationEnabled(true); 
            for(FeatureInfo feature : getPackageManager().getSystemAvailableFeatures())
         	   if("android.hardware.touchscreen.multitouch".equals(feature.name)){
         		   mMap.getUiSettings().setZoomControlsEnabled(false);
         	   }
        }
        
        mMap.setOnMapClickListener(new OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
            	Log.d("Destination", "Lat "+point.latitude+", Lon"+point.longitude);
            	destination = new LatLng(point.latitude, point.longitude);
            	mMap.clear(); //Remove other marker
            	
            	//Have to create new marker because maps had to clear route poly lines
	        	MarkerOptions markerOptions = new MarkerOptions().draggable(true);
	            markerOptions.position(destination);
	            shelterMarker = mMap.addMarker(markerOptions);
    	        
    	        
    	        // Setting custom icon for the marker either missle or explosion
    	        shelterMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shelter));
    	        
            	
            	RoutePlan r = new RoutePlan(startLocation,destination,GMapV2Direction.MODE_WALKING);

            	try{
            		
            		routeInfo = new GMapV2Direction().execute(r).get();
            		
            		PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.argb(98, 123, 104, 238));

                	for(int i = 0 ; i < routeInfo.directionPoint.size() ; i++) {          
                		rectLine.add(routeInfo.directionPoint.get(i));
                	}

                	mMap.addPolyline(rectLine);
                	
            	}catch(Exception e){
            		e.printStackTrace();
            	}

            	
            }

        });

    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
        	mMap = ((MapFragment) getFragmentManager().findFragmentById( R.id.map)).getMap();
            if (mMap != null) {
            	mMap.setMyLocationEnabled(true);
                // Set default zoom
                //mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
            }
            mMap.setLocationSource(this);
        }
    }

	@Override
	public void activate(OnLocationChangedListener listener) {
			mListener = listener;
		
	}

	@Override
	public void deactivate() {
		mListener = null; 
		
	}

	@Override
	public void onLocationChanged(Location location) {
		if( mListener != null )
	    {
	        mListener.onLocationChanged( location );
	        //Move the camera to the user's location once it's available
	        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
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

	
	public void destinationSelected(View view){
		RouteApplication appState = ((RouteApplication)this.getApplication());
		appState.routeInformation = routeInfo; //Save Information in global singleton
		
		Intent intent = new Intent(DestinationActivity.this, OptionsActivity.class);
		
		DestinationActivity.this.startActivity(intent);	
	}

}
