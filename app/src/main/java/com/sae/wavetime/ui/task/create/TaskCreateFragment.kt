package com.sae.wavetime.ui.task.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.databinding.FragmentTaskCreateBinding

class TaskCreateFragment : Fragment(R.layout.fragment_task_create) {
    private lateinit var adapter: TaskRewardAdapter
    private var _binding: FragmentTaskCreateBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskCreateBinding.bind(view)

        adapter = TaskRewardAdapter()

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showMainUi(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).showMainUi(true)
        _binding = null
    }
}