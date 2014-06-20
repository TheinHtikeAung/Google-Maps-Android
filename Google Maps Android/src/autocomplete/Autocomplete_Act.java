package autocomplete;

import helper.Utils;

import java.util.ArrayList;
import java.util.List;

import placedetails.PlaceDetail;
import placedetails.PlaceDetails_Api;
import placedetails.PlaceDetails_Api.PlaceDetails_Api_OnComplete;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import autocomplete.Autocomplete_Api.Autocomplete_Api_onComplete;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.theinhtikeaung.googlemapsandroid.R;

import direction.GeocoderTask;
import direction.GeocoderTask.GeocoderOnComplete;

public class Autocomplete_Act extends ActionBarActivity implements
								GooglePlayServicesClient.ConnectionCallbacks,
								GooglePlayServicesClient.OnConnectionFailedListener{
	
	private final String TAG = Autocomplete_Act.class.getSimpleName();
	private ActionBar actionBar;
	
	private ListView listView;
	private ArrayList<Place> places;
	private Autocomplete_Adapter adapter;	
	
	private EditText txtSearch;
	private ImageButton btnClear;
	private Button btnCancel;
	
	private Handler handler;
	private Intent intent;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private LocationClient mLocationClient;
	
	// Global variable to hold the current location
    Location mCurrentLocation;
    private ConnectionResult connectionResult;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.autocomplete_layout);
		
		mLocationClient = new LocationClient(this, this, this);
		
		actionBar = getSupportActionBar();
		actionBar.hide();
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(listItemClickListener);
		
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtSearch.setCursorVisible(true);
			}
		});
		
		txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	performGeocode(txtSearch.getText().toString());
		            return true;
		        }
		        return false;
		    }
		});
		txtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(handler != null) handler.removeMessages(0);
			    
			    if(txtSearch.getText().toString().equals("")){
			    	addRecommendData();
			    	
			    }else{
			    	handler = new Handler(); 
				    handler.postDelayed(new Runnable() { 
				         public void run() { 
				        	 Log.i(TAG, "Autocomplete started");
				        	 performSearch();
				         } 
				    }, 2000);
			    }
			}
		});
		
		btnClear = (ImageButton) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtSearch.setText("");
			}
		});
		
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		
		// Get keyword
		intent = getIntent();
		String keyword = intent.getStringExtra("keyword");
		txtSearch.setText(keyword);
	}
	
	private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			if(txtSearch.getText().toString().equals("")){
				serveCurrentLocation();
				
			}else{
				final Place p = places.get(position);
				
				// Search by Place Detail API
				new PlaceDetails_Api(new PlaceDetails_Api_OnComplete() {
					@Override
					public void onComplete(final PlaceDetail pd) {
						goToMap(p.terms.get(0), pd);
					}
				}).execute(p.reference);
			}
		}
	};
	
	private void addRecommendData(){
		ArrayList<RD> rds = new ArrayList<RD>();
		RD rd2 = new RD("Current Location", "", "location");
		rds.add(rd2);
		RecommendData_Adapter adapter2 = new RecommendData_Adapter(this, R.layout.sample_listitem, rds);
		listView.setAdapter(adapter2);
	}
	
	private void setAdapter(ArrayList<Place> places){
		this.places = places;
		adapter = new Autocomplete_Adapter(this, R.layout.list_item, this.places);
		listView.setAdapter(adapter);
	}
	
	private void performSearch(){
		String keyword = txtSearch.getText().toString();
		
		if(!txtSearch.getText().toString().equals("")){
			setAdapter(new ArrayList<Place>());
			new Autocomplete_Api(new Autocomplete_Api_onComplete() {
				@Override
				public void onComplete(ArrayList<Place> places) {
					setAdapter(places);
				}
			}).execute(keyword);
		}else{
			addRecommendData();
		}
	}
	
	private void goToMap(String keyword, PlaceDetail pd){
    	intent.putExtra("keyword", keyword);
    	
    	String pd_str = new Gson().toJson(pd);
    	intent.putExtra("pd", pd_str);
    	
    	setResult(RESULT_OK, intent);
    	back();
	}
	
	private void performGeocode(final String keyword){
		
		new GeocoderTask(this, new GeocoderOnComplete() {
			
			@Override
			public void geoSuccess(List<Address> addresses) {
				Address a = addresses.get(0);
				
				final PlaceDetail pd = new PlaceDetail(a.getLatitude(), a.getLongitude());
				
				Log.i(TAG, "Geocode address size - " + addresses.size());
				Log.i(TAG, a.toString());
				
				String address;
				if(Utils.tryParseInt(a.getAddressLine(0))){
					address = keyword;
				}else{
					address = a.getAddressLine(0);
				}
				
				String addressText = String.format("%s, %s", address, a.getCountryName());
				goToMap(addressText, pd);
			}
			
			@Override
			public void geoFailed() {
				Toast.makeText(Autocomplete_Act.this, "Input address is not found.", Toast.LENGTH_LONG).show();
			}
		}, 1).execute(keyword);
	}
	
	public void back()
	{
		this.finish();
		try
		{
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		catch (Exception e) { Toast.makeText(this, "An error occured " + e.toString(), Toast.LENGTH_LONG).show(); }
	}
	
	@Override
	public void onBackPressed()
	{
		back();
	}
	
	public void serveCurrentLocation(){
		mCurrentLocation = mLocationClient.getLastLocation();
		
		if(mCurrentLocation != null){
			Log.i(TAG, "Lat - " + mCurrentLocation.getLatitude() + " Lng - " + mCurrentLocation.getLongitude());
			goToMap("Current Location", new PlaceDetail(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
		}else{
			Log.i(TAG, "mCurrentLocation is null");
			servicesConnected();
		}
		
	}
	
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	/*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    	Log.i(TAG, "onActivityResult");
                   
                    break;
                }
        }
     }
 
    
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			
			checkLocationService();
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Location Updates");
				
			}
			return false;
		}
	}
	
	private void checkLocationService(){
		LocationManager lm = null;
		boolean gps_enabled, network_enabled = false;
		if (lm == null)
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/*try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}*/
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!network_enabled) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Please go and enable Location Service setting");
			dialog.setPositiveButton("Open location service",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub	
							Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(myIntent);
							// get gps
						}
					});
			dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {

				}
			});
			dialog.show();

		}
	}
	
	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.i(TAG, "Connected");

    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
    	Log.i(TAG, "Disconnected");
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
    	Log.i(TAG, "onConnectionFailed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
//            showErrorDialog(connectionResult.getErrorCode());
            Log.i(TAG, connectionResult.getErrorCode() + "");
            
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
}

