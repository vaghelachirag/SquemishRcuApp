package com.squmish.rcuapp.viewmodel.verificationDetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import com.squmish.rcuapp.uttils.Utils
import com.example.rcuapp.view.adapter.DocumentAdapter
import com.squmish.rcuapp.view.detail.ActivityDetail
import com.squmish.rcuapp.view.detail.FragmentBasicInformation
import com.example.rcuapp.view.dialougs.AcceptRejectFIDialog
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.FragmentBasicInformationBinding
import com.squmish.rcuapp.interfaces.OnItemSelected
import com.squmish.rcuapp.model.base.BaseViewModel
import com.squmish.rcuapp.model.getAcceptRejectResponse.GetAcceptRejectResponse
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDocument
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.room.InitDb
import com.squmish.rcuapp.room.dao.MasterDataDao
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BasicInformationViewModel(private val context: Context, val binding: FragmentBasicInformationBinding) : BaseViewModel(){

    // Login Params
    var refNo : ObservableField<String> = ObservableField()
    var status : ObservableField<String> = ObservableField()
    var caseID : ObservableField<String> = ObservableField()
    var bankName : ObservableField<String> = ObservableField()
    var verificationFor : ObservableField<String> = ObservableField()
    var verificationType : ObservableField<String> = ObservableField()
    var applicant : ObservableField<String> = ObservableField()
    var coapplicant : ObservableField<String> = ObservableField()
    var addressFor : ObservableField<String> = ObservableField()
    var applicantMobileNumber : ObservableField<String> = ObservableField()
    var applicantfathername : ObservableField<String> = ObservableField()
    var productsubproduct : ObservableField<String> = ObservableField()
    var officeName : ObservableField<String> = ObservableField()
    var prepost : ObservableField<String> = ObservableField()
    var loanAmount : ObservableField<String> = ObservableField()
    var triggers : ObservableField<String> = ObservableField()
    var backendName : ObservableField<String> = ObservableField()
    var assignDt : ObservableField<String> = ObservableField()


    private var masterDataDao: MasterDataDao? = null

    var acceptReasonList: MutableList<String>? = null
    var rejectReasonList: MutableList<String>? = null

    private var verificationDocumentAdapter: DocumentAdapter? = null


    fun init() {

        if (ActivityDetail.selectedData != null){
            refNo.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getRefNo().toString()))
            status.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getStatus().toString()))
            caseID.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getCaseId().toString()))
            bankName.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getBankAlias().toString()) +" "+  Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getBankName().toString()))
            verificationFor.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getVerificationFor().toString()))
            verificationType.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getVerificationTypeName().toString()) + "/"+ Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getDocumentName().toString()) + "/"+Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getSubType().toString()) )
            applicant.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getApplicantName().toString()) + " / "+ Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getApplicantFatherName().toString()))
            coapplicant.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getOtherApplicantName().toString()))
            addressFor.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getApplicantAddress().toString()) +" "+ Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getApplicantPinCode().toString()) +" "+  Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getApplicantCity().toString() +" "+ ActivityDetail.selectedData!!.getApplicantState().toString()))
            applicantMobileNumber.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getApplicantMobile().toString()) +  " / "+Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getApplicantSecondaryMobile().toString()))
            applicantfathername.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getApplicantName().toString()) + " / "+Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getApplicantFatherName().toString()))
            productsubproduct.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getLoanProductName().toString())  + " / "+ Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getSubLoanProduct().toString()))
            officeName.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getBusinessName().toString()))
            prepost.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getPrePost().toString()))
            loanAmount.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getLoanAmount().toString()))
            triggers.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getTriggerRemarks().toString()))
            backendName.set(Utility.getNullToBlankString(
                ActivityDetail.selectedData!!.getBackendName().toString() + " " +Utility.getNullToBlankString(
                    ActivityDetail.selectedData!!.getBackendMobileNo().toString())))
            assignDt.set(Utility.getNullToBlankString(Utility.convertDateTime(ActivityDetail.selectedData!!.getFiassignedAt().toString())))
            if (ActivityDetail.selectedData !=null){
                if (ActivityDetail.selectedData!!.getDocuments() != null){
                    setVerificationDocumentAdapter()
                }
              //  (context as ActivityDetail).hideTab()
            }
        }

        // Room Database
        masterDataDao = InitDb.appDatabase.getMasterData()
        getAcceptRejectList()
        setAction()
    }

    private fun setAction() {

       Log.e("FiStatus",ActivityDetail.selectedData!!.getStatus().toString())

        if (!ActivityDetail.selectedData!!.getStatus().isNullOrEmpty()){
            if (ActivityDetail.selectedData!!.getStatus()!!.equals("REJECTED")){
                binding.txtRejectReason.visibility = View.VISIBLE
                binding.txtRejectReason.text = Utility.getNullToBlankString(ActivityDetail.selectedData!!.getRejectReason().toString())
            }
        }

        binding.txtBackendNameHeader.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:"+ ActivityDetail.selectedData!!.getBackendMobileNo()))
            context.startActivity(intent)
        }

        binding.txtBasicMobileNumber.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:"+ ActivityDetail.selectedData!!.getApplicantMobile()))
            context.startActivity(intent)
        }

        if (ActivityDetail.selectedData != null && !ActivityDetail.selectedData!!.getApplicantLatitude().isNullOrEmpty()){
            binding.ivMap.visibility = View.GONE
            binding.txtBasicAddress.setCompoundDrawablesWithIntrinsicBounds(null, null,
                context.let { ContextCompat.getDrawable(it, R.drawable.map_pin) }, null)
        }

        val destinationURI = "http://maps.google.com/maps?daddr="+ ActivityDetail.selectedData!!.getApplicantLatitude()+ ","+ ActivityDetail.selectedData!!.getApplicantLongitude()
        binding.txtBasicAddress.setOnClickListener {
            if (ActivityDetail.selectedData != null && !ActivityDetail.selectedData!!.getApplicantLatitude().isNullOrEmpty()){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destinationURI))
            context.startActivity(intent)
            }
        }
    }

    private fun getAcceptRejectList() {
        CoroutineScope(Dispatchers.IO).launch {
            acceptReasonList   =  masterDataDao!!.getDataByKeyName(AppConstants.acceptReason)
            rejectReasonList   =  masterDataDao!!.getDataByKeyName(AppConstants.rejectReason)
        }
    }

    fun showAcceptRejectDialoug(isAccept: Boolean) {

        var reasonList: MutableList<String>? = null
        reasonList = if (isAccept){
            acceptReasonList
        } else{
            rejectReasonList
        }

        AcceptRejectFIDialog(context as Activity,reasonList!!,isAccept)
            .setListener(object : AcceptRejectFIDialog.OkButtonListener {
                override fun onOkPressed(
                    acceptRejectFIDialog: AcceptRejectFIDialog,
                    selectedReason: String?,
                    isAcceptReject: Boolean
                ) {
                    if(isAcceptReject){
                        acceptRejectFIDialog.dismiss()
                        getAcceptRejectRequestResponse(true,selectedReason)
                    }
                    else{
                        acceptRejectFIDialog.dismiss()
                        getAcceptRejectRequestResponse(false,selectedReason)
                    }
                }

            })
            .show()
    }
   fun getAcceptRejectRequestResponse(isAccept: Boolean, selectedReason: String?){

       val params = HashMap<String,Any>()
      // params["FirequestId"] = AppConstants.verificationId.toString()
       params["FirequestId"] =  AppConstants.verificationId.toString()
       params["Reason"] = selectedReason.toString()
       params["IsAccept"] = isAccept

       if (Utility.isNetworkConnected(context)){
           isLoading.postValue(true)
           Networking.with(context)
               .getServices()
               .getAcceptRejectResponse(Networking.wrapParams(params))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(object : CallbackObserver<GetAcceptRejectResponse>() {
                   override fun onSuccess(response: GetAcceptRejectResponse) {
                       isLoading.postValue(false)
                   }

                   override fun onFailed(code: Int, message: String) {
                       isLoading.postValue(false)
                   }

                   override fun onNext(t: GetAcceptRejectResponse) {
                       Log.e("Status",t.getStatusCode().toString())
                       isLoading.postValue(false)
                       if(t.getStatusCode() == 200){
                           Utils().showToast(context,t.getMessage().toString())
                            FragmentBasicInformation().redirectToDashboardScreen()
                            binding.llAcceptReject.visibility = View.GONE
                         //  (context as ActivityDetail).showTab()
                         //  (context as ActivityDetail).setAcceptedStatePageAdapter()
                           (context as ActivityDetail).finish()

                       }else{
                           Utils().showToast(context,t.getMessage().toString())
                           FragmentBasicInformation().redirectToDashboardScreen()
                       }
                       Log.e("StatusCode",t.getStatus().toString())
                   }
               })
       }else{
           Utils().showToast(context,context.getString(R.string.nointernetconnection).toString())
       }
    }

    private fun setVerificationDocumentAdapter() {
        if (ActivityDetail.selectedData!!.getDocuments() != null) {
            val documentList = ActivityDetail.selectedData!!.getDocuments()
            verificationDocumentAdapter = DocumentAdapter(
                context,
                documentList!!,
                this,
                object : OnItemSelected<GetVerificationDocument> {
                    override fun onItemSelected(t: GetVerificationDocument?, position: Int) {
                        Log.e("OnItem", "OnItem$position")
                    }
                })
            binding.rvDocument.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            binding.rvDocument.setAdapter(verificationDocumentAdapter)
        }
    }
}