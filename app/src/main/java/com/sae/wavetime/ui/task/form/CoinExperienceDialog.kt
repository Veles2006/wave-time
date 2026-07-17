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
import androidx.core.graphics.drawable.toDrawable
import com.sae.wavetime.R

class CoinExperienceDialog(
    private val title: String,
    private val message: String,
    private val unit: String,
    private val maxValue: Int,
    private val initialValue: Int?,
    private val onConfirm: (Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogCoinExperienceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCoinExperienceBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        setupContent()
        setupListeners()
        showKeyboard()

        return dialog
    }

    private fun setupContent() {
        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message
        binding.tilValue.hint = unit

        binding.tilValue.helperText = getString(
            R.string.reward_input_range,
            maxValue
        )

        binding.edtValue.setText(initialValue?.toString().orEmpty())

        binding.edtValue.setSelection(
            binding.edtValue.text?.length ?: 0
        )
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val text = binding.edtValue.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val value = text.toIntOrNull()

            when {
                text.isBlank() -> {
                    binding.tilValue.error = getString(
                        R.string.error_value_required
                    )
                }

                value == null -> {
                    binding.tilValue.error = getString(
                        R.string.error_invalid_number
                    )
                }

                value !in 0..maxValue -> {
                    binding.tilValue.error = getString(
                        R.string.error_reward_value_range,
                        maxValue
                    )
                }

                else -> {
                    binding.tilValue.error = null
                    onConfirm(value)
                    dismiss()
                }
            }
        }
    }

    private fun showKeyboard() {
        binding.edtValue.requestFocus()

        binding.edtValue.postDelayed({
            val inputMethodManager = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager

            inputMethodManager.showSoftInput(
                binding.edtValue,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, 200)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}