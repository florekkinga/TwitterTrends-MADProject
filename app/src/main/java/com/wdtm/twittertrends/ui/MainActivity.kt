package com.wdtm.twittertrends.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView;
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.api.TwitterAPI
import com.wdtm.twittertrends.db.QueryHistory
import com.wdtm.twittertrends.models.Query
import com.wdtm.twittertrends.models.Trend
import java.io.IOException
import kotlin.math.log


// TODO: Extract MapFragment to another class

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    /* location permissions */
    private var isLocationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003
    private val ERROR_DIALOG_REQUEST = 9001
    private val PERMISSIONS_REQUEST_ENABLE_GPS = 9002

    /* buttons */
    private lateinit var recentSearchesButton: Button
    private lateinit var findTrendsButton: Button

    /* map */
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker
    private var isMarker: Boolean = false

    /* search */
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TwitterAPI.init(this)
        QueryHistory.init(this)

        recentSearchesButton = findViewById(R.id.recentButton)
        findTrendsButton = findViewById(R.id.findButton)
        searchView = findViewById(R.id.idSearchView)
        recentSearchesButton.setOnClickListener { showRecentSearches() }
        findTrendsButton.setOnClickListener { showTrends() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView.query.toString()
                val addressList: List<Address>? = null
                if (location != "") {
                    addMarkedInFoundLocation(addressList, location)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setPadding(0, 150, 0, 0)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isCompassEnabled = true

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            getLocationPermission()
            return
        }
        googleMap.isMyLocationEnabled = true

        requestLocationUpdates()
        setMapListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                clearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        isLocationPermissionGranted = false
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                isLocationPermissionGranted = true
            }
        }
    }

    fun addMarker(
        position: LatLng,
        title: String = "",
        tag: String = "tag",
        draggable: Boolean = true,
        visible: Boolean = true
    ) {

        TwitterAPI.fetchLocation(position.latitude.toString(), position.longitude.toString(), {
            if (!isMarker) {
                isMarker = true
            } else {
                marker.remove()
            }
            val snippet = "%.4f, %.4f".format(position.latitude, position.longitude)
            marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .snippet(snippet)
                    .draggable(draggable)
                    .title(it.name)
                    .visible(visible)
            )
            marker.tag = tag
            marker.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
        }, {
            // TODO: Handle error case
        })
    }

    private fun addMarkedInFoundLocation(
        addressList: List<Address>?,
        location: String
    ) {
        var addressList1 = addressList
        val geocoder = Geocoder(this@MainActivity)
        try {
            addressList1 = geocoder.getFromLocationName(location, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!addressList1.isNullOrEmpty()) {
            val address: Address = addressList1[0]
            val latLng = LatLng(address.latitude, address.longitude)
            addMarker(latLng)
        }
    }

    private fun showTrends() {
        val trendsFragment = TrendsFragment.newInstance()
        var trends: Array<Trend>
        if (isMarker) {
            TwitterAPI.fetchLocation(marker.position, { data ->
                TwitterAPI.fetchTrends(data.id, { trendsList ->
                    trends = trendsList.toTypedArray()
                    trendsFragment.loadTrends(trends)
                    trendsFragment.show(supportFragmentManager, "trends_fragment")
                }, {
                    // TODO: Handle error case
                })

                TwitterAPI.fetchQuery(marker.position, { query ->
                    QueryHistory.add(query)
                }, {
                    // TODO: Handle error case
                })
            }, {
                // TODO: Handle error case
            })
        } else {
            Toast.makeText(this, "Add marker to the map", Toast.LENGTH_SHORT).show()
        }
    }

    fun showRecentTrends(trendsList: List<Trend>){
        val trendsFragment = TrendsFragment.newInstance()
        val trends = trendsList.toTypedArray()
        trendsFragment.loadTrends(trends)
        trendsFragment.show(supportFragmentManager, "trends_fragment")
    }

    private fun clearHistory(){
        QueryHistory.clear()
    }

    private fun showRecentSearches() {
        val recentSearchesFragment = RecentSearchesFragment.newInstance()
        var recentSearches: Array<Query> = arrayOf()
        QueryHistory.getAll({ data ->
            recentSearches = data.toTypedArray()
            recentSearches.distinctBy { it.location.name }
            recentSearches.sortByDescending { it.date }
            if(recentSearches.isNotEmpty()) {
                recentSearchesFragment.loadSearchHistory(recentSearches)
                recentSearchesFragment.show(supportFragmentManager, "recent searches fragment")
            }
        }, {
            // TODO: Handle error case
        })
    }

    private fun setMapListener() {
        map.setOnMapLongClickListener(OnMapLongClickListener { latLng ->
            addMarker(latLng)
        })
    }

    private fun requestLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
//                for (location in locationResult.locations) {
//                    // TODO: Update UI with location data
//                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            getLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

        val available =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)

        when {
            available == ConnectionResult.SUCCESS -> {
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                val dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this@MainActivity, available, ERROR_DIALOG_REQUEST)
                dialog?.show()
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
}