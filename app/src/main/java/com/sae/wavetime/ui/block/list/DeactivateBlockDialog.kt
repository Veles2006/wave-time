package com.sae.wavetime.ui.block.list

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.sae.wavetime.R
import com.sae.wavetime.databinding.DialogDeactiveBlockBinding

class DeactivateBlockDialog : DialogFragment() {
    private var _binding: DialogDeactiveBlockBinding? = null
    private val binding get() = _binding!!

    private var onDeactivate: (() -> Unit)? = null
    private var countDownTimer: CountDownTimer? = null

    private var isConfirmationReady = false

    fun setOnDeactivateListener(listener: () -> Unit) {
        onDeactivate = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDeactiveBlockBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        val initialTitle =
            arguments?.getString(ARG_INITIAL_TITLE).orEmpty()

        val initialMessage =
            arguments?.getString(ARG_INITIAL_MESSAGE).orEmpty()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.tvDialogTitle.text = initialTitle
        binding.tvDialogMessage.text = initialMessage

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDeactivate.setOnClickListener {
            if (isConfirmationReady) {
                onDeactivate?.invoke()
                dismiss()
            } else {
                startConfirmationCountdown()
            }
        }

        return dialog
    }

    private fun applyCountdownButtonStyle() {
        binding.btnDeactivate.isEnabled = false

        binding.btnDeactivate.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_disabled
                )
            )

        binding.btnDeactivate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.button_disabled_text
            )
        )
    }

    private fun applyConfirmButtonStyle() {
        binding.btnDeactivate.isEnabled = true

        binding.btnDeactivate.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.error
                )
            )

        binding.btnDeactivate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.on_primary
            )
        )
    }

    private fun startConfirmationCountdown() {
        val warningTitle =
            arguments?.getString(ARG_WARNING_TITLE).orEmpty()

        val warningMessage =
            arguments?.getString(ARG_WARNING_MESSAGE).orEmpty()
        binding.tvDialogTitle.text = warningTitle
        binding.tvDialogMessage.text = warningMessage

        applyCountdownButtonStyle()

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(
            CONFIRMATION_DELAY_MILLIS,
            COUNTDOWN_INTERVAL_MILLIS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining =
                    (millisUntilFinished + 999L) / 1000L

                binding.btnDeactivate.text = getString(
                    R.string.deactivate_countdown,
                    secondsRemaining
                )
            }

            override fun onFinish() {
                isConfirmationReady = true

                binding.btnDeactivate.setText(R.string.confirm_deactivation)

                applyConfirmButtonStyle()
            }
        }.start()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        countDownTimer = null

        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DeactivateBlockDialog"
        private const val ARG_INITIAL_TITLE =
            "initial_title"

        private const val ARG_INITIAL_MESSAGE =
            "initial_message"

        private const val ARG_WARNING_TITLE =
            "warning_title"

        private const val ARG_WARNING_MESSAGE =
            "warning_message"
        private const val CONFIRMATION_DELAY_MILLIS = 10_000L
        private const val COUNTDOWN_INTERVAL_MILLIS = 1_000L

        fun newInstance(
            initialTitle: String,
            initialMessage: String,
            warningTitle: String,
            warningMessage: String
        ): DeactivateBlockDialog {
            return DeactivateBlockDialog().apply {
                arguments = Bundle().apply {
                    putString(
                        ARG_INITIAL_TITLE,
                        initialTitle
                    )

                    putString(
                        ARG_INITIAL_MESSAGE,
                        initialMessage
                    )

                    putString(
                        ARG_WARNING_TITLE,
                        warningTitle
                    )

                    putString(
                        ARG_WARNING_MESSAGE,
                        warningMessage
                    )
                }
            }
        }
    }
}