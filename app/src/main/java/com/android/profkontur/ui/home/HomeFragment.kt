package com.android.profkontur.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private lateinit var ProfMethodicButton: CardView
    private lateinit var MotivationMethodicButton: CardView


    private lateinit var viewModel: HomeViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)

        MotivationMethodicButton = view.findViewById(R.id.MotivationMethodicButton)
        MotivationMethodicButton.setOnClickListener {startMethodic("motivation")}
        ProfMethodicButton= view.findViewById(R.id.ProfMethodicButton)
        ProfMethodicButton.setOnClickListener { startMethodic("proforientation")}
    }

    private fun startMethodic(Methodic: String){
        val bundle = Bundle().apply {
            putString("Methodic", Methodic) //  Ключ ("testName") должен соответствовать ключу, который вы будете использовать для получения данных
        }
        findNavController().navigate(R.id.action_navigation_home_to_methodicFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}