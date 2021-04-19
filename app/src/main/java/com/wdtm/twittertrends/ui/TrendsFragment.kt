package com.wdtm.twittertrends.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdtm.twittertrends.BuildConfig.DEBUG
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.models.Trend

class TrendsFragment : DialogFragment() {

    private lateinit var trendsRecyclerView: RecyclerView
    private var dataSet: Array<Trend> = arrayOf<Trend>()
    private var adapter: TrendsListAdapter = TrendsListAdapter(dataSet)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_trends, container)
        trendsRecyclerView = view.findViewById(R.id.trendRecycleView)
        trendsRecyclerView.layoutManager = LinearLayoutManager(context)
        context?.let { adapter.setContext(it) }
        trendsRecyclerView.adapter = adapter
        return view
    }

    fun loadTrends(trends: Array<Trend>){
        adapter.dataSet = trends
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): TrendsFragment {
            val frag = TrendsFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }
}