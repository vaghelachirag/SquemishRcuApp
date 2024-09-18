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
import com.squmish.rcuapp.model.pendingRequest.GetPendingRequestData
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Session.Companion.DATA
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.view.detail.ActivityDetail
import com.squmish.rcuapp.viewmodel.DashboardViewModel
import java.util.Locale


class DashboardFragment: BaseFragment() {

    private var _binding: DashboardFragmentBinding? = null

    private val binding get() = _binding!!
    private val dashboardViewModel by lazy { DashboardViewModel(activity as Context,this@DashboardFragment,binding) }

    // Location
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    @Deprecated("Deprecated in Java")
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
}