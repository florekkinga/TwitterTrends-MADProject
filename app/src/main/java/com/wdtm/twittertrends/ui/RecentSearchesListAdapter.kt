package com.wdtm.twittertrends.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ContextUtils.getActivity
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.models.Location
import com.wdtm.twittertrends.models.Query
import com.wdtm.twittertrends.models.Trend

class RecentSearchesListAdapter(var dataSet: Array<Query>, private val dialogFragment: RecentSearchesFragment) :
    RecyclerView.Adapter<RecentSearchesListAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTextView: TextView = view.findViewById(R.id.searchTextView)
        val latLngTextView: TextView = view.findViewById(R.id.latLngTextView)
        val searchTrendsButton: AppCompatImageButton = view.findViewById(R.id.searchTrendsButton)
        val addMarkerButton: AppCompatImageButton = view.findViewById(R.id.addMarkerButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recent_search, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val query = dataSet[position]
        viewHolder.searchTextView.text = query.location.name
        val latLng = viewHolder.itemView.context.getString(R.string.latLang, query.location.latitude.toDouble(), query.location.longitude.toDouble())
        viewHolder.latLngTextView.text = latLng
        viewHolder.searchTrendsButton.setOnClickListener { searchTrends(query.trends) }
        viewHolder.addMarkerButton.setOnClickListener { addMarkerAndDismissDialog(query.location) }
    }

    @SuppressLint("RestrictedApi")
    private fun addMarkerAndDismissDialog(location: Location) {
        val latLng = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
        dialogFragment.dismiss()
        (getActivity(context) as MainActivity).addMarker(latLng)
    }

    @SuppressLint("RestrictedApi")
    private fun searchTrends(trends: List<Trend>) {
        dialogFragment.dismiss()
        (getActivity(context) as MainActivity).showRecentTrends(trends)
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun getItemCount() = dataSet.size
}
