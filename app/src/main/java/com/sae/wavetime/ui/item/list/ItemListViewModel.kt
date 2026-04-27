package com.sae.wavetime.ui.item.list

import android.util.Log.e
import android.util.Log.i
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.ui.model.InventoryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val inventoryRepo: InventoryRepository,
    private val useItemUseCase: UseItemUseCase
) : ViewModel() {


    val state: StateFlow<ItemListState> =
        inventoryRepo.getInventoryItems()
            .map { items ->
                ItemListState(
                    isLoading = false,
                    items = items
                )
            }
            .catch { e ->
                emit(
                    ItemListState(
                        isLoading = false,
                        error = e.message
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ItemListState(isLoading = true)
            )


    fun useItem(itemId: String, amount: Int) {
        viewModelScope.launch {
            try {
                useItemUseCase.execute(itemId, amount)
            } catch (e: Exception) {

            }
        }
    }
}