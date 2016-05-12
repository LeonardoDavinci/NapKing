package com.innovation4you.napking.data.provider;

import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.model.RestStop;

import java.util.List;

public interface INapKingDataProvider {

	List<RestStop> findRestStops(final String condition);

	List<SearchResult> search(final String condition, final int minutesLeft, final double currentLat, final double currentLng);

	RestStop findRestStopById(final long id);
}
