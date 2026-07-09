package com.sae.wavetime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.sae.wavetime.databinding.DialogFeatureGuideBinding

class FeatureGuideDialog : DialogFragment() {
    private var _binding: DialogFeatureGuideBinding? = null
    private val binding get() = _binding!!
    private var onClose: (() -> Unit)? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFeatureGuideBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        val title = arguments?.getString(FeatureGuideDialog.Companion.ARG_TITLE).orEmpty()
        val message = arguments?.getString(FeatureGuideDialog.Companion.ARG_MESSAGE).orEmpty()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.tvGuideTitle.text = title
        binding.tvGuideMessage.text = message

        binding.root.setOnClickListener {
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
        ): FeatureGuideDialog {
            return FeatureGuideDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }
}