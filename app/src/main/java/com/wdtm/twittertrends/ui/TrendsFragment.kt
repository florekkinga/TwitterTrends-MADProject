package com.wdtm.twittertrends.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.wdtm.twittertrends.R

class TrendsFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_trends, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val title = arguments!!.getString("title", "Enter Name")
            dialog?.setTitle(title)
    }

    companion object {
        fun newInstance(title: String?): TrendsFragment {
            val frag = TrendsFragment()
            val args = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }
}