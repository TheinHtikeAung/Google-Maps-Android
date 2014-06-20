package direction.api;

import helper.Utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import autocomplete.Autocomplete_Api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Direction_Api {

	private final String TAG = Autocomplete_Api.class.getSimpleName();
	
	public Context c;
	public Direction_Api_onComplete interClass;
	
	public Direction_Api(Context c, Direction_Api_onComplete interClass){
		this.c = c;
		this.interClass = interClass;
	}
	
	public void execute(String from, String to, TravelMode mode){
		
		String start = from;
        String dest = to;
        
        String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";

        final StringBuffer mBuf = new StringBuffer(sJsonURL);
        mBuf.append("origin=");
        mBuf.append(start);
        mBuf.append("&destination=");
        mBuf.append(dest);
        mBuf.append("&alternatives=true&sensor=true&mode=");
        mBuf.append(mode.getValue());
        mBuf.append("&departure_time=" + System.currentTimeMillis()/1000);

        Log.i("URL ->", mBuf.toString());
        
        AsyncHttpClient client = new AsyncHttpClient();
		client.get(mBuf.toString(), new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        Log.i(TAG, "Response from direction server");
		        
		        try {
					final JSONObject json = new JSONObject(response);
					final String status = json.getString("status");
					
					if(status.equalsIgnoreCase("OK")){
						
						ArrayList<Route> routes = new ArrayList<Route>();
						final JSONArray routes_json = json.getJSONArray("routes");
						
						for(int i=0; i<routes_json.length(); i++){
							
							final JSONObject route_obj = routes_json.getJSONObject(i);
							
							/*// Bound
							final JSONObject bounds_json = route_obj.getJSONObject("bounds");
							final JSONObject northeast_json = bounds_json.getJSONObject("northeast");
							final JSONObject southwest_json = bounds_json.getJSONObject("southwest");
							final Bound bound = new Bound(northeast_json.getString("lat"), northeast_json.getString("lng"), southwest_json.getString("lat"), southwest_json.getString("lng"));
							
							// Copyright
							String copyrights = route_obj.getString("copyrights");
							
							// Leg
							ArrayList<Leg> legs = new ArrayList<Leg>();
							final JSONArray legs_json = route_obj.getJSONArray("legs");
							for(int j=0; j<legs_json.length(); j++){
								
								Leg leg = new Leg();
								final JSONObject leg_obj = legs_json.getJSONObject(j);
								
								final JSONObject arrival_time_json = leg_obj.optJSONObject("arrival_time");
								if(arrival_time_json != null){
									leg.at_text = arrival_time_json.getString("text");
									leg.at_timezone = arrival_time_json.getString("time_zone");
									leg.at_value = arrival_time_json.getString("value");
									
									final JSONObject dt_time_json = leg_obj.optJSONObject("departure_time");
									leg.dt_text = dt_time_json.getString("text");
									leg.dt_timezone = dt_time_json.getString("time_zone");
									leg.dt_value = dt_time_json.getString("value");
								}
								
								final JSONObject distance_json = leg_obj.getJSONObject("distance");
								leg.distance_text = distance_json.getString("text");
								leg.distance_value = distance_json.getString("value");
								
								final JSONObject duration_json = leg_obj.getJSONObject("duration");
								leg.duration_text = duration_json.getString("text");
								leg.duration_value = duration_json.getString("value");
								
								leg.end_address = leg_obj.getString("end_address");
								final JSONObject end_loc_obj = leg_obj.getJSONObject("end_location");
								leg.end_lat = end_loc_obj.getString("lat");
								leg.end_lng = end_loc_obj.getString("lng");
								
								leg.start_address = leg_obj.getString("start_address");
								final JSONObject start_loc_obj = leg_obj.getJSONObject("start_location");
								leg.start_lat = start_loc_obj.getString("lat");
								leg.start_lng = start_loc_obj.getString("lng");
								
								// Steps
								ArrayList<Step> steps = new ArrayList<Step>();
								final JSONArray steps_json = leg_obj.getJSONArray("steps");
								for(int k=0; k<steps_json.length(); k++){
									final JSONObject steps_obj = steps_json.getJSONObject(k);
									Step step = new Step();
									
									final JSONObject step_distance_json = steps_obj.getJSONObject("distance");
									step.distance_text = step_distance_json.getString("text");
									step.distance_value = step_distance_json.getString("value");
									
									final JSONObject step_duration_json = steps_obj.getJSONObject("duration");
									step.duration_text = step_duration_json.getString("text");
									step.duration_value = step_duration_json.getString("value");
									
									final JSONObject step_end_loc_obj = steps_obj.getJSONObject("end_location");
									step.end_lat = step_end_loc_obj.getString("lat");
									step.end_lng = step_end_loc_obj.getString("lng");
									
									final JSONObject step_start_loc_obj = steps_obj.getJSONObject("start_location");
									step.start_lat = step_start_loc_obj.getString("lat");
									step.start_lng = step_start_loc_obj.getString("lng");
									
									step.html_instructions = steps_obj.getString("html_instructions").replaceAll("<(.*?)*>", "");
									step.polyline_points = steps_obj.getJSONObject("polyline").getString("points");
									
									// Optional for transit mode
									final JSONObject td_obj = steps_obj.optJSONObject("transit_details");
									if(td_obj != null){
										
										final TransitDetail td = new TransitDetail();
										
										final JSONObject as_obj = td_obj.getJSONObject("arrival_stop");
										final JSONObject as_loc_obj = as_obj.getJSONObject("location");
										
										td.as_loc_lat = as_loc_obj.getString("lat");
										td.as_loc_lat = as_loc_obj.getString("lng");
										td.as_loc_name = as_obj.getString("name");
										
										final JSONObject at_obj = td_obj.getJSONObject("arrival_time");
										td.at_text = at_obj.getString("text");
										td.at_timezone = at_obj.getString("time_zone");
										td.at_value = at_obj.getString("value");
										
										final JSONObject ds_obj = td_obj.getJSONObject("departure_stop");
										final JSONObject ds_loc_obj = ds_obj.getJSONObject("location");
										
										td.ds_loc_lat = ds_loc_obj.getString("lat");
										td.ds_loc_lat = ds_loc_obj.getString("lng");
										td.ds_loc_name = ds_obj.getString("name");
										
										final JSONObject dt_obj = td_obj.getJSONObject("departure_time");
										td.dt_text = dt_obj.getString("text");
										td.dt_timezone = dt_obj.getString("time_zone");
										td.dt_value = dt_obj.getString("value");
										
										td.headsign =td_obj.getString("headsign");
										td.headway =td_obj.optString("headway");
										td.num_stops =td_obj.getString("num_stops");
										
										final JSONObject line_obj = td_obj.getJSONObject("line");
										td.line_color = line_obj.optString("color");
										td.line_name = line_obj.optString("name");
										td.line_shortname = line_obj.optString("short_name");
										td.line_textcolor = line_obj.optString("text_color");
										
										td.vehicle_type = line_obj.getJSONObject("vehicle").getString("type");
										
										step.transitDetail = td;
									}
									
									step.travel_mode = steps_obj.getString("travel_mode");
									
									// Check innerSteps for Transit mode
									final JSONArray inner_steps_ary = steps_obj.optJSONArray("steps");
									ArrayList<Step> innterSteps = new ArrayList<Step>();
									if(inner_steps_ary != null){
										for(int l=0; l<inner_steps_ary.length(); l++){
											
											final JSONObject inner_steps_obj = steps_json.getJSONObject(k);
											Step innerstep = new Step();
											
											final JSONObject inner_step_distance_json = inner_steps_obj.getJSONObject("distance");
											innerstep.distance_text = inner_step_distance_json.getString("text");
											innerstep.distance_value = inner_step_distance_json.getString("value");
											
											final JSONObject innerstep_duration_json = leg_obj.getJSONObject("duration");
											innerstep.duration_text = innerstep_duration_json.getString("text");
											innerstep.duration_value = innerstep_duration_json.getString("value");
											
											final JSONObject innerstep_end_loc_obj = leg_obj.getJSONObject("end_location");
											innerstep.end_lat = innerstep_end_loc_obj.getString("lat");
											innerstep.end_lng = innerstep_end_loc_obj.getString("lng");
											
											final JSONObject innerstep_start_loc_obj = leg_obj.getJSONObject("start_location");
											innerstep.start_lat = innerstep_start_loc_obj.getString("lat");
											innerstep.start_lng = innerstep_start_loc_obj.getString("lng");
											
											innerstep.html_instructions = steps_obj.getString("html_instructions").replaceAll("<(.*?)*>", "");
											innerstep.polyline_points = steps_obj.getJSONObject("polyline").getString("points");
											
											innerstep.travel_mode = steps_obj.getString("travel_mode");
											
											innterSteps.add(innerstep);
										}
										step.innerSteps = innterSteps;
									}
									
									steps.add(step);
								}
								leg.steps = steps;
								legs.add(leg);
							}
							
							// Overview Polyline
							String overview_polyline = route_obj.getJSONObject("overview_polyline").getString("points");
							String summary = route_obj.getString("summary");
							JSONArray warning_objs = route_obj.optJSONArray("warnings");
							String warning;
							if(warning_objs.length() > 0){
								warning = warning_objs.getString(0);
							}else{
								warning = "";
							}*/
							
//							Route route = new Route(bound, copyrights, legs, overview_polyline, summary, warning);
							routes.add(getRouteFromJsonObj(route_obj));
						}
						
						interClass.directionSuccess(routes);
					}else{
						Log.i(TAG, "Direction failed");
						interClass.directionFail();
					}
					
					
				} catch (JSONException e) {
					e.printStackTrace();
					Utils.checkConn(c);
				}
		    }
		});
	}
	
	public static Route getRouteFromJsonObj(JSONObject route_obj) throws JSONException{
		
		// Bound
		final JSONObject bounds_json = route_obj.getJSONObject("bounds");
		final JSONObject northeast_json = bounds_json.getJSONObject("northeast");
		final JSONObject southwest_json = bounds_json.getJSONObject("southwest");
		final Bound bound = new Bound(northeast_json.getString("lat"), northeast_json.getString("lng"), southwest_json.getString("lat"), southwest_json.getString("lng"));
		
		// Copyright
		String copyrights = route_obj.getString("copyrights");
		
		// Leg
		ArrayList<Leg> legs = new ArrayList<Leg>();
		final JSONArray legs_json = route_obj.getJSONArray("legs");
		for(int j=0; j<legs_json.length(); j++){
			
			Leg leg = new Leg();
			final JSONObject leg_obj = legs_json.getJSONObject(j);
			
			final JSONObject arrival_time_json = leg_obj.optJSONObject("arrival_time");
			if(arrival_time_json != null){
				leg.at_text = arrival_time_json.getString("text");
				leg.at_timezone = arrival_time_json.getString("time_zone");
				leg.at_value = arrival_time_json.getString("value");
				
				final JSONObject dt_time_json = leg_obj.optJSONObject("departure_time");
				leg.dt_text = dt_time_json.getString("text");
				leg.dt_timezone = dt_time_json.getString("time_zone");
				leg.dt_value = dt_time_json.getString("value");
			}
			
			final JSONObject distance_json = leg_obj.getJSONObject("distance");
			leg.distance_text = distance_json.getString("text");
			leg.distance_value = distance_json.getString("value");
			
			final JSONObject duration_json = leg_obj.getJSONObject("duration");
			leg.duration_text = duration_json.getString("text");
			leg.duration_value = duration_json.getString("value");
			
			leg.end_address = leg_obj.getString("end_address");
			final JSONObject end_loc_obj = leg_obj.getJSONObject("end_location");
			leg.end_lat = end_loc_obj.getString("lat");
			leg.end_lng = end_loc_obj.getString("lng");
			
			leg.start_address = leg_obj.getString("start_address");
			final JSONObject start_loc_obj = leg_obj.getJSONObject("start_location");
			leg.start_lat = start_loc_obj.getString("lat");
			leg.start_lng = start_loc_obj.getString("lng");
			
			// Steps
			ArrayList<Step> steps = new ArrayList<Step>();
			final JSONArray steps_json = leg_obj.getJSONArray("steps");
			for(int k=0; k<steps_json.length(); k++){
				final JSONObject steps_obj = steps_json.getJSONObject(k);
				Step step = new Step();
				
				final JSONObject step_distance_json = steps_obj.getJSONObject("distance");
				step.distance_text = step_distance_json.getString("text");
				step.distance_value = step_distance_json.getString("value");
				
				final JSONObject step_duration_json = steps_obj.getJSONObject("duration");
				step.duration_text = step_duration_json.getString("text");
				step.duration_value = step_duration_json.getString("value");
				
				final JSONObject step_end_loc_obj = steps_obj.getJSONObject("end_location");
				step.end_lat = step_end_loc_obj.getString("lat");
				step.end_lng = step_end_loc_obj.getString("lng");
				
				final JSONObject step_start_loc_obj = steps_obj.getJSONObject("start_location");
				step.start_lat = step_start_loc_obj.getString("lat");
				step.start_lng = step_start_loc_obj.getString("lng");
				
				step.html_instructions = steps_obj.getString("html_instructions").replaceAll("<(.*?)*>", "");
				step.polyline_points = steps_obj.getJSONObject("polyline").getString("points");
				
				// Optional for transit mode
				final JSONObject td_obj = steps_obj.optJSONObject("transit_details");
				if(td_obj != null){
					
					final TransitDetail td = new TransitDetail();
					
					final JSONObject as_obj = td_obj.getJSONObject("arrival_stop");
					final JSONObject as_loc_obj = as_obj.getJSONObject("location");
					
					td.as_loc_lat = as_loc_obj.getString("lat");
					td.as_loc_lat = as_loc_obj.getString("lng");
					td.as_loc_name = as_obj.getString("name");
					
					final JSONObject at_obj = td_obj.getJSONObject("arrival_time");
					td.at_text = at_obj.getString("text");
					td.at_timezone = at_obj.getString("time_zone");
					td.at_value = at_obj.getString("value");
					
					final JSONObject ds_obj = td_obj.getJSONObject("departure_stop");
					final JSONObject ds_loc_obj = ds_obj.getJSONObject("location");
					
					td.ds_loc_lat = ds_loc_obj.getString("lat");
					td.ds_loc_lat = ds_loc_obj.getString("lng");
					td.ds_loc_name = ds_obj.getString("name");
					
					final JSONObject dt_obj = td_obj.getJSONObject("departure_time");
					td.dt_text = dt_obj.getString("text");
					td.dt_timezone = dt_obj.getString("time_zone");
					td.dt_value = dt_obj.getString("value");
					
					td.headsign =td_obj.getString("headsign");
					td.headway =td_obj.optString("headway");
					td.num_stops =td_obj.getString("num_stops");
					
					final JSONObject line_obj = td_obj.getJSONObject("line");
					td.line_color = line_obj.optString("color");
					td.line_name = line_obj.optString("name");
					td.line_shortname = line_obj.optString("short_name");
					td.line_textcolor = line_obj.optString("text_color");
					
					td.vehicle_type = line_obj.getJSONObject("vehicle").getString("type");
					
					step.transitDetail = td;
				}
				
				step.travel_mode = steps_obj.getString("travel_mode");
				
				// Check innerSteps for Transit mode
				final JSONArray inner_steps_ary = steps_obj.optJSONArray("steps");
				ArrayList<Step> innterSteps = new ArrayList<Step>();
				if(inner_steps_ary != null){
					for(int l=0; l<inner_steps_ary.length(); l++){
						
						final JSONObject inner_steps_obj = steps_json.getJSONObject(k);
						Step innerstep = new Step();
						
						final JSONObject inner_step_distance_json = inner_steps_obj.getJSONObject("distance");
						innerstep.distance_text = inner_step_distance_json.getString("text");
						innerstep.distance_value = inner_step_distance_json.getString("value");
						
						final JSONObject innerstep_duration_json = leg_obj.getJSONObject("duration");
						innerstep.duration_text = innerstep_duration_json.getString("text");
						innerstep.duration_value = innerstep_duration_json.getString("value");
						
						final JSONObject innerstep_end_loc_obj = leg_obj.getJSONObject("end_location");
						innerstep.end_lat = innerstep_end_loc_obj.getString("lat");
						innerstep.end_lng = innerstep_end_loc_obj.getString("lng");
						
						final JSONObject innerstep_start_loc_obj = leg_obj.getJSONObject("start_location");
						innerstep.start_lat = innerstep_start_loc_obj.getString("lat");
						innerstep.start_lng = innerstep_start_loc_obj.getString("lng");
						
						innerstep.html_instructions = steps_obj.getString("html_instructions").replaceAll("<(.*?)*>", "");
						innerstep.polyline_points = steps_obj.getJSONObject("polyline").getString("points");
						
						innerstep.travel_mode = steps_obj.getString("travel_mode");
						
						innterSteps.add(innerstep);
					}
					step.innerSteps = innterSteps;
				}
				
				steps.add(step);
			}
			leg.steps = steps;
			legs.add(leg);
		}
		
		// Overview Polyline
		String overview_polyline = route_obj.getJSONObject("overview_polyline").getString("points");
		String summary = route_obj.getString("summary");
		JSONArray warning_objs = route_obj.optJSONArray("warnings");
		String warning;
		if(warning_objs.length() > 0){
			warning = warning_objs.getString(0);
		}else{
			warning = "";
		}
		
		Route route = new Route(bound, copyrights, legs, overview_polyline, summary, warning);
		
		return route;
	}
	
	public enum TravelMode {
	    BIKING("bicycling"),
	    DRIVING("driving"),
	    WALKING("walking"),
	    TRANSIT("transit"),
	    BUS("bus");
	    
	    protected String _sValue;

	    private TravelMode(String sValue) {
	            this._sValue = sValue;
	    }

	    protected String getValue() { return _sValue; }
	  }
	
	public interface Direction_Api_onComplete{
		public void directionSuccess(ArrayList<Route> routes);
		public void directionFail();
	}
}


