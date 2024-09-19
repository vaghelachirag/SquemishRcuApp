package com.squmish.rcuapp
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.squmish.rcuapp.databinding.ActivityTestBinding
import com.squmish.rcuapp.model.dashboard.DashboardDataModel
import com.squmish.rcuapp.viewmodel.DashboardMenuViewModel


class ActivityTest: AppCompatActivity()  {
    private lateinit var binding: ActivityTestBinding

    private var dashboardDataModel = mutableListOf<DashboardDataModel>()


  //  private val dashboardMenuViewModel by lazy { DashboardMenuViewModel(this,binding) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
    }
}