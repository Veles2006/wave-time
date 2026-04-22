package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.BlockDao
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.model.domain.Block

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