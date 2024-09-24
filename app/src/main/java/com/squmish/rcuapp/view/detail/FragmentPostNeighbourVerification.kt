package com.squmish.rcuapp.view.detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.squmish.rcuapp.databinding.FragmentPostNeighbourVerificationBinding
import com.squmish.rcuapp.interfaces.FragmentLifecycleInterface
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.squmish.rcuapp.uttils.AppConstants
import com.squmish.rcuapp.uttils.Utility.Companion.setAllEnabled
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.viewmodel.verificationDetail.PostNeighbourVerificationViewModel


class FragmentPostNeighbourVerification  : BaseFragment(), FragmentLifecycleInterface {


    private var _binding: FragmentPostNeighbourVerificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val postNeighbourVerificationViewModel by lazy { PostNeighbourVerificationViewModel(context as Activity,binding) }

    var data : String = ""



    companion object {
        fun newInstance(selectedData: GetVerificationDetailData?): FragmentPostNeighbourVerification {
            val bundle = Bundle()
            val fragmentPostNeighbourVerification = FragmentPostNeighbourVerification()
            fragmentPostNeighbourVerification.arguments = bundle
            return fragmentPostNeighbourVerification
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostNeighbourVerificationBinding.inflate(inflater, container, false)

        binding.viewModel = postNeighbourVerificationViewModel
        binding.lifecycleOwner = this
        context?.let { postNeighbourVerificationViewModel.init(it) }
//      basicInformationModel.init(context, FragmentDetail.selectedData!!)

        postNeighbourVerificationViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        setView()
        Log.e("OnCrete","PhotoNeighbour")
        return binding.root
    }

    private fun setView() {
        postNeighbourVerificationViewModel.isNeighbourReconised.observeForever {
            if (it){
                binding.inpReason.visibility = View.GONE
                binding.edtReason.setText(buildString {})
            }
            else{
                binding.inpReason.visibility = View.VISIBLE
            }
        }


        if(ActivityDetail.selectedData!!.getStatus() != null){
            if(ActivityDetail.selectedData!!.getStatus() == AppConstants.statusPending){
              //  binding.constraintLayout.forEach { child -> child.setAllEnabled(false) }
            }
            else{
                binding.constraintLayout.forEach { child -> child.setAllEnabled(true) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        Log.e("OnResume","Post Neighbour")

    }

}