package com.wdtm.twittertrends.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.models.Trend


class TrendsListAdapter(var dataSet: Array<Trend>) :
    RecyclerView.Adapter<TrendsListAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trendTextView: TextView = view.findViewById(R.id.trendTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trend, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val trend = dataSet[position]
        viewHolder.trendTextView.text = trend.name
        viewHolder.itemView.setOnClickListener { goToTwitter(trend.url) }
    }

    private fun goToTwitter(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun getItemCount() = dataSet.size
}
