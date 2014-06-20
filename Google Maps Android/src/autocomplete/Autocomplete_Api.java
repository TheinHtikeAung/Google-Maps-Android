package autocomplete;

import helper.SH;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Autocomplete_Api {
	
	private final String TAG = Autocomplete_Api.class.getSimpleName();
	private Autocomplete_Api_onComplete interClass;
	
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	
	public Autocomplete_Api(Autocomplete_Api_onComplete interClass){
		this.interClass = interClass;
	}
	
	public void execute(String input){
		
		StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
		
        try {
            sb.append("?sensor=false&key=" + SH.MAP_BROWSER_KEY);
            sb.append("&components=country:sg");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
        
        Log.i(TAG, "Request URL - " + sb.toString());

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(sb.toString(), new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        Log.i(TAG, "Response from map server - " + response);
		        
		        // Create a JSON object hierarchy from the results
				try {
					JSONObject jsonObj = new JSONObject(response);
					JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

					ArrayList<Place> places = new ArrayList<Place>(predsJsonArray.length());
					for (int i = 0; i < predsJsonArray.length(); i++) {
						String description = predsJsonArray.getJSONObject(i).getString("description");
						String reference = predsJsonArray.getJSONObject(i).getString("reference");
						
						ArrayList<String> terms = new ArrayList<String>();
						JSONArray termsJsonArray = predsJsonArray.getJSONObject(i).getJSONArray("terms");
						for (int j = 0; j < termsJsonArray.length(); j++) {
							 terms.add(termsJsonArray.getJSONObject(j).getString("value"));
						}
						
						places.add(new Place(description, terms, reference));
					}
					
					interClass.onComplete(places);

				} catch (JSONException e) {
					e.printStackTrace();
				}
		    }
		});
	}

	public interface Autocomplete_Api_onComplete{
		public void onComplete(ArrayList<Place> places);
	}
}
