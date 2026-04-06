package com.sae.wavetime.ui.task.create

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.model.RewardSelectUiModel
import kotlinx.coroutines.launch

class SelectItemDialog(
    private val onConfirm: (List<RewardSelectUiModel>) -> Unit
) : DialogFragment(R.layout.dialog_select_item) {

    private lateinit var adapter: RewardSelectAdapter

    private val viewModel: RewardSelectViewModel by viewModels {
        RewardSelectViewModelFactory(
            ItemRepository(
                DatabaseProvider.getDatabase(requireContext()).itemDao()
            )
        )
    }

    private fun render(state: RewardSelectState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.rewards)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvItems = view.findViewById<RecyclerView>(R.id.rvItems)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)

        adapter = RewardSelectAdapter(
            onIncrease = { reward ->
                viewModel.increaseQuantity(reward)
            },
            onDecrease = { reward ->
                viewModel.decreaseQuantity(reward)
            }
        )

        rvItems.layoutManager = LinearLayoutManager(requireContext())
        rvItems.adapter = adapter

        // 👇 gọi load data
        viewModel.loadRewards()

        // 👇 collect state (an toàn lifecycle)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }

        // 👇 set size dialog (phải làm ở đây)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        btnConfirm.setOnClickListener {
            val selected = viewModel.state.value.rewards
                .filter { it.quantity > 0 }

            onConfirm(selected)
            dismiss()
        }
    }
}