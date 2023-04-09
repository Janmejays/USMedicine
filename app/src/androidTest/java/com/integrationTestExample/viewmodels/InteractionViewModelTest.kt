package com.integrationTestExample.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.integrationTestExample.data.network.RetrofitInstance
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.data.repository.InteractionRepository
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.room.entities.Interaction
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test

/**
 * class for interaction view model tests
 */
class InteractionViewModelTest {
    private lateinit var viewModel: InteractionViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var db: AppDatabase

    /**
     * rule for observing live data,this is mandatory
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    /**
     * setting up the classes and objects required for the test
     * here we need room instance ,interaction view model instance,search view model instance
     */
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val datasource = InteractionRepository(db)
        viewModel = InteractionViewModel(datasource)

        val api = RetrofitInstance.api
        // val mockedApiInterface: DrugAPI = Mockito.mock(DrugAPI::class.java)
        val repo = DrugRepository(api, db)// repo instance
        val dataSource = SearchViewModel(repo)//
        searchViewModel = dataSource
    }

    @After
    fun closeDb() {
        db.close()
    }

    /**
     * test for get interaction lists
     */
    @Test
    fun get_interaction_list() = runBlocking {
        var result:String
        val expected = "111111"
        val interactionTestData = arrayListOf(
            Interaction(
                id = 0,
                description = "test description",
                minConceptId1 = "111111",
                minConceptId2 = "222222",
                minConceptName1 = "First drug",
                minConceptName2 = "Second Drug",
                severity = "Test severity"
            )
        )
        /**
         * saving fake interaction class
         */
        searchViewModel.saveInteraction(interactionTestData)
        /**
         * selecting interaction details from fake rxcui cid
         */
        viewModel.getInteractionData("111111")
            //observing the live data using getOrAwaitValue class
        viewModel._interactionlist.getOrAwaitValue().let {
            result= it[0].minConceptId1
        }

        TestCase.assertEquals(result, expected)

    }
}
