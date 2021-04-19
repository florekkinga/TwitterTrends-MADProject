package com.wdtm.twittertrends.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.models.Query

class RecentSearchesListAdapter(var dataSet: Array<Query>) :
    RecyclerView.Adapter<RecentSearchesListAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchTextView: TextView = view.findViewById(R.id.searchTextView)
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
        viewHolder.searchTrendsButton.setOnClickListener { searchTrends() }
        viewHolder.addMarkerButton.setOnClickListener { addMarkerAndDismissDialog() }
    }

    private fun addMarkerAndDismissDialog() {
        TODO("Not yet implemented")
    }

    private fun searchTrends() {
        TODO("Not yet implemented")
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun getItemCount() = dataSet.size
}
