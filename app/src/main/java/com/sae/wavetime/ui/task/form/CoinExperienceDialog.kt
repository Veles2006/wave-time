package com.sae.wavetime.ui.task.form

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sae.wavetime.R

class CoinExperienceDialog(
    private val onConfirm: (String) -> Unit
) : DialogFragment(R.layout.dialog_coin_experience) {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilCoinOrExperience = view.findViewById<TextInputLayout>(R.id.tilCoinOrExperience)
        val edtCoinOrExperience = view.findViewById<TextInputEditText>(R.id.edtCoinOrExperience)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {

            val text = edtCoinOrExperience.text?.toString()?.trim()

            if (text.isNullOrEmpty()) {
                tilCoinOrExperience.error = "This field cannot be empty."
                return@setOnClickListener
            } else {
                tilCoinOrExperience.error = null
            }

            onConfirm(text)
            dismiss()
        }
    }
}