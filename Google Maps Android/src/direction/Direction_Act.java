package direction;

import helper.SH;
import helper.Utils;

import java.util.ArrayList;

import placedetails.PlaceDetail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import autocomplete.Autocomplete_Act;
import com.google.gson.Gson;
import com.theinhtikeaung.googlemapsandroid.R;

import direction.api.DirectionInfo;
import direction.api.Direction_Api;
import direction.api.Leg;
import direction.api.Route;
import direction.api.Step;
import direction.api.Direction_Api.Direction_Api_onComplete;
import direction.api.Direction_Api.TravelMode;

public class Direction_Act extends ActionBarActivity implements Direction_Api_onComplete{

	private final String TAG = Autocomplete_Act.class.getSimpleName();
	private ActionBar actionBar;
	private ImageButton btnCar, btnTransit, btnWalking;
//	, btnBicycle;
	private TravelMode MODE;
	
	private TextView txt1, txt2;
	private PlaceDetail from, to;
	
	private ListView listView;
	private ArrayList<Route> routes;
	
	private ArrayList<RouteItem> routeItems;
	private RouteAdapter adapter;
	
	private ArrayList<RouteItem2> routeItems2;
	private RouteAdapter2 adapter2;
	
	private final int Autocomplete_Act_Intent = 1;
	private final int Map_Act_Intent = 2;
	private TXT txt;
	
	private RelativeLayout no_route_view, linear3;
	private ImageView imgSwift;
	
	DirectionInfo di;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.direction_layout);
		
		actionBar = getSupportActionBar();
		actionBar.hide();
		
		progressDialog = Utils.getProgressDialog(this);
		no_route_view = (RelativeLayout) findViewById(R.id.no_route_view);
		
		txt1 = (TextView) findViewById(R.id.txt1);
		txt2 = (TextView) findViewById(R.id.txt2);
		
		txt1.setOnClickListener(txt1ClickListener);
		txt2.setOnClickListener(txt2ClickListener);

		btnCar = (ImageButton) findViewById(R.id.btnCar);
		btnTransit = (ImageButton) findViewById(R.id.btnTransit);
		btnWalking = (ImageButton) findViewById(R.id.btnWalking);
//		btnBicycle = (ImageButton) findViewById(R.id.btnBicycle);
	
		btnCar.setOnClickListener(carClickListener);
		btnTransit.setOnClickListener(transitClickListener);
		btnWalking.setOnClickListener(walkingClickListener);
//		btnBicycle.setOnClickListener(bicycleClickListener);
		
		linear3 = (RelativeLayout) findViewById(R.id.linear3);
		imgSwift = (ImageView) findViewById(R.id.imgSwift);
		linear3.setOnClickListener(swiftOnClickListener);
		imgSwift.setOnClickListener(swiftOnClickListener);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(listClickListener);
				
		// Set to get old state
		/*Intent i = getIntent();
		if(i.hasExtra("di")){
			String di_str = i.getStringExtra("di");
			DirectionInfo di = new Gson().fromJson(di_str, DirectionInfo.class);
			txt1.setText(di.keyword_start);
			txt2.setText(di.keyword_end);
			this.from = di.from;
			this.to = di.to;
			MODE = di.mode;
			if(MODE == TravelMode.DRIVING){
				setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_bottom_line);
			}else if(MODE == TravelMode.TRANSIT){
				setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_bottom_line);
			}else if(MODE == TravelMode.WALKING){
				setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_bottom_line);
			}
			
			tryRounting();
			
		}else{
			//Default for temp
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_bottom_line);
			MODE = TravelMode.DRIVING;
		}*/
		
		// Look for routing history
		Intent i = getIntent();
		String di_str = i.getStringExtra("di");
		this.di = new Gson().fromJson(di_str, DirectionInfo.class);
		if(di != null){
			txt1.setText(di.keyword_start);
			txt2.setText(di.keyword_end);
			MODE = di.mode;
			from = di.from;
			to = di.to;
			
			if(MODE == TravelMode.DRIVING){
				setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_bottom_line);
			}else if(MODE == TravelMode.TRANSIT){
				setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_bottom_line);
			}else if(MODE == TravelMode.WALKING){
				setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_bottom_line);
			}
			
			tryRounting();
			
		}else{
			

/*			to = new PlaceDetail(SH.CUSTOM_LAT, SH.CUSTOM_LNG);
			txt2.setText("Custom Location Name");*/
			
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_bottom_line);
			MODE = TravelMode.DRIVING;
		}
		
	}
	
	private OnClickListener swiftOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			String txt1_str = txt1.getText().toString();
			String txt2_str = txt2.getText().toString();
			
			txt1.setText(txt2_str);
			txt2.setText(txt1_str);
			
			PlaceDetail temp = from;
			from = to;
			to = temp;
			
			tryRounting();
		}
	};
	
	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Route route = routes.get(position);
			
			DirectionInfo di = new DirectionInfo(txt1.getText().toString(), txt2.getText().toString(), MODE, from, to);
			
			String route_str = new Gson().toJson(route);
			String di_str = new Gson().toJson(di);
			
			Log.i(TAG, "route_str : " + route_str);
			Log.i(TAG, "di_str : " + di_str);
			
			Intent i = getIntent();
			i.putExtra("route", route_str);
			i.putExtra("di", di_str);
			setResult(Activity.RESULT_OK, i);
			finish();
		}
	};
	
	private OnClickListener txt1ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			txt = TXT.TXT1;
			goToAutocompleteAct(txt1.getText().toString());
		}
	};
	
	private OnClickListener txt2ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			txt = TXT.TXT2;
			goToAutocompleteAct(txt2.getText().toString());
		}
	};
	
	private void goToAutocompleteAct(String keyword){
		Intent i = new Intent(this, Autocomplete_Act.class);
		i.putExtra("keyword", keyword);
		startActivityForResult(i, Autocomplete_Act_Intent);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == Autocomplete_Act_Intent){
				
				Log.i(TAG, "Got Result from Map_Frag");
				
				final String keyword = data.getStringExtra("keyword");
				String pd_str = data.getStringExtra("pd");
				
				PlaceDetail pd = new Gson().fromJson(pd_str, PlaceDetail.class);
				setDiaplayAndRoute(keyword, pd);
				/* TODO delete me
				if(reference.equals("")){
					Log.i(TAG, "under reference existed");					
					// Serach by decode
					performSearch(keyword);
				}else{

					// Search by Place Detail API
					new PlaceDetails_Api(new PlaceDetails_Api_OnComplete() {
						@Override
						public void onComplete(final PlaceDetail pd) {
							
						}
					}).execute(reference);
				}*/
			}
		}
	}
	
	private void setDiaplayAndRoute(final String keyword, final PlaceDetail pd){
		Log.i(TAG, pd.toString());
		
		if(txt == TXT.TXT1){
			txt1.setText(keyword);
			from = pd;
		}else{
			txt2.setText(keyword);
			to = pd;
		}
		tryRounting();
	}
	
	private void tryRounting(){
		
		String start = txt1.getText().toString();
		String end = txt2.getText().toString();
		
		if(!start.equals("") && !end.equals("")){
			progressDialog.show();
	        new Direction_Api(this, this).execute(from.getLanLng(), to.getLanLng(), MODE);
		}
	}
	
	@Override
	public void directionSuccess(ArrayList<Route> routes) {
		this.routes = routes;
		Log.i(TAG, routes.size() + "");
		
		setAdapter();		
	
		progressDialog.dismiss();
	}

	@Override
	public void directionFail() {
		Log.i(TAG, "Routing failed");
		no_route_view.setVisibility(View.VISIBLE);
		progressDialog.dismiss();
		listView.setVisibility(View.INVISIBLE);
	}
	
	/*private void performSearch(final String keyword){
		
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
				
				String addressText = String.format("%s, %s", a.getMaxAddressLineIndex() > 0 ? address : "", a.getCountryName());
				setDiaplayAndRoute(addressText, pd);
			}
			
			@Override
			public void geoFailed() {
				Toast.makeText(this, "Input address is not found.", Toast.LENGTH_LONG).show();
			}
		}, 1).execute(keyword);
	}*/
	
	private void setAdapter(){
		
		no_route_view.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		
		if(MODE != TravelMode.TRANSIT){
			
			this.routeItems = new ArrayList<RouteItem>();
			for (Route r : routes) {
				for (Leg leg : r.legs) {
					this.routeItems.add(new RouteItem(leg.duration_text, leg.distance_text, r.summary));
				}
			}
			
			adapter = new RouteAdapter(this, R.layout.route_listitem, this.routeItems);
			listView.setAdapter(adapter);
			
		}else{
			
			this.routeItems2 = new ArrayList<RouteItem2>();
			
			for (Route r : routes) {
				for (Leg leg : r.legs) {
					
					String headerStr, footerStr = "";
					
					if(leg.dt_text == null){
						headerStr = String.format("%s (%s)", leg.duration_text, leg.distance_text);
						footerStr = String.format("Via %s", r.summary);
					}else{
						headerStr = String.format("%s - %s (%s)", leg.dt_text, leg.at_text, leg.duration_text);
					}
					
					ArrayList<RouteItemDetail> rids = new ArrayList<RouteItemDetail>();
					
					Log.i(TAG, "Leg step size -" + leg.steps.size());
					
					for(int i = 0; i<leg.steps.size(); i++){
						Step step = leg.steps.get(i);
						
						RouteItemDetail rid = null;
						if(step.travel_mode.equalsIgnoreCase("WALKING")){
								rid = new RouteItemDetail(TravelMode.WALKING, "");
							
						}else if(step.travel_mode.equalsIgnoreCase("TRANSIT")){
							
							String text = step.transitDetail.line_shortname;
							
							if(step.transitDetail.vehicle_type.equalsIgnoreCase("SUBWAY")){
								rid = new RouteItemDetail(TravelMode.TRANSIT, text);
							}else if(step.transitDetail.vehicle_type.equalsIgnoreCase("BUS")){
								rid = new RouteItemDetail(TravelMode.BUS, text);
							}
							
							if(footerStr.equalsIgnoreCase("")) footerStr = String.format("Via: %s (%s)", leg.steps.get(i).transitDetail.ds_loc_name, leg.steps.get(i).transitDetail.dt_text);
						}
						rids.add(rid);
					}
					
					this.routeItems2.add(new RouteItem2(headerStr, rids, footerStr));
				}
			}
			Log.i(TAG, "RouteItem2 Size" + this.routeItems2.size());
			adapter2 = new RouteAdapter2(this, R.layout.transit_route_listitem, this.routeItems2);
			listView.setAdapter(adapter2);
		}
	}
	
	private OnClickListener carClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_bottom_line);
			setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_selector);
//			setViewBackgroundWithoutResettingPadding(btnBicycle, R.drawable.button_selector);
			setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_selector);
			
			MODE = TravelMode.DRIVING;
			tryRounting();
		}
	};
	
	private OnClickListener transitClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_bottom_line);
			setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_selector);
//			setViewBackgroundWithoutResettingPadding(btnBicycle, R.drawable.button_selector);
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_selector);
			
			MODE = TravelMode.TRANSIT;
			tryRounting();
		}
	};
	
	private OnClickListener walkingClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_bottom_line);
			setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_selector);
//			setViewBackgroundWithoutResettingPadding(btnBicycle, R.drawable.button_selector);
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_selector);
			
			MODE = TravelMode.WALKING;
			tryRounting();
		}
	};
	
	private OnClickListener bicycleClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			setViewBackgroundWithoutResettingPadding(btnBicycle, R.drawable.button_bottom_line);
			setViewBackgroundWithoutResettingPadding(btnTransit, R.drawable.button_selector);
			setViewBackgroundWithoutResettingPadding(btnWalking, R.drawable.button_selector);
			setViewBackgroundWithoutResettingPadding(btnCar, R.drawable.button_selector);
			
			MODE = TravelMode.BIKING;
			tryRounting();
		}
	};
	
	public static void setViewBackgroundWithoutResettingPadding(final View v, final int backgroundResId) {
	    final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
	    final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
	    v.setBackgroundResource(backgroundResId);
	    v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}
	
	public enum TXT {
		   TXT1,
		   TXT2
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	  	Log.i(TAG, "onSaveInstanceState");
	  	savedInstanceState.putString("message", "This is my message to be reloaded");
	}
}
