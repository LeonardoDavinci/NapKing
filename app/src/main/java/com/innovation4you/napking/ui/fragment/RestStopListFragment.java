package com.innovation4you.napking.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;
import com.innovation4you.napking.R;
import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.ui.activity.RestStopDetailActivity;
import com.innovation4you.napking.ui.adapter.BaseViewHolder;
import com.innovation4you.napking.ui.adapter.ListAdapter;
import com.innovation4you.napking.util.OccupancyColorHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestStopListFragment extends BaseFragment implements View.OnClickListener {

	@BindView(R.id.fragment_rest_stop_list_recyclerview)
	RecyclerView recyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setContentView(R.layout.fragment_rest_stop_list, inflater, container, savedInstanceState);

		recyclerView.setHasFixedSize(true);

		return root;
	}

	public void setSearchResults(final List<SearchResult> searchResults) {
		if (searchResults == null || searchResults.isEmpty()) {
			recyclerView.setAdapter(null);
		} else {
			recyclerView.setAdapter(new SearchResultAdapter(getActivity(), searchResults, this));
		}
	}

	@Override
	public void onClick(View v) {
		final SearchResult searchResult = (SearchResult) v.getTag();
		startActivity(RestStopDetailActivity.createIntent(getActivity(), searchResult));
	}

	public static class SearchResultViewHolder extends BaseViewHolder {

		@BindView(R.id.list_item_rest_stop_occupancy_text)
		public TextView occupancyText;

		@BindView(R.id.list_item_rest_stop_title)
		public TextView title;

		@BindView(R.id.list_item_rest_stop_subtext)
		public TextView subtext;

		public SearchResultViewHolder(View itemView, View.OnClickListener onClickListener) {
			super(itemView, onClickListener);
			ButterKnife.bind(this, itemView);
		}
	}

	private static class SearchResultAdapter extends ListAdapter<SearchResult, SearchResultViewHolder> {

		final DateFormat dateFormatter = SimpleDateFormat.getTimeInstance(java.text.DateFormat.SHORT);
		final Context context;
		final OccupancyColorHelper occupancyColorHelper;

		public SearchResultAdapter(final Context context, final List<SearchResult> items, final View.OnClickListener onClickListener) {
			super(items, onClickListener);
			this.context = context;
			this.occupancyColorHelper = new OccupancyColorHelper(context);
		}

		@Override
		protected int getLayoutResId() {
			return R.layout.list_item_rest_stop;
		}

		@Override
		public SearchResultViewHolder onCreateViewHolder(View view) {
			return new SearchResultViewHolder(view, onClickListener);
		}

		@Override
		public void onBindViewHolder(SearchResultViewHolder viewHolder, SearchResult item, int position) {
			final Spanny titleSpan = new Spanny(item.name)
					.append(" Rest Stop", new TypefaceSpan("sans-serif-light"));
			viewHolder.title.setText(titleSpan);

			final Spanny subTextSpan = new Spanny()
					.append(dateFormatter.format(item.calculateArrivalTime()))
					.append(" arrival   ", new TypefaceSpan("sans-serif-light"))
					.append(String.format(Locale.ENGLISH, "%.2f", (item.distanceFromSource / 1000d)))
					.append(" km", new TypefaceSpan("sans-serif-light"));
			viewHolder.subtext.setText(subTextSpan);

			final Spanny occupancyTextSpan = new Spanny(String.valueOf(item.occupancy))
					.append("%", new RelativeSizeSpan(0.6f));
			viewHolder.occupancyText.setText(occupancyTextSpan);

			final Drawable occupancyDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.background_round));
			DrawableCompat.setTint(occupancyDrawable, occupancyColorHelper.getOccupancyColor(item.occupancy));
			viewHolder.occupancyText.setBackground(occupancyDrawable);

			viewHolder.itemView.setTag(item);
		}
	}
}
