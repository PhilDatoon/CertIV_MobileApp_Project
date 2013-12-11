package com.phildatoon.compass;

/**
 * Source code reference:
 * Stewart Godwin
 * 
 * Retrieved 25 Nov 2013
 */
import com.phildatoon.at5.R;

import android.app.ActionBar;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends Activity implements SensorEventListener {

	// define the display assembly compass picture
	private ImageView image;

	// record the compass picture angle turned
	private float currentDegree = 0f;

	// device sensor manager
	private SensorManager mSensorManager;

	TextView tvHeading;
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_compass);
		
		// adds back button on the action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		image = (ImageView) findViewById(R.id.imageViewCompass);

		// TextView that will tell the user what degree is he heading
		tvHeading = (TextView) findViewById(R.id.tvHeading);

		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);
		
		tvHeading.setText((360 - degree) + "° " +
				getDirection(360 - degree));
		
		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(
				currentDegree,
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF,
				0.5f);
		
		// how long the animation will take place
		ra.setDuration(210);
		
		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		image.startAnimation(ra);
		currentDegree = -degree;
	}

	public String getDirection(float degree) {
		String direction = "";
		
		if (degree == 0f || degree == 360f) {
			direction = "N";
		} else if (degree > 0f && degree < 90f) {
			direction = "NE";
		} else if (degree == 90f) {
			direction = "E";
		} else if (degree > 90f && degree < 180f) {
			direction = "SE";
		} else if (degree == 180f) {
			direction = "S";
		} else if (degree > 180f && degree < 270f) {
			direction = "SW";
		} else if (degree == 270f) {
			direction = "W";
		} else {
			direction = "NW";
		}
		
		return direction;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// finishes the activity once user pressed back button on action bar
		this.finish();
		return true;
	}
}
