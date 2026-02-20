package com.sae.wavetime.data.repository

import com.sae.wavetime.data.model.BlockOfItem
import com.sae.wavetime.data.model.Item
import com.sae.wavetime.data.model.KeyInfoPopulated

class ItemRepository {

    suspend fun getItems(): List<Item> {
        return listOf(
            Item(
                id = "1",
                name = "White Key",
                tier = "white",
                rank = 1,
                category = "key",
                keyInfo = KeyInfoPopulated(
                    blockId = BlockOfItem(
                        id = "1",
                        appName = "Youtube",
                        packageName = "com.google.android.youtube",
                        blockType = "permanent"
                    ),
                    isMaster = false
                ),
                description = "White Key Description",
                icon = "icon"
            ),
            Item(
                id = "2",
                name = "Green Key",
                tier = "green",
                rank = 2,
                category = "key",
                keyInfo = KeyInfoPopulated(
                    blockId = BlockOfItem(
                        id = "1",
                        appName = "Youtube",
                        packageName = "com.google.android.youtube",
                        blockType = "permanent"
                    ),
                    isMaster = false
                ),
                description = "Green Key Description",
                icon = "icon"
            )
        )
    }
}