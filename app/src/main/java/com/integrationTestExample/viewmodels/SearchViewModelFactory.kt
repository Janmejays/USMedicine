package com.integrationTestExample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.integrationTestExample.data.repository.DrugRepository

/**
 * class used to create search view model dependency
 */

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(
    private val repository: DrugRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }

}