package com.sae.wavetime.data.repository

import android.content.Context
import androidx.core.content.ContextCompat
import com.sae.wavetime.R
import com.sae.wavetime.data.dao.BlockDao
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.model.api.Block
import com.sae.wavetime.ui.model.AppUiModel

class BlockRepository(
    private val blockDao: BlockDao
) {

    suspend fun getBlocks(): List<Block> {
        return blockDao.getAll().toDomainList()
    }

//    suspend fun getApps(): List<AppUiModel> {
//        return listOf(
//            AppUiModel(
//                appName = "Youtube",
//                packageName = "com.google.android.youtube",
//                icon = ContextCompat.getDrawable(
//                    context,
//                    R.drawable.waifu_1
//                )!!
//            )
//        )
//    }
}