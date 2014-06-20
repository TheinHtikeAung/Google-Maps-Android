package direction;

import direction.api.Direction_Api.TravelMode;

public class RouteItemDetail {

	public TravelMode mode;
	public String text;
	public RouteItemDetail(TravelMode m, String text) {
		super();
		this.mode = m;
		this.text = text;
	}
}
