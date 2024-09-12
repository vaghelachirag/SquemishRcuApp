package com.squmish.rcuapp.view.detail
import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.squmish.rcuapp.uttils.Utils
import com.squmish.rcuapp.view.adapter.VerificationDetailViewPagerAdapter
import com.squmish.rcuapp.view.base.BaseActivity
import com.squmish.rcuapp.view.detail.fiRequest.FragmentRCOVerification
import com.squmish.rcuapp.viewmodel.DetailViewModel

import com.squmish.rcuapp.databinding.ActivityDetailBinding
import com.squmish.rcuapp.interfaces.FragmentLifecycleInterface
import com.squmish.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData



open class ActivityDetail  : BaseActivity()  {

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel by lazy { DetailViewModel(this) }

    @SuppressLint("DiscouragedPrivateApi", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        detailViewModel.init(this)
        setProgressData()
        setView()
        setActionBarHeader()
        setAction()
        binding.viewPager.setSwipeable(false)

    }

    private fun setAction() {
        binding.layoutDetail.ivBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setActionBarHeader() {
        binding.layoutDetail.tvTitle.text = "Verification Detail"
    }

    companion object {
        public  var selectedData: GetVerificationDetailData? = null
        public  var useraddress : String = ""
    }

    override fun onResume() {
        super.onResume()
    //  detailViewModel.init(this)
        Log.e("OnResume","OnResume")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
        binding.lifecycleOwner = this

       detailViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showProgressbar()
            else if (!isLoading) hideProgressbar()
        }

        detailViewModel.isData.observeForever {
            if (detailViewModel.isData.value == true){
                //  Log.e("Verification",detailViewModel.getVerificationDetailData.getStatus().toString())
            }
        }

        detailViewModel.getVerificationDetailData.observeForever {
            Log.e("Data","Data" + detailViewModel.getVerificationDetailData.value!!.getStatus().toString())
            selectedData = detailViewModel.getVerificationDetailData.value
            Utils().setVerificationType(selectedData)
            setStatePageAdapter()
        }

        val tabStrip = binding.tabLayout.getChildAt(0)
        for (i in 0 until binding.tabLayout.childCount) {
            binding.tabLayout.getChildAt(i).setOnTouchListener { _, _ -> true }
        }

    }

    private fun setStatePageAdapter() {

        //  Log.e("GetVerification",detailViewModel.getVerificationDetailData.value!!.getStatus().toString())
        val viewPagerAdapter = VerificationDetailViewPagerAdapter(supportFragmentManager, 0)
        viewPagerAdapter.addFragment(FragmentBasicInformation.newInstance(selectedData), "Basic Information")
        if (!selectedData!!.isDocumentVerification){
            viewPagerAdapter.addFragment(FragmentPreNeighbourVerification.newInstance(selectedData), "Pre-Neighbour Verification")
        }

        viewPagerAdapter.addFragment(FragmentRCOVerification.newInstance(selectedData), "RCU Verification")

        viewPagerAdapter.addFragment(FragmentPhotograph.newInstance(), "Photograph")
        if (!selectedData!!.isDocumentVerification){
            viewPagerAdapter.addFragment(FragmentPostNeighbourVerification.newInstance(selectedData), "Post-Neighbour Verification")
        }

        viewPagerAdapter.addFragment(FragmentFinalSubmit.newInstance(selectedData), "Final Submit")

        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager, true)
        binding.viewPager.currentItem = 0
        binding.viewPager.isSaveEnabled = true
        binding.viewPager.offscreenPageLimit = 8

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var currentPosition = 0
            override fun onPageSelected(newPosition: Int) {
                val fragmentToHide: FragmentLifecycleInterface =
                    viewPagerAdapter.getItem(currentPosition) as FragmentLifecycleInterface
                fragmentToHide.onPauseFragment()
                val fragmentToShow: FragmentLifecycleInterface =
                    viewPagerAdapter.getItem(newPosition) as FragmentLifecycleInterface
                fragmentToShow.onResumeFragment(null)
                currentPosition = newPosition
            }
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }


    private fun setProgressData() {
    }

}