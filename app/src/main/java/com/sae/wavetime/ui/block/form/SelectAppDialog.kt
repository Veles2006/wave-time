package com.sae.wavetime.ui.block.form

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.launch

class SelectAppDialog(
    private val onConfirm: (app: AppUiModel) -> Unit
) : DialogFragment(R.layout.dialog_select_app) {
    private lateinit var adapter: AppSelectAdapter
    private var selectedApp: AppUiModel? = null

    private val installedAppViewModel: InstalledAppViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        InstalledAppViewModelFactory(
            InstalledAppRepository(
                db.installedAppDao(),
                InstalledAppResolver(requireContext().applicationContext),
                AppIconResolver(requireContext().applicationContext)
            )
        )
    }


    private fun renderApps(
        apps: List<AppUiModel>,
        progressBarLayout: LinearLayout,
        appListLayout: ConstraintLayout
    ) {
        progressBarLayout.visibility = View.GONE
        appListLayout.visibility = View.VISIBLE
        adapter.submitList(apps)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBarLayout = view.findViewById<LinearLayout>(R.id.layoutLoading)
        val appListLayout = view.findViewById<ConstraintLayout>(R.id.layoutAppList)
        val rvApps = view.findViewById<RecyclerView>(R.id.rvApps)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)

        adapter = AppSelectAdapter{ app ->
            selectedApp = app
        }

        rvApps.layoutManager = LinearLayoutManager(requireContext())
        rvApps.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                installedAppViewModel.apps.collect { apps ->
                    renderApps(apps, progressBarLayout, appListLayout)
                }
            }
        }

        btnConfirm.setOnClickListener {
            val app = selectedApp
            if (app != null) {
                onConfirm(app)
                dismiss()
            } else {
                Log.d("cc", "Haven't selected app")
            }

        }
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
}