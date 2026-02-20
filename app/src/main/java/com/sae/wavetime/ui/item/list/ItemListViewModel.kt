package com.sae.wavetime.ui.item.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.model.Item
import com.sae.wavetime.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ItemListState())
    val state: StateFlow<ItemListState> = _state

    fun loadItems() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val items: List<Item> = repository.getItems()

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
}