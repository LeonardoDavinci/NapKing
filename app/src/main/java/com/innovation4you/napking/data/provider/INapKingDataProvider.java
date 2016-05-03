package com.innovation4you.napking.data.provider;

import com.innovation4you.napking.model.RestStop;

import java.util.List;

public interface INapKingDataProvider {

	List<RestStop> findRestStops(final String condition);
}
