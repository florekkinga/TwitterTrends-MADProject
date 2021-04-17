package com.wdtm.twittertrends

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wdtm.twittertrends.api.TwitterAPI


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    /* location permissions */
    private var isLocationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003
    private val ERROR_DIALOG_REQUEST = 9001
    private val PERMISSIONS_REQUEST_ENABLE_GPS = 9002

    /* buttons */
    private lateinit var recentSearchesButton : Button
    private lateinit var findTrendsButton : Button

    /* map */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker
    private var isMarker : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TwitterAPI.init(this)

        TwitterAPI.fetchLocation("37.7821120598956", "-122.400612831116", { data ->
            Log.d("WORKS", data.toString())
        }, {
            Log.d("UPSI", ":(")
        })

        TwitterAPI.fetchTrends("1", { data ->
            Log.d("WORKS", data.toString())
        }, {
            Log.d("UPSI", ":(")
        })

        TwitterAPI.fetchQuery("37.7821120598956", "-122.400612831116", { data ->
            Log.d("WORKS", data.toString())
        }, {
            Log.d("UPSI", ":(")
        })

        recentSearchesButton = findViewById(R.id.recentButton)
        findTrendsButton = findViewById(R.id.findButton)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()

        recentSearchesButton.setOnClickListener { showRecentSearches() }
        findTrendsButton.setOnClickListener { showTrends() }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showTrends() {
        TODO("Not yet implemented")
    }

    private fun showRecentSearches() {
        TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission()
            return
        }
        googleMap.isMyLocationEnabled = true

        requestLocationUpdates()
        setMapListener(googleMap)
    }

    private fun setMapListener(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener(OnMapLongClickListener { latLng ->
            if (!isMarker) {
                isMarker = true
            } else {
                marker.remove()
            }

            marker = googleMap.addMarker(MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("?")
                .snippet("snippet")
                .visible(true))
            marker.tag = "tag"
            marker.showInfoWindow()

            TwitterAPI.fetchLocation(latLng.latitude.toString(), latLng.longitude.toString(), {
                if (!isMarker) {
                    isMarker = true
                } else {
                    marker.remove()
                }

                marker = googleMap.addMarker(MarkerOptions().position(latLng)
                    .draggable(true)
                    .title(it.name)
                    .snippet("snippet - TODO")
                    .visible(true))

                marker.tag = "tag"
                marker.showInfoWindow()
            }, {
                // TODO: Handle error case
            })
        })
    }

    private fun requestLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // TODO: Update UI with location data
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkMapServices()) {
            if (!isLocationPermissionGranted) {
                getLocationPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (!isLocationPermissionGranted) {
                getLocationPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>,
                                            grantResults: IntArray) {
        isLocationPermissionGranted = false
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true
            }
        }
    }

    private fun checkMapServices(): Boolean {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun isMapsEnabled(): Boolean {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            return false
        }
        return true
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val enableGpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS)
                }
        val alert = builder.create()
        alert.show()
    }

    private fun isServicesOK(): Boolean {

        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)

        when {
            available == ConnectionResult.SUCCESS -> {
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this@MainActivity, available, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
}