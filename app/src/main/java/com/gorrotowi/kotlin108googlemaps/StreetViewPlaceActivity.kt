package com.gorrotowi.kotlin108googlemaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_street_view_place.*

class StreetViewPlaceActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {


    private var currenPosition:LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view_place)

        intent?.extras?.let {bundle ->
            val lat = bundle.getDouble("lat",Double.MIN_VALUE)
            val lng = bundle.getDouble("lng",Double.MIN_VALUE)

            if (lat != Double.MIN_VALUE && lng != Double.MIN_VALUE){
                currenPosition = LatLng(lat,lng)
            }else{
                Toast.makeText(this@StreetViewPlaceActivity, "Algo ocurriò mal", Toast.LENGTH_LONG).show()
                finish()
            }

        }
        placeStreetView?.onCreate(savedInstanceState)
        placeStreetView.getStreetViewPanoramaAsync(this)
    }
    override fun onStreetViewPanoramaReady(streetViewPAnorama: StreetViewPanorama?) {
        streetViewPAnorama?.setPosition(currenPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        placeStreetView?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        placeStreetView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        placeStreetView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        placeStreetView?.onStop()
    }

    override fun onResume() {
        super.onResume()
        placeStreetView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        placeStreetView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        placeStreetView?.onSaveInstanceState(outState)
    }

}
