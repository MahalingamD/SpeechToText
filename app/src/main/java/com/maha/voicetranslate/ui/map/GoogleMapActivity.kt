package com.maha.voicetranslate.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.maha.voicetranslate.R
import com.maha.voicetranslate.model.EventDetail
import kotlinx.android.synthetic.main.activity_google_map.*


class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    var mSearchDistance = ""
    var mIsEnableDirections = "0"
    val mZoomLevel = 15f

    var mEventDetail = arrayListOf<EventDetail>()

    var mFragmentList = arrayListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        getBundle()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }



    private fun getBundle() {
        val bundle: Bundle? = intent.getBundleExtra("BUNDLE")
        if (bundle != null) {
            mEventDetail =  bundle.getSerializable("EventList") as ArrayList<EventDetail>
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isMapToolbarEnabled = true

        addmarker()

    }


    private fun addmarker() {
        try {
            mMap.clear()

            if (!mEventDetail.isNullOrEmpty()) {
                val boundsBuilder: LatLngBounds.Builder = LatLngBounds.Builder()

                mEventDetail.forEach {
                    val aMarkerLocation = LatLng(it.aLatitude, it.alongitude)
                    mMap.addMarker(MarkerOptions().position(aMarkerLocation).title(it.aEventName))

                    boundsBuilder.include(aMarkerLocation)
                }
                val latLngBounds = boundsBuilder.build()

                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen
                val aCameraUpdateFactory =
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding)


                mMap.setOnMapLoadedCallback {
                    mMap.moveCamera(aCameraUpdateFactory)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
