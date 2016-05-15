package com.innovation4you.napking.data.provider;

import com.google.android.gms.maps.model.LatLng;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.model.SearchResult;

import java.util.List;

public interface INapKingDataProvider {

	List<RestStop> findRestStops(final String condition);

	List<SearchResult> search(final LatLng source, final LatLng destination, final int minutesLeft);

	RestStop findRestStopById(final long id);
}
