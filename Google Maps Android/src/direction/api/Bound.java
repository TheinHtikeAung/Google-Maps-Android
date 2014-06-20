package direction.api;

public class Bound {
	
	public String northeast_lat;
	public String northeast_lng;
	public String southwest_lat;
	public String southwest_lng;
	
	public Bound(String northeast_lat, String northeast_lng, String southwest_lat, String southwest_lng) {
		super();
		this.northeast_lat = northeast_lat;
		this.northeast_lng = northeast_lng;
		this.southwest_lat = southwest_lat;
		this.southwest_lng = southwest_lng;
	}
}
