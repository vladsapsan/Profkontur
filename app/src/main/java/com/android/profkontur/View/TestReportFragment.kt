package com.android.profkontur.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.android.profkontur.Model.QuestViewModelFactory
import com.android.profkontur.R
import com.android.profkontur.ViewModel.TestsVIewModel


class TestReportFragment : Fragment() {


    private lateinit var TestResultText : TextView

    private lateinit var viewModel: TestsVIewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment(), QuestViewModelFactory(requireActivity()))[TestsVIewModel::class.java]

        TestResultText= view.findViewById(R.id.TestResultText);
        TestResultText.text = (viewModel.scaleScores.value.toString())
    }

}