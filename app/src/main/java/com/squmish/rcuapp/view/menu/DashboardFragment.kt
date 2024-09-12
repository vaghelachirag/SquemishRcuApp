package com.squmish.rcuapp.view.menu

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
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
import com.squmish.rcuapp.databinding.DashboardFragmentBinding
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.squmish.rcuapp.model.pendingRequest.GetPendingRequestData
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Session.Companion.DATA
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.view.detail.ActivityDetail
import com.squmish.rcuapp.view.detail.ActivityDetail.Companion.useraddress
import com.squmish.rcuapp.viewmodel.DashboardViewModel
import java.util.Locale


class DashboardFragment: BaseFragment() {

    private var _binding: DashboardFragmentBinding? = null

    private val binding get() = _binding!!
    private val dashboardViewModel by lazy { DashboardViewModel(activity as Context,this@DashboardFragment,binding) }

    // Location
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    var geocoder: Geocoder? = null
    var addresses: List<Address>? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    // For Location Request
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CHECK_SETTINGS = 0x1

    companion object {
        public  var currentLat : Double = 0.0
        public  var currentLong : Double = 0.0
        public  var useraddress : String = ""
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener!!) }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient!!.connect();
        }

        getUserCurrentLocation()

        Dexter.withActivity(requireActivity())
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    requestGPSSettings()
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()


        dashboardViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        dashboardViewModel.totalVerification.observeForever {
            binding.totalVerificationCount.text = dashboardViewModel.totalVerification.value.toString()
        }

        binding.refreshLayout.setOnRefreshListener {
            dashboardViewModel.getPendingRequest()
        }

        return binding.root
    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(requireActivity())
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
                        requireActivity(), REQUEST_CHECK_SETTINGS)
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

    fun  redirectToDetailScreen(getPendingRequestData: GetPendingRequestData) {
        val bundle = Bundle()
        bundle.putSerializable(DATA, getPendingRequestData.getFirequestId())
        AppConstants.verificationId = getPendingRequestData.getFirequestId()!!
        //findNavController().navigate(R.id.action_DashboardFragment_to_FragmentDetail)

        val iDetain = Intent(context, ActivityDetail::class.java)
        startActivity(iDetain)
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.init(requireContext())
    }

    private fun getUserCurrentLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.i("LOCATION", location.toString())
                Log.e("Location",location.toString())
                currentLat = location.latitude
                currentLong = location.longitude
                addresses = geocoder!!.getFromLocation(location.latitude, location.longitude, 1);
                if(!addresses.isNullOrEmpty()){
                    useraddress = addresses!![0].getAddressLine(0)
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

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
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