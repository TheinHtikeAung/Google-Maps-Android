package direction.api;

import java.util.ArrayList;

public class Leg {

	public String at_text;
	public String at_timezone;
	public String at_value;
	
	public String dt_text;
	public String dt_timezone;
	public String dt_value;
	
	public String distance_text;
	public String distance_value;
	
	public String duration_text;
	public String duration_value;
	
	public String end_lat;
	public String end_lng;
	public String end_address;
	
	public String start_lat;
	public String start_lng;
	public String start_address;
	
	public ArrayList<Step> steps;
	
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
