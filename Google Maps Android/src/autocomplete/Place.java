package autocomplete;

import java.util.ArrayList;

public class Place {
	
	public String description;
	public ArrayList<String> terms;
	public String reference;
	
	public Place(String description, ArrayList<String> terms, String reference) {
		super();
		this.description = description;
		this.terms = terms;
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Place [description=" + description + ", terms=" + terms
				+ ", reference=" + reference + "]";
	}
	
	
}
