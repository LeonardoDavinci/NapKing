package com.innovation4you.napking.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.innovation4you.napking.R;
import com.innovation4you.napking.model.OccupancyEntry;
import com.innovation4you.napking.util.platform.DimensionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class OccupancyChartView extends View {

	private float baseLineY;
	private String[] weekDayTexts;
	public static final int SKIP_LABEL_COUNT = 3;

	public OccupancyChartView(Context context) {
		super(context);
		init();
	}

	public OccupancyChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OccupancyChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public OccupancyChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	List<OccupancyEntry> occupancyEntries;
	float barWidth, barSpacing;
	float baseLineWidth, smallIndicatorHeight, bigIndicatorHeight;
	Paint barPaint, baseLinePaint;
	int[] occupancyLevelColors = new int[3];
	List<Label> labels;
	float[] indicators;
	float labelHeight;
	float nowIndicatorX;

	private void init() {
		barWidth = DimensionUtils.convertDipToPixel(16, getContext());
		barSpacing = DimensionUtils.convertDipToPixel(4, getContext());
		baseLineWidth = DimensionUtils.convertDipToPixel(2, getContext());
		smallIndicatorHeight = DimensionUtils.convertDipToPixel(4, getContext());
		bigIndicatorHeight = DimensionUtils.convertDipToPixel(8, getContext());

		occupancyLevelColors[0] = ContextCompat.getColor(getContext(), R.color.occupancy_level_low);
		occupancyLevelColors[1] = ContextCompat.getColor(getContext(), R.color.occupancy_level_middle);
		occupancyLevelColors[2] = ContextCompat.getColor(getContext(), R.color.occupancy_level_high);

		barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setStrokeWidth(barWidth);

		baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		baseLinePaint.setStrokeWidth(baseLineWidth);
		baseLinePaint.setTextSize(DimensionUtils.convertSpToPixels(10, getContext()));
		baseLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.occupancy_chart_base_line));

		weekDayTexts = getContext().getResources().getStringArray(R.array.weekdays);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width;
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED && occupancyEntries != null) {
			width = (int) (occupancyEntries.size() * (barWidth + barSpacing) - barSpacing);
		} else {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}

		setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
		compile();
	}

	private void compile() {
		if (occupancyEntries != null && !occupancyEntries.isEmpty()) {
			labels = new ArrayList<>(occupancyEntries.size() + 7);
			indicators = new float[occupancyEntries.size() * 4];
			baseLineY = getHeight() * 0.7f;

			// Calculate text height
			final Rect rect = new Rect();
			baseLinePaint.getTextBounds("TEST", 0, 4, rect);
			labelHeight = rect.height();

			OccupancyEntry oe;
			int lastWeekDay = -1;
			String text;
			float x, y;
			int indicatorPos = 0;
			float labelCenter;
			float indicatorHeight;

			for (int i = 0; i < occupancyEntries.size(); i++) {
				oe = occupancyEntries.get(i);
				labelCenter = i * (barWidth + barSpacing) + barWidth + barSpacing / 2;

				// Calculate indicators
				indicators[indicatorPos] = labelCenter;
				indicators[indicatorPos + 1] = baseLineY + baseLineWidth / 2;
				indicators[indicatorPos + 2] = labelCenter;
				indicatorHeight = smallIndicatorHeight;

				// Calculate weekday label
				if (lastWeekDay != oe.weekDay) {
					indicatorHeight = bigIndicatorHeight;
					text = getWeekDayText(oe.weekDay);
					x = labelCenter - baseLinePaint.measureText(text) / 2;
					y = baseLineY + baseLineWidth * 3 + labelHeight * 2 + bigIndicatorHeight;
					labels.add(new Label(text, x, y));
					lastWeekDay = oe.weekDay;
				}

				// Calculate hour label
				if (i % SKIP_LABEL_COUNT == 0) {
					text = getHourText(oe);
					x = labelCenter - baseLinePaint.measureText(text) / 2;
					y = baseLineY + baseLineWidth * 2 + labelHeight + bigIndicatorHeight;
					labels.add(new Label(text, x, y));
				}

				indicators[indicatorPos + 3] = indicators[indicatorPos + 1] + indicatorHeight;
				indicatorPos += 4;
			}

			calculateNowIndicatorPosition();
		}
	}

	private void calculateNowIndicatorPosition() {
		final Calendar now = new GregorianCalendar();
		nowIndicatorX = ((now.get(Calendar.DAY_OF_WEEK) - 2) * 24 + now.get(Calendar.HOUR_OF_DAY) + 1) * (barWidth + barSpacing)
				+ now.get(Calendar.MINUTE) * (barWidth / 60f);
	}

	@NonNull
	private String getHourText(OccupancyEntry oe) {
		if (oe.hour == 0) {
			oe.hour = 24;
		}
		return String.valueOf(oe.hour);
	}

	private String getWeekDayText(final int weekDay) {
		return weekDayTexts[weekDay];
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (occupancyEntries != null && !occupancyEntries.isEmpty()) {

			// Draw bars
			OccupancyEntry oe;
			float x;
			for (int i = 0; i < occupancyEntries.size(); i++) {
				oe = occupancyEntries.get(i);
				x = i * (barWidth + barSpacing) + barWidth / 2;
				barPaint.setColor(getOccupancyColor(oe.occupancy));
				canvas.drawLine(x, baseLineY, x, baseLineY - baseLineY * (oe.occupancy / 100f), barPaint);
			}

			// Draw baseline
			final float baseLineMiddle = baseLineY + baseLineWidth / 2;
			canvas.drawLine(0f, baseLineMiddle, getWidth(), baseLineMiddle, baseLinePaint);
			canvas.drawLines(indicators, baseLinePaint);

			// Draw labels
			Label label;
			for (int i = 0; i < labels.size(); i++) {
				label = labels.get(i);
				canvas.drawText(label.text, label.x, label.y, baseLinePaint);
			}

			// Draw current time indicator
			canvas.drawLine(nowIndicatorX, 0, nowIndicatorX, getHeight(), baseLinePaint);
		}
	}

	private int getOccupancyColor(final int occupancy) {
		int level = 0;
		if (occupancy >= 65) {
			level = 2;
		} else if (occupancy >= 35) {
			level = 1;
		}
		return occupancyLevelColors[level];
	}

	public void setOccupancyEntries(List<OccupancyEntry> occupancyEntries) {
		this.occupancyEntries = occupancyEntries;
		Collections.sort(this.occupancyEntries);
		requestLayout();
	}

	private static class Label {

		public final String text;
		public final float x, y;

		public Label(String text, float x, float y) {
			this.text = text;
			this.x = x;
			this.y = y;
		}
	}
}
