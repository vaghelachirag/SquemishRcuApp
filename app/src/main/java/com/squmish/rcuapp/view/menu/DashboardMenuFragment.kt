package com.squmish.rcuapp.view.menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squmish.rcuapp.databinding.ActivityTestBinding
import com.squmish.rcuapp.databinding.DashboardFragmentBinding
import com.squmish.rcuapp.databinding.DashboardMenuFragmentBinding
import com.squmish.rcuapp.view.base.BaseFragment
import com.squmish.rcuapp.viewmodel.DashboardMenuViewModel

class DashboardMenuFragment: BaseFragment()  {

    private var _binding: DashboardMenuFragmentBinding? = null

    private val binding get() = _binding!!

    private val dashboardMenuViewModel by lazy { DashboardMenuViewModel(activity as Context,binding) }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DashboardMenuFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = dashboardMenuViewModel
        binding.lifecycleOwner = this
        dashboardMenuViewModel.init()

        return binding.root
    }
}