package com.squmish.rcuapp.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.squmish.rcuapp.MainActivity
import com.squmish.rcuapp.R
import com.squmish.rcuapp.view.menu.DashboardActivity
import java.util.Locale


class LocationUpdatesService : Service(){

    private val locationRange = 100f

    private val USER_PREFS = "user_prefs"

    private val PLACE_ID = "placeId"

    private val EXTRA_STARTED_FROM_NOTIFICATION = "started_from_notification"

    private val mBinder = LocalBinder()

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private val NOTIFICATION_ID = 12345678

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var mChangingConfiguration = false

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    private var mServiceHandler: Handler? = null

    /**
     * The current location.
     */
    private var currentLocation: Location? = null
    private val TAG = "LocationUpdatesService"

    private var listPlaces: ArrayList<Location>? = null


    override fun onCreate() {
        super.onCreate()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0!!)
                p0?.let { mLocationResult ->
                    onNewLocation(mLocationResult.lastLocation!!)
                }
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Location Service started")
        val startedFromNotification =
            intent?.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false)

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification!!) {
            removeLocationUpdates()
            stopSelf()
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return Service.START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "in onBind()")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()")
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service")

            startForeground(NOTIFICATION_ID, serviceNotification())
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mServiceHandler?.removeCallbacksAndMessages(null)
    }

    fun requestLocationUpdates() {
        Log.e(TAG, "Requesting location updates")
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback!!,
                Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }

    }

    private fun removeLocationUpdates() {
        Log.e(TAG, "Removing location updates")
        try {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback!!)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }


    private fun getLastLocation() {
        try {
            mFusedLocationClient?.lastLocation
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        currentLocation = task.result
                    } else {
                        Log.e(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }

    }

    fun onNewLocation(location: Location) {
        Log.e("Location", "New location: $location")

        var addresses: List<Address?>
        val geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!;
        val address = addresses[0]!!.getAddressLine(0)

        DashboardActivity.currentLat = location.latitude
        DashboardActivity.currentLong = location.longitude
        DashboardActivity.useraddress = address

     /*   var toast = Toast.makeText(applicationContext,location.latitude.toString() + " "+location.longitude.toString() + " "+address,Toast.LENGTH_SHORT)
        toast.show()
*/
        currentLocation = location

        val sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        listPlaces?.let { mPlaces ->
            for (getLocation: Location in mPlaces) {
                if (sharedPreferences.getString(PLACE_ID, "") != getLocation.provider) {
                    val distance: Float = location.distanceTo(getLocation)
                    if (distance <= locationRange) {
                        Log.e(TAG, "Location Triggered " + getLocation.provider)
                        editor?.putString(PLACE_ID, getLocation.provider)
                        editor?.apply()
                        if (isAppIsInBackground(applicationContext)) {
                            Log.e(TAG, "App In Background " + getLocation.provider)
                            Log.e(TAG, "Send Notification " + getLocation.provider)
                            sendNotification(this, getLocation.provider!!)
                        } else {
                            Log.e(TAG, "App In Foreground " + getLocation.provider)
                            val pushNotification = Intent("NotifyUser")
                            pushNotification.putExtra("pinned_location_name", getLocation.provider)
                            pushNotification.putExtra("pinned_location_lat", getLocation.latitude.toString())
                            pushNotification.putExtra("pinned_location_long", getLocation.longitude.toString())
                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                        }
                    }
                }
            }
        }
    }

    private fun serviceNotification(): Notification {
        // Get an instance of the Notification manager
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        val intent = Intent(this, LocationUpdatesService::class.java)

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                "location_service_channel",
                name,
                NotificationManager.IMPORTANCE_HIGH
            )

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager?.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(this)
            .setContentTitle("Location Service")
            .setOngoing(true)
            .setSmallIcon(R.drawable.icon_main)
            .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("location_service_channel") // Channel ID
        } else {
            builder.priority = Notification.PRIORITY_HIGH
        }

        return builder.build()
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        Log.e(TAG, "createLocationRequest")
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true

        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.let {
                val runningProcesses = it.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isInBackground
    }

    private fun sendNotification(context: Context, placeId: String) {
        try {
            // Get an instance of the Notification manager
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            // Android O requires a Notification Channel.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.app_name)
                // Create the channel for the notification
                val mChannel = NotificationChannel("channel_01", name, NotificationManager.IMPORTANCE_HIGH)

                // Set the Notification Channel for the Notification Manager.
                mNotificationManager?.createNotificationChannel(mChannel)
            }

            // Create an explicit content Intent that starts the main Activity.
            val notificationIntent = Intent(context, MainActivity::class.java)

            // Construct a task stack.
            val stackBuilder = TaskStackBuilder.create(context)

            // Add the main Activity to the task stack as the parent.
            stackBuilder.addParentStack(MainActivity::class.java)

            // Push the content Intent onto the stack.
            stackBuilder.addNextIntent(notificationIntent)

            // Get a PendingIntent containing the entire back stack.
            val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            // Get a notification builder that's compatible with platform versions >= 4
            val builder = NotificationCompat.Builder(context)

            // Define the notification settings.
            builder.setSmallIcon(R.drawable.icon_main)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icon_main))
                .setColor(Color.RED)
                .setContentTitle("You are around at $placeId")
                .setSound(defaultSoundUri)
                .setContentIntent(notificationPendingIntent)

            // Set the Channel ID for Android O.
            builder.setChannelId("channel_01") // Channel ID

            // Dismiss notification once the user touches it.
            builder.setAutoCancel(true)

            // Issue the notification
            mNotificationManager?.notify(0, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}