package com.example.android.accelerometerplay;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBarTestActivity extends AppCompatActivity implements SensorEventListener, OnCheckedChangeListener, OnClickListener {
	TextView sensor1Lbl;
	TextView sensor2Lbl;
	//TextView sensorSnap;
	TextView xField;
	TextView yField;
	TextView zField;
	TextView xField2;
	TextView yField2;
	TextView zField2;
	CheckBox isTracking;
	
	Context context;
    private SensorManager mSensorManager;
    private Display mDisplay;
    private Sensor mAccelerometer;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    
	String myVersion = "not available"; // initialize String
	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;
	private float minX=Float.MAX_VALUE;
	private float maxX=Float.MIN_VALUE;
	private float minY=Float.MAX_VALUE;
	private float maxY=Float.MIN_VALUE;
	private float minZ=Float.MAX_VALUE;
	private float maxZ=Float.MIN_VALUE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMain();
		initUI();
		initOthers();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isTracking.isChecked()) {
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		}
	} 

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this, mAccelerometer);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar will automatically handle clicks on the Home/Up
		// button, so long as you specify a parent activity in
		// AndroidManifest.xml.

		switch (item.getItemId()) {
			case R.id.action_settings:
				alertView("You really expected settings already here...?");
				return true;
			case R.id.action_about:
				Toast.makeText(context, "Version: " + myVersion, Toast.LENGTH_LONG).show();
				return true;
	        default:
			return super.onOptionsItemSelected(item); // this is to ensure any
														// fragment that
														// requires menu
														// handling gets a
														// chance to handle its
														// menu item handling if
														// its not covered in
														// this menu handler
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
	        switch (mDisplay.getRotation()) {
	        case Surface.ROTATION_0:
	            mSensorX = event.values[0];
	            mSensorY = event.values[1];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_90:
	            mSensorX = -event.values[1];
	            mSensorY = event.values[0];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_180:
	            mSensorX = -event.values[0];
	            mSensorY = -event.values[1];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_270:
	            mSensorX = event.values[1];
	            mSensorY = -event.values[0];
	            mSensorZ = event.values[2];
	            break;
	        }
	        if (mSensorX < minX) minX=mSensorX;
	        if (mSensorX > maxX) maxX=mSensorX;
	        if (mSensorY < minY) minY=mSensorY;
	        if (mSensorY > maxY) maxY=mSensorY;
	        if (mSensorZ < minZ) minZ=mSensorZ;
	        if (mSensorZ > maxZ) maxZ=mSensorZ;

	        xField.setText(mSensorX + "_" + minX + "/" + maxX + "_");
	        yField.setText(mSensorY + "_" + minY + "/" + maxY + "_");
	        zField.setText(mSensorZ + "_" + minZ + "/" + maxZ + "_");
			break;
		case Sensor.TYPE_PROXIMITY:
			sensor1Lbl.setText("Prxmty:" + event.values[0]);

			break;
		case Sensor.TYPE_LIGHT:
			sensor2Lbl.setText("Light:" + event.values[0]);
			break;
		default:
			System.out.println("Unknown Sensor " + event.sensor.getName() + "/" + event.sensor.getType() + "/" + event.values[0]);
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		} else {
			mSensorManager.unregisterListener(this, mAccelerometer);
		}
	}
	
	@Override
	public void onClick(View view) {
		System.out.println("clicked");
		switch (view.getId()) {
		case R.id.button1:
			boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
			if (isAdmin) {
				mDevicePolicyManager.lockNow();
			} else {
				Toast.makeText(getApplicationContext(),
						"Not Registered as admin", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button2:
			// this stores a snapshot of current x,y,z values into the 3 fields. 
			Button but = (Button) view;
			if ("Snap".equalsIgnoreCase(but.getText().toString())) {
				but.setText("Clear");
				xField2.setText(mSensorX + "*");
				yField2.setText(mSensorY + "*");
				zField2.setText(mSensorZ + "*");				
			} else if ("Clear".equalsIgnoreCase(but.getText().toString())) {
				but.setText("Snap");
				xField2.setText("");
				yField2.setText("");
				zField2.setText("");				
			}
			
			break;
		}
		
	}
	
	private void initMain() {
		context = getApplicationContext(); // or activity.getApplicationContext()
		
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Register listener for acccelrometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // register listeners for proximity and light sensors
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);        
        
        // Get an instance of the WindowManager
	    WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay(); // this will be used later for getting orientation
        
        // initialise for lock screen
		mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, MyAdminReceiver.class);
        // Initialise admin access authorisation
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Explaining myself? pah!!");
		//startActivityForResult(intent, ADMIN_INTENT);
		startActivity(intent);
		/* below code is for if u ever want to stop the admin powers in code:
		 * of course it shdould be here
		mDevicePolicyManager.removeActiveAdmin(mComponentName); 
		 */
	}
	
	private void initUI() {
		setContentView(R.layout.activity_action_bar_test);
		
		// init controls
		sensor1Lbl = (TextView) findViewById(R.id.Sensor1);
		sensor2Lbl = (TextView) findViewById(R.id.Sensor2);
		xField = (TextView) findViewById(R.id.xValue);
        yField = (TextView) findViewById(R.id.yValue);
        zField = (TextView) findViewById(R.id.zValue);
        xField2 = (TextView) findViewById(R.id.xValue2);
        yField2 = (TextView) findViewById(R.id.yValue2);
        zField2 = (TextView) findViewById(R.id.zValue2);
        
        isTracking = (CheckBox)findViewById(R.id.isTracking);
        isTracking.setOnCheckedChangeListener(this);
        
        Button a = (Button)findViewById(R.id.button1);
        a.setOnClickListener(this);
        a = (Button)findViewById(R.id.button2);
        a.setOnClickListener(this);
	}
	
	private void initOthers() {
		// Get app version info
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();	

		try {
			myVersion = packageManager.getPackageInfo(packageName, 0).versionName
					+ "("
					+ packageManager.getPackageInfo(packageName, 0).versionCode
					+ ")";
		} catch (PackageManager.NameNotFoundException e) {
		    e.printStackTrace();
		}		
	}
	
	/**
	 * Helper method for displaying a simple alert box
	 * @param message
	 */
	private void alertView(String message ) {
		 AlertDialog.Builder builder = new AlertDialog.Builder(ActionBarTestActivity.this);
		 builder.setMessage(message);
		 builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialoginterface, int which) {
		        	dialoginterface.dismiss();
		        }               
		        });
		        
		/* builder.setTitle( "Hello" )
		    .setIcon(R.drawable.ic_launcher)
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialoginterface, int i) {
		          dialoginterface.cancel();   
		          }});*/

		 builder.create().show();		 
	}
	

	
}
