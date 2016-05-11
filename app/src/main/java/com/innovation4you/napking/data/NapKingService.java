package com.innovation4you.napking.data;

import com.innovation4you.napking.data.provider.INapKingDataProvider;
import com.innovation4you.napking.model.RestStop;

import java.util.List;

public class NapKingService {

	private static NapKingService instance;

	private INapKingDataProvider dataProvider;

	public static void init(INapKingDataProvider dataProvider) {
		if (instance == null) {
			instance = new NapKingService(dataProvider);
		}
	}

	private NapKingService(INapKingDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public static List<RestStop> getRestStops(final String condition) {
		return instance.dataProvider.findRestStops(condition);
	}

	public static RestStop getRestStop(final long id) {
		return instance.dataProvider.findRestStopById(id);
	}
}
