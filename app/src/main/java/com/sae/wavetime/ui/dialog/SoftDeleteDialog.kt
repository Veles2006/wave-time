package com.sae.wavetime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.sae.wavetime.databinding.DialogSoftDeleteBinding
import androidx.core.graphics.drawable.toDrawable

class SoftDeleteDialog : DialogFragment() {

    private var _binding: DialogSoftDeleteBinding? = null
    private val binding get() = _binding!!

    private var onConfirm: (() -> Unit)? = null

    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirm = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSoftDeleteBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        val title = arguments?.getString(ARG_TITLE).orEmpty()
        val message = arguments?.getString(ARG_MESSAGE).orEmpty()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(
            title: String,
            message: String
        ): SoftDeleteDialog {
            return SoftDeleteDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }
}