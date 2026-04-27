package com.sae.wavetime.ui.item.list

import android.util.Log.i
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.ui.model.InventoryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val inventoryRepo: InventoryRepository,
    private val useItemUseCase: UseItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ItemListState())
    val state: StateFlow<ItemListState> = _state

    fun loadItems() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val items: List<InventoryUiModel> = inventoryRepo.getInventoryItems()

                _state.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }

    fun useItem(itemId: String, amount: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                useItemUseCase.execute(itemId, amount)

                _state.update {
                    it.copy(
                        isLoading = false,
                    ) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }
}