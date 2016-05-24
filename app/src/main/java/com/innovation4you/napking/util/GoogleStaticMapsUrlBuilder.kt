package com.innovation4you.napking.util

import android.net.Uri
import android.text.TextUtils
import java.util.*

/**
 * A builder which supports creating an url for static Google maps images

 * @author danielamerbauer
 */
class GoogleStaticMapsUrlBuilder {

    private val urlBuilder: StringBuilder

    init {
        urlBuilder = StringBuilder()
    }

    fun center(address: String): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(CENTER_KEY, address)
        return this
    }

    fun center(lat: Double, lng: Double): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(CENTER_KEY, formatCoordinates(lat, lng))
        return this
    }

    private fun formatCoordinates(lat: Double, lng: Double): String {
        return String.format(Locale.ENGLISH, "%.6f", lat) + "," + String.format(Locale.ENGLISH, "%.6f", lng)
    }

    fun size(width: Int, height: Int): GoogleStaticMapsUrlBuilder {
        val sizeValue = width.toString() + SIZE_PARAMETER_SEPERATOR + height.toString()
        appendUrlParameter(SIZE_KEY, sizeValue)
        return this
    }

    fun apiKey(apiKey: String): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(API_KEY_KEY, apiKey)
        return this
    }

    fun sensor(sensor: Boolean): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(SENSOR_KEY, sensor.toString())
        return this
    }

    fun zoom(zoomLevel: Int): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(ZOOM_KEY, zoomLevel.toString())
        return this
    }

    fun scale(scaleLevel: Int): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(SCALE_KEY, scaleLevel.toString())
        return this
    }

    fun useSystemLanguage(): GoogleStaticMapsUrlBuilder {
        // Use default system language
        appendUrlParameter(LANGUAGE_KEY, Locale.getDefault().language)
        return this
    }

    fun language(languageCode: String): GoogleStaticMapsUrlBuilder {
        appendUrlParameter(LANGUAGE_KEY, languageCode)
        return this
    }

    fun addMarker(color: Int, label: String, address: String): GoogleStaticMapsUrlBuilder {
        return addMarker(String.format("0x%06X", 0xFFFFFF and color), label, address)
    }

    fun addMarker(color: String, label: String, lat: Double, lng: Double): GoogleStaticMapsUrlBuilder {
        return addMarker(color, label, formatCoordinates(lat, lng))
    }

    fun addMarker(color: String, label: String, position: String): GoogleStaticMapsUrlBuilder {
        val markersValueBuilder = StringBuilder()

        // Append color
        markersValueBuilder.append(MARKER_COLOR_KEY)
        markersValueBuilder.append(MARKER_KEY_VALUE_SEPERATOR)
        markersValueBuilder.append(color)

        if (!TextUtils.isEmpty(label)) {
            // Append label
            markersValueBuilder.append(MARKER_PARAMETER_SEPERATOR)
            markersValueBuilder.append(MARKER_LABEL_KEY)
            markersValueBuilder.append(MARKER_KEY_VALUE_SEPERATOR)
            markersValueBuilder.append(label)
        }

        // Append position
        markersValueBuilder.append(MARKER_PARAMETER_SEPERATOR)
        markersValueBuilder.append(position)

        appendUrlParameter(MARKERS_KEY, markersValueBuilder.toString())
        return this
    }

    fun build(): String {
        return Uri.parse(BASE_URL + urlBuilder.toString().replace(" ", "+")).toString()
    }

    private fun appendUrlParameter(key: String, value: String) {
        urlBuilder.append(URL_PARAMETER_SEPERATOR)
        urlBuilder.append(key)
        urlBuilder.append(URL_KEY_VALUE_SEPERATOR)
        urlBuilder.append(value)
    }

    companion object {

        private val BASE_URL = "http://maps.googleapis.com/maps/api/staticmap?"

        // URL separators
        private val URL_PARAMETER_SEPERATOR = '&'
        private val URL_KEY_VALUE_SEPERATOR = '='
        private val MARKER_PARAMETER_SEPERATOR = '|'
        private val MARKER_KEY_VALUE_SEPERATOR = ':'
        private val SIZE_PARAMETER_SEPERATOR = 'x'

        // URL paramter keys
        private val API_KEY_KEY = "key"
        private val CENTER_KEY = "center"
        private val SIZE_KEY = "size"
        private val SENSOR_KEY = "sensor"
        private val LANGUAGE_KEY = "language"
        private val MARKERS_KEY = "markers"
        private val MARKER_COLOR_KEY = "color"
        private val MARKER_LABEL_KEY = "label"
        private val ZOOM_KEY = "zoom"
        private val SCALE_KEY = "scale"

        val MARKER_COLOR_BLUE = "blue"
        val MARKER_COLOR_RED = "red"
        val MARKER_COLOR_GREEN = "green"
        val MARKER_COLOR_ORANGE = "orange"
        val MARKER_COLOR_PURPLE = "purple"

        var MARKER_COLORS = arrayOf(MARKER_COLOR_BLUE, MARKER_COLOR_ORANGE, MARKER_COLOR_GREEN, MARKER_COLOR_RED, MARKER_COLOR_PURPLE)
    }
}
