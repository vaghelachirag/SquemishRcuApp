package com.squmish.rcuapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squmish.rcuapp.R
import com.squmish.rcuapp.model.dashboard.DashboardDataModel

class DashboardSelectionAdapter(var context: Context) :  RecyclerView.Adapter<DashboardSelectionAdapter.ViewHolder>(){

    private var dataList = emptyList<DashboardDataModel>()

    internal fun setDataList(dataList: List<DashboardDataModel>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var title: TextView = itemView.findViewById(R.id.title)
        var desc: TextView = itemView.findViewById(R.id.desc)
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardSelectionAdapter.ViewHolder {

        // Inflate the custom layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_selection, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: DashboardSelectionAdapter.ViewHolder, position: Int) {

        // Get the data model based on position
        val data = dataList[position]

        // Set item views based on your views and data model
        holder.title.text = data.title
        holder.desc.text = data.desc

        holder.image.setImageResource(data.image)
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}