package com.innovation4you.napking.model;

import java.util.List;

public class RestStop {

	public long id;
	public String name;
	public double lat, lng;
	public int parkingSpaces;
	public String[] facilities;
	public List<OccupancyEntry> occupancies;

	public RestStop(final long id, final String name, final List<OccupancyEntry> occupancies, final double lat, final double lng) {
		this.name = name;
		this.occupancies = occupancies;
		this.lat = lat;
		this.lng = lng;
	}
}
