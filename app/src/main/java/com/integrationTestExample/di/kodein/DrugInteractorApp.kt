package com.integrationTestExample.di.kodein

import androidx.multidex.MultiDexApplication
import com.integrationTestExample.data.network.RetrofitInstance
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.data.repository.InteractionRepository
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.viewmodels.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class DrugInteractorApp : MultiDexApplication(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@DrugInteractorApp)) //Application instance
        bind() from singleton { AppDatabase.getInstance(instance()) }// DataBase instance
        bind() from singleton { DrugRepository(RetrofitInstance.api, instance()) }//list repo instance
        bind() from singleton { InteractionRepository(instance()) }//interaction repo instance
        bind() from provider { ListViewModelFactory(instance()) }//list view model factory instance
        bind() from provider { InteractionViewModelFactory(instance()) }//interaction view model factory instance
        bind() from provider { SearchViewModelFactory(instance()) }//search view model factory instance
        bind() from provider { SearchViewModel(instance()) }//search view model
        bind() from provider { ListViewModel(instance()) }//list view model
        bind() from provider { InteractionViewModel(instance()) }//interaction view model
    }
}