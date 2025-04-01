package com.android.profkontur.View.Methodic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MethodicItemModel
import com.android.profkontur.Model.MethodicTestListAdapter
import com.android.profkontur.R
import com.android.profkontur.ViewModel.Methodic.MethodicViewModel
import com.android.profkontur.ui.home.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class MethodicFragment : Fragment() {


    private lateinit var recyclerMethodicView: RecyclerView
    private lateinit var viewModel: MethodicViewModel

    private lateinit var ProgressBar: ProgressBar



    private lateinit var GetReportButton: MaterialButton
    private lateinit var CurrentDoneTestsNumber: TextView
    private lateinit var AllTestsNUmber: TextView
    private lateinit var MethodicName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SetTransitAnimation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_methodic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireParentFragment()).get(MethodicViewModel::class.java)

        if(viewModel.Methodic.value == ""){
            arguments?.let {
                MethodicName = it.getString("Methodic").toString()
                viewModel.Methodic.value = MethodicName
                viewModel.loadMethodicTestList(MethodicName)
            }
        }else{
            MethodicName = viewModel.Methodic.value.toString()
        }

        Init(view)

        viewModel.testList.observe(viewLifecycleOwner, Observer { testList ->
            if (testList != null) {
                val adapter = MethodicTestListAdapter(testList) { testItem ->
                    // Обработка нажатия на карточку теста
                    if(testItem.isDone==true){
                        Toast.makeText(context,"Тест уже пройден",Toast.LENGTH_SHORT).show()
                    }else{
                    startTest(testItem)
                    }
                }
                recyclerMethodicView.adapter = adapter
            }
        })


        viewModel.TestMethodicAnswerResponse.observe(viewLifecycleOwner, Observer { Response ->
            if(Response.result_url!=null){
                startResultWebViewMethodic()
            }
        })

        // Наблюдение за состоянием загрузки
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingstate.collectLatest { loadingState ->
                    when (loadingState) {
                        is LoadingState.Loading -> {
                            ProgressBar.visibility = View.VISIBLE
                            recyclerMethodicView.visibility = View.GONE
                        }
                        is LoadingState.Ready -> {
                            AllTestsNUmber.text = viewModel.TestSCount.value.toString()
                            CurrentDoneTestsNumber.text = viewModel.DoneTestSCount.value.toString()
                            if (AllTestsNUmber.text == CurrentDoneTestsNumber.text) {
                                GetReportButton.visibility = View.VISIBLE
                            }
                            ProgressBar.visibility = View.GONE
                            recyclerMethodicView.visibility = View.VISIBLE
                        }
                        is LoadingState.Error -> {
                            ProgressBar.visibility = View.GONE
                            recyclerMethodicView.visibility = View.GONE
                            Toast.makeText(requireContext(), loadingState.messege, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        // Загружаем список тестов при создании фрагмента
        if(MethodicName!="") {

        }else{

        }

        GetReportButton.setOnClickListener {
            startResultWebViewMethodic()
        }

    }


    private fun startResultWebViewMethodic(){
        if(viewModel.TestMethodicAnswerResponse.value?.result_url==null){
            viewModel.SendTestResult()
        }else{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.TestMethodicAnswerResponse.value?.result_url))
            startActivity(intent)
      //  val bundle = Bundle().apply {

            
      //      putString("WebUri", viewModel.TestMethodicAnswerResponse.value?.result_url) //  Ключ ("testName") должен соответствовать ключу, который вы будете использовать для получения данных
        //}
      //  findNavController().navigate(R.id.action_methodicFragment_to_webViewRelustFragment, bundle)
        }
    }

    private fun Init(view: View){
        GetReportButton= view.findViewById(R.id.GetReportButton)
        ProgressBar = view.findViewById(R.id.ProgressBar)
        recyclerMethodicView = view.findViewById(R.id.recycler_view)
        recyclerMethodicView.layoutManager = LinearLayoutManager(context)
        AllTestsNUmber = view.findViewById(R.id.AllTestsNUmber);
        CurrentDoneTestsNumber= view.findViewById(R.id.CurrentDoneTestsNumber);
    }

    private fun SetTransitAnimation(){
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }

    private fun startTest(test: MethodicItemModel){
        val bundle = Bundle().apply {
            putString("testName", test.machine_name) //  Ключ ("testName") должен соответствовать ключу, который вы будете использовать для получения данных
        }
        findNavController().navigate(R.id.action_methodicFragment_to_metaTestFragment, bundle)
    }

}