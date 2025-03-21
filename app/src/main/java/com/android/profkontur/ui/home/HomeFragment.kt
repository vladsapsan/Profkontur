package com.android.profkontur.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.android.profkontur.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private lateinit var TestButton: Button
    private val binding get() = _binding!!

    private lateinit var recyclerMethodicView: RecyclerView
    private lateinit var viewModel: HomeViewModel

    private lateinit var ProgressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.loadMethodicTestList("motivation") // Загружаем список тестов при создании фрагмента

        TestButton = view.findViewById(R.id.TakeTestButtons)
        TestButton.setOnClickListener{
            openSecondFragment()
        }
    }



    private fun startTest(test:MethodicItemModel){
        val bundle = Bundle().apply {
            putString("testName", test.machine_name) //  Ключ ("testName") должен соответствовать ключу, который вы будете использовать для получения данных
        }
        findNavController().navigate(R.id.action_navigation_home_to_metaTestFragment, bundle)
    }

    //Запуск фрагмента с тестом
    private fun openSecondFragment() {
        findNavController().navigate(R.id.action_navigation_home_to_metaTestFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}