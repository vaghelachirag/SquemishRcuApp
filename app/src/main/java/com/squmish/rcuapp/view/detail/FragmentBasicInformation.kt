package com.squmish.rcuapp.view.detail

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.squmish.rcuapp.databinding.FragmentBasicInformationBinding
import com.squmish.rcuapp.interfaces.FragmentLifecycleInterface
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.viewmodel.verificationDetail.BasicInformationViewModel


class FragmentBasicInformation  : BaseFragment(), FragmentLifecycleInterface {

    private var _binding: FragmentBasicInformationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val basicInformationModel by lazy { BasicInformationViewModel(activity as Context,binding) }

    var data : String = ""


    var dashboardActivity : Activity? = null;

    companion object {
        fun newInstance(selectedData: GetVerificationDetailData?): FragmentBasicInformation {
            val bundle = Bundle()
            //  bundle.putSerializable(DATA, selectedData)
            val fragmentEnquiry = FragmentBasicInformation()
            fragmentEnquiry.arguments = bundle
            return fragmentEnquiry
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBasicInformationBinding.inflate(inflater, container, false)
        binding.viewModel = basicInformationModel
        binding.lifecycleOwner = this
        context?.let { basicInformationModel.init() }
        setView()
        setAction()


        // DashboardActivity().navController.popBackStack(R.id.dashboardFragment, false);
        return binding.root
    }

    private fun setAction() {
        binding.btnAccept.setOnClickListener {
            basicInformationModel.showAcceptRejectDialoug(true)
        }
        binding.btnReject.setOnClickListener {
            basicInformationModel.showAcceptRejectDialoug(false)
        }
    }


    private fun setView() {
        if(ActivityDetail.selectedData!!.getStatus() != null){
            if(ActivityDetail.selectedData!!.getStatus() == AppConstants.statusPending){
                binding.llAcceptReject.visibility = View.VISIBLE
            }
            else{
                binding.llAcceptReject.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun redirectToDashboardScreen(){
      // binding.llAcceptReject.visibility = View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        dashboardActivity = activity

    }


    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        Log.e("OnResume","Basic Information")
        basicInformationModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {

        }

    }

}