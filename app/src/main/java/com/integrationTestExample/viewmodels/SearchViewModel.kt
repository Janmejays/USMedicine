package com.integrationTestExample.viewmodels


import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.integrationTestExample.data.models.drugdetails.DrugDetails
import com.integrationTestExample.data.models.interaction.DrugInteraction
import com.integrationTestExample.data.models.ndcProperties.NDCRelation
import com.integrationTestExample.data.models.suggestions.DrugSuggestions
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import com.integrationTestExample.utils.Constants.Companion.EXISTS
import com.integrationTestExample.utils.Constants.Companion.INSERTED_SUCCESSFULLY
import com.integrationTestExample.utils.Constants.Companion.NO_DRUGS_FOUND
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * class used for search text, barcode and qr
 * all the repo operations are handles by RX kotlin and Reactivex functions
 */
class SearchViewModel(
    private val repository: DrugRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var _mrxcui: String = ""
    private var errorTriggered: Boolean = false

    /**
     * for Suggestion List Livedata
     */
    val _suggestionList by lazy { MutableLiveData<DrugSuggestions>() }
    val suggestionList: LiveData<DrugSuggestions> get() = _suggestionList

    /**
     * for Error Live Data
     */

    private val _errorLivedata by lazy { MutableLiveData<String>() }
    var errorLiveData: LiveData<String> = _errorLivedata

    val _saveLivedata by lazy { MutableLiveData<String>() }
    val saveLivedata: LiveData<String> get() = _saveLivedata

    private val _saveIneractionLivedata by lazy { MutableLiveData<String>() }
    val saveIneractionLivedata: LiveData<String> get() = _saveIneractionLivedata

    /**
     * for Drug Details Live Data
     */
    private val _drugLiveData by lazy { MutableLiveData<DrugDetails>() }
    val drugLiveData: LiveData<DrugDetails> get() = _drugLiveData

    private val _NDCRelationLiveData by lazy { MutableLiveData<NDCRelation>() }
    val NDCRelationLiveData: LiveData<NDCRelation> get() = _NDCRelationLiveData

    /**
     * for Interaction Details Live Data
     */
    val _interactionLiveData by lazy { MutableLiveData<DrugInteraction>() }
    val interactionLiveData: LiveData<DrugInteraction> get() = _interactionLiveData

    /**
     * for getting suggestions from typed text
     */
    fun getSuggestionListRxKotlin(SuggestionName: String) {
        compositeDisposable += repository.getspellingsuggestionsDrugsRxKotlin(SuggestionName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { suggestionList ->
                    _suggestionList.value =
                        suggestionList
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /**
     * for getting drug details from selected suggestion list item
     */
    fun getDrugList(drugName: String) {
        try {
            compositeDisposable += repository.getDrugByName(drugName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { drugData ->
                        _drugLiveData.value = drugData
                    },
                    onError = { e -> showErrorLiveData(e.message!!) }
                )
        } catch (Ex: Exception) {
            Ex.printStackTrace()
        }
    }

    /**
     * for getting all rxcui from local db
     */
    fun getInteractionList(rxcui: String) {
        _mrxcui += rxcui
        compositeDisposable += repository.getrxcui()//repository.getspellingsuggestionsDrugsRxKotlin(SuggestionName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { rxcuiList ->
                    if (rxcuiList.isEmpty()) {
                        _mrxcui = "0000000"
                    } else {
                        val original: List<String> = rxcuiList + rxcui
                        _mrxcui = TextUtils.join(" ", original)
                    }

                    getIntercationList(_mrxcui)
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    fun getIntercationList(mrxcui: String) {
        compositeDisposable += repository.getInteractionList(mrxcui)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { drugData ->
                    _interactionLiveData.value = drugData
                    _mrxcui = ""
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }


    /**
     * for Inserting drug details to main table
     */
    @SuppressLint("CheckResult")
    fun insertDrugData(data: ConceptProperty) {
        Observable.fromCallable {
            try {
                errorTriggered = false
                repository.saveDrug(data)
            } catch (e: Exception) {
                e.printStackTrace()
                errorTriggered = true
                _errorLivedata.postValue(EXISTS)
            }
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (!errorTriggered) {
                    _saveLivedata.postValue(INSERTED_SUCCESSFULLY)

                }
            }
    }

    /**
     * for deleteing interactions befor inserting new records and insert list of data to
     * main table
     */
    @SuppressLint("CheckResult")
    fun deleteInteraction(interaction: List<Interaction>) {
        Observable.fromCallable {
            repository.deleteInteraction()
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                saveInteraction(interaction)
            }
    }

    /**
     * for inserting interactions to table_interaction
     */
    @SuppressLint("CheckResult", "StringFormatInvalid", "StringFormatMatches")
    fun saveInteraction(interaction: List<Interaction>) {
        Observable.fromCallable {
            try {
                repository.saveInteraction(interaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _saveIneractionLivedata.postValue(INSERTED_SUCCESSFULLY)
            }
    }

    /**
     * for getting detail by passing rxcui code.
     */
    fun getPrescribe(text: String) {
        compositeDisposable += repository.getPrescribe("$text.json")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { drugName ->
                    if (!drugName.idGroup.name.isNullOrEmpty()) {
                        getSuggestionListRxKotlin(drugName.idGroup.name)
                    } else {
                        showErrorLiveData(NO_DRUGS_FOUND)
                    }
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }


    /**
     * for getting details from barcode with relations
     */
    fun getndcproperties(id: String) {
        compositeDisposable += repository.getNDrelation(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { ndc ->
                    if (ndc != null) {
                        _NDCRelationLiveData.postValue(ndc)
                    }
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    /**
     * for getting drug suggestions from text reader
     */
    fun getapproximateTerm(text: String) {
        compositeDisposable += repository.getapproximateTerm(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { approximateTerm ->
                    getPrescribe(approximateTerm.approximateGroup.candidate[0].rxcui)
                },
                onError = { e -> showErrorLiveData(e.message!!) }
            )
    }

    private fun showErrorLiveData(msg: String) {
        _errorLivedata.value = msg
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
/*
    fun getSuggestionList(text: String) {
        try {
            var response: Observable<DrugSuggestions>
            response = repository.getspellingsuggestionsDrugs(text)
            response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSuggestionObserver())
        } catch (Ex: Exception) {
            Ex.printStackTrace()
        }
    }

    private fun getSuggestionObserver(): Observer<DrugSuggestions> {
        return object : Observer<DrugSuggestions> {
            override fun onComplete() {
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
                //    _suggestionList.postValue(listOf(e.toString()))

            }
            override fun onNext(t: DrugSuggestions) {
                val kk = t
                //  _suggestionList.postValue(t.suggestionGroup.suggestionList.suggestion)
                _suggestionList.postValue(t.suggestionGroup.suggestionList)
            }
            override fun onSubscribe(d: Disposable) {
                // d.dispose()
            }
        }
    }

    fun getDrugList(text: String) {
        try {
            val response = repository.drugs(text)
            response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDrugsObserver())
        } catch (Ex: Exception) {
            Ex.printStackTrace()
        }
    }

    private fun getDrugsObserver(): Observer<DrugDetails> {
        return object : Observer<DrugDetails> {
            override fun onComplete() {
                val c = "completed"
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(t: DrugDetails) {


                 _drugLiveData.value = t
            }

            override fun onSubscribe(d: Disposable) {
                //d.dispose()
            }
        }
    }
*/
}