package com.sae.wavetime.ui.block.form

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.databinding.DialogSelectAppBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.launch

class SelectAppDialog(
    private val onConfirm: (app: AppUiModel) -> Unit
) : DialogFragment() {

    private var _binding: DialogSelectAppBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AppSelectAdapter
    private var selectedApp: AppUiModel? = null

    private val installedAppViewModel: InstalledAppViewModel by viewModels {
        val appContext = requireContext().applicationContext
        val db = DatabaseProvider.getDatabase(appContext)

        val appIconResolver = AppIconResolver(appContext)
        val installedAppResolver = InstalledAppResolver(appContext)

        InstalledAppViewModelFactory(
            installedAppRepo = InstalledAppRepository(
                db.installedAppDao(),
                installedAppResolver,
                appIconResolver
            ),
            blockRepo = BlockRepository(
                blockDao = db.blockDao(),
                appIconResolver = appIconResolver,
                installedAppResolver = installedAppResolver
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectAppBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = AppSelectAdapter { app ->
            selectedApp = app
            binding.btnConfirm.isEnabled = true
        }

        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectAppDialog.adapter
        }
    }

    private fun setupListeners() {
        binding.edtAppSearch.doAfterTextChanged { editable ->
            selectedApp = null
            adapter.clearSelection()
            binding.btnConfirm.isEnabled = false

            installedAppViewModel.searchApps(
                editable?.toString().orEmpty()
            )
        }

        binding.btnConfirm.setOnClickListener {
            val app = selectedApp

            if (app != null) {
                onConfirm(app)
                dismiss()
            } else {
                Log.d(TAG, "No app selected")
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                installedAppViewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: InstalledAppUiState) {
        val hasApps = state.apps.isNotEmpty()

        binding.layoutLoading.isVisible = state.isLoading

        binding.layoutAppList.isVisible =
            !state.isLoading && hasApps

        binding.tvEmpty.isVisible =
            !state.isLoading && !hasApps

        adapter.submitList(state.apps)

        isCancelable = !state.isLoading
        dialog?.setCanceledOnTouchOutside(!state.isLoading)

        binding.edtAppSearch.isEnabled = !state.isLoading
        binding.rvApps.isEnabled = !state.isLoading

        binding.btnConfirm.isEnabled =
            !state.isLoading && selectedApp != null
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            setLayout(
                (resources.displayMetrics.widthPixels * 0.92).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onDestroyView() {
        binding.rvApps.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "SelectAppDialog"
    }
}