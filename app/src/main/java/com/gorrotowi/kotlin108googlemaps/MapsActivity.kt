package com.gorrotowi.kotlin108googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import com.gorrotowi.kotlin108googlemaps.adapters.JacarandaAdapter
import com.gorrotowi.kotlin108googlemaps.entitys.JacarandaItem
import com.gorrotowi.kotlin108googlemaps.entitys.JacarandasDataset
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var jacarandaAdapter: JacarandaAdapter

    private val currentLocation = LatLng(19.402957, -99.154677)
    private var currentSelectMarker: LatLng? = null
    val geoContextClient: GeoApiContext by lazy {
        GeoApiContext.Builder()
            .apiKey("AIzaSyA645cz5C7m8zNgmvM_HtRmOFw0hWi3alA")
            .queryRateLimit(3)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS).build()
    }


    private var circleMarker: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        toolbarMap?.let {
            setSupportActionBar(it)
            supportActionBar?.title = ""
        }

        jacarandaAdapter = JacarandaAdapter()

        jacarandaAdapter.setOnItemClickListener { _, jacarandaItem ->
            focusCameraOnPosition(jacarandaItem.location, true, 17f)

            drawCircleMark(jacarandaItem.location)
            showCardDistance(currentLocation, jacarandaItem.location)
            currentSelectMarker = jacarandaItem.location

            val intenteStreetView = Intent(this@MapsActivity, StreetViewPlaceActivity::class.java)
            intenteStreetView.putExtra("lat", jacarandaItem.location.latitude)
            intenteStreetView.putExtra("lng", jacarandaItem.location.longitude)
            startActivity(intenteStreetView)
        }

        rcListJacarandas?.layoutManager = LinearLayoutManager(
            this@MapsActivity,
            RecyclerView.HORIZONTAL,
            false
        )

        btnItemCardShowGroup?.setOnClickListener {
            showRoute()
        }

        rcListJacarandas?.adapter = jacarandaAdapter


    }

    private fun showRoute() {
        rcListJacarandas?.visibility = View.GONE
        cardDistance?.visibility = View.GONE

        currentSelectMarker?.let { positionToGo ->
            showLineBetweenMarkers(currentLocation, positionToGo)
        }


    }

    private fun showLineBetweenMarkers(initLocation: LatLng, positionToGo: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .title("Inicio")
                .position(initLocation)
        )
        mMap.addMarker(
            MarkerOptions()
                .title("Destino")
                .position(positionToGo)
        )

        val directionsApiCall = DirectionsApi.newRequest(geoContextClient)
            .mode(TravelMode.DRIVING)
            .departureTimeNow()
            .origin(com.google.maps.model.LatLng(initLocation.latitude, initLocation.longitude))
            .destination(com.google.maps.model.LatLng(positionToGo.latitude, positionToGo.longitude))




        GlobalScope.launch(Dispatchers.IO) {
            val results = directionsApiCall.await()
            val pathDirections = results?.routes?.firstOrNull()?.overviewPolyline?.decodePath()
            val directionsLeg = results?.routes?.firstOrNull()?.legs?.firstOrNull()
            val distance = directionsLeg?.distance?.humanReadable
            val duration = directionsLeg?.duration?.humanReadable
            val durationTraffic = directionsLeg?.durationInTraffic?.humanReadable

            val polyDirectionsOptions = PolylineOptions()
                .add(initLocation, positionToGo)

                .width(5f)
                .color(ContextCompat.getColor(this@MapsActivity, R.color.colorAccent))


            pathDirections?.map {
                polyDirectionsOptions?.add(LatLng(it.lat, it.lng))
            }


            launch(Dispatchers.Main){
                mMap.addPolyline(polyDirectionsOptions)
            }
        }
        val latlngBounds = LatLngBounds.builder()
            .include(initLocation)
            .include(positionToGo)
            .build()

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latlngBounds, 100)
        mMap.animateCamera(cameraUpdate)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this@MapsActivity)
            .inflate(R.menu.menu_maps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_search -> {
                initGooglePlaces()
            }

            R.id.action_normal -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }

            R.id.action_hybrid -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }

            R.id.action_terrain -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }

            R.id.action_none -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NONE
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initGooglePlaces() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
//        val autocompleteFilter = AutocompleteSupportFragment().apply {
//            setTypeFilter(TypeFilter.ADDRESS)
//            setTypeFilter(TypeFilter.GEOCODE)
//            setCountry("MX")
//        }

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        ).setTypeFilter(TypeFilter.ADDRESS)
            .setTypeFilter(TypeFilter.GEOCODE)
            .setCountry("MX")
            .build(this@MapsActivity)

        startActivityForResult(intent, 3009)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener(this@MapsActivity)

        checkForLocationPermissions()

        loadMapStyle()

        focusCameraOnPosition(currentLocation)

        updateUISettingMap()

        populateMapJacarandas()

        mMap.setOnCameraIdleListener {
            val currentCameraLocation = mMap.cameraPosition.target
            Log.e("positionIDLE", "${currentCameraLocation?.toString()}")
        }
        mMap.setOnCameraMoveCanceledListener {

        }
        mMap.setOnCameraMoveStartedListener {

        }


    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        Log.d("MarkerClicked", "${marker?.id}")
        Log.d("MarkerClicked", "${marker?.title}")
        Log.d("MarkerClicked", "${marker?.snippet}")
        Log.d("MarkerClicked", "${marker?.position?.toString()}")

        val tagMarker = marker?.tag as? JacarandaItem

        currentSelectMarker = marker?.position

        Log.d("MarkerTag", "${tagMarker?.toString()}")

        val positionMark = tagMarker?.id ?: 0
        Log.d("positiontoscroll", "$positionMark")

        marker?.position?.let { position ->
            drawCircleMark(position)
            showCardDistance(currentLocation, position)
        }

        rcListJacarandas?.scrollToPosition(positionMark)
        jacarandaAdapter.notifyDataSetChanged()


        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3009) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                    Log.e("PlaceResult", "${place?.toString()}")
                    place?.latLng?.let {
                        focusCameraOnPosition(it, true)
                        drawCircleMark(it)
                    }

                }
                Activity.RESULT_CANCELED -> {
                    Log.e("PlaceCanceled", "No Place selected")
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val statusPlace = data?.let { Autocomplete.getStatusFromIntent(it) }
                    Log.e("AutocompleateError", "${statusPlace?.statusMessage}")
                }
            }
        }
    }

    private fun showCardDistance(initLocation: LatLng, destinationLocation: LatLng) {
        val firstLocation = Location("")
        val secondLocation = Location("")

        firstLocation.latitude = initLocation.latitude
        firstLocation.longitude = initLocation.longitude

        secondLocation.latitude = destinationLocation.latitude
        secondLocation.longitude = destinationLocation.longitude


        val distanceFromLocation = firstLocation.distanceTo(secondLocation)

        val distanceArray = FloatArray(3)

        Location.distanceBetween(
            initLocation.latitude,
            initLocation.longitude,
            destinationLocation.latitude,
            destinationLocation.longitude,
            distanceArray
        )

        val distanceFromArray = distanceArray[0]

        val cardMessage = if (distanceFromLocation >= 1000) {
            val distanceKm = distanceFromLocation / 1000
            "La distancia entre tu ubicación y la jacaranda es de $distanceKm km"
        } else {
            "La distancia entre tu ubicación y la jacaranda es de $distanceFromLocation mts"
        }

        txtCardDistance?.text = cardMessage
        cardDistance?.visibility = View.VISIBLE

    }

    private fun drawCircleMark(location: LatLng) {
        val circleOptions = CircleOptions()
            .center(location)
            .radius(20.0)
            .strokeWidth(8f)
            .strokeColor(getColorIntWithAlpha("#d490dd", 1f))
            .fillColor(getColorIntWithAlpha("#d490dd", 0.5f))

        circleMarker?.remove()
        circleMarker = mMap.addCircle(circleOptions)
    }

    private fun populateMapJacarandas() {
        val json = loadJsonFromAssets("dataset/jacarandas.json")
//        Log.v("JsonJacarandas", "$json")
        val any = try {
            val gson = Gson()
            val jacarandasJson = gson.fromJson(json, JacarandasDataset::class.java)
//        Log.i("JsonJacarandas", "${jacarandasJson?.toString()}")

            val jacarandasList = jacarandasJson?.jacarandas?.mapIndexed { index, featuresItem ->
                val jacanrandaLatLng = LatLng(
                    featuresItem?.geometry?.Y ?: 0.0,
                    featuresItem?.geometry?.X ?: 0.0
                )
                JacarandaItem(index, jacanrandaLatLng)
            }

            jacarandasList?.let {
                jacarandaAdapter.dataSource = it
            }

            jacarandasList?.mapIndexed { index, jacarandaItem ->

                val snippetString =
                    "Jacaranda position : ${jacarandaItem.location.latitude},${jacarandaItem.location.longitude}"

                val markerOptions = MarkerOptions()
                    .position(jacarandaItem.location)
//                    .title("Marker ${jacarandaItem.id}")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_jacaranda_purple))
//                    .snippet(snippetString)
                val marker = mMap.addMarker(markerOptions)
                marker.tag = jacarandaItem

            }

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("GSON", "Gson cannot handle json")
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkForLocationPermissions() {
        KotlinPermissions.with(this@MapsActivity)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .onAccepted {
                mMap.isMyLocationEnabled = true
            }
            .onDenied {
                Toast.makeText(
                    this@MapsActivity,
                    "Location not available",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .ask()
    }

    private fun updateUISettingMap() {
        val uiSettingMap = mMap.uiSettings
        uiSettingMap?.apply {
            isMapToolbarEnabled = false
            isZoomControlsEnabled = true
            isCompassEnabled = false
            isZoomGesturesEnabled = true
            isIndoorLevelPickerEnabled = false
            isMyLocationButtonEnabled = false
        }
    }

    private fun loadMapStyle() {
        try {
            mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.google_map_dark_style)
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
    }

    private fun focusCameraOnPosition(location: LatLng, animate: Boolean = false, zoom: Float = 16f) {
        val cameraPosition = CameraPosition.builder()
            .target(location)
            .zoom(zoom)
//            .bearing(180f)
//            .tilt(90f)
            .build()

        if (animate) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    fun loadJsonFromAssets(path: String): String? {
        return try {
            val inputStream = assets.open(path)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun getColorIntWithAlpha(hexColor: String, alpha: Float): Int =
        Color.parseColor(hexColor.alphaColor(alpha))

    fun String.alphaColor(percentage: Float = 0f): String = if (percentage < 0 || percentage > 1) {
        fail("The percentage need to be a float number between 0.0 and 1.0")
    } else {
        val alphInt = ((percentage * 255) + 0.000001).roundToInt()
        val alphaHex = String.format("%02X", alphInt)
        this.replace("#", "#$alphaHex")
    }

    fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }

}
