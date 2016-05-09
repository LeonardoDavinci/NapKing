package com.innovation4you.napking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.innovation4you.napking.R;
import com.innovation4you.napking.data.NapKingService;
import com.innovation4you.napking.ui.view.OccupancyChartView;
import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class RestStopDetailFragment extends BaseFragment {

	@BindView(R.id.fragment_rest_stop_detail_occupancy_chart_scroll_view)
	HorizontalScrollView occupancyChartScrollView;

	@BindView(R.id.fragment_rest_stop_detail_occupancy_chart)
	OccupancyChartView occupancyChartView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setContentView(R.layout.fragment_rest_stop_detail, inflater, container, savedInstanceState);

		occupancyChartView.addOnLayoutChangeListener(new OneTimeLayoutChangeListener() {
			@Override
			public void onLayoutChange() {
				occupancyChartScrollView.scrollTo((int) occupancyChartView.getNowIndicatorX(), 0);
			}
		});
		occupancyChartView.setup(NapKingService.getRestStops("").get(0).occupancies, (int) TimeUnit.MINUTES.toMillis(140));
		return root;
	}
}
