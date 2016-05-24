package com.innovation4you.napking.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.innovation4you.napking.R
import com.innovation4you.napking.model.OccupancyEntry
import com.innovation4you.napking.util.OccupancyColorHelper
import com.innovation4you.napking.util.platform.DimensionUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class OccupancyChartView : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private val timeIndicatorFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
    private lateinit var occupancyColorHelper: OccupancyColorHelper

    private var occupancyEntries: List<OccupancyEntry>? = null
    private var drivingDuration: Int = 0

    private var barWidth: Float = 0f
    private var barSpacing: Float = 0f
    private var baseLineWidth: Float = 0f
    private var smallIndicatorHeight: Float = 0f
    private var bigIndicatorHeight: Float = 0f
    private lateinit var barPaint: Paint
    private lateinit var baseLinePaint: Paint
    private lateinit var indicatorPaint: Paint
    private lateinit var timeLabels: MutableList<Label>
    private lateinit var indicators: FloatArray
    private var timeLabelHeight: Float = 0f
    private var indicatorLabelHeight: Float = 0f
    public var nowIndicatorX: Float = 0f
        private set
    private var arrivalIndicatorX: Float = 0f
    private var contentSpace: Float = 0f
    private lateinit var indicatorLabels: Array<Label?>
    private var indicatorTextTextSize: Float = 0f
    private var indicatorTimeTextSize: Float = 0f
    private var baseLineY: Float = 0f
    private lateinit var weekDayTexts: Array<String>

    private fun init() {
        occupancyColorHelper = OccupancyColorHelper(context)

        barWidth = DimensionUtils.convertDipToPixel(16f, context)
        barSpacing = DimensionUtils.convertDipToPixel(2f, context)
        baseLineWidth = DimensionUtils.convertDipToPixel(2f, context)
        smallIndicatorHeight = DimensionUtils.convertDipToPixel(4f, context)
        bigIndicatorHeight = DimensionUtils.convertDipToPixel(8f, context)
        contentSpace = resources.getDimension(R.dimen.content) * 1.5f

        barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        barPaint.strokeWidth = barWidth

        baseLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        baseLinePaint.strokeWidth = baseLineWidth
        baseLinePaint.textSize = DimensionUtils.convertSpToPixels(10f, context)
        baseLinePaint.color = ContextCompat.getColor(context, R.color.occupancy_chart_base_line)

        indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        indicatorPaint.strokeWidth = baseLineWidth
        indicatorTextTextSize = DimensionUtils.convertSpToPixels(12f, context)
        indicatorTimeTextSize = DimensionUtils.convertSpToPixels(16f, context)
        indicatorPaint.textSize = indicatorTimeTextSize

        weekDayTexts = context.resources.getStringArray(R.array.weekdays)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int
        if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.UNSPECIFIED && occupancyEntries != null) {
            width = (occupancyEntries!!.size * (barWidth + barSpacing) - barSpacing).toInt()
        } else {
            width = View.MeasureSpec.getSize(widthMeasureSpec)
        }

        setMeasuredDimension(width, View.MeasureSpec.getSize(heightMeasureSpec))
        compile()
    }

    private fun compile() {
        if (occupancyEntries != null && !occupancyEntries!!.isEmpty()) {
            timeLabels = ArrayList<Label>(occupancyEntries!!.size + 7)
            indicators = FloatArray(occupancyEntries!!.size * 4)
            baseLineY = height * 0.52f

            // Calculate time labels text height
            val rect = Rect()
            baseLinePaint.getTextBounds("TEST", 0, 4, rect)
            timeLabelHeight = rect.height().toFloat()

            // Calculate indicator labels text height
            indicatorPaint.getTextBounds("TEST", 0, 4, rect)
            indicatorLabelHeight = rect.height().toFloat()

            var oe: OccupancyEntry
            var lastWeekDay = -1
            var text: String
            var x: Float
            var y: Float
            var indicatorPos = 0
            var labelCenter: Float
            var indicatorHeight: Float

            for (i in occupancyEntries!!.indices) {
                oe = occupancyEntries!![i]
                labelCenter = i * (barWidth + barSpacing) + barWidth + barSpacing / 2

                // Calculate indicators
                indicators[indicatorPos] = labelCenter
                indicators[indicatorPos + 1] = baseLineY + baseLineWidth / 2
                indicators[indicatorPos + 2] = labelCenter
                indicatorHeight = smallIndicatorHeight

                // Calculate weekday label
                if (lastWeekDay != oe.weekDay) {
                    indicatorHeight = bigIndicatorHeight
                    text = getWeekDayText(oe.weekDay)
                    x = labelCenter - baseLinePaint.measureText(text) / 2
                    y = baseLineY + baseLineWidth * 3 + timeLabelHeight * 2 + bigIndicatorHeight
                    timeLabels.add(Label(text, x, y))
                    lastWeekDay = oe.weekDay
                }

                // Calculate hour label
                if (i % SKIP_LABEL_COUNT == 0) {
                    text = getHourText(oe)
                    x = labelCenter - baseLinePaint.measureText(text) / 2
                    y = baseLineY + baseLineWidth * 2 + timeLabelHeight + bigIndicatorHeight
                    timeLabels.add(Label(text, x, y))
                }

                indicators[indicatorPos + 3] = indicators[indicatorPos + 1] + indicatorHeight
                indicatorPos += 4
            }

            compileTimeIndicators()
        }
    }

    private fun compileTimeIndicators() {
        indicatorLabels = arrayOfNulls<Label>(4)
        val now = GregorianCalendar()
        nowIndicatorX = calculateTimeIndicatorPosition(now)

        var text = timeIndicatorFormatter.format(now.time)
        indicatorPaint.textSize = indicatorTimeTextSize
        indicatorLabels[0] = Label(text, nowIndicatorX - indicatorPaint.measureText(text) / 2, height.toFloat() - indicatorLabelHeight * 2 - 25f)
        text = resources.getString(R.string.now)
        indicatorPaint.textSize = indicatorTextTextSize
        indicatorLabels[1] = Label(text, nowIndicatorX - indicatorPaint.measureText(text) / 2, height - indicatorLabelHeight)

        now.add(Calendar.MILLISECOND, drivingDuration)
        arrivalIndicatorX = calculateTimeIndicatorPosition(now)

        text = timeIndicatorFormatter.format(now.time)
        indicatorPaint.textSize = indicatorTimeTextSize
        indicatorLabels[2] = Label(text, arrivalIndicatorX - indicatorPaint.measureText(text) / 2, height.toFloat() - indicatorLabelHeight * 2 - 25f)
        text = resources.getString(R.string.arrival)
        indicatorPaint.textSize = indicatorTextTextSize
        indicatorLabels[3] = Label(text, arrivalIndicatorX - indicatorPaint.measureText(text) / 2, height - indicatorLabelHeight)
    }

    private fun calculateTimeIndicatorPosition(time: Calendar): Float {
        return ((time.get(Calendar.DAY_OF_WEEK) + 5) % 7 * 24 + time.get(Calendar.HOUR_OF_DAY)) * (barWidth + barSpacing) + time.get(Calendar.MINUTE) * (barWidth / 60f)
    }

    private fun getHourText(oe: OccupancyEntry): String {
        if (oe.hour == 0) {
            return "24"
        }
        return oe.hour.toString()
    }

    private fun getWeekDayText(weekDay: Int): String {
        return weekDayTexts[weekDay]
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (occupancyEntries != null && !occupancyEntries!!.isEmpty()) {

            // Draw bars
            var oe: OccupancyEntry
            var x: Float
            for (i in occupancyEntries!!.indices) {
                oe = occupancyEntries!![i]
                x = i * (barWidth + barSpacing) + barWidth / 2

                barPaint.color = occupancyColorHelper.getOccupancyColor(oe.occupancy)
                barPaint.alpha = if (x <= nowIndicatorX) 100 else 255
                canvas.drawLine(x, baseLineY, x, baseLineY - baseLineY * (oe.occupancy / 100f), barPaint)

                // Check if arrivalIndicator is on the bar
                if (x - barWidth / 2 <= arrivalIndicatorX && x + barWidth / 2 >= arrivalIndicatorX) {
                    indicatorPaint.color = barPaint.color
                }
            }

            // Draw baseline
            val baseLineMiddle = baseLineY + baseLineWidth / 2
            canvas.drawLine(0f, baseLineMiddle, width.toFloat(), baseLineMiddle, baseLinePaint)
            canvas.drawLines(indicators, baseLinePaint)

            // Draw timeLabels
            var label: Label
            for (i in timeLabels.indices) {
                label = timeLabels[i]
                drawLabel(canvas, label, baseLinePaint)
            }

            // Draw arrival time indicator
            canvas.drawLine(arrivalIndicatorX, 0f, arrivalIndicatorX, indicatorLabels[2]!!.y - contentSpace, indicatorPaint)
            indicatorPaint.textSize = indicatorTimeTextSize
            drawLabel(canvas, indicatorLabels[2]!!, indicatorPaint)
            indicatorPaint.textSize = indicatorTextTextSize
            drawLabel(canvas, indicatorLabels[3]!!, indicatorPaint)

            // Draw current time indicator
            indicatorPaint.color = baseLinePaint.color
            canvas.drawLine(nowIndicatorX, 0f, nowIndicatorX, indicatorLabels[0]!!.y - contentSpace, indicatorPaint)
            indicatorPaint.textSize = indicatorTimeTextSize
            drawLabel(canvas, indicatorLabels[0]!!, indicatorPaint)
            indicatorPaint.textSize = indicatorTextTextSize
            drawLabel(canvas, indicatorLabels[1]!!, indicatorPaint)
        }
    }

    private fun drawLabel(canvas: Canvas, label: Label, paint: Paint) {
        canvas.drawText(label.text, label.x, label.y, paint)
    }

    fun setup(occupancyEntries: List<OccupancyEntry>, drivingDuration: Int) {
        this.occupancyEntries = occupancyEntries
        this.drivingDuration = drivingDuration
        Collections.sort(this.occupancyEntries!!)
        requestLayout()
    }

    fun updateTimeIndicators() {
        compileTimeIndicators()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private class Label(val text: String, val x: Float, val y: Float)

    companion object {

        val SKIP_LABEL_COUNT = 3
    }
}
