package com.integrationTestExample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.integrationTestExample.data.repository.DrugRepository

/**
 * class used to create dependencies for list view model
 */

@Suppress("UNCHECKED_CAST")
class ListViewModelFactory(
    private val repository: DrugRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListViewModel(repository) as T
    }

}