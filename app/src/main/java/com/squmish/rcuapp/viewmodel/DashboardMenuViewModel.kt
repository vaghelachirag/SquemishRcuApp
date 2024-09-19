package com.squmish.rcuapp.viewmodel

import DashboardSelectionAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.ActivityTestBinding
import com.squmish.rcuapp.databinding.DashboardMenuFragmentBinding
import com.squmish.rcuapp.interfaces.OnItemSelected
import com.squmish.rcuapp.model.base.BaseViewModel
import com.squmish.rcuapp.model.dashboard.getDashboardApiResponse.GetDashboardApiResponse
import com.squmish.rcuapp.model.dashboard.getDashboardApiResponse.GetMobileDashboardDetailDto
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.uttils.Utility
import com.squmish.rcuapp.uttils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DashboardMenuViewModel(val context: Context,val binding: DashboardMenuFragmentBinding) : BaseViewModel() {

    var dashboardMenuList: ArrayList<GetMobileDashboardDetailDto> = ArrayList()
    private var dashboardAdapter: DashboardSelectionAdapter? = null


    fun init() {
        getDashboardMenu()
    }

    @SuppressLint("HardwareIds")
    fun getDashboardMenu() {

        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .getDashboardMenuResponse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetDashboardApiResponse>() {
                    override fun onSuccess(response: GetDashboardApiResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    override fun onNext(t: GetDashboardApiResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        if(t.getStatusCode() == 200){
                            if(t.getData() != null){
                                dashboardMenuList = t.getData()!!.getMobileDashboardDetailDto()!!
                                setDashboardMenuAdapter()
                            }
                        }else{
                            Utils().showToast(context,t.getMessage().toString())
                        }
                        Log.e("StatusCode",t.getStatus().toString())
                    }



                })
        }else{
            Utils().showToast(context,context.getString(R.string.nointernetconnection).toString())
        }
    }
    private fun setDashboardMenuAdapter() {
        dashboardAdapter =  DashboardSelectionAdapter(context,dashboardMenuList, this, object :
            OnItemSelected<GetMobileDashboardDetailDto> {
            override fun onItemSelected(t: GetMobileDashboardDetailDto?, position: Int) {
                Log.e("OnItem", "OnItem$position")
               // dashboardFragment.redirectToDetailScreen(verificationList[position])
            }
        })
        binding.rvDashboardMenu.layoutManager = GridLayoutManager(context,2)
      //  binding.rvDashboardMenu.setLayoutManager(Grid(context))
        binding.rvDashboardMenu.setAdapter(dashboardAdapter)
    }
}