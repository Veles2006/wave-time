package com.sae.wavetime.ui.task.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskHistoryBinding
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.launch

class TaskHistoryFragment : Fragment(R.layout.fragment_task_history) {
    private lateinit var adapter: HistoryAdapter
    private var _binding: FragmentTaskHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskHistoryViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        val taskRepo = TaskRepository(
            db.taskDao(),
            db.taskTemplateDao()
        )

        TaskHistoryViewModelFactory(taskRepo)
    }

    private fun render(state: TaskHistoryState) {
        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.items)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskHistoryBinding.bind(view)

        adapter = HistoryAdapter()

        viewModel.loadHistory()

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}