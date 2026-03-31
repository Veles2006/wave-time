package com.sae.wavetime.ui.task.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskListBinding
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.launch

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private lateinit var adapter: TaskAdapter
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(
            TaskRepository(
                DatabaseProvider.getDatabase(requireContext()).taskDao()
            )
        )
    }

    private fun render(state: TaskListState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.tasks)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskListBinding.bind(view)

        adapter = TaskAdapter()

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
        viewModel.loadTasks()

        binding.btnCreateTask.setOnClickListener {
            findNavController().navigate(R.id.taskCreateFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}