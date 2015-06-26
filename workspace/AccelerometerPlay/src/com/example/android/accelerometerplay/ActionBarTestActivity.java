package com.example.android.accelerometerplay;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBarTestActivity extends AppCompatActivity implements SensorEventListener, OnCheckedChangeListener, OnClickListener {
	TextView sensor1Lbl;
	TextView sensor2Lbl;
	TextView sensor3Lbl;
	//TextView sensorSnap;
	TextView xField;
	TextView yField;
	TextView zField;
	TextView xField2;
	TextView yField2;
	TextView zField2;
	CheckBox isTracking;
	EditText logFld;
	
	Context context;
    private SensorManager mSensorManager;
    private Display mDisplay;
    private Sensor mAccelerometer;
    private Sensor mProximtySnsr;
    private Sensor mLightSnsr;

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    
	private String myVersion = "not available"; // initialize String
	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;
	private float minX=Float.MAX_VALUE;
	private float maxX=Float.MIN_VALUE;
	private float minY=Float.MAX_VALUE;
	private float maxY=Float.MIN_VALUE;
	private float minZ=Float.MAX_VALUE;
	private float maxZ=Float.MIN_VALUE;
	
	private String currentLog = "";
	private Timer t;
    private int timeCounter;
	private int surfaceRotation;
    
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
			mSensorManager.registerListener(this, mProximtySnsr, SensorManager.SENSOR_DELAY_UI);
			mSensorManager.registerListener(this, mLightSnsr, SensorManager.SENSOR_DELAY_UI);   
		}
	} 

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mProximtySnsr);
		mSensorManager.unregisterListener(this, mLightSnsr);
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
	        	surfaceRotation = 0;
	            mSensorX = event.values[0];
	            mSensorY = event.values[1];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_90:
	        	surfaceRotation = 1;
	        	mSensorX = event.values[1]; //mSensorX = -event.values[1];
	            mSensorY = event.values[0];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_180:
	        	surfaceRotation = 2; 
	        	mSensorX = event.values[0]; //mSensorX = -event.values[0];
	        	mSensorY = event.values[1]; // mSensorY = -event.values[1];
	            mSensorZ = event.values[2];
	            break;
	        case Surface.ROTATION_270:
	        	surfaceRotation = 3;
	            mSensorX = event.values[1];
	            mSensorY = event.values[0]; // mSensorY = -event.values[0];
	            mSensorZ = event.values[2];
	            break;
	        }
	        
	        /*if (mSensorX < minX) minX=mSensorX;
	        if (mSensorX > maxX) maxX=mSensorX;
	        if (mSensorY < minY) minY=mSensorY;
	        if (mSensorY > maxY) maxY=mSensorY;
	        if (mSensorZ < minZ) minZ=mSensorZ;
	        if (mSensorZ > maxZ) maxZ=mSensorZ;

	        xField.setText(round(mSensorX) + "_" + round(minX) + "/" + round(maxX) + "_");
	        yField.setText(round(mSensorY) + "_" + round(minY) + "/" + round(maxY) + "_");
	        zField.setText(round(mSensorZ) + "_" + round(minZ) + "/" + round(maxZ) + "_");*/
	        xField.setText(round(mSensorX) + "");
	        yField.setText(round(mSensorY) + "");
	        zField.setText(round(mSensorZ) + "");
	        
	        sensor3Lbl.setText((surfaceRotation * 90) + "\u00b0");
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
			mSensorManager.registerListener(this, mProximtySnsr, SensorManager.SENSOR_DELAY_UI);
			mSensorManager.registerListener(this, mLightSnsr, SensorManager.SENSOR_DELAY_UI);   
		} else {
			mSensorManager.unregisterListener(this, mAccelerometer);
			mSensorManager.unregisterListener(this, mProximtySnsr);
			mSensorManager.unregisterListener(this, mLightSnsr);
		}
	}
	
	@Override
	public void onClick(View view) {
		System.out.println("clicked");
		Button but;
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
			but = (Button) view;
			if ("Snap".equalsIgnoreCase(but.getText().toString())) {
				but.setText("Clear");				
				xField2.setText(round(Math.toDegrees(Math.atan(mSensorZ/mSensorX))) + "\u00b0 " + round(mSensorX) + "_");
				yField2.setText((surfaceRotation*90) + getBtoAAngle(mSensorX, mSensorY) + "\u00b0 " + round(mSensorY) + "_");
				zField2.setText(round(Math.toDegrees(Math.atan(mSensorZ/mSensorY))) + "\u00b0 " + round(mSensorZ) + "_");				
			} else if ("Clear".equalsIgnoreCase(but.getText().toString())) {
				but.setText("Snap");
				xField2.setText("");
				yField2.setText("");
				zField2.setText("");				
			}
			
			break;
		case R.id.button3:
			// This initiates 10 snapshots, 1 every x seconds
			((Button) view).setEnabled(false);
			timeCounter=0;
			if (t != null) {
				t.cancel();
			}
			t = new Timer();
		    t.scheduleAtFixedRate(new TimerTask() {

		        @Override
		        public void run() {
		            // TODO Auto-generated method stub
		            runOnUiThread(new Runnable() {

						public void run() {
		                    timeCounter++;
		                    if (timeCounter<=10) {
		                    	String xAngle = round(Math.toDegrees(Math.atan(mSensorZ/mSensorX))) + "\u00b0 ";
		        				String yAngle = getBtoAAngle(mSensorX, mSensorY) + "\u00b0 ";
		        				String zAngle = round(Math.toDegrees(Math.atan(mSensorZ/mSensorY))) + "\u00b0 ";
		                    	doLog(timeCounter + ": (" + round(mSensorX) + " , " + round(mSensorY) + " , " + round(mSensorZ)
		                    		+ "\t (" + xAngle + " , " + yAngle + " , " + zAngle + ")");
		
		                    	try {
		                    	ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
		                    	toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); 
		                    	} catch (Exception e) {
		                    		doLog(e.toString());
		                    	}
		                    	if (timeCounter>=10) {
		                    		t.cancel();
			                    	t = null;
			                    	((Button)findViewById(R.id.button3)).setEnabled(true);	
		                    	}
		                    } else {
		                    	// this code will probably never be reached but just here as a safety to turn off the timer.
	                    		t.cancel();
		                    	t = null;
		                    	((Button)findViewById(R.id.button3)).setEnabled(true);
		                    }
		                }
		            });

		        }
		    }, 1000, 3000); // 1000 means start from 1 sec, and the second 3000 is do the loop each 3 sec.
			break;
		case R.id.button4:
			clearLog();
			break;
		}
		
	}

	private void initMain() {
		context = getApplicationContext(); // or activity.getApplicationContext()
		setContentView(R.layout.activity_action_bar_test);
		
		// this is really UI but because logging is so important, doing it first
		logFld = (EditText) findViewById(R.id.logFld);
		logFld.setMovementMethod(new ScrollingMovementMethod()); // this is required to enable scrolling
		
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        // Register listener for acccelrometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // register listeners for proximity and light sensors
        mProximtySnsr = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);		
		mLightSnsr = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		             
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
		// init controls
		sensor1Lbl = (TextView) findViewById(R.id.Sensor1);
		sensor2Lbl = (TextView) findViewById(R.id.Sensor2);
		sensor3Lbl = (TextView) findViewById(R.id.Sensor3);
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
        a = (Button)findViewById(R.id.button3);
        a.setOnClickListener(this);
        a = (Button)findViewById(R.id.button4);
        a.setOnClickListener(this);
        
        StringBuilder sb = new StringBuilder();
		for (Sensor sensor : mSensorManager.getSensorList(Sensor.TYPE_ALL)) {
			sb.append("************\r\n");
			sb.append(sensor).append("\r\n");
		}
		
		doLog(sb.toString());
	}
	
	private void initOthers() {
		// Get app version info
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();	

		try {
			myVersion = packageManager.getPackageInfo(packageName, 0).versionName
					+ " ("
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

	DecimalFormat twoDForm = new DecimalFormat("#.##");
	/**
	 * This method rounds to 2 decimal places
	 * @param val
	 * @return
	 */
	private float round(float val) {
        return Float.valueOf(twoDForm.format(val));
	}
	private float round(double val) {
        return Float.valueOf(twoDForm.format(val));
	}
    
    private void doLog(String log) {
    	currentLog = log + "\r\n" + currentLog;
    	logFld.setText(currentLog);
    }
    
    private void clearLog() {
    	currentLog = "";
    	logFld.setText(currentLog);
    }
    
	
	private float getBtoAAngle(float aValue, float bValue) {
		return round(Math.toDegrees(Math.atan(aValue / bValue)));
	}
}
