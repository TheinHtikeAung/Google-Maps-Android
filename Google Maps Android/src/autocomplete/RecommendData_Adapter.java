package autocomplete;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.theinhtikeaung.googlemapsandroid.R;

public class RecommendData_Adapter extends ArrayAdapter<RD>{
	
	private final String TAG = RecommendData_Adapter.class.getSimpleName();
	private Context c;
	int layoutResourceId;
	private ArrayList<RD> rds;	

	public RecommendData_Adapter(Context context, int resource, ArrayList<RD> rds) {
		super(context, resource, rds);
		c = context;
		this.layoutResourceId = resource;
		this.rds = rds;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			// inflate the layout
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		
		TextView txt1 = (TextView) convertView.findViewById(R.id.txt1);
		TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		
		RD rd = rds.get(position);
		txt1.setText(rd.msg1);
		txt2.setText(rd.msg2);
		
		Resources res = c.getResources();
		
		int id = res.getIdentifier(rd.img, "drawable", c.getPackageName());
		Drawable drawable = res.getDrawable(id);
		img.setImageDrawable(drawable);
		
		return convertView;
	}

}
