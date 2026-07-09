package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository

class InstalledAppViewModelFactory(
    private val installedAppRepo: InstalledAppRepository,
    private val blockRepo: BlockRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstalledAppViewModel::class.java)) {
            return InstalledAppViewModel(installedAppRepo, blockRepo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}