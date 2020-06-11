package com.example.cslab.eped;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.telephony.SignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.CellLocation;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;


	public static final String TAG = HomeActivity.class.getSimpleName();
	
	public static final String EMAIL = "tbarrasso@sevenplusandroid.org";
	
	private CellLocation mCellLocation;
	private SignalStrength mSignalStrength;
	private boolean mDone = false;
	private TextView mText = null;
	private String mTextStr;
	private Button mSubmit, mCancel;
	private TelephonyManager mManager;

public class ave extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LOCATION", location.toString());
                //Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
             super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
    	mText = (TextView) findViewById(R.id.text);
    	mSubmit = (Button) findViewById(R.id.submit);
    	mCancel = (Button) findViewById(R.id.cancel);
    	
    	// Prevent button press.
    	mSubmit.setEnabled(false);
    	
    	// Handle click events.
    	mSubmit.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View mView)
    		{
    			sendResults();
    			finish();
    		}
    	}

        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
        }

    }
     final PhoneStateListener mListener = new PhoneStateListener()
    {
    	@Override
    	public void onCellLocationChanged(CellLocation mLocation)
    	{
    		if (mDone) return;
    		
    		Log.d(TAG, "Cell location obtained.");
    	
    		mCellLocation = mLocation;
    		
    		update();
    	}
    	
    	@Override
    	public void onSignalStrengthsChanged(SignalStrength sStrength)
    	{
    		if (mDone) return;
    		
    		Log.d(TAG, "Signal strength obtained.");
    		
    		mSignalStrength = sStrength;
    		
    		update();
    	}
    };
     private final void complete()
    {
    	mDone = true;
    	
    	try
    	{
    		mText.setText(mTextStr);
    	
			// Stop listening.
			mManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
			Toast.makeText(getApplicationContext(), R.string.done, Toast.LENGTH_SHORT).show();
			
			mSubmit.setEnabled(true);
		}
		catch (Exception e)
		{
			Log.e(TAG, "ERROR!!!", e);
		}
    }
    
    private final void update()
    {
    	if (mSignalStrength == null || mCellLocation == null) return;
    	
    	final ReflectionTask mTask = new ReflectionTask();
    	mTask.execute();
    }
    
    /**
     * @return The Radio of the {@link Build} if available.
     */
    public static final String getRadio()
    {
    	if (Build.VERSION.SDK_INT >= 8 && Build.VERSION.SDK_INT < 14)
    		return Build.RADIO;
    	else if (Build.VERSION.SDK_INT >= 14)
    		return Build.getRadioVersion();
    	else
    		return null;
    }
    
    private static final String[] mServices =
	{
		"WiMax", "wimax", "wimax", "WIMAX", "WiMAX"
	};
    
    /**
     * @return A String containing a dump of any/ all WiMax
     * classes/ services loaded via {@link Context}.
     */
    public final String getWimaxDump()
    {
    	String mStr = "";
    	
    	for (final String mService : mServices)
    	{
    		final Object mServiceObj = getApplicationContext()
    			.getSystemService(mService);
    		if (mServiceObj != null)
    		{
    			mStr += "getSystemService(" + mService + ")\n\n";
    			mStr += ReflectionUtils.dumpClass(mServiceObj.getClass(), mServiceObj);
			}
    	}
    	
    	return mStr;
    }
        public final void sendResults()
    {
    	final Intent mIntent = new Intent(Intent.ACTION_SEND);
		mIntent.setType("plain/text");
		mIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { EMAIL });
		mIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.results));
		mIntent.putExtra(Intent.EXTRA_TEXT, mTextStr);
		HomeActivity.this.startActivity(Intent.createChooser(mIntent, "Send results."));
    }
}
