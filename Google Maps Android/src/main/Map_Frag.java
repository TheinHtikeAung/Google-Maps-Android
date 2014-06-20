package main;

import helper.SH;

import java.util.ArrayList;

import placedetails.PlaceDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import autocomplete.Autocomplete_Act;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.theinhtikeaung.googlemapsandroid.R;

import direction.Direction_Act;
import direction.api.DirectionInfo;
import direction.api.GMH;
import direction.api.Leg;
import direction.api.Route;
import direction.api.Step;
import direction.api.Direction_Api.TravelMode;

public class Map_Frag extends Fragment{
	
	private final String TAG = Map_Frag.class.getSimpleName();
	
	private GoogleMap map;
	private SupportMapFragment fm;
	private UiSettings mUiSettings;
	
	private LatLng mLatLng;
	private MarkerOptions markerOptions;
	private AutoCompleteTextView txtSearch;
	
//	private GeocoderTask geocoderTask;
	
	private final int Autocomplete_Act_Intent = 1;
	private final int Direction_Act_Intent = 2;
	
	private ProgressBar progressBar;
	private ImageView imgSearch;
	private ImageButton btnRoute;
	
	private SlidingUpPanelLayout sLayout;
	
	// Linear Layout From to
	private LinearLayout linearFromTo, linearSliderMain, linearSearch, linearMiddle, linearWarning;
	private RelativeLayout relativeFromTo, headerLayout;
	private ImageView imgMode, imgClear;
	private TextView txtFrom, txtTo;
	
	// For Sliding
	private TextView txtTime, txtDistance, txtAddress, txtWarning;
	private ListView listView;
	private StepAdapter stepAdapter;
	
	public Route route;
	public DirectionInfo di;
	private ArrayList<StepObj> stepObj;
	
	private PlaceDetail pd;
	private String keyword;
		
	private LinearLayout linearTime, linearLine, linearPlace;
	private ScrollView scrollView;
	
	private Context c;
	
	public String testing = "Sth";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.map_layout2, null);
		
		c = getActivity();
		
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		imgSearch = (ImageView) v.findViewById(R.id.imgSearch);
		
		// Search EditText Assignment
		txtSearch = (AutoCompleteTextView) v.findViewById(R.id.txtSearch);
		txtSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), Autocomplete_Act.class);
				i.putExtra("keyword", txtSearch.getText().toString());
				startActivityForResult(i, Autocomplete_Act_Intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		
		btnRoute = (ImageButton) v.findViewById(R.id.btnRoute);
		btnRoute.setOnClickListener(new  OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), Direction_Act.class);
				String di_str = new Gson().toJson(di);
				i.putExtra("di", di_str);
				startActivityForResult(i, Direction_Act_Intent);
			}
		});
		
		// LinearSearch
		linearSearch = (LinearLayout) v.findViewById(R.id.linearSearch);
		linearWarning = (LinearLayout) v.findViewById(R.id.linearWarning);
		
		// Map Assign
		fm = (SupportMapFragment)  getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        map = fm.getMap();
        map.setMyLocationEnabled(true);
        map.setPadding(0, 85, 0, 0);
        mUiSettings = map.getUiSettings();
        
        linearFromTo = (LinearLayout) v.findViewById(R.id.linearFromTo);
        txtFrom = (TextView) v.findViewById(R.id.txtFrom);
        txtTo = (TextView) v.findViewById(R.id.txtTo);
        imgMode = (ImageView) v.findViewById(R.id.imgMode);
        imgClear = (ImageView) v.findViewById(R.id.imgClear);
        imgClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Clear for Serach
				keyword = null;
			    pd = null;
			    txtSearch.setText("");
				
				// Clear for Routing
				di = null;
				route = null;
				
				showSearchBar(true);
				map.clear();
			}
		});
        relativeFromTo = (RelativeLayout) v.findViewById(R.id.relativeFromTo);
        relativeFromTo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				finish();
				Intent i = new Intent(c, Direction_Act.class);
				String di_str = new Gson().toJson(di);
				i.putExtra("di", di_str);
				startActivityForResult(i, Direction_Act_Intent);
			}
		});
        
        sLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        
        // Set up sliding panel
        txtTime = (TextView) v.findViewById(R.id.txtTime);
        txtDistance = (TextView) v.findViewById(R.id.txtDistance);
        txtAddress = (TextView) v.findViewById(R.id.txtAddress);
        txtWarning = (TextView) v.findViewById(R.id.txtWarning);
        headerLayout = (RelativeLayout) v.findViewById(R.id.headerLayout);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setVisibility(View.INVISIBLE);
        linearMiddle = (LinearLayout) v.findViewById(R.id.linearMiddle);
        
        // For scroll view
        scrollView = (ScrollView) v.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);
		linearTime = (LinearLayout) v.findViewById(R.id.linearTime);
		linearLine = (LinearLayout) v.findViewById(R.id.linearLine);
		linearPlace = (LinearLayout) v.findViewById(R.id.linearPlace);
        
        sLayout.setDragView(headerLayout);
        sLayout.setPanelHeight(0);
        
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
            	
            	if(route != null){
            		showRoute();
            	}else if(keyword != null){
            		showSearchResult();
            	}else{
            		addCustomLocation();
            		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(SH.CUSTOM_LAT), Double.parseDouble(SH.CUSTOM_LNG)), 15));
            	}
            	
            	/*//Calculate the markers to get their position
      	      LatLngBounds.Builder b = new LatLngBounds.Builder();
      	      b.include(new LatLng(Double.parseDouble(route.bound.northeast_lat), Double.parseDouble(route.bound.northeast_lng)));
      	      b.include(new LatLng(Double.parseDouble(route.bound.southwest_lat), Double.parseDouble(route.bound.southwest_lng)));
      	      
      	      LatLngBounds bounds = b.build();
      	      //Change the padding as per needed
      	      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
      	      map.animateCamera(cu);*/
            }
        });
        
        Log.i(TAG, testing);
        
        return v;
	}
	
	public void addCustomLocation(){

        // Add company location
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(Double.parseDouble(SH.CUSTOM_LAT), Double.parseDouble(SH.CUSTOM_LNG)));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_24));        
        
        options.title("Custom Location");
        options.snippet("City Hall");
        map.addMarker(options);
	}
	
	/*private void performSearch(){
		
		InputMethodManager imm = (InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
        txtSearch.setCursorVisible(false);
		
		String addressSearchString = txtSearch.getText().toString();

        if (Utils.isNullOrEmptyString(addressSearchString))
        {
            return;
        }

        geocoderTask = new GeocoderTask();
        geocoderTask.execute(addressSearchString);
	}*/
	
	// ---------------------------------------      Geo Coder AsyncTask       --------------------------------------------------
	/*private class GeocoderTask extends AsyncTask<String, Void, List<Address>>
    {

        @Override
        protected List<Address> doInBackground(String... locationName) {
        	showProgressBar();
        	
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = null;

            try
            {
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                geocoderTask.cancel(true);
                Utils.checkConn(getActivity());
                Toast.makeText(getActivity(), "Connection timeout. Try again.", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses == null || addresses.size() == 0)
            {
                Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            map.clear();

            for(int i = 0; i<addresses.size(); i++)
            {
                Address address = addresses.get(i);

//                mLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());
                
                addMarkerInMap(address.getLatitude(), address.getLongitude(), addressText, null);

                markerOptions = new MarkerOptions();
                markerOptions.position(mLatLng);
                markerOptions.title(addressText);
                markerOptions.snippet(txtSearch.getText().toString());

                map.addMarker(markerOptions);

                //Show the first location
                if(i == 0)
                {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));

                    String addressSearchString = mAddressEditText.getText().toString();
                    String descriptionString = mDescriptionEditText.getText().toString();

                    if (mLatLng != null)
                    {
                        Log.d(TAG, "Address: " + addressText + " - latitude: " + mLatLng.latitude + " - longitude: " + mLatLng.longitude );
                    }
                }
            }
            
            hideProgressBar();
        }
    }*/
	
	private void  addMarkerInMap(double lat, double lng, String title, String snippet){
		
		mLatLng = new LatLng(lat, lng);
		
		markerOptions = new MarkerOptions();
        markerOptions.position(mLatLng);
        markerOptions.title(title);
//        markerOptions.snippet(txtSearch.getText().toString());

        map.addMarker(markerOptions);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i(TAG, "Got Result from Map_Frag");
		
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == Autocomplete_Act_Intent){
				
				Log.i(TAG, "Got Result from Map_Frag");
				
				keyword = data.getStringExtra("keyword");
				String pd_str = data.getStringExtra("pd");
				pd = new Gson().fromJson(pd_str, PlaceDetail.class);
				
				showSearchResult();
				
				/*String keyword = data.getStringExtra("keyword");
				String reference = data.getStringExtra("reference");
				txtSearch.setText(keyword);
				
				if(reference.equals("")){
					// Serach by decode
					performSearch();
				}else{
					showProgressBar();
					// Search by Place Detail API
					new PlaceDetails_Api(new PlaceDetails_Api_OnComplete() {
						@Override
						public void onComplete(final PlaceDetail pd) {
							Log.i(TAG, pd.toString());
							
							map.clear();
							addMarkerInMap(pd.lat, pd.lng, txtSearch.getText().toString(), null);
							map.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
							
							hideProgressBar();
						}
					}).execute(reference);
				}*/
				
			}else if(requestCode == Direction_Act_Intent){
				
				String route_str = data.getStringExtra("route");
				this.route = new Gson().fromJson(route_str, Route.class);
				
				String di_str = data.getStringExtra("di");
				this.di = new Gson().fromJson(di_str, DirectionInfo.class);
				
				Log.i(TAG, "route_str => " + route_str);
				Log.i(TAG, "di_str => " + di_str);
				
				Log.i(TAG, "Got it");
				showRoute();
						    	
			      /*PolylineOptions polyoptions = new PolylineOptions();
			      polyoptions.color(R.color.Blue);
			      polyoptions.width(7);
			      polyoptions.addAll(GMH.decodePoly(route.overview_polyline));
			      map.addPolyline(polyoptions);
			      
			      Leg l = route.legs.get(0);
			      
			      LatLng start_loc = new LatLng(Double.parseDouble(l.start_lat), Double.parseDouble(l.start_lng));
	
			      // Start marker
			      MarkerOptions options = new MarkerOptions();
			      options.position(start_loc);
			      options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
			      map.addMarker(options);
	
			      LatLng end_loc = new LatLng(Double.parseDouble(l.end_lat), Double.parseDouble(l.end_lng));
			      
			      // End marker
			      options = new MarkerOptions();
			      options.position(end_loc);
			      options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));  
			      map.addMarker(options);
			      
			      //Calculate the markers to get their position
			      LatLngBounds.Builder b = new LatLngBounds.Builder();
			      b.include(new LatLng(Double.parseDouble(route.bound.northeast_lat), Double.parseDouble(route.bound.northeast_lng)));
			      b.include(new LatLng(Double.parseDouble(route.bound.southwest_lat), Double.parseDouble(route.bound.southwest_lng)));
			      
			      LatLngBounds bounds = b.build();
			      //Change the padding as per needed
			      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
			      map.animateCamera(cu);
			      
			      // Set From To layout
			      txtFrom.setText(di.keyword_start);
			      txtTo.setText(di.keyword_end);
			      
			      if(di.mode == TravelMode.TRANSIT){
			    	  imgMode.setImageResource(R.drawable.bus_64);
			      }else if(di.mode == TravelMode.DRIVING){
			    	  imgMode.setImageResource(R.drawable.car_4_48);
			      }else if(di.mode == TravelMode.WALKING){
			    	  imgMode.setImageResource(R.drawable.walking);
			      }
			      
			      // Set up sliding panel
			      txtTime.setText(route.legs.get(0).duration_text);
			      txtDistance.setText(String.format("(%s)", route.legs.get(0).distance_text));
			      txtAddress.setText(String.format("Via %s", route.summary));
			      
			      setAdapter();
			      showSearchBar(false);*/
			}
		}
	}
	
	private void showSearchResult(){
		// Update UI
		txtSearch.setText(keyword);
		map.clear();
		addMarkerInMap(pd.lat, pd.lng, txtSearch.getText().toString(), null);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
	}
	
	private void showRoute(){
		/*Intent data = getIntent();
		
		String route_str = data.getStringExtra("route");
		this.route = new Gson().fromJson(route_str, Route.class);
		
		String di_str = data.getStringExtra("di");
		this.di = new Gson().fromJson(di_str, DirectionInfo.class);
		
		Log.i(TAG, "Got it");*/
		
		map.clear();
		showSearchBar(false);
		addCustomLocation();
		
		if (!this.route.warning.equalsIgnoreCase("")) {
			txtWarning.setText(this.route.warning);
		} else {
			linearWarning.setVisibility(View.GONE);
		}
						    	
      PolylineOptions polyoptions = new PolylineOptions();
      polyoptions.color(R.color.Blue);
      polyoptions.width(7);
      polyoptions.addAll(GMH.decodePoly(route.overview_polyline));
      map.addPolyline(polyoptions);
      
      Leg l = route.legs.get(0);
      
      LatLng start_loc = new LatLng(Double.parseDouble(l.start_lat), Double.parseDouble(l.start_lng));

      // Start marker
      MarkerOptions options = new MarkerOptions();
      options.position(start_loc);
      options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
      map.addMarker(options);

      LatLng end_loc = new LatLng(Double.parseDouble(l.end_lat), Double.parseDouble(l.end_lng));
      
      // End marker
      options = new MarkerOptions();
      options.position(end_loc);
      options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));  
      map.addMarker(options);
      
      // Set From To layout
      txtFrom.setText(di.keyword_start);
      txtTo.setText(di.keyword_end);
      
      if(di.mode == TravelMode.TRANSIT){
    	  imgMode.setImageResource(R.drawable.bus_64);
      }else if(di.mode == TravelMode.DRIVING){
    	  imgMode.setImageResource(R.drawable.car_4_48);
      }else if(di.mode == TravelMode.WALKING){
    	  imgMode.setImageResource(R.drawable.walking);
      }
      
      //Calculate the markers to get their position
      LatLngBounds.Builder b = new LatLngBounds.Builder();
      b.include(new LatLng(Double.parseDouble(route.bound.northeast_lat), Double.parseDouble(route.bound.northeast_lng)));
      b.include(new LatLng(Double.parseDouble(route.bound.southwest_lat), Double.parseDouble(route.bound.southwest_lng)));
      
      LatLngBounds bounds = b.build();
      //Change the padding as per needed
      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
      map.animateCamera(cu);
      
      setAdapter();
	}
	
	private void setAdapter(){
		if(di.mode == TravelMode.TRANSIT){
			
			listView.setVisibility(View.INVISIBLE);
			scrollView.setVisibility(View.VISIBLE);
			
			Leg l = route.legs.get(0);
			
			if(l.dt_text == null){
				txtTime.setText("");
				txtDistance.setText(l.duration_text);
			}else{
				txtTime.setText(String.format("%s - %s", l.dt_text, l.at_text));
				txtDistance.setText(String.format("(%s)", l.duration_text));
			}
			
			txtAddress.setVisibility(View.INVISIBLE);
			
			int size = this.route.legs.get(0).steps.size();
			linearMiddle.removeAllViews();
			
			for (int i = 0; i<size; i++){
				
				Step s = this.route.legs.get(0).steps.get(i);
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
				layoutParams.gravity = Gravity.CENTER;
				
				// ImageView
				ImageView imageView = new ImageView(getActivity());
				imageView.setLayoutParams(layoutParams);
				
				// Arrow
				layoutParams = new LinearLayout.LayoutParams(20, 20);
				layoutParams.gravity = Gravity.CENTER;
				ImageView arrow = new ImageView(c);
				arrow.setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
				arrow.setLayoutParams(layoutParams);
				
				// Text
				layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.gravity = Gravity.CENTER;
				TextView textView = new TextView(c);
				textView.setLayoutParams(layoutParams);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
				
				if(s.travel_mode.equalsIgnoreCase("WALKING")){
					imageView.setImageResource(R.drawable.walking);
				}else if(s.travel_mode.equalsIgnoreCase("TRANSIT")){
					if(s.transitDetail.vehicle_type.equalsIgnoreCase("SUBWAY")){
						imageView.setImageResource(R.drawable.mrt_logo);
					}else if(s.transitDetail.vehicle_type.equalsIgnoreCase("BUS")){
						imageView.setImageResource(R.drawable.bus_64);
					}
					textView.setText(s.transitDetail.line_shortname);
				}
				
				linearMiddle.addView(imageView);
				linearMiddle.addView(textView);
				
				if(i + 1 != size){
					linearMiddle.addView(arrow);
				}
				
				setViewForTransitRoute();
			}
			
		}else{
			listView.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.INVISIBLE);
			
			// Set up sliding panel
			txtTime.setText(route.legs.get(0).duration_text);
			txtDistance.setText(String.format("(%s)", route.legs.get(0).distance_text));
			txtAddress.setText(String.format("Via %s", route.summary));
			
			linearMiddle.removeAllViews();
			
			Leg l = this.route.legs.get(0);
			
			this.stepObj = new ArrayList<StepObj>();
			
			StepObj firstObj = new StepObj(di.keyword_start, l.start_address);
			StepObj lastObj = new StepObj(di.keyword_end, l.end_address);
			
			stepObj.add(firstObj);
			for (Step s : l.steps) {
				stepObj.add(new StepObj(s.html_instructions, s.distance_text));
			}
			stepObj.add(lastObj);
			
			stepAdapter = new StepAdapter(c, R.layout.step_listitem, stepObj);
			
			listView.setVisibility(View.VISIBLE);
			listView.setAdapter(stepAdapter);
		}
		
	}
	
	@SuppressLint("ResourceAsColor")
	private void setViewForTransitRoute(){
		
		Leg l = this.route.legs.get(0);
		
		linearLine.removeAllViews();
		linearTime.removeAllViews();
		linearPlace.removeAllViews();
		
		LinearLayout.LayoutParams layoutParams;
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView txt1 = new TextView(c);
		txt1.setLayoutParams(layoutParams);
		txt1.setText(l.dt_text);
		linearTime.addView(txt1);
		
		ImageView imgCircle1 = new ImageView(c);
		layoutParams = new LinearLayout.LayoutParams(30, 30);
		imgCircle1.setLayoutParams(layoutParams);
		imgCircle1.setImageResource(R.drawable.circle_outline_48);
		linearLine.addView(imgCircle1);
		
		TextView txtLoc1 = new TextView(c);
		txtLoc1.setOnClickListener(placeClickListener);
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		txtLoc1.setLayoutParams(layoutParams);
		txtLoc1.setText(di.keyword_start);
		txtLoc1.setTag(new LatLng(l.getStartLat(), l.getStartLng()));
		linearPlace.addView(txtLoc1);
		
		boolean Prev_Tansit = false;
		
		for(int i = 0; i<l.steps.size(); i++){
			
			Step s = l.steps.get(i);
			
			ImageView imgMode = new ImageView(c);
			layoutParams = new LinearLayout.LayoutParams(40, 40);
			layoutParams.setMargins(0, 55, 0, 55);
			layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
			imgMode.setLayoutParams(layoutParams);
			
			TextView txtMiddle1 = new TextView(c);
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 70);
			txtMiddle1.setGravity(Gravity.BOTTOM | Gravity.LEFT);
			txtMiddle1.setLayoutParams(layoutParams);
			
			TextView txtMiddle2 = new TextView(c);
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 70);
			txtMiddle2.setGravity(Gravity.TOP | Gravity.LEFT);
			txtMiddle2.setLayoutParams(layoutParams);
			txtMiddle2.setTextColor(R.color.gray);
			
			View v = new View(c);
			layoutParams = new LinearLayout.LayoutParams(8, 160);
			layoutParams.gravity = Gravity.CENTER;
			v.setBackgroundResource(R.color.Silver);
			v.setLayoutParams(layoutParams);
			
			boolean transit = true;
			if(s.travel_mode.equalsIgnoreCase("WALKING")){
				transit = false;
				
				imgMode.setImageResource(R.drawable.walking);
				txtMiddle1.setText("Walk");
				txtMiddle2.setText(String.format("%s (%s)", s.distance_text, s.duration_text));
				
			}else{
				
				if(s.transitDetail.vehicle_type.equalsIgnoreCase("BUS")){
					imgMode.setImageResource(R.drawable.bus_64);
					txtMiddle1.setText(s.html_instructions.replace("Bus", s.transitDetail.line_shortname));
					
				}else if(s.transitDetail.vehicle_type.equalsIgnoreCase("SUBWAY")){
					imgMode.setImageResource(R.drawable.mrt_logo);
					txtMiddle1.setText(s.html_instructions.replace("Subway", s.transitDetail.line_shortname));
					v.setBackgroundColor(Color.parseColor(s.transitDetail.line_color));
					
				}
				txtMiddle2.setText(String.format("%s stops (%s)", s.transitDetail.num_stops, s.duration_text));
			}
			
			
			if(transit && i != 0) setHeader(i, s, Prev_Tansit, new LatLng(s.getStartLat(), s.getStartLng()));
			
			/*if(Prev_Tansit){
				layoutParams = (android.widget.LinearLayout.LayoutParams) imgMode.getLayoutParams();
				layoutParams.setMargins(0,45, 0, 45);
				imgMode.setLayoutParams(layoutParams);
			}*/
			
			linearTime.addView(imgMode);
			linearLine.addView(v);
			linearPlace.addView(txtMiddle1);
			linearPlace.addView(txtMiddle2);
				
			if(transit && i+1 != l.steps.size()) setFooter(i, s, new LatLng(s.getEndLat(), s.getEndLng()));
			
			if(transit) Prev_Tansit = true;
			else Prev_Tansit = false;
		}
		
		// Set Footer for main
		setLayoutView(l.at_text, di.keyword_end, false, new LatLng(l.getEndLat(), l.getEndLng()));
		
		scrollView.setVisibility(View.VISIBLE);
	}
	
	private void setHeader(int i, Step s, boolean boo, LatLng latlng){
		setLayoutView(s.transitDetail.dt_text, s.transitDetail.ds_loc_name, boo, latlng);
	}
	
	private void setFooter(int i, Step s, LatLng latlng){
		setLayoutView(s.transitDetail.at_text, s.transitDetail.as_loc_name, false, latlng);
	}
	
	public void setLayoutView(String time, String place, boolean boo, LatLng latlng){
		LinearLayout.LayoutParams layoutParams;
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView txtFooterTime = new TextView(c);
		txtFooterTime.setLayoutParams(layoutParams);
		txtFooterTime.setText(time);
		
		ImageView imgFooterCircle = new ImageView(c);
		layoutParams = new LinearLayout.LayoutParams(30, 30);
		imgFooterCircle.setLayoutParams(layoutParams);
		imgFooterCircle.setImageResource(R.drawable.circle_outline_48);
		
		TextView txFooterPlace = new TextView(c);
		txFooterPlace.setOnClickListener(placeClickListener);
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		txFooterPlace.setLayoutParams(layoutParams);
		txFooterPlace.setPadding(5, 5, 5, 5);
		txFooterPlace.setBackgroundResource(R.drawable.textview_bg);
		txFooterPlace.setText(place);
		txFooterPlace.setTag(latlng);
		
		if(!boo){
			
			linearTime.addView(txtFooterTime);
			
			linearLine.addView(imgFooterCircle);
			linearPlace.addView(txFooterPlace);
			
			/*View v = new View(this);
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 15);
			layoutParams.gravity = Gravity.CENTER;
			v.setBackgroundResource(R.color.black);
			v.setLayoutParams(layoutParams);
			
			linearTime.addView(v);*/
		}
	}
	
	private OnClickListener placeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			sLayout.collapsePane();
			
			LatLng latlng = (LatLng) v.getTag();
			Log.i(TAG, latlng.toString());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 80));
		}
	};
	
	private void showSearchBar(boolean boo){
		if(boo){
			sLayout.setPanelHeight(0);
			linearFromTo.setVisibility(View.INVISIBLE);
			linearSearch.setVisibility(View.VISIBLE);
		}else{
			sLayout.setPanelHeight(headerLayout.getHeight());
		    linearFromTo.setVisibility(View.VISIBLE);
		    linearSearch.setVisibility(View.INVISIBLE);
		}
	}


    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
        	
        	mUiSettings.setZoomControlsEnabled(false);
        	
            mUiSettings.setMyLocationButtonEnabled(true);
            mUiSettings.setCompassEnabled(true);
        }
    }
		
	@Override
	public void onDestroyView(){
		try{
			SupportMapFragment fm1 = (SupportMapFragment)  getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
	        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        ft.remove(fm1);
	        ft.commit();
	      }catch(Exception e){
	      }
	    super.onDestroyView();  
	}
	
	/*private void showProgressBar(){
		((Activity)c).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				imgSearch.setVisibility(View.INVISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}
		});
		
	}
	
	private void hideProgressBar(){
		((Activity)c).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				imgSearch.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
		
	}*/
}


