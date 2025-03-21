package com.android.profkontur.View.Test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MetaData
import com.android.profkontur.Model.QuestViewModelFactory
import com.android.profkontur.R
import com.android.profkontur.ViewModel.Test.TestsVIewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MetaTestFragment() : Fragment() {

    private lateinit var TestName:String

    private  lateinit var DscTestTextView: TextView
    private lateinit var ProgressBar: ProgressBar
    private lateinit var AboutTestLayout: LinearLayout
    private lateinit var TestLayuot: LinearLayout
    private  lateinit var TestNameTextView: TextView
    private  lateinit var TimeTestTextView: TextView
    private  lateinit var QuestionCountTextView: TextView
    private lateinit var TypeTestTextView:TextView
    private  lateinit var StartTestButton: Button
    private  lateinit var MetaData: MetaData


    private lateinit var viewModel: TestsVIewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            TestName = it.getString("testName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meta_test, container, false)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videModelInit()

        initAboutTestViews(view)

        StartTestButton.setOnClickListener {
            StartTest()
        }

        // Наблюдение за состоянием загрузки
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingstate.collectLatest { loadingState ->
                    when (loadingState) {
                        is LoadingState.Loading -> {
                            ProgressBar.visibility = View.VISIBLE
                            AboutTestLayout.visibility = View.GONE
                        }
                        is LoadingState.Ready -> {
                            DisplayMeta()
                            ProgressBar.visibility = View.GONE
                            AboutTestLayout.visibility = View.VISIBLE
                        }
                        is LoadingState.Error -> {
                            ProgressBar.visibility = View.GONE
                            AboutTestLayout.visibility = View.GONE
                            Toast.makeText(requireContext(), loadingState.messege, Toast.LENGTH_SHORT).show()


                        }
                    }
                }
            }
        }
    }

    private fun StartTest(){
        findNavController().navigate(R.id.action_metaTestFragment_to_aboutTestFragment)
    }

    fun videModelInit(){
        viewModel = ViewModelProvider(requireParentFragment(), QuestViewModelFactory(requireActivity()))[TestsVIewModel::class.java]
        if(viewModel.AllTestData.value?.meta?.machine_name!=TestName){
            viewModel.TestReset()
        }
        viewModel.LoadDataFromApi(TestName)
    }
    fun DisplayMeta(){
        MetaData = viewModel.getCurrentMeta()!!
        if(MetaData!=null){
            DscTestTextView.text = MetaData.short_dsc
            TestNameTextView.text = MetaData.name
            TimeTestTextView.text = MetaData.time
            if(MetaData.prefix_name!=null){TypeTestTextView.text = MetaData.prefix_name}else{TypeTestTextView.text="Тест"}
            QuestionCountTextView.text = MetaData.questions
        }else{

        }
    }

    fun initAboutTestViews(view: View){
        QuestionCountTextView = view.findViewById(R.id.QuestionCountTextView);
        TypeTestTextView = view.findViewById(R.id.TypeTestTextView);
        DscTestTextView = view.findViewById(R.id.DscTestTextView);
        TestNameTextView = view.findViewById(R.id.TestNameTextView);
        TimeTestTextView = view.findViewById(R.id.TimeTestTextView);
        AboutTestLayout= view.findViewById(R.id.AboutTestLayout);
        TestLayuot= view.findViewById(R.id.TestLayuot);
        ProgressBar= view.findViewById(R.id.ProgressBar);
        StartTestButton = view.findViewById(R.id.StartTestButton);
    }

}