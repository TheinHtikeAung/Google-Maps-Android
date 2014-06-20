package direction;

import helper.Utils;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class GeocoderTask extends AsyncTask<String, Void, List<Address>>
{
	private final String TAG = GeocoderTask.class.getSimpleName();
	private Context c;
	private int result;
	private GeocoderOnComplete interClass;
	
	public GeocoderTask(Context c, GeocoderOnComplete interClass,int result){
		this.c = c;
		this.interClass = interClass;
		this.result = result;
	}

    @Override
    protected List<Address> doInBackground(String... locationName) {
    	
    	Log.i(TAG, "Geocoding started !");
    	
        Geocoder geocoder = new Geocoder(c);
        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocationName(locationName[0], result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            this.cancel(true);
            Utils.checkConn(c);
            Toast.makeText(c, "Connection timeout. Try again.", Toast.LENGTH_SHORT).show();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {

        if(addresses == null || addresses.size() == 0)
        {
            Toast.makeText(c, "Location not found", Toast.LENGTH_SHORT).show();
            interClass.geoFailed();
        }else{
        	interClass.geoSuccess(addresses);
        }
    }
    
    public interface GeocoderOnComplete{
    	public void geoFailed();
    	public void geoSuccess(List<Address> addresses);
    }
}
