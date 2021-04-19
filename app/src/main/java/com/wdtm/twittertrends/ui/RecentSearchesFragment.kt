package com.wdtm.twittertrends.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.models.Query

class RecentSearchesFragment : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private var dataSet: Array<Query> = arrayOf<Query>()
    private var adapter: RecentSearchesListAdapter = RecentSearchesListAdapter(dataSet)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, container)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        context?.let { adapter.setContext(it) }
        recyclerView.adapter = adapter
        return view
    }

    fun loadSearchHistory(recentSearches: Array<Query>) {
        adapter.dataSet = recentSearches
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): RecentSearchesFragment {
            val frag = RecentSearchesFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }
}