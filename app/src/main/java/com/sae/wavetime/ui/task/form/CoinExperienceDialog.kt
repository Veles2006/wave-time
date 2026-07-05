package com.sae.wavetime.ui.task.form

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import com.sae.wavetime.databinding.DialogCoinExperienceBinding

class CoinExperienceDialog(
    private val title: String,
    private val message: String,
    private val unit: String,
    private val errorMessage: String,
    private val onConfirm: (String) -> Unit
) : DialogFragment() {

    private var _binding: DialogCoinExperienceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCoinExperienceBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message
        binding.tilValue.hint = unit

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val value = binding.edtValue.text?.toString()?.trim().orEmpty()

            if (value.isBlank()) {
                binding.tilValue.error = errorMessage
                return@setOnClickListener
            }

            binding.tilValue.error = null
            onConfirm(value)
            dismiss()
        }

        binding.edtValue.requestFocus()
        binding.edtValue.postDelayed({
            val imm = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtValue, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}