package com.squmish.rcuapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.ItemDashboardBinding
import com.squmish.rcuapp.interfaces.OnItemSelected
import com.squmish.rcuapp.model.pendingRequest.GetPendingRequestData
import com.squmish.rcuapp.uttils.Utility
import com.squmish.rcuapp.viewmodel.DashboardViewModel

class DashboardAdapter(val context: Context, private val list: ArrayList<GetPendingRequestData>, val viewModel: DashboardViewModel, val onItemSelected: OnItemSelected<GetPendingRequestData>,) :  RecyclerView.Adapter<DashboardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemDashboardBinding>(
            layoutInflater,
            R.layout.item_dashboard,
            parent,
            false
        )
        return DashboardViewHolder(binder, viewModel)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(list[position])

        holder.binding.cardView.setOnClickListener {
            onItemSelected.onItemSelected(list[position], position)
        }
        holder.binding.txtVerificationFor.text = Utility.getNullToBlankString(list[position].getVerificationFor().toString())
        holder.binding.txtAssignDt.text = Utility.getNullToBlankString(Utility.convertDateTime(list[position].getFiassignedAt().toString()))
        holder.binding.txtApplicateName.text =  list[position].getApplicantName()  + " [Applicant]"
        holder.binding.txtAddress.text = list[position].getApplicantAddress() + ", "+list[position].getApplicantCity() +  ", "+ list[position].getApplicantPinCode() + ", "+  list[position].getApplicantState()
    }
    override fun getItemCount(): Int {
        return list.size
    }
}