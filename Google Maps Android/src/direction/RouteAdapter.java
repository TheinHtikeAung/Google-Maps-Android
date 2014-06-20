package direction;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.theinhtikeaung.googlemapsandroid.R;

public class RouteAdapter extends ArrayAdapter<RouteItem>{
	
	private final String TAG = RouteAdapter.class.getSimpleName();
	private Context c;
	int layoutResourceId;
	private ArrayList<RouteItem> routeItems;

	public RouteAdapter(Context context, int resource, ArrayList<RouteItem> routeItems) {
		super(context, resource, routeItems);
		c = context;
		this.layoutResourceId = resource;
		this.routeItems = routeItems;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		
		RouteItem r = routeItems.get(position);
		
		TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
		txtTime.setText(r.time);
		
		TextView txtDistance = (TextView) convertView.findViewById(R.id.txtDistance);
		txtDistance.setText("(" + r.distance + ")");
		
		TextView txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
		txtAddress.setText("Via " + r.address);
		
		return convertView;
	}

}
