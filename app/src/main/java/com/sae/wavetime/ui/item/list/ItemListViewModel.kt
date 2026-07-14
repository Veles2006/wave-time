package com.sae.wavetime.ui.item.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.ui.common.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val inventoryRepo: InventoryRepository,
    private val itemRepo: ItemRepository,
    private val useItemUseCase: UseItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ItemListState(isLoading = true)
    )

    val state: StateFlow<ItemListState> =
        inventoryRepo.observeInventoryItems()
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Load items failed"
                    )
                }

                emit(emptyList())
            }
            .combine(_uiState) { items, uiState ->
                uiState.copy(
                    isLoading = false,
                    items = items
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ItemListState(isLoading = true)
            )


    fun useItem(itemId: String, amount: Int = 1) {
        viewModelScope.launch {
            _uiState.update {
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

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notificationMessage = if (durationMinutes > 0) {
                            UiText.StringResource(
                                resId = R.string.item_used_with_duration,
                                args = listOf(
                                    UiText.LocalizedKeyName(item.name),
                                    durationMinutes
                                )
                            )
                        } else {
                            UiText.StringResource(
                                resId = R.string.item_used,
                                args = listOf(
                                    UiText.LocalizedKeyName(item.name)
                                )
                            )
                        }
                    )
                }
            }.onFailure { e ->
                Log.e("ItemListViewModel", "Use item failed", e)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Use item failed"
                    )
                }
            }
        }
    }

    fun clearNotificationMessage() {
        _uiState.update {
            it.copy(notificationMessage = null)
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}