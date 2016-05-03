package com.innovation4you.napking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innovation4you.napking.R;
import com.innovation4you.napking.data.NapKingService;
import com.innovation4you.napking.ui.view.OccupancyChartView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestStopDetailFragment extends BaseFragment {

	@BindView(R.id.fragment_rest_stop_detail_occupancy_chart)
	OccupancyChartView occupancyChartView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setContentView(R.layout.fragment_rest_stop_detail, inflater, container, savedInstanceState);

		occupancyChartView = ButterKnife.findById(root, R.id.fragment_rest_stop_detail_occupancy_chart);
		occupancyChartView.setOccupancyEntries(NapKingService.getRestStops("").get(0).occupancies);
		return root;
	}
}
