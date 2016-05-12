package com.innovation4you.napking.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.ui.activity.base.ContainerActivity;
import com.innovation4you.napking.ui.fragment.RestStopDetailFragment;

public class RestStopDetailActivity extends ContainerActivity<RestStopDetailFragment> {

	public static Intent createIntent(final Context context, final SearchResult searchResult) {
		final Intent i = new Intent(context, RestStopDetailActivity.class);
		i.putExtra(RestStopDetailFragment.KEY_SEARCH_RESULT, searchResult);
		return i;
	}

	@Override
	protected RestStopDetailFragment createFragment(Bundle args) {
		return RestStopDetailFragment.newInstance(args);
	}
}
