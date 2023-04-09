package com.integrationTestExample.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.integrationTestExample.data.repository.InteractionRepository
import com.integrationTestExample.room.entities.Interaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


/**
 * all repository function class are by reactive code
 * used rx kotlin and reactivex for asynchronous operations
 */
class InteractionViewModel(
    private val repository: InteractionRepository
) : ViewModel() {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //interaction live data
    val _interactionlist by lazy { MutableLiveData<List<Interaction>>() }
    val interactionlist: LiveData<List<Interaction>> get() = _interactionlist

    /**
     * function for getting the interaction data
     * while passing the specific rxcuid code will return
     * the interaction drugs contained that id
     */
    fun getInteractionData(rxcuid: String) {
        compositeDisposable += repository.getInteractionData(rxcuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { ineractionDataList ->
                    _interactionlist.postValue(ineractionDataList)
                },
                onError = {}
            )
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}