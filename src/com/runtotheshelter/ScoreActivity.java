package com.runtotheshelter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ScoreActivity extends Activity {
	private RouteApplication appState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		appState = ((RouteApplication)this.getApplication());
		
		int timeRemaining = appState.timeRemaining;
		
		TextView text = (TextView) findViewById(R.id.timeremaining);
		TextView explanation = (TextView) findViewById(R.id.explanation);
		
		if(timeRemaining > 0) //The user made it in time
		{
			text.setText("Time Remaining: "+appState.timeRemaining);
			explanation.setText("Now that you've just saved your life, remember to smile :)");
		}else //The user did NOT make it in time
		{
			text.setText("Boom, Maybe next time you'll run just a little faster");
			
			explanation.setText("Perhaps you're trying to go a little far, take it easy and enjoy yourself.");
			
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score, menu);
		return true;
	}

	public void restartSelected(View view){
		RouteApplication appState = ((RouteApplication)this.getApplication());
		appState.reset();

		Intent intent = new Intent(ScoreActivity.this, DestinationActivity.class);
		
		ScoreActivity.this.startActivity(intent);	
	}
	
}
