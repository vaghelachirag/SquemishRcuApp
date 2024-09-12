package com.squmish.rcuapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.TestActivityBinding
import com.squmish.rcuapp.view.base.BaseActivity
import com.squmish.rcuapp.view.menu.DashboardFragment
import java.util.Locale


open class ActivityTest: BaseActivity()  {

    private lateinit var binding: TestActivityBinding

    var data : String = ""
 //   private val documentProfileVerificationViewModel by lazy { DocumentProfileVerificationViewModel( this,binding) }


    // Location
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    var geocoder: Geocoder? = null
    var addresses: List<Address>? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    // For Location Request
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CHECK_SETTINGS = 0x1




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener!!) }
        }
    }

    @SuppressLint("DiscouragedPrivateApi", "SimpleDateFormat", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestActivityBinding.inflate(layoutInflater)
     //   binding.viewModel = documentProfileVerificationViewModel
     //   binding.lifecycleOwner = this
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient!!.connect();
        }

        getUserCurrentLocation()

        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    requestGPSSettings()
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()


    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
    }

    private fun requestGPSSettings() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        locationRequest.setInterval(2000)
        locationRequest.setFastestInterval(500)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i("", "All location settings are satisfied.")
                }

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try { status.startResolutionForResult(
                        this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Applicationsett", e.toString())
                    }
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                        "",
                        "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
                    )
                }
            }
        }
    }

    private fun getUserCurrentLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.i("LOCATION", location.toString())
                Log.e("Location",location.toString())

                addresses = geocoder!!.getFromLocation(location.latitude, location.longitude, 1);
                if(!addresses.isNullOrEmpty()){
                  //  DashboardFragment.useraddress = addresses!![0].getAddressLine(0)
                    Log.e("Address",addresses.toString())
                }
                //Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                Log.e("Location","Status Changed")
            }
            override fun onProviderEnabled(provider: String) {
                Log.e("Location","Enable")
            }
            override fun onProviderDisabled(provider: String) {
                Log.e("Location","Disable")
                //  Utility.showLocationAlert(this@ActivityDetail)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            /*  try {
                  mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                      val location: Location? = task.result
                      if (location == null) {

                      } else {

                          currentLat = location.latitude
                          currentLong = location.longitude
                          addresses = geocoder!!.getFromLocation(location.latitude, location.longitude, 1);
                          if (!addresses.isNullOrEmpty()){
                              useraddress = addresses!![0].getAddressLine(0)
                              Log.e("CurrentLocation",location.latitude.toString())
                          }
                      }
                  }
                  locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10f, locationListener!!)
              }catch (_: Exception){

              }*/
        }
    }
}