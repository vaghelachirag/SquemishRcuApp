package com.squmish.rcuapp.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squmish.rcuapp.viewmodel.verificationDetail.PictureViewModel
import com.google.android.material.button.MaterialButton
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.ItemPicturesBinding
import com.squmish.rcuapp.model.getverificationDetailResponse.GetFiVerificationDocument
import com.squmish.rcuapp.uttils.AppConstants


class PicturesViewHolder(val binding: ItemPicturesBinding, val context: Context, val viewModel: PictureViewModel) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ResourceAsColor")
    fun bind(data: GetFiVerificationDocument) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel

        Glide.with(context)
            .load(AppConstants.baseURLImage + data.documentPath)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.ivPicture);
    }

    private fun deletePictureButtonClicked(position: Int,data: GetFiVerificationDocument?) {
        Log.e("onDelete", "OnDelete$position" + ""+data!!.getDocumentId())
        viewModel.deleteSurveyPicture(context, data.getDocumentId()!!,position)
    }

    fun onClickDelete(position: Int, data: GetFiVerificationDocument?) {
        showAlertDialog(context,"Are you sure want to Delete?",data)
    }

    public fun showAlertDialog(context: Context, strTitle: String, data: GetFiVerificationDocument?) {


        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.attributes.windowAnimations = R.style.DialogTheme;
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_alert_dialoug)

        dialog.window!!.setBackgroundDrawableResource(R.color.dialoug_main_bg);

        val txtHeader  : TextView = dialog.findViewById(R.id.tvMessage)
        txtHeader.text = strTitle

        // Button
        val buttonOk : MaterialButton = dialog.findViewById(R.id.btnOk)
        val buttonCancel : MaterialButton = dialog.findViewById(R.id.btnCancel)

        buttonOk.setOnClickListener {
            deletePictureButtonClicked(position,data)
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}