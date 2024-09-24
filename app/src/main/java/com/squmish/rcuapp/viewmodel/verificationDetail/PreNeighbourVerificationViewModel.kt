package com.squmish.rcuapp.viewmodel.verificationDetail

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.squmish.rcuapp.uttils.Utils
import com.squmish.rcuapp.view.detail.ActivityDetail
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.FragmentPreNeighbourVerificationBinding
import com.squmish.rcuapp.model.base.BaseViewModel
import com.squmish.rcuapp.model.getPreNeighbourData.GetPreNeighbourResponse
import com.squmish.rcuapp.model.getverificationDetailResponse.GetFiRequestPreNeighboutVerificationDto
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class PreNeighbourVerificationViewModel(private val context: Context, val binding: FragmentPreNeighbourVerificationBinding) : BaseViewModel() {

    // Login Params
    var neighbour1Name: ObservableField<String> = ObservableField()
    var neighbour2Name: ObservableField<String> = ObservableField()
    var neighbour1Mobile: ObservableField<String> = ObservableField()
    var neighbour2Mobile: ObservableField<String> = ObservableField()
    var neighbour1Remark: ObservableField<String> = ObservableField()
    var neighbour2Remark: ObservableField<String> = ObservableField()
    var reason: ObservableField<String> = ObservableField()


    private var preNeighbourMutableLiveData: MutableLiveData<GetFiRequestPreNeighboutVerificationDto> =
        MutableLiveData()

    var isNeighbourReconised = MutableLiveData<Boolean>()
    var isNeighbourReconisedText = MutableLiveData<String>()
    var selectedReasonPosition = MutableLiveData<Int>()
    var selectedItemPosition: Int = 0

    private var neighbourRecognisedList: List<String>? = null

    @SuppressLint("SuspiciousIndentation")
    fun init(context: Context?) {

        val materialStatusList =  context!!.resources.getStringArray(R.array.neighbourrecognised_array)
        neighbourRecognisedList = materialStatusList.asList()
        binding.spnNeighbourReconised.setListAdapter(neighbourRecognisedList)

        isNeighbourReconised.value = false
        isNeighbourReconisedText.value = ""
        selectedReasonPosition.value = 0

        if (ActivityDetail.selectedData != null) {

            neighbour1Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Name().toString()))
            neighbour1Mobile.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Mobile().toString()))
            neighbour1Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Remark().toString()))
            neighbour2Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Name().toString()))
            neighbour2Mobile.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Mobile().toString()))
            neighbour2Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Remark().toString()))
            reason.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getReason().toString()))
            isNeighbourReconised.value = ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getIsNeighbourRecognised().toString() != "false"
        }

        binding.spnNeighbourReconised.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()){
                    isNeighbourReconised.value = !(s.toString() == "No" || s.toString() == "Denied")
                    isNeighbourReconisedText.value = s.toString()
                }else{
                    isNeighbourReconised.value = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.spnNeighbourReconised.setText(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getIsNeighbourRecognised().toString()))
    }

    // On Saved Clicked
    fun onSaveClicked() {
       /* if (neighbour1Mobile.get().toString() == "") {
            Utils().showSnackBar(
                context,
                "Please Enter Neighbour1 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour1Mobile.get().toString().length < 10) {
            Utils().showSnackBar(
                context,
                "Please Enter Valid Neighbour1 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour2Mobile.get().toString() == "") {
            Utils().showSnackBar(
                context,
                "Please Enter Neighbour2 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour2Mobile.get().toString().length < 10) {
            Utils().showSnackBar(
                context,
                "Please Enter Valid Neighbour2 Mobile Number",
                binding.constraintLayout
            )
        } else*/
        if (binding.spnNeighbourReconised.text.isNullOrEmpty()) {
            Utils().showSnackBar(context, "Please Select Reason", binding.constraintLayout)
        }
        else {
            val model = GetFiRequestPreNeighboutVerificationDto()
            model.setNeighbour1Name(neighbour1Name.get().toString())
            model.setNeighbour2Name(neighbour2Name.get().toString())
            model.setNeighbour1Mobile(neighbour1Mobile.get().toString())
            model.setNeighbour2Mobile(neighbour2Mobile.get().toString())
            model.setNeighbour1Remark(neighbour1Remark.get().toString())
            model.setNeighbour2Remark(neighbour2Remark.get().toString())
            model.setReason(reason.get().toString())
            model.setIsNeighbourRecognised(isNeighbourReconisedText.value)
            preNeighbourMutableLiveData.value = model
            savePreNeighbourData(model)
        }
    }

        private fun savePreNeighbourData(model: GetFiRequestPreNeighboutVerificationDto) {

            val params = HashMap<String, Any>()
            params["firequestId"] = AppConstants.verificationId.toString()
            params["neighbour1Name"] = model.getNeighbour1Name().toString()
            params["neighbour2Name"] = model.getNeighbour2Name().toString()
            params["neighbour1Mobile"] = model.getNeighbour1Mobile().toString()
            params["neighbour2Mobile"] = model.getNeighbour2Mobile().toString()
            params["neighbour1Remark"] = model.getNeighbour1Remark().toString()
            params["neighbour2Remark"] = model.getNeighbour2Remark().toString()
            params["isNeighbourRecognised"] = isNeighbourReconisedText.value.toString()
            params["reason"] = reason.get().toString()


            if (Utility.isNetworkConnected(context)) {
                isLoading.postValue(true)
                Networking.with(context)
                    .getServices()
                    .getSavePreNeighbourData(Networking.wrapParams(params))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CallbackObserver<GetPreNeighbourResponse>() {
                        override fun onSuccess(response: GetPreNeighbourResponse) {
                            isLoading.postValue(false)
                        }

                        override fun onFailed(code: Int, message: String) {
                            isLoading.postValue(false)
                        }

                        override fun onNext(t: GetPreNeighbourResponse) {
                            Log.e("Status", t.getStatusCode().toString())
                            isLoading.postValue(false)
                            if (t.getStatusCode() == 200) {
                                Utils().showSnackBar(
                                    context,
                                    t.getMessage().toString(),
                                    binding.constraintLayout
                                )
                            } else {
                                Utils().showSnackBar(
                                    context,
                                    t.getMessage().toString(),
                                    binding.constraintLayout
                                )
                            }
                            Log.e("StatusCode", t.getStatus().toString())
                        }
                    })
            } else {
                Utils().showSnackBar(
                    context,
                    context.getString(R.string.nointernetconnection).toString(),
                    binding.constraintLayout
                )
            }
        }
    }