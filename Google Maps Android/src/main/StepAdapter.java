package main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.theinhtikeaung.googlemapsandroid.R;

public class StepAdapter extends ArrayAdapter<StepObj>{
	
	private final String TAG = StepAdapter.class.getSimpleName();
	private Context c;
	int layoutResourceId;
	private ArrayList<StepObj> stepObjs;

	public StepAdapter(Context context, int resource, ArrayList<StepObj> stepObjs) {
		super(context, resource, stepObjs);
		c = context;
		this.layoutResourceId = resource;
		this.stepObjs = stepObjs;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
//		}
		
		StepObj r = getItem(position);
		
		// Set header and footer
		TextView txt1 = (TextView) convertView.findViewById(R.id.txt1);
		TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);
		
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		
		String text = r.instructiton;
		txt1.setText(text);
		txt2.setText(r.distance);
		
		if(position == 0){
			img.setImageResource(R.drawable.start_blue);
		}else if(position + 1 == stepObjs.size()){
			img.setImageResource(R.drawable.end_green);
		}else{
			if(text.toLowerCase().contains("head") || text.toLowerCase().contains("Continue")){
				img.setImageResource(R.drawable.head_48);
			}else if(text.toLowerCase().contains("left")){
				img.setImageResource(R.drawable.keep_left);
			}else if(text.toLowerCase().contains("right")){
				img.setImageResource(R.drawable.keep_right);
			}else if(text.toLowerCase().contains("merge")){
				img.setImageResource(R.drawable.merge);
			}else{
				img.setImageResource(R.drawable.head_48);
			}
		}
		
		return convertView;
	}

}
