package placedetails;

public class PlaceDetail {

	public double lat;
	public double lng;
	
	public PlaceDetail(String lat, String lng) {
		super();
		this.lat = Double.parseDouble(lat);
		this.lng = Double.parseDouble(lng);
	}
	
	public PlaceDetail(Double lat, Double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "PlaceDetail [lat=" + lat + ", lng=" + lng + "]";
	}
	
	public String getLanLng(){
		return lat + "," +lng;
	}
}
