package com.innovation4you.napking.ui.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.binaryfork.spanny.Spanny
import com.innovation4you.napking.R
import com.innovation4you.napking.model.SearchResult
import com.innovation4you.napking.ui.activity.RestStopDetailActivity
import com.innovation4you.napking.ui.adapter.BaseViewHolder
import com.innovation4you.napking.ui.adapter.ListAdapter
import com.innovation4you.napking.util.OccupancyColorHelper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife

class RestStopListFragment : BaseFragment(), View.OnClickListener {

    @BindView(R.id.fragment_rest_stop_list_recyclerview)
    internal var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setContentView(R.layout.fragment_rest_stop_list, inflater, container, savedInstanceState)

        recyclerView!!.setHasFixedSize(true)

        return root
    }

    fun setSearchResults(searchResults: List<SearchResult>?) {
        if (searchResults == null || searchResults.isEmpty()) {
            recyclerView!!.adapter = null
        } else {
            recyclerView!!.adapter = SearchResultAdapter(activity, searchResults, this)
        }
    }

    override fun onClick(v: View) {
        val searchResult = v.tag as SearchResult
        startActivity(RestStopDetailActivity.createIntent(activity, searchResult))
    }

    class SearchResultViewHolder(itemView: View, onClickListener: View.OnClickListener) : BaseViewHolder(itemView, onClickListener) {

        @BindView(R.id.list_item_rest_stop_occupancy_text)
        var occupancyText: TextView? = null

        @BindView(R.id.list_item_rest_stop_title)
        var title: TextView? = null

        @BindView(R.id.list_item_rest_stop_subtext)
        var subtext: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    private class SearchResultAdapter(internal val context: Context, items: List<SearchResult>, onClickListener: View.OnClickListener) : ListAdapter<SearchResult, SearchResultViewHolder>(items, onClickListener) {

        internal val dateFormatter = SimpleDateFormat.getTimeInstance(java.text.DateFormat.SHORT)
        internal val occupancyColorHelper: OccupancyColorHelper

        init {
            this.occupancyColorHelper = OccupancyColorHelper(context)
        }

        protected override val layoutResId: Int
            get() = R.layout.list_item_rest_stop

        override fun onCreateViewHolder(view: View): SearchResultViewHolder {
            return SearchResultViewHolder(view, onClickListener)
        }

        override fun onBindViewHolder(viewHolder: SearchResultViewHolder, item: SearchResult, position: Int) {
            val titleSpan = Spanny(item.name).append(" Rest Stop", TypefaceSpan("sans-serif-light"))
            viewHolder.title!!.text = titleSpan

            val subTextSpan = Spanny().append(dateFormatter.format(item.calculateArrivalTime())).append(" arrival   ", TypefaceSpan("sans-serif-light")).append(String.format(Locale.ENGLISH, "%.2f", item.distanceFromSource / 1000.0)).append(" km", TypefaceSpan("sans-serif-light"))
            viewHolder.subtext!!.text = subTextSpan

            val occupancyTextSpan = Spanny(item.occupancy.toString()).append("%", RelativeSizeSpan(0.6f))
            viewHolder.occupancyText!!.text = occupancyTextSpan

            val occupancyDrawable = DrawableCompat.wrap(context.resources.getDrawable(R.drawable.background_round)!!)
            DrawableCompat.setTint(occupancyDrawable, occupancyColorHelper.getOccupancyColor(item.occupancy))
            viewHolder.occupancyText!!.background = occupancyDrawable

            viewHolder.itemView.tag = item
        }
    }
}
