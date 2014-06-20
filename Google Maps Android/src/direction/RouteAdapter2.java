package direction;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theinhtikeaung.googlemapsandroid.R;

import direction.api.Direction_Api.TravelMode;

public class RouteAdapter2 extends ArrayAdapter<RouteItem2>{
	
	private final String TAG = RouteAdapter2.class.getSimpleName();
	private Context c;
	int layoutResourceId;

	public RouteAdapter2(Context context, int resource, ArrayList<RouteItem2> routeItems2) {
		super(context, resource, routeItems2);
		c = context;
		this.layoutResourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
//		}
		
		RouteItem2 r = getItem(position);
		
		// Set header and footer
		TextView txtHeader = (TextView) convertView.findViewById(R.id.txtHeader);
		TextView txtFooter = (TextView) convertView.findViewById(R.id.txtFooter);
		txtHeader.setText(r.header);
		txtFooter.setText(r.footer);
		
		LinearLayout linear = (LinearLayout) convertView.findViewById(R.id.linearlayout);
		
		Log.i(TAG, "RIDS Size" + r.rid.size());
		
		for(int i = 0; i < r.rid.size(); i++){
			
			RouteItemDetail rid = r.rid.get(i);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
			layoutParams.gravity = Gravity.CENTER;
			
			// ImageView
			ImageView imageView = new ImageView(c);
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
			
			if(rid.mode == TravelMode.TRANSIT){
				imageView.setImageResource(R.drawable.mrt_logo);
			}else if(rid.mode == TravelMode.WALKING){
				imageView.setImageResource(R.drawable.walking);
			}else if(rid.mode == TravelMode.BUS){
				imageView.setImageResource(R.drawable.bus_64);
			}
			
			textView.setText(rid.text);
			
			linear.addView(imageView);
			linear.addView(textView);
			
			if(i + 1 != r.rid.size()){
				linear.addView(arrow);
			}
		}
		
		return convertView;
	}

}
