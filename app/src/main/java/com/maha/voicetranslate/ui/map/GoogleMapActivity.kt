package com.maha.voicetranslate.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.maha.voicetranslate.R
import kotlinx.android.synthetic.main.activity_google_map.*


class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    var mSearchDistance = ""
    var mIsEnableDirections = "0"
    val mZoomLevel = 15f



    var mFragmentList = arrayListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        getBundle()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        setDefaultFun()
    }

    private fun setDefaultFun() {

       // if (mIsEnableDirections == "0") map_navigation_but1.hide()
       // else map_navigation_but1.hide()


        map_navigation_but1.hide()
    }

    private fun getBundle() {
        //val bundle: Bundle? = intent.extras
        //if (bundle != null) {

        val alist=intent.getSerializableExtra("latlng") as ArrayList<LatLng>
        mLatitude = intent.getDoubleExtra("latitude", 0.0)
        mLongitude = intent.getDoubleExtra("longitude", 0.0)

        // }
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

        addmarker(mLatitude, mLongitude)

    }



    private fun addmarker(aLatitude: Double, aLongitude: Double) {
        mMap.clear()
        val markerlocation = LatLng(aLatitude, aLongitude)
        mMap.addMarker(MarkerOptions().position(markerlocation).title("Marker in location"))
     //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerlocation, mZoomLevel))
    }



}
