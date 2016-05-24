package com.innovation4you.napking.ui.fragment

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import butterknife.BindView
import com.bumptech.glide.Glide
import com.innovation4you.napking.R
import com.innovation4you.napking.app.Cfg
import com.innovation4you.napking.data.NapKingRepository
import com.innovation4you.napking.model.RestStop
import com.innovation4you.napking.model.SearchResult
import com.innovation4you.napking.ui.view.OccupancyChartView
import com.innovation4you.napking.util.GoogleStaticMapsUrlBuilder
import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener
import java.util.*
import java.util.concurrent.TimeUnit

class RestStopDetailFragment : BaseFragment() {

    @BindView(R.id.fragment_rest_stop_detail_collapsing_toolbar_layout)
    internal lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    @BindView(R.id.fragment_rest_stop_detail_map)
    internal lateinit var mapImageView: ImageView

    @BindView(R.id.fragment_rest_stop_detail_toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.fragment_rest_stop_detail_occupancy_chart_scroll_view)
    internal lateinit var occupancyChartScrollView: HorizontalScrollView

    @BindView(R.id.fragment_rest_stop_detail_occupancy_chart)
    internal lateinit var occupancyChartView: OccupancyChartView

    internal var searchResult: SearchResult? = null
    internal var restStop: RestStop? = null

    internal lateinit var timer: Timer
    internal lateinit var timerTask: TimerTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        if (searchResult == null || restStop == null) {
            activity.finish()
        }

        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if (occupancyChartView != null) {
                    occupancyChartView!!.updateTimeIndicators()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setContentView(R.layout.fragment_rest_stop_detail, inflater, container, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarLayout.title = restStop!!.name

        occupancyChartView.addOnLayoutChangeListener(object : OneTimeLayoutChangeListener() {
            override fun onLayoutChange() {
                occupancyChartScrollView!!.smoothScrollTo(occupancyChartView!!.nowIndicatorX.toInt() - root!!.width / 2, 0)
            }
        })
        occupancyChartView.setup(restStop!!.occupancies, TimeUnit.MINUTES.toMillis(searchResult!!.drivingDuration.toLong()).toInt())

        return root
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onStart() {
        super.onStart()
        try {
            timer.schedule(timerTask, 60000, 60000)
        } catch (e: IllegalStateException) {
            Log.e("TimerTask", "Couldn't start timertask: " + e.message)
        }

    }

    override fun onLayoutFinished() {
        super.onLayoutFinished()

        loadMap()
    }

    private fun loadMap() {
        val builder = GoogleStaticMapsUrlBuilder().apiKey(Cfg.GOOGLE_STATIC_MAPS_API_KEY).center(restStop!!.lat, restStop!!.lng).addMarker(GoogleStaticMapsUrlBuilder.MARKER_COLOR_BLUE, "", restStop!!.lat, restStop!!.lng).zoom(12).sensor(false).size(mapImageView!!.width, mapImageView!!.height)

        Glide.with(activity).load(builder.build()).centerCrop().crossFade().into(mapImageView!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            activity.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        if (arguments != null && arguments.containsKey(KEY_SEARCH_RESULT)) {
            searchResult = arguments.getSerializable(KEY_SEARCH_RESULT) as SearchResult
            if (searchResult != null) {
                restStop = NapKingRepository.get().findRestStopById(searchResult!!.restStopId)
            }
        }
    }

    companion object {

        val KEY_SEARCH_RESULT = "rest_stop_id"

        fun newInstance(args: Bundle): RestStopDetailFragment {
            val f = RestStopDetailFragment()
            f.arguments = args
            return f
        }
    }
}
