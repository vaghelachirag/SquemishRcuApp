package com.squmish.rcuapp
import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
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
import com.squmish.rcuapp.databinding.ActivityMainBinding
import com.squmish.rcuapp.databinding.ActivityTestBinding
import com.squmish.rcuapp.model.dashboard.DashboardDataModel
import com.squmish.rcuapp.services.LocationUpdatesService
import com.squmish.rcuapp.view.adapter.DashboardAdapter
import com.squmish.rcuapp.view.adapter.DashboardSelectionAdapter
import java.util.Locale


class ActivityTest: AppCompatActivity()  {
    private lateinit var binding: ActivityTestBinding

    private lateinit var  dashboardSelectionAdapter: DashboardSelectionAdapter
    private var dashboardDataModel = mutableListOf<DashboardDataModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun setDashboardSelectionList() {
        //add data
        dashboardDataModel.add(DashboardDataModel("New","Desc",R.drawable.dashboard_list))
        dashboardDataModel.add(DashboardDataModel("Draft","Desc",R.drawable.dashboard_list))
        dashboardDataModel.add(DashboardDataModel("Pending Regular","Desc",R.drawable.dashboard_list))
        dashboardDataModel.add(DashboardDataModel("Pending Bulk","Desc",R.drawable.dashboard_list))

        dashboardSelectionAdapter.setDataList(dashboardDataModel)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
    }
}