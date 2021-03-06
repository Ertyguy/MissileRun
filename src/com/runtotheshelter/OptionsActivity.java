package com.runtotheshelter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class OptionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		RouteInformation routeInformation = ((RouteApplication)this.getApplication()).routeInformation;
		
		TextView text;
		Log.d("debug",routeInformation.startAddress);
		text = (TextView) findViewById(R.id.startaddress);
		text.setText(routeInformation.startAddress.split(",")[0]);
		
		text = (TextView) findViewById(R.id.destinationaddress);
		text.setText(routeInformation.destinationAddress.split(",")[0]);
		
		text = (TextView) findViewById(R.id.distance);
		text.setText(routeInformation.distance+" m");
		
		Log.d("test",routeInformation.toString());
		NumberPicker np = (NumberPicker) findViewById(R.id.timepicker_input);
		np.setMinValue(1);
		np.setMaxValue(59);
		np.setWrapSelectorWheel(true);
		np.setValue(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	public void launchSelected(View view){
		RouteApplication appState = ((RouteApplication)this.getApplication());
		
		NumberPicker timepicker_input = (NumberPicker)findViewById(R.id.timepicker_input);
	    int min = timepicker_input.getValue();
		appState.detonationTime = min*60; //Save Information in global singleton
		appState.setDetonationTime = appState.detonationTime;
		Intent intent = new Intent(OptionsActivity.this, RunningActivity.class);
		
		OptionsActivity.this.startActivity(intent);	
	}

}
