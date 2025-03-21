package com.android.profkontur.View.Methodic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MethodicFragment : Fragment() {


    private lateinit var recyclerMethodicView: RecyclerView
    private lateinit var viewModel: MethodicViewModel

    private lateinit var ProgressBar: ProgressBar
    private lateinit var MethodicName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            MethodicName = it.getString("Methodic").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_methodic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MethodicViewModel::class.java)

        ProgressBar = view.findViewById(R.id.ProgressBar)

        recyclerMethodicView = view.findViewById(R.id.recycler_view)
        recyclerMethodicView.layoutManager = LinearLayoutManager(context)

        viewModel.testList.observe(viewLifecycleOwner, Observer { testList ->
            if (testList != null) {
                val adapter = MethodicTestListAdapter(testList) { testItem ->
                    // Обработка нажатия на карточку теста
                    startTest(testItem)
                }
                recyclerMethodicView.adapter = adapter
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
        if(MethodicName.isNotEmpty()) {
            viewModel.loadMethodicTestList(MethodicName)
        }else{

        }

    }



    private fun startTest(test: MethodicItemModel){
        val bundle = Bundle().apply {
            putString("testName", test.machine_name) //  Ключ ("testName") должен соответствовать ключу, который вы будете использовать для получения данных
        }
        findNavController().navigate(R.id.action_methodicFragment_to_metaTestFragment, bundle)
    }

}