package com.android.profkontur.View.Test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.profkontur.Model.QuestViewModelFactory
import com.android.profkontur.Model.Scale
import com.android.profkontur.Model.Scales
import com.android.profkontur.R
import com.android.profkontur.ViewModel.Test.TestsVIewModel
import com.google.android.material.transition.MaterialSharedAxis


class TestReportFragment : Fragment() {


    private lateinit var TestResultText : TextView
    private lateinit var TipTextView : TextView
    private lateinit var BackTestButton : Button



    private lateinit var viewModel: TestsVIewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SetTransitAnimation()
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

        initviews(view)

        InterpretationReport(viewModel.scaleScores.value, viewModel.AllTestData.value?.counting?.scales)
    }



    private fun initviews(view: View){
        BackTestButton = view.findViewById(R.id.BackTestButton);
        BackTestButton.setOnClickListener { findNavController().navigate(R.id.action_testReportFragment_to_navigation_home) }
        TestResultText = view.findViewById(R.id.TestResultText);
        TipTextView = view.findViewById(R.id.TipTextView);
    }
    //Интерпретация результатов теста
    private fun InterpretationReport(scores:Map<String,Int>,scales:Scales?) {
        if (scores.isNotEmpty()) {
            var highestScore = Int.MIN_VALUE // Начальное значение для самой высокой оценки
            var highestScoreScale: String? = null // Начальное значение для шкалы с самой высокой оценкой
            for ((scale, score) in scores) {
                if (score > highestScore) {
                    highestScore = score
                    highestScoreScale = scale
                }
            }
            when (scales) {
                is Scales.MapScales -> {
                    if(scales.scales?.get(highestScoreScale)?.desc?.isNotEmpty() == true) {
                        TestResultText.text = HtmlCompat.fromHtml(
                            scales.scales?.get(highestScoreScale)?.desc.toString(),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        TipTextView.text = HtmlCompat.fromHtml(
                            scales.scales?.get(highestScoreScale)?.tip.toString(),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }else{
                        TestResultText.text= viewModel.scaleScores.value.toString()
                    }
                }

                is Scales.SingleScale -> {
                    TestResultText.text = viewModel.scaleScores.value.toString()
                }
                else -> {

                }
            }

        }
    }

    private fun DecriptReport(scores:Map<String,Int>,scales:Scales?){

    }

    private fun SetTransitAnimation(){
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }


}