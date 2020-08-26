package com.idniyal.ojolfirebase.ui.home

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.idniyal.ojolfirebase.R
import com.idniyal.ojolfirebase.utils.GPSTrack
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.StringBuilder
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    var map: GoogleMap? = null

    var tanggal: String? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latAkhir: Double? = null
    var lonAkhir: Double? = null

    var jarak: String? = null
    var dialog: Dialog? = null

    var keyy: String? = null
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout
                .fragment_home, container,
            false
        )
    }

    //menampilkan maps ke fragment
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-6.3088652, 106.682188), 12f
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //menginisialisasi dari mapsview
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }
    }

    //menampilkan lokasi user berdasarkan gps device
    private fun showGPS() {
        val gps = context?.let { GPSTrack(it) }

        if (gps?.canGetLocation()!!) {
            latAwal = gps.latitude
            lonAwal = gps.longitude

            showMarker(latAwal ?: 0.0, lonAwal ?: 0.0, "My location")

            val name = showName(latAwal ?: 0.0, lonAwal ?: 0.0)

            home_awal.text = name

        } else gps.showSettingGPS()

    }


    //GEOCODER
    //menerjemahkan dari koordinat jadi nama lokasi
    private fun showName(lat: Double, lon: Double): String? {
        var name = ""
        var geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            if (addresses.size > 0) {
                val fetchedAddress = addresses.get(0)
                val strAddress = StringBuilder()

                for (i in 0..fetchedAddress.maxAddressLineIndex) {
                    name = strAddress.append(fetchedAddress.getAddressLine(i))
                        .append("").toString()

                }
            }
        } catch (e: Exception) {

        }
        return name
    }

    //menampilkan lokasi menggunakan marker

    //marker origin
    private fun showMainMarker(lat: Double, lon: Double, msg: String){
        val res = context?.resources
        val marker1 = BitmapFactory
            .decodeResource(res, R.drawable.placeholder)
        val smallmarker = Bitmap
            .createScaledBitmap(marker1, 80, 120, false)

        val coordinate = LatLng(lat, lon)

        //membuat pin baru di android
        map?.addMarker(MarkerOptions()
            .position(coordinate)
            .title(msg)
            .icon(BitmapDescriptorFactory.fromBitmap(smallmarker)))

        //mengatur zoom camera
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16f))

        //biar posisi markernya selalu ada di tengah
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))


    }

    //marker destination
    private fun showMarker(lat: Double, lon: Double, msg: String) {
        val coordinat = LatLng(lat, lon)

        map?.addMarker(MarkerOptions()
            .position(coordinat)
            .title(msg))

        map?.animateCamera(CameraUpdateFactory
            .newLatLngZoom(coordinat, 16f))

        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }



    override fun onResume() {
//        keyy?.let { bookingHistoryUser(it) }
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

//    private fun bookingHistoryUser(it: String): Any {
//
//    }

}