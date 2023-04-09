package com.integrationTestExample.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.utils.Constants.Companion.DELETED
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation


class ListViewModel(
    private val repository: DrugRepository
) : ViewModel() {

    /**
     * this is the view model used for home screen, interaction screen,
     * interaction home screen
     * All the repo operations are done with Rxkotlin and io.reactivex
     * note :not used any operations with rxjava ,only used rxkotlin.
     */

    private val compositeDisposable by lazy { CompositeDisposable() }

    val del: MutableLiveData<String> = MutableLiveData<String>()

    //live data for getting the drug list
    val _druglist by lazy { MutableLiveData<List<ConceptProperty>>() }
    val drugList: LiveData<List<ConceptProperty>> get() = _druglist
    val _druglistTest by lazy { MutableLiveData<List<ConceptProperty>>() }

    //live data for the interaction list
    private val _interactiondruglist by lazy { MutableLiveData<List<ConceptProperty>>() }
    val interactiondruglist: LiveData<List<ConceptProperty>> get() = _interactiondruglist

    // live data for the error
    private val _errorLivedata by lazy { MutableLiveData<String>() }




    /**
    |from list fragment for getting all drug details with interaction
     */
    fun getDrugsList() {
        compositeDisposable += repository.getrxcui()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { rxcui ->
                    val size = rxcui.size
                    if (size == 1) {
                        updateDatabase(rxcui[0], false)
                    } else {
                        for (i in 0 until size) {
                            checkInteraction(rxcui[i])
                        }
                    }
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /**
     * checking for drug interaction present on each medicine

     */
    private fun checkInteraction(rxcui: String) {
        compositeDisposable += repository.checkInteraction(rxcui)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { count ->
                    if (count > 0) {
                        updateDatabase(rxcui, true)
                    } else {
                        updateDatabase(rxcui, false)

                    }
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /**
    /// function for updating the data base if interaction is present else it will be 0(false)
     */
    @SuppressLint("CheckResult")
    fun updateDatabase(id: String, flag: Boolean) {
        Observable.fromCallable {
            try {
                repository.updateDrug(id, flag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
            .subscribeOn(computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getDrugList()
            }
    }

    init {
        getDrugList()
    }

    /**
     * for getting the updated list(all saved drug details)
     */
    fun getDrugList() {
        compositeDisposable += repository.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { druDataList ->
                    _druglist.postValue(druDataList)
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /***
     * this function is created for testing only
     * for avoiding the init call
     */
    fun getDrugListTest() {
        compositeDisposable += repository.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { druDataList ->
                    _druglistTest.postValue(druDataList)
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }


    /**
     * for getting the interactions (where interaction=1)
     */
    fun getIntercationDrugList() {
        compositeDisposable += repository.getIntercationDrugList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { drugDataList ->
                    _interactiondruglist.value = drugDataList
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /**
     * for fetching error and displaying to views
     */
    private fun showErrorLiveData(error: String) {
        _errorLivedata.value = error
    }

    /**
     * for deleting drug from table
     * i)delete from main table and delete from interaction table
     */
    fun deleteITem(rxscui: String) {
        deleteDrugData(rxscui)
    }

    /**
     * for deleting drug from table
     * i)delete from main table
     */
    @SuppressLint("CheckResult")
    fun deleteDrugData(rxcui: String) {
        Observable.fromCallable {
            repository.deleteDrug(rxcui)
        }
            .subscribeOn(computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                deleteInteractionDrug(rxcui)

            }
    }

    /**
     * for deleting drug from table
     * i)delete from interaction table
     */
    @SuppressLint("CheckResult")
    fun deleteInteractionDrug(rxcui: String) {
        Observable.fromCallable {
            repository.deleteInteractionDrug(rxcui)
        }
            .subscribeOn(computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                del.value = DELETED
            }
    }

    override fun onCleared() {
        super.onCleared()
        //clearing the disposable, it is mandatory.
        compositeDisposable.clear()
    }

}