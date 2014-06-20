package placedetails;

import helper.SH;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/* 
 * Ref API - https://developers.google.com/places/documentation/details#PlaceDetailsRequests
 * 
 */

public class PlaceDetails_Api {
	
	private final String TAG = PlaceDetails_Api.class.getSimpleName();
	private PlaceDetails_Api_OnComplete interClass;
	
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_DETAILS = "/details";
	private static final String OUT_JSON = "/json";
	
	public PlaceDetails_Api(PlaceDetails_Api_OnComplete interClass){
		this.interClass = interClass;
	}
	
	public void execute(String reference){
		
		StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
		
		sb.append("?sensor=false&key=" + SH.MAP_BROWSER_KEY);
		sb.append("&reference=" + reference);
            
        Log.i(TAG, "Request URL - " + sb.toString());

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(sb.toString(), new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        Log.i(TAG, "Response from map server");
		        
		        // Create a JSON object hierarchy from the results
				try {
					JSONObject jsonObj = new JSONObject(response);
					String status = jsonObj.getString("status");
					
					// If status OK
					if(status.equalsIgnoreCase("OK")){
						
						JSONObject result_obj = jsonObj.getJSONObject("result");
						
						JSONObject location_obj = result_obj.getJSONObject("geometry").getJSONObject("location");
						String lat = location_obj.optString("lat");
						String lng = location_obj.optString("lng");
						
						PlaceDetail pd = new PlaceDetail(lat, lng);
						
						interClass.onComplete(pd);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	public interface PlaceDetails_Api_OnComplete{
		public void onComplete(PlaceDetail pd);
	}
}
