package autocomplete;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.theinhtikeaung.googlemapsandroid.R;

public class Autocomplete_Adapter extends ArrayAdapter<Place>{
	
	private final String TAG = Autocomplete_Adapter.class.getSimpleName();
	private Context c;
	int layoutResourceId;
	private ArrayList<Place> places;

	public Autocomplete_Adapter(Context context, int resource, ArrayList<Place> places) {
		super(context, resource, places);
		c = context;
		this.layoutResourceId = resource;
		this.places = places;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		
		Place p = places.get(position);
		
		TextView txt1 = (TextView) convertView.findViewById(R.id.txt1);
		txt1.setText(p.terms.get(0));
		
		TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < p.terms.size(); i++) {
			sb.append(p.terms.get(i));
			
			if(i != p.terms.size() - 1){
				sb.append(", ");
			}
		}
		
		if(sb.toString().equals("")){
			txt2.setVisibility(View.GONE);
		}else{
			txt2.setText(sb.toString());
		}
		
		
		
		return convertView;
	}

}
