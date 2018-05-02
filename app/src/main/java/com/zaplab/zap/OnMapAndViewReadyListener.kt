package com.zaplab.zap

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment



/**
 * Created by Ramshad on 4/27/18.
 */
class OnMapAndViewReadyListener(
        private val mapFragment: SupportMapFragment, private val devCallback: OnGlobalLayoutAndMapReadyListener) : OnGlobalLayoutListener, OnMapReadyCallback {
    private val mapView: View?

    private var isViewReady: Boolean = false
    private var isMapReady: Boolean = false
    private var googleMap: GoogleMap? = null

    /** A listener that needs to wait for both the GoogleMap and the View to be initialized.  */
    interface OnGlobalLayoutAndMapReadyListener {
        fun onMapReady(googleMap: GoogleMap?)
    }

    init {
        mapView = mapFragment.view
        isViewReady = false
        isMapReady = false
        googleMap = null

        registerListeners()
    }

    private fun registerListeners() {
        // View layout.
        if (mapView!!.getWidth() !== 0 && mapView!!.getHeight() !== 0) {
            // View has already completed layout.
            isViewReady = true
        } else {
            // Map has not undergone layout, register a View observer.
            mapView!!.getViewTreeObserver().addOnGlobalLayoutListener(this)
        }

        // GoogleMap. Note if the GoogleMap is already ready it will still fire the callback later.
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // NOTE: The GoogleMap API specifies the listener is removed just prior to invocation.
        this.googleMap = googleMap
        isMapReady = true
        fireCallbackIfReady()
    }

    // We use the new method when supported
    @SuppressLint("NewApi")  // We check which build version we are using.
    override fun onGlobalLayout() {
        // Remove our listener.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mapView!!.getViewTreeObserver().removeGlobalOnLayoutListener(this)
        } else {
            mapView!!.getViewTreeObserver().removeOnGlobalLayoutListener(this)
        }
        isViewReady = true
        fireCallbackIfReady()
    }

    private fun fireCallbackIfReady() {
        if (isViewReady && isMapReady) {
            devCallback.onMapReady(googleMap)
        }
    }
}
