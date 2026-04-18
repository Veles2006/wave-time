package com.sae.wavetime.ui.task.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.ui.model.RewardSelectUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val taskRepo: TaskRepository,
    private val itemRepo: ItemRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state

    fun loadRewards() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val rewards: List<RewardSelectUiModel> = itemRepo.getRewardSelects()

                _state.update {
                    it.copy(
                        isLoading = false,
                        availableRewards = rewards,
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

    fun observeTask(id: String) {
        viewModelScope.launch {
            taskRepo.getTaskByIdFlow(id)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { task ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            task = task,
                            error = if (task == null) "Task not found" else null
                        )
                    }
                }
        }
    }

    fun increaseQuantity(target: RewardSelectUiModel) {
        val newList = _state.value.availableRewards.map { item ->
            if (item.id == target.id) {
                item.copy(quantity = item.quantity + 1)
            } else {
                item
            }
        }

        _state.value = _state.value.copy(availableRewards = newList)
    }

    fun decreaseQuantity(target: RewardSelectUiModel) {
        val newList = _state.value.availableRewards.map { item ->
            if (item.id == target.id && item.quantity > 0) {
                item.copy(quantity = item.quantity - 1)
            } else {
                item
            }
        }

        _state.value = _state.value.copy(availableRewards = newList)
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepo.insertTask(task)
        }
    }

    fun updateTaskReward(newRewards: List<RewardSelectUiModel>) {

        val selected = newRewards.filter { it.quantity > 0 }

        _state.update {
            it.copy(selectedRewards = selected)
        }
    }

    fun initForm(taskId: String?) {
        if (taskId == null) {
            _state.value = TaskFormState()
        } else {
            observeTask(taskId)
        }
    }
}
