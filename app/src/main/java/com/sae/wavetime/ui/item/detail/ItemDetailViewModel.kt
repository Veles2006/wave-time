package com.sae.wavetime.ui.item.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.domain.usecase.ObserveInventoryDetailUseCase
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.ui.common.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val itemRepo: ItemRepository,
    private val useItemUseCase: UseItemUseCase,
    private val observeInventoryDetailUseCase: ObserveInventoryDetailUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ItemDetailState())
    val state: StateFlow<ItemDetailState> = _state

    fun observeInventoryItem(itemId: String) {
        viewModelScope.launch {
            observeInventoryDetailUseCase(itemId)
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Không thể tải vật phẩm"
                        )
                    }
                }
                .collectLatest { inventoryItem ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            inventoryItem = inventoryItem,
                            error = if (inventoryItem == null) {
                                "Không tìm thấy vật phẩm"
                            } else {
                                null
                            }
                        )
                    }
                }
        }
    }
    fun useItem(itemId: String, amount: Int = 1) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            runCatching {
                val item = itemRepo.getItemById(itemId)
                    ?: error("Item not found")

                useItemUseCase.execute(itemId, amount)

                item
            }.onSuccess { item ->
                val durationMinutes = item.keyInfo.durationMinutes

                _state.update {
                    it.copy(
                        isLoading = false,
                        notificationMessage = if (durationMinutes > 0) {
                            UiText.StringResource(
                                resId = R.string.item_used_with_duration,
                                args = listOf(item.name, durationMinutes)
                            )
                        } else {
                            UiText.StringResource(
                                resId = R.string.item_used,
                                args = listOf(item.name)
                            )
                        }
                    )
                }
            }.onFailure { e ->
                Log.e("ItemDetailViewModel", "Use item failed", e)

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Use item failed"
                    )
                }
            }
        }
    }

}