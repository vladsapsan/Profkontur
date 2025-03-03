package com.android.profkontur.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.profkontur.ChekingTestFragment
import com.android.profkontur.R
import com.android.profkontur.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private lateinit var TestButton:Button
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TestButton = view.findViewById(R.id.TakeTestButtons)
        TestButton.setOnClickListener{
            openSecondFragment()
        }
    }


    //Запуск фрагмента с тестом
    private fun openSecondFragment() {
        findNavController().navigate(R.id.action_navigation_home_to_chekingTestFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}