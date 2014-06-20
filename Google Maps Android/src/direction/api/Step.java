package direction.api;

import java.util.ArrayList;

public class Step {

	public String distance_text;
	public String distance_value;
	
	public String duration_text;
	public String duration_value;
	
	public String end_lat;
	public String end_lng;
	
	public String html_instructions;
	
	public String polyline_points;
	
	public String start_lat;
	public String start_lng;
	
	public String travel_mode;
	
	// Optional for walking route in transit mode
	public ArrayList<Step> innerSteps;
	
	// Optional for Transit mode
	public TransitDetail transitDetail;
	
	public Double getStartLat(){
		return Double.parseDouble(start_lat);
	}
	
	public Double getStartLng(){
		return Double.parseDouble(start_lng);
	}
	
	public Double getEndLat(){
		return Double.parseDouble(end_lat);
	}
	
	public Double getEndLng(){
		return Double.parseDouble(end_lng);
	}
	
	
}
