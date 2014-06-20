package direction.api;

import placedetails.PlaceDetail;

import direction.api.Direction_Api.TravelMode;

public class DirectionInfo {

	public String keyword_start;
	public String keyword_end;
	
	public TravelMode mode;
	
	public PlaceDetail from;
	public PlaceDetail to;
	
	public DirectionInfo(String keyword_start, String keyword_end,
			TravelMode mode, PlaceDetail from, PlaceDetail to) {
		super();
		this.keyword_start = keyword_start;
		this.keyword_end = keyword_end;
		this.mode = mode;
		this.from = from;
		this.to = to;
	}
}
