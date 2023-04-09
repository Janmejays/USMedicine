package com.integrationTestExample.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.integrationTestExample.data.network.RetrofitInstance
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.room.entities.ConceptProperty
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ListViewModelTest : TestCase() {
    /**
     * rule required for observing the live data
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()//For LiveData
    private lateinit var viewModel: ListViewModel
    private lateinit var viewModelSearch: SearchViewModel
    private lateinit var db: AppDatabase

    /**
     * set up the instances for testing
     */
    @Before
    public override fun setUp() {
        super.setUp()
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries().build()
        val datasource = DrugRepository(RetrofitInstance.api, db)
        viewModel = ListViewModel(datasource)
        viewModelSearch = SearchViewModel(datasource)
    }

    /**
     * test for get drug list
     */
    @Test
    fun getDrugList() = runBlocking {
        var result: String
        val cp = ConceptProperty(
            id = 12, name = "Zyrtec", rxcui = "009874", suppress = "false",
            false, "en", "zy", "gg", "gg"
        )
        /**
         * saving fake data (drug) to concept property table
         */
        viewModelSearch.insertDrugData(cp)
        /**
         * get the saved data
         */
        viewModel.getDrugListTest()
        viewModel._druglistTest.getOrAwaitValue().let {
            result = it[0].name
        }
        /**
         * if drug name return from local db is equal to fake drug name input
         * then assert that test is passed
         */
        assertEquals("Zyrtec", result)
    }

    /**
     * this test cease for checking delete in edit fragment
     *
     */
    @Test
    fun deleteListViewModelTest() {
        val expected = "deleted"
        var result: String
        val cp = ConceptProperty(
            id = 12, name = "Zyrtec", rxcui = "23232", suppress = "false",
            false, "en", "zy", "gg", "gg"
        )
        viewModelSearch.insertDrugData(cp)
        viewModel.deleteDrugData("23232")
        viewModel.del.getOrAwaitValue().let {
            result = it
        }

        assertEquals(expected, result)
    }

    /**
     * clearing the instance
     */
    @After
    fun closeDb() {
        db.close()
    }
}