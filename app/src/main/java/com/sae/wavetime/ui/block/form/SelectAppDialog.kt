package com.sae.wavetime.ui.block.form

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.domain.usecase.CreateBlockWithKeyUseCase
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.block.list.BlockListViewModel
import com.sae.wavetime.ui.block.list.BlockListViewModelFactory
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.launch

class SelectAppDialog(
    private val onConfirm: (name: String, packageName: String) -> Unit
) : DialogFragment(R.layout.dialog_select_app) {
    private lateinit var adapter: AppSelectAdapter

    private val viewModel: BlockFormViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        val blockRepo = BlockRepository(
            db.blockDao(),
            AppIconResolver(requireContext().applicationContext),
            InstalledAppResolver(requireContext().applicationContext)
        )
        val itemRepo = ItemRepository(db.itemDao())

        BlockFormViewModelFactory(
            blockRepo,
            CreateBlockWithKeyUseCase(
                blockRepo,
                itemRepo
            )
        )
    }


    private fun render(state: BlockFormState, progressBarLayout: LinearLayout, appListLayout: ConstraintLayout) {
        if (state.isLoading) {
            progressBarLayout.visibility = View.VISIBLE
            appListLayout.visibility = View.GONE
        } else {
            progressBarLayout.visibility = View.GONE
            appListLayout.visibility = View.VISIBLE
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.apps)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBarLayout = view.findViewById<LinearLayout>(R.id.layoutLoading)
        val appListLayout = view.findViewById<ConstraintLayout>(R.id.layoutAppList)
        val rvApps = view.findViewById<RecyclerView>(R.id.rvApps)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)

        adapter = AppSelectAdapter{ app ->
            viewModel.setSelectedApp(app)
        }

        rvApps.layoutManager = LinearLayoutManager(requireContext())
        rvApps.adapter = adapter

        viewModel.loadApps()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state, progressBarLayout, appListLayout)
                }
            }
        }

        btnConfirm.setOnClickListener {
            val selectedApp = viewModel.state.value.selectedApp
            if (selectedApp != null) {
                onConfirm(selectedApp.appName, selectedApp.packageName)
                dismiss()
            } else {
                Log.d("cc", "Haven't selected app")
            }

        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}