package com.sae.wavetime.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.sae.wavetime.R

class SoftDeleteDialog : DialogFragment(R.layout.dialog_confirm_delete) {

    private var onConfirm: (() -> Unit)? = null

    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirm = listener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)

        tvTitle.text = arguments?.getString("title") ?: ""
        // callback = không survive lifecycle, nên sửa trong tương lai
        btnConfirm.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }
    companion object {
        fun newInstance(title: String): SoftDeleteDialog {
            val dialog = SoftDeleteDialog()
            val bundle = Bundle()
            bundle.putString("title", title)
            dialog.arguments = bundle
            return dialog
        }
    }
}