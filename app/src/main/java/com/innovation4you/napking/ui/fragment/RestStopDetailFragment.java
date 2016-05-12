package com.innovation4you.napking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.innovation4you.napking.R;
import com.innovation4you.napking.app.Cfg;
import com.innovation4you.napking.data.NapKingService;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.ui.view.OccupancyChartView;
import com.innovation4you.napking.util.GoogleStaticMapsUrlBuilder;
import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class RestStopDetailFragment extends BaseFragment {

	public static final String KEY_REST_STOP_ID = "rest_stop_id";

	@BindView(R.id.fragment_rest_stop_detail_collapsing_toolbar_layout)
	CollapsingToolbarLayout collapsingToolbarLayout;

	@BindView(R.id.fragment_rest_stop_detail_map)
	ImageView mapImageView;

	@BindView(R.id.fragment_rest_stop_detail_toolbar)
	Toolbar toolbar;

	@BindView(R.id.fragment_rest_stop_detail_occupancy_chart_scroll_view)
	HorizontalScrollView occupancyChartScrollView;

	@BindView(R.id.fragment_rest_stop_detail_occupancy_chart)
	OccupancyChartView occupancyChartView;

	RestStop restStop;

	public static RestStopDetailFragment newInstance(final long restStopId) {
		final RestStopDetailFragment f = new RestStopDetailFragment();
		final Bundle args = new Bundle();
		args.putLong(KEY_REST_STOP_ID, restStopId);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restStop = getRestStop();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setContentView(R.layout.fragment_rest_stop_detail, inflater, container, savedInstanceState);

		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		final ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		collapsingToolbarLayout.setTitle(restStop.name);

		occupancyChartView.addOnLayoutChangeListener(new OneTimeLayoutChangeListener() {
			@Override
			public void onLayoutChange() {
				occupancyChartScrollView.smoothScrollTo((int) occupancyChartView.getNowIndicatorX() - root.getWidth() / 2, 0);
			}
		});
		occupancyChartView.setup(NapKingService.getRestStop(0).occupancies, (int) TimeUnit.MINUTES.toMillis(240));

		return root;
	}

	@Override
	protected void onLayoutFinished() {
		super.onLayoutFinished();

		loadMap();
	}

	private void loadMap() {
		final GoogleStaticMapsUrlBuilder builder = new GoogleStaticMapsUrlBuilder()
				.apiKey(Cfg.GOOGLE_STATIC_MAPS_API_KEY)
				.center(restStop.lat, restStop.lng)
				.addMarker(GoogleStaticMapsUrlBuilder.MARKER_COLOR_BLUE, "", restStop.lat, restStop.lng)
				.zoom(12)
				.sensor(false)
				.size(mapImageView.getWidth(), mapImageView.getHeight());

		Glide.with(getActivity())
				.load(builder.build())
				.centerCrop()
				.crossFade()
				.into(mapImageView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private RestStop getRestStop() {
		if (getArguments() != null && getArguments().containsKey(KEY_REST_STOP_ID)) {
			return NapKingService.getRestStop(getArguments().getLong(KEY_REST_STOP_ID));
		}
		return null;
	}
}
