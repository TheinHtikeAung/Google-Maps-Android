package direction.api;

import java.util.ArrayList;

public class Route {

	public Bound bound;
	public String copyrights;
	
	public ArrayList<Leg> legs;
	public String overview_polyline;
	public String summary;
	public String warning;
	
	public Route(Bound bound, String copyrights, ArrayList<Leg> legs,
			String overview_polyline, String summary, String warning) {
		super();
		this.bound = bound;
		this.copyrights = copyrights;
		this.legs = legs;
		this.overview_polyline = overview_polyline;
		this.summary = summary;
		this.warning = warning;
	}
}
