package com.innovation4you.napking.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Locale;

/**
 * A builder which supports creating an url for static Google maps images
 *
 * @author danielamerbauer
 */
public class GoogleStaticMapsUrlBuilder {

	private static final String BASE_URL = "http://maps.googleapis.com/maps/api/staticmap?";

	// URL separators
	private static final char URL_PARAMETER_SEPERATOR = '&';
	private static final char URL_KEY_VALUE_SEPERATOR = '=';
	private static final char MARKER_PARAMETER_SEPERATOR = '|';
	private static final char MARKER_KEY_VALUE_SEPERATOR = ':';
	private static final char SIZE_PARAMETER_SEPERATOR = 'x';

	// URL paramter keys
	private static final String API_KEY_KEY = "key";
	private static final String CENTER_KEY = "center";
	private static final String SIZE_KEY = "size";
	private static final String SENSOR_KEY = "sensor";
	private static final String LANGUAGE_KEY = "language";
	private static final String MARKERS_KEY = "markers";
	private static final String MARKER_COLOR_KEY = "color";
	private static final String MARKER_LABEL_KEY = "label";
	private static final String ZOOM_KEY = "zoom";
	private static final String SCALE_KEY = "scale";

	public static final String MARKER_COLOR_BLUE = "blue";
	public static final String MARKER_COLOR_RED = "red";
	public static final String MARKER_COLOR_GREEN = "green";
	public static final String MARKER_COLOR_ORANGE = "orange";
	public static final String MARKER_COLOR_PURPLE = "purple";

	public static String[] MARKER_COLORS = new String[]{MARKER_COLOR_BLUE, MARKER_COLOR_ORANGE, MARKER_COLOR_GREEN, MARKER_COLOR_RED,
			MARKER_COLOR_PURPLE};

	private StringBuilder urlBuilder;

	public GoogleStaticMapsUrlBuilder() {
		urlBuilder = new StringBuilder();
	}

	public GoogleStaticMapsUrlBuilder center(final String address) {
		appendUrlParameter(CENTER_KEY, address);
		return this;
	}

	public GoogleStaticMapsUrlBuilder center(final double lat, final double lng) {
		appendUrlParameter(CENTER_KEY, formatCoordinates(lat, lng));
		return this;
	}

	@NonNull
	private String formatCoordinates(double lat, double lng) {
		return String.format(Locale.ENGLISH, "%.6f", lat) + "," + String.format(Locale.ENGLISH, "%.6f", lng);
	}

	public GoogleStaticMapsUrlBuilder size(final int width, final int height) {
		final String sizeValue = String.valueOf(width) + SIZE_PARAMETER_SEPERATOR + String.valueOf(height);
		appendUrlParameter(SIZE_KEY, sizeValue);
		return this;
	}

	public GoogleStaticMapsUrlBuilder apiKey(final String apiKey) {
		appendUrlParameter(API_KEY_KEY, apiKey);
		return this;
	}

	public GoogleStaticMapsUrlBuilder sensor(final boolean sensor) {
		appendUrlParameter(SENSOR_KEY, String.valueOf(sensor));
		return this;
	}

	public GoogleStaticMapsUrlBuilder zoom(int zoomLevel) {
		appendUrlParameter(ZOOM_KEY, String.valueOf(zoomLevel));
		return this;
	}

	public GoogleStaticMapsUrlBuilder scale(int scaleLevel) {
		appendUrlParameter(SCALE_KEY, String.valueOf(scaleLevel));
		return this;
	}

	public GoogleStaticMapsUrlBuilder useSystemLanguage() {
		// Use default system language
		appendUrlParameter(LANGUAGE_KEY, Locale.getDefault().getLanguage());
		return this;
	}

	public GoogleStaticMapsUrlBuilder language(String languageCode) {
		appendUrlParameter(LANGUAGE_KEY, languageCode);
		return this;
	}

	public GoogleStaticMapsUrlBuilder addMarker(final int color, final String label, final String address) {
		return addMarker(String.format("0x%06X", (0xFFFFFF & color)), label, address);
	}

	public GoogleStaticMapsUrlBuilder addMarker(final String color, final String label, final double lat, final double lng) {
		return addMarker(color, label, formatCoordinates(lat, lng));
	}

	public GoogleStaticMapsUrlBuilder addMarker(final String color, final String label, final String position) {
		final StringBuilder markersValueBuilder = new StringBuilder();

		// Append color
		markersValueBuilder.append(MARKER_COLOR_KEY);
		markersValueBuilder.append(MARKER_KEY_VALUE_SEPERATOR);
		markersValueBuilder.append(color);

		if (!TextUtils.isEmpty(label)) {
			// Append label
			markersValueBuilder.append(MARKER_PARAMETER_SEPERATOR);
			markersValueBuilder.append(MARKER_LABEL_KEY);
			markersValueBuilder.append(MARKER_KEY_VALUE_SEPERATOR);
			markersValueBuilder.append(label);
		}

		// Append position
		markersValueBuilder.append(MARKER_PARAMETER_SEPERATOR);
		markersValueBuilder.append(position);

		appendUrlParameter(MARKERS_KEY, markersValueBuilder.toString());
		return this;
	}

	public String build() {
		return Uri.parse(BASE_URL + urlBuilder.toString().replace(" ", "+")).toString();
	}

	private void appendUrlParameter(final String key, final String value) {
		urlBuilder.append(URL_PARAMETER_SEPERATOR);
		urlBuilder.append(key);
		urlBuilder.append(URL_KEY_VALUE_SEPERATOR);
		urlBuilder.append(value);
	}
}
