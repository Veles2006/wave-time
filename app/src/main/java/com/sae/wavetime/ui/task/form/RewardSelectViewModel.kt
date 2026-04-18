package com.sae.wavetime.ui.task.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.ui.model.RewardSelectUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RewardSelectViewModel(
    private val repository: ItemRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RewardSelectState())
    val state: StateFlow<RewardSelectState> = _state

    fun loadRewards() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val rewards: List<RewardSelectUiModel> = repository.getRewardSelects()

                _state.update {
                    it.copy(
                        isLoading = false,
                        rewards = rewards,
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

    fun increaseQuantity(target: RewardSelectUiModel) {
        val newList = _state.value.rewards.map { item ->
            if (item.id == target.id) {
                item.copy(quantity = item.quantity + 1)
            } else {
                item
            }
        }

        _state.value = _state.value.copy(rewards = newList)
    }

    fun decreaseQuantity(target: RewardSelectUiModel) {
        val newList = _state.value.rewards.map { item ->
            if (item.id == target.id && item.quantity > 0) {
                item.copy(quantity = item.quantity - 1)
            } else {
                item
            }
        }

        _state.value = _state.value.copy(rewards = newList)
    }

    fun updateTaskReward(newRewards: List<RewardSelectUiModel>) {

        val selected = newRewards.filter { it.quantity > 0 }

        _state.update {
            it.copy(selectedRewards = selected)
        }
    }
}
