package com.squmish.rcuapp.view.detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.squmish.rcuapp.databinding.FragmentRcuVerificationBinding
import com.squmish.rcuapp.interfaces.FragmentLifecycleInterface
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Utility.Companion.setAllEnabled
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.viewmodel.verificationDetail.RCUVerificationViewModel

class FragmentRCUVerification : BaseFragment(), FragmentLifecycleInterface {

    private var _binding: FragmentRcuVerificationBinding? = null
    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    var data : String = ""
    private val rcuVerificationViewModel by lazy { RCUVerificationViewModel( context as Activity,binding) }


    companion object {

        fun newInstance(selectedData: GetVerificationDetailData?): FragmentRCUVerification {
            val bundle = Bundle()
            //  bundle.putSerializable(DATA, selectedData)
            val fragmentRCUVerification = FragmentRCUVerification()
            fragmentRCUVerification.arguments = bundle
            return fragmentRCUVerification
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRcuVerificationBinding.inflate(inflater, container, false)
        binding.viewModel = rcuVerificationViewModel
        binding.lifecycleOwner = this
//        basicInformationModel.init(context, FragmentDetail.selectedData!!)
        rcuVerificationViewModel.init(context)
        setObserver()
        setView()

        rcuVerificationViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        return binding.root
    }


    private fun setView() {
        if(ActivityDetail.selectedData!!.getStatus() != null){
            if(ActivityDetail.selectedData!!.getStatus() == AppConstants.statusPending){
             //   binding.llMain.forEach { child -> child.setAllEnabled(false) }
            }
            else{
                binding.llMain.forEach { child -> child.setAllEnabled(true) }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setObserver() {

        // Address Confirmed
        rcuVerificationViewModel.isAddressConfirmed.observeForever {
            try {
                setVisibility(it)
            }catch (_: Exception){
            }
        }

        // Address Belong Confirmed
        rcuVerificationViewModel.isAddressBelong.observeForever {
            if (it == true){
                binding.llAddressDetail.rbAddressBelongYes.isChecked = true
                binding.llAddressDetail.radioDurningVisitYes.isChecked = false
                binding.llAddressDetail.radioDurningVisitNo.isChecked = false
                binding.llAddressDetail.inpAddressBelongRemark.visibility = View.GONE
                binding.llAddressDetail.llHouseIsOpen.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llRelationWithApplicant.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.root.visibility = View.VISIBLE
                binding.llPersonalInformation.llHouseSize.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llFamilyMember.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llFamilyMemberCount.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llHouseOwnership.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llStayingAddress.visibility = View.VISIBLE
                binding.llPersonalInformation.llHouseSize.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llRent.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llAge.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.inpapplicantPersonMet.visibility =  View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.inpapplicantPersonMobileNumber.visibility = View.VISIBLE
            }
            if (it == false){
                binding.llAddressDetail.rbAddressBelongNo.isChecked = true
                binding.llAddressDetail.inpAddressBelongRemark.visibility = View.VISIBLE
                binding.llAddressDetail.llHouseIsOpen.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llRelationWithApplicant.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.root.visibility = View.VISIBLE
                binding.llPersonalInformation.llHouseSize.visibility = View.VISIBLE
                binding.llPersonalInformation.llPersonalInformationOne.llFamilyMember.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llFamilyMemberCount.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llHouseOwnership.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llStayingAddress.visibility = View.GONE
                binding.llPersonalInformation.llHouseSize.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llRent.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.llAge.visibility = View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.inpapplicantPersonMet.visibility =  View.GONE
                binding.llPersonalInformation.llPersonalInformationOne.inpapplicantPersonMobileNumber.visibility = View.GONE
            }
        }

        // NameBoard Confirmed
        rcuVerificationViewModel.isNameboardseenattheHouse.observeForever {
            if (it == true){
                binding.llPersonalInformation.rbIsNameboardseenattheHouseYes.isChecked = true
                binding.llPersonalInformation.llNameboardMismatched.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llPersonalInformation.rbIsNameboardseenattheHouseNo.isChecked = true
                binding.llPersonalInformation.llNameboardMismatched.visibility = View.GONE
            }
            else{

            }
        }

        // Name Board Mismatched
        rcuVerificationViewModel.isNameboardmismatched.observeForever {
            if (it == true){
                binding.llPersonalInformation.rbApplicantIsNameboardseenattheHouseLabelYes.isChecked = true
                binding.llPersonalInformation.inpApplicantReasonLabel.visibility = View.VISIBLE
            }else if (it == false){
                binding.llPersonalInformation.rbApplicantIsNameboardseenattheHouseLabeleNo.isChecked = true
                binding.llPersonalInformation.inpApplicantReasonLabel.visibility = View.GONE
            }
            else{

            }
        }

        // Major Medical History
        rcuVerificationViewModel.isMajorMedicalHistory.observeForever {
            if (it == true){
                binding.llApplicationBackground.rbIsapplicanthaveanymajormedicalhistoryYes.isChecked = true
                binding.llApplicationBackground.inpMedicalHistoryRemark.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llApplicationBackground.rbIsapplicanthaveanymajormedicalhistoryNo.isChecked = true
                binding.llApplicationBackground.inpMedicalHistoryRemark.visibility = View.GONE
            }
            else{

            }
        }


        // Political Connection
        rcuVerificationViewModel.isAnyPoliticalIssue.observeForever {
            if (it == true){
                binding.llApplicationBackground.rbIsapplicanthaveanypoliticalconnectionYes.isChecked = true
                binding.llApplicationBackground.inpIsapplicanthaveanypoliticalconnectionRemark.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llApplicationBackground.rbIsapplicanthaveanypoliticalconnectionNo.isChecked = true
                binding.llApplicationBackground.inpIsapplicanthaveanypoliticalconnectionRemark.visibility = View.GONE
            }
            else{

            }
        }


        // Political Connection
        rcuVerificationViewModel.isAnyLoanRunning.observeForever {
            if (it == true){
                binding.llApplicationBackground.rbApplicantIsanyotherloanrunningLabelYes.isChecked = true
                binding.llApplicationBackground.llOtherLoan.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llApplicationBackground.rbRbApplicantIsanyotherloanrunningLabelNo.isChecked = true
                binding.llApplicationBackground.llOtherLoan.visibility = View.GONE
            }
            else{

            }
        }

        // Political Connection
        rcuVerificationViewModel.isAreaNegative.observeForever {
            if (it == true){
                binding.llApplicationBackground.rbApplicantIsAreaNegativeLabelYes.isChecked = true
                binding.llApplicationBackground.inpapplicantIsAreaNegativeLabel.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llApplicationBackground.rbApplicantIsAreaNegativeLabelNo.isChecked = true
                binding.llApplicationBackground.inpapplicantIsAreaNegativeLabel.visibility = View.GONE
            }
            else{

            }
        }

        // Political Connection
        rcuVerificationViewModel.isCastCommunityDominatedArea.observeForever {
            if (it == true){
                binding.llApplicationBackground.rbApplicantIsCastCommunityDominatedAreaYes.isChecked = true
                binding.llApplicationBackground.inpapplicantIsCastCommunityDominatedAreaLabel.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llApplicationBackground.rbApplicantIsCastCommunityDominatedAreaNo.isChecked = true
                binding.llApplicationBackground.inpapplicantIsCastCommunityDominatedAreaLabel.visibility = View.GONE
            }
            else{

            }
        }

        // Political Connection
        rcuVerificationViewModel.isHouseOpen.observeForever {
            if (it == true){
                binding.llAddressDetail.radioDurningVisitYes.isChecked = true
                binding.llPersonalInformation.llPersonalInformationOne.root.visibility = View.VISIBLE
                binding.llPersonalInformation.llHouseSize.visibility = View.VISIBLE
            }
            if (it == false) {
                binding.llAddressDetail.radioDurningVisitNo.isChecked = true
                binding.llPersonalInformation.llPersonalInformationOne.root.visibility = View.GONE
                binding.llPersonalInformation.llHouseSize.visibility = View.GONE
            }
            if (it != true) {
                // b is null or equals false
                Log.e("False","False")
            }
        }

        // Political Connection
        rcuVerificationViewModel.isHouseRented.observeForever {
            if (it == true){
                binding.llPersonalInformation.llPersonalInformationOne.llRent.visibility = View.VISIBLE
            }
            else if (it == false){
                binding.llPersonalInformation.llPersonalInformationOne.llRent.visibility = View.GONE
            }
            else{

            }
        }
    }

    private fun setVisibility(visibility: Boolean) {
        if (visibility){
            binding.llAddressDetail.radioAddressConfirmedYes.isChecked = true
            binding.llAddressDetail.llAddressConfirmed.visibility =   View.GONE
            binding.llPersonalInformation.root.visibility = View.VISIBLE
            binding.llApplicationBackground.root.visibility = View.VISIBLE
            binding.llAddressDetail.llAddressNotConfirmed.visibility = View.VISIBLE
        }
        else{
            binding.llAddressDetail.radioAddressConfirmedNo.isChecked = true
            binding.llAddressDetail.llAddressConfirmed.visibility =   View.VISIBLE
            binding.llPersonalInformation.root.visibility = View.GONE
            binding.llApplicationBackground.root.visibility = View.GONE
            binding.llAddressDetail.llAddressNotConfirmed.visibility = View.GONE
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPauseFragment() {
    }

    override fun onResumeFragment(s: String?) {
    }

}