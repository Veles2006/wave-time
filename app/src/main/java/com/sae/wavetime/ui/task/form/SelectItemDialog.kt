package com.sae.wavetime.ui.task.form

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.DialogSelectItemBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.UiText
import com.sae.wavetime.ui.common.asString
import com.sae.wavetime.ui.model.RewardSelectUiModel
import kotlinx.coroutines.launch

class SelectItemDialog(
    private val onConfirm: (List<RewardSelectUiModel>) -> Unit,
) : DialogFragment(R.layout.dialog_select_item) {

    private var _binding: DialogSelectItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RewardSelectAdapter

    private fun showRewardSelectionMessage(message: UiText) {
        Toast.makeText(
            requireContext(),
            message.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()

        viewModel.clearRewardSelectionMessage()
    }

    private val viewModel: TaskFormViewModel by activityViewModels {
        val database = DatabaseProvider.getDatabase(requireContext())

        TaskFormViewModelFactory(
            TaskRepository(
                database.taskDao(),
                database.taskTemplateDao(),
            ),
            ItemRepository(
                database.itemDao(),
            ),
            AnalyticsTracker(requireContext()),
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DialogSelectItemBinding.bind(view)

        setupRecyclerView()
        observeState()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()

        val dialogWidth = (resources.displayMetrics.widthPixels * 0.9).toInt()

        dialog?.window?.apply {
            setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    private fun setupRecyclerView() {
        adapter = RewardSelectAdapter(
            onIncrease = { reward ->
                viewModel.increaseQuantity(reward)
            },
            onDecrease = { reward ->
                viewModel.decreaseQuantity(reward)
            },
        )

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectItemDialog.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.edtItemSearch.doAfterTextChanged { editable ->
            viewModel.onRewardSearchQueryChanged(
                editable?.toString().orEmpty()
            )
        }

        binding.btnConfirm.setOnClickListener {
            val selectedRewards =
                viewModel.state.value.selectedRewards

            onConfirm(selectedRewards)
            dismiss()
        }
    }

    private fun render(state: TaskFormState) {
        val hasItems = state.filteredAvailableRewards.isNotEmpty()

        binding.rvItems.isVisible = hasItems
        binding.tvNoItem.isVisible = !hasItems

        adapter.submitList(state.filteredAvailableRewards)

        state.rewardSelection.message?.let { message ->
            showRewardSelectionMessage(message)
        }
    }

    override fun onDestroyView() {
        binding.rvItems.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
