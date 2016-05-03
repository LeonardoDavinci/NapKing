package com.innovation4you.napking.model;

import java.util.List;

public class RestStop {

	public String name;
	public List<OccupancyEntry> occupancies;

	public RestStop(String name, List<OccupancyEntry> occupancies) {
		this.name = name;
		this.occupancies = occupancies;
	}

	public RestStop(String name) {

		this.name = name;
	}
}
