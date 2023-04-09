package com.integrationTestExample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.integrationTestExample.data.repository.InteractionRepository


@Suppress("UNCHECKED_CAST")
class InteractionViewModelFactory(
    private val repository: InteractionRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InteractionViewModel( repository) as T
    }

}