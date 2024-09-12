package com.squmish.rcuapp.view.menu
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rcuapp.view.dialougs.ChangePasswordDialoug
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
import com.squmish.rcuapp.MainActivity
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.ActivityDashboardBinding
import com.squmish.rcuapp.interfaces.OnItemSelected
import com.squmish.rcuapp.model.getMenuListResponse.GetMenuListData
import com.squmish.rcuapp.model.getMenuListResponse.GetMenuListResponse
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Session
import com.squmish.rcuapp.uttils.Utility
import com.squmish.rcuapp.uttils.Utils
import com.squmish.rcuapp.view.adapter.MenuItemAdapter
import com.squmish.rcuapp.view.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Locale


@Suppress("DEPRECATION")
class DashboardActivity : BaseActivity() {


    private lateinit var binding: ActivityDashboardBinding
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var activityDashboard : DashboardActivity? = null

    // Session
    var session: Session? = null;

    val isLoading = MutableLiveData<Boolean>()
    var menuList: ArrayList<GetMenuListData> = ArrayList()

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener!!) }
        }
    }


    companion object {
        public  var currentLat : Double = 0.0
        public  var currentLong : Double = 0.0
        public  var useraddress : String = ""
    }

    @SuppressLint("DiscouragedPrivateApi", "SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboard = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        session = Session(this);

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

      /*  binding.pullToRefresh.setOnRefreshListener(OnRefreshListener {
         Log.e("Onrefresh","OnRefresh")
        })
*/


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



        // Set up ActionBar
        setSupportActionBar(binding.toolbarDashboard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title ="Test"
        }

        getSessionData()


        supportActionBar!!.title = "Test"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_menup)

        navController = findNavController(R.id.navHostFragmentPickford)

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.dashboardFragment,
            R.id.settingFragment,
            R.id.dashboardFragment,
        ).setDrawerLayout(binding.drawer).build()


        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        setupNavControl()
        setDrawerAction()
        setupToolbarWithMenu(getString(R.string.dashboard),binding)
        getMenuListResponse()
        askNotificationPermission()
        Log.e("Token", getToken(this).toString())
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

    private fun getToken(context: Context): String? {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
    }


    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun getSessionData() {
        val viewMenu = findViewById<View>(R.id.layoutMenu)
        val userName = viewMenu.findViewById<View>(R.id.txt_UserName) as TextView
        val profileImage = viewMenu.findViewById<View>(R.id.navHeaderLogo) as AppCompatImageView
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .circleCrop()
            .error(R.mipmap.ic_launcher_round)

        Glide.with(this).load(session!!.getUserProfileImageKey()).apply(options).into(profileImage)
        userName.text = session!!.getUserNameKey()
    }

    private fun setDrawerAction() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }

            when (menuItem.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    binding.toolbarDashboard.setTitle("Test")
                    supportActionBar!!.title = "test"
                    supportActionBar?.setDisplayShowHomeEnabled(false);
                    binding.toolbarDashboard.setNavigationIcon(null);

                }
                R.id.settingFragment -> {
                    navController.navigate(R.id.settingFragment)
                    binding.toolbarDashboard.setTitle(R.string.action_settings)
                    binding.toolbarDashboard.setNavigationIcon(null);

                }
                R.id.logout -> {
                    Utils().showAlertDialog(this,resources.getString(R.string.logoutAlert))
                }
            }
            true
        }
        binding.ivMenu.setOnClickListener {
            Log.e("Menu","MenuClicked")
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }else{
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }

    }

    private fun showLogoutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are You Sure Want To Logout?")

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            session!!.clearSession()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun setupNavControl() {
        binding.navigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
    }

    private fun getMenuListResponse() {
        if (Utility.isNetworkConnected(this)){
            isLoading.postValue(true)
            Networking.with(this)
                .getServices()
                .getMenuListResponse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetMenuListResponse>() {
                    override fun onSuccess(response: GetMenuListResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onNext(t: GetMenuListResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                            if(t.getData() != null){
                                menuList  =  t.getData()!!
                                menuList.sortBy { it.getOrderNo() }
                                setMenuAdapter()
                            }
                        }else{
                            Utils().showToast(this@DashboardActivity ,t.getMessage().toString())
                        }
                    }
                })
        }else{

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setMenuAdapter() {
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = MenuItemAdapter(this, menuList,object :
            OnItemSelected<GetMenuListData> {

            override fun onItemSelected(t: GetMenuListData?, position: Int) {
                clickMenuEvent(t)
            }

        })

        binding.rvMenu.addItemDecoration(
            DividerItemDecoration(
                binding.rvMenu.context,
                (binding.rvMenu.layoutManager as LinearLayoutManager).orientation
            )
        )
    }

    private fun clickMenuEvent(menuData: GetMenuListData?) {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        if (menuData!!.getIsWebView() == true){

            val bundle = Bundle()
            bundle.putString("webURL", menuData.getMenuId().toString())

            if (title != null) binding.tvTitle.text = menuData.getName()

            binding.toolbarDashboard.setTitle(menuData.getName())
            navController.navigate(R.id.webViewFragment,bundle)
            supportActionBar?.setDisplayShowHomeEnabled(false);
            binding.toolbarDashboard.setNavigationIcon(null);
        }else{

            if (menuData.getMenuId() == AppConstants.home){
                navController.navigate(R.id.dashboardFragment)
                supportActionBar?.setDisplayShowHomeEnabled(false);
                binding.toolbarDashboard.setNavigationIcon(null);
                if (title != null) binding.tvTitle.text = "Dashboard"
            }
            if (menuData.getMenuId() == AppConstants.changePassword){
              /*  navController.navigate(R.id.changePasswordFragment)
                binding.toolbarDashboard.setTitle(R.string.changePassword_Txt)
                binding.toolbarDashboard.setNavigationIcon(null);*/
                //showChangePasswordDialoug()
                Utils().showChangePasswordDialog(this,binding)
            }
            if (menuData.getMenuId() == AppConstants.logout){
                Utils().showAlertDialog(this,resources.getString(R.string.logoutAlert))
            }
        }
    }

    private fun showChangePasswordDialoug() {
        ChangePasswordDialoug(this)
            .setListener(object : ChangePasswordDialoug.OkButtonListener {
                override fun onOkPressed(
                    acceptRejectFIDialog: ChangePasswordDialoug
                ) {

                }

            })
            .show()
    }
}