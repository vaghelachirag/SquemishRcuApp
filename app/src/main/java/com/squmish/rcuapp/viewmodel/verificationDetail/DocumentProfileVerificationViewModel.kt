package com.squmish.rcuapp.viewmodel.verificationDetail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.squmish.rcuapp.model.saveresidenceverification.SaveVerificationDataDetail

import com.squmish.rcuapp.uttils.Utils
import com.squmish.rcuapp.view.detail.ActivityDetail
import com.google.gson.Gson
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.FragmentDocumentProfileVerificationBinding
import com.squmish.rcuapp.model.base.BaseViewModel
import com.squmish.rcuapp.model.getSaveResidenceVerificationResponse.GetSaveResidenceVerificationResponse
import com.squmish.rcuapp.model.saveDocumentProfileVerification.SaveDocumentProfileVerification
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Utility
import com.squmish.rcuapp.view.menu.DashboardActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DocumentProfileVerificationViewModel(private val context: Context, private val  binding: FragmentDocumentProfileVerificationBinding) : BaseViewModel() {

    // Address Detail Params
    var edtLatitude: ObservableField<String> = ObservableField()
    var edtLongitude: ObservableField<String> = ObservableField()
    var edtOtherObservations: ObservableField<String> = ObservableField()
    var edtAddressBelongRemark: ObservableField<String> = ObservableField()


    // Personal Information One
    var edPersonMet: ObservableField<String> = ObservableField()
    var edReason: ObservableField<String> = ObservableField()
    var edPersonMetDesignation: ObservableField<String> = ObservableField()
    var edAuthorizePersonName: ObservableField<String> = ObservableField()
    var edAuthorizePersonDesignation: ObservableField<String> = ObservableField()
    var edDocument: ObservableField<String> = ObservableField()



    // All Variable
    var isAddressConfirmed = MutableLiveData<Boolean>()
    var isOfficeOpen =  MutableLiveData<Any?>()
    var isDocumentSigned = MutableLiveData<Any?>()
    var isPersonallyMet = MutableLiveData<Any?>()
     var isAnyProof = MutableLiveData<Any?>()


    fun init(context: Context?) {
        if (ActivityDetail.selectedData !=null && ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification() !=null){
            setSelectedData()
        }
    }
    val onAddressConfirmed = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        if (checkedId == R.id.radio_AddressConfirmedYes) {
            isAddressConfirmed.value = true
        }
        if (checkedId == R.id.radio_AddressConfirmedNo) {
            isAddressConfirmed.value = false
        }
        if (checkedId == R.id.radio_OfficeOpenYes) {
            isOfficeOpen.value = true
        }
        if (checkedId == R.id.radio_OfficeOpenNo) {
            isOfficeOpen.value = false
        }

        if (checkedId == R.id.radio_applicant_Document_signedYes) {
            isDocumentSigned.value = true
            binding.llAddressDetail.llAuthorizePerson.visibility = View.VISIBLE
        }
        if (checkedId == R.id.radio_applicant_Document_signedNo) {
            isDocumentSigned.value = false
            binding.llAddressDetail.llAuthorizePerson.visibility = View.GONE
        }
        if (checkedId == R.id.radio_applicant_personally_met_with_authorised_labelYes) {
            isPersonallyMet.value = true
        }
        if (checkedId == R.id.radio_applicant_personally_met_with_authorised_label_signedNo) {
            isPersonallyMet.value = false
        }
        if (checkedId == R.id.radio_applicant_isAnyProof_labelYes) {
            isAnyProof.value = true
            binding.llAddressDetail.inpapplicantNameOfDocumentLabel.visibility = View.VISIBLE
        }
        if (checkedId == R.id.radio_applicant_isAnyProof_labelNo) {
            isAnyProof.value = false
            binding.llAddressDetail.inpapplicantNameOfDocumentLabel.visibility = View.GONE
        }
    }

    fun onSaveClicked(){

        if (isAddressConfirmed.value == null){
            Utils().showSnackBar(context,"Please select address confirmed",binding.constraintLayout)
        }else if (isOfficeOpen.value == null && isAddressConfirmed.value == true){
            Utils().showSnackBar(context,"Please select Office Open at the time of visit",binding.constraintLayout)
        }
        else if (isOfficeOpen.value == true && edPersonMet.get().isNullOrEmpty()){
            Utils().showSnackBar(context,"Please enter Name of the Person Met",binding.constraintLayout)
        }
        else if (isOfficeOpen.value == true && edPersonMetDesignation.get().isNullOrEmpty()){
            Utils().showSnackBar(context,"Please enter Designation of the person met",binding.constraintLayout)
        }
        else if (isOfficeOpen.value == true && isDocumentSigned.value == null){
            Utils().showSnackBar(context,"Please select Whether document signed/Issued by the authorised Person",binding.constraintLayout)
        }
        else if (isOfficeOpen.value == true && isPersonallyMet.value == null){
            Utils().showSnackBar(context,"Please select Whether we personally met with authorised person ",binding.constraintLayout)
        }
        else if (isOfficeOpen.value == true && isAnyProof.value == null){
            Utils().showSnackBar(context,"Please select Is any Proof/ Evidences shown ",binding.constraintLayout)
        }
        else {
            callSaveDocumentProfileApi()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setSelectedData() {

        try {
            isAddressConfirmed.value =  ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getAddressConfirmed()
            isOfficeOpen.value =  ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getIsOfficeOpen()
            isDocumentSigned.value =  ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getIsDocumentSigned()
            isAnyProof.value =  ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getIsProofShown()
            isPersonallyMet.value =  ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getIsMetAuthorisedPerson()

            edReason.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getReason().toString()))
            edtLatitude.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getLatitude().toString()))
            edtLongitude.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getLongitude().toString()))
            edtOtherObservations.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getOtherObservations().toString()))
            edPersonMet.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getPersonMet().toString()))
            edPersonMetDesignation.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getPersonMetDesignation().toString()))
            edAuthorizePersonName.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getAuthorisedPersonName().toString()))
            edAuthorizePersonDesignation.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getAuthorisedPersonDesignation().toString()))
            edDocument.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestDocumentProfileVerification()!!.getDocumentName().toString()))

            edtLatitude.set(DashboardActivity.currentLat.toString())
            edtLongitude.set(DashboardActivity.currentLong.toString())

        }catch (_: Exception){
        }
        // Log.e("Selected", ActivityDetail.selectedData!!.getFiRequestResidenceVerification()!!.getPersonMet().toString())
    }

    private fun callSaveDocumentProfileApi() {
        val saveVerificationDataDetail: SaveVerificationDataDetail = SaveVerificationDataDetail()
        saveVerificationDataDetail.setFirequestId(AppConstants.verificationId)
        saveVerificationDataDetail.setVerificationType(AppConstants.documentProfileVerificationType)
        val saveFiRequestResidenceVerification: SaveDocumentProfileVerification = SaveDocumentProfileVerification()
        //  saveFiRequestResidenceVerification.setVisitDate("2024-08-14T22:32:20.503")
        saveVerificationDataDetail.setFirequestDocumentProfileVerification(saveFiRequestResidenceVerification)
        saveFiRequestResidenceVerification.setAddressConfirmed(isAddressConfirmed.value)
        saveFiRequestResidenceVerification.setIsOfficeOpen(isOfficeOpen.value)
        saveFiRequestResidenceVerification.setReason(edReason.get())
        saveFiRequestResidenceVerification.setPersonMet(edPersonMet.get())
        saveFiRequestResidenceVerification.setPersonMetDesignation(edPersonMetDesignation.get())
        saveFiRequestResidenceVerification.setIsDocumentSigned(isDocumentSigned.value)
        saveFiRequestResidenceVerification.setAuthorisedPersonName(edAuthorizePersonName.get())
        saveFiRequestResidenceVerification.setAuthorisedPersonDesignation(edAuthorizePersonDesignation.get())
        saveFiRequestResidenceVerification.setIsMetAuthorisedPerson(isPersonallyMet.value)
        saveFiRequestResidenceVerification.setIsProofShown(isAnyProof.value)
        saveFiRequestResidenceVerification.setDocumentName(edDocument.get())
        saveFiRequestResidenceVerification.setLatitude(edtLatitude.get())
        saveFiRequestResidenceVerification.setLongitude(edtLongitude.get())
        saveFiRequestResidenceVerification.setOtherObservations(edtOtherObservations.get())


        val gson = Gson()
        val json = gson.toJson(saveVerificationDataDetail)
        Log.e("Json", json)


        if (Utility.isNetworkConnected(context)) {
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .getSaveFiResidenceResponse(saveVerificationDataDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetSaveResidenceVerificationResponse>() {
                    override fun onSuccess(response: GetSaveResidenceVerificationResponse) {
                        isLoading.postValue(false)
                    }
                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }
                    override fun onNext(t: GetSaveResidenceVerificationResponse) {
                        isLoading.postValue(false)
                        if (t.getStatusCode() == 200) {
                            Utils().showSnackBar(context, t.getMessage().toString(), binding.constraintLayout)
                        } else {
                            Utils().showSnackBar(context, t.getMessage().toString(), binding.constraintLayout)
                        }
                    }
                })
        } else {
            Utils().showSnackBar(context, context.getString(R.string.nointernetconnection).toString(), binding.constraintLayout)
        }
    }
}