package com.integrationTestExample.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.integrationTestExample.data.network.RetrofitInstance
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.room.DrugValues
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var repo: DrugRepository
    private lateinit var drugValues: DrugValues
    private lateinit var db: AppDatabase

    /**
     * rule for live data
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    /**
     * set upo the required instances
     * in this class requires db, search view model instances
     */
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>() //context instance
         db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries().build()//db instance
        val api = RetrofitInstance.api
        // val mockedApiInterface: DrugAPI = Mockito.mock(DrugAPI::class.java)
        repo = DrugRepository(api, db)// repo instance
        val dataSource = SearchViewModel(repo)//
        viewModel = dataSource //view model instance
        drugValues = DrugValues()// dummy values from class
    }
    @After
    fun closeDb() {
        db.close()
    }

    /**
     * this is the test case for inserting the drug values
     * to room
     * Observing the live data (_saveLivedata)
     */

    @Test
    fun ensure_Insert_Drug__works() = runBlocking {
        var result: String
        val expected = "Inserted Successfully"
        val id = drugValues.id
        val name = drugValues.name
        val rxcui = drugValues.rxcui
        val suppress = drugValues.suppress
        val interaction = drugValues.interaction
        val language = drugValues.language
        val synonym = drugValues.synonym
        val tty = drugValues.tty
        val umlsciu = drugValues.umlscui
        val cp =
            ConceptProperty(id, name, rxcui, suppress, interaction, language, synonym, tty, umlsciu)
        /**
         * saving the fake data
         */
        viewModel.insertDrugData(cp)
        //observing save livedata
        viewModel._saveLivedata.getOrAwaitValue().let {
            result = it
        }
        TestCase.assertEquals(result, expected)

    }

    /** we are checking if the input drugs has any interction to each other
     * if interaction found we are returning the minConcept drugs rxcui.
     */
    @Test
    fun getInteractionList() = runBlocking {
        val rxcui = "384455 10109"//tiggecyclin & strptomicin
        val excptected = "10109"
        var result: String
        viewModel.getIntercationList(rxcui)
        viewModel._interactionLiveData.getOrAwaitValue().let {
            result =
                it.fullInteractionTypeGroup[0].fullInteractionType[0].interactionPair[0].interactionConcept[0].minConceptItem.rxcui
        }

        TestCase.assertEquals(result, excptected)
    }

    /**
     * test for get suggestion list
     * input drug is passed to function and
     * observing if any suggestions found,if found , test is passed
     */
    @Test
    fun getSuggestionListRxKotlinTest() = runBlocking {
        val input = "tiggecycline"
        val expected = "tigecycline"
        var result: String
        viewModel.getSuggestionListRxKotlin(input)
        viewModel.suggestionList.getOrAwaitValue().let {

            result = it.suggestionGroup.suggestionList.suggestion.toString()
            result = result.drop(1).dropLast(1)
        }
        TestCase.assertEquals(result, expected)

    }

    /**
     *here we are trying to insert the same drug again, rxcui code is the primary key
     * so it will block duplicate entries and error live data will be triggered.
     */
    @Test
    fun ensure_primary_key_validation_in_ConceptProperty_works() = runBlocking {
        var result: String
        val expected = "Ooops drug is already added in your list!!!!"
        val id = drugValues.id
        val name = drugValues.name
        val rxcui = drugValues.rxcui
        val suppress = drugValues.suppress
        val interaction = drugValues.interaction
        val language = drugValues.language
        val synonym = drugValues.synonym
        val tty = drugValues.tty
        val umlsciu = drugValues.umlscui
        val cp =
            ConceptProperty(id, name, rxcui, suppress, interaction, language, synonym, tty, umlsciu)
        viewModel.insertDrugData(cp)
        viewModel._saveLivedata.getOrAwaitValue().let {
            viewModel.insertDrugData(cp)
        }
        viewModel.errorLiveData.getOrAwaitValue().let {
            result = it

        }
        TestCase.assertEquals(result, expected)

    }

    /**
     * insertion to interaction table initially delete all the records in that table
     * then only insertion will happen
     * we have to varify 2 functions
     * i) deletion
     * ii) insertion
     */
    @Test
    fun ensure_insert_to_interaction_table_works()= runBlocking {
        var result: String
        val expected = "Inserted Successfully"
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
        viewModel.deleteInteraction(interactionTestData)
        viewModel.saveIneractionLivedata.getOrAwaitValue().let {
            result = it
        }
        TestCase.assertEquals(result, expected)

    }

    /**
     * test for getapproximateTerm
     * this is used to get suggestion list from text reader
     * we pasing a long text that is in medicine bottle or cover
     * and tries to get the suggestions from that string
     */
  @Test
  fun getapproximateTermTest()= runBlocking{
      var result:String
        val input="Tigecycline for I njection 50 mg USP mfg exp end 12-12-2023"
        val expected="tigecycline 50 mg injection"

        viewModel.getapproximateTerm(input)
        viewModel.suggestionList.getOrAwaitValue().let {

            result = it.suggestionGroup.suggestionList.suggestion.toString()
            result = result.drop(1).dropLast(1)
        }
        TestCase.assertEquals(result, expected)
    }

    /**
     * this is the test used for getting the drug name from barcode
     * if we get barcode with starting 0 then we are adding extra 0 to first letter
     * else we are adding 0 to the 6 th posittion and passing it to the
     * ndc relation api.
     * then it will give exact medicin name , no need to call the suggestion api.
     * the same in qr code also work,we will get the GTIN code and crop it from 5-15
     * and pass to this same api
     */
    @Test
    fun getndcpropertiesTest()= runBlocking {
        var result:String
        val input="53746011701"
        val expected="hydrocodone bitartrate 10 MG / ibuprofen 200 MG Oral Tablet"

        viewModel.getndcproperties(input)
        viewModel.NDCRelationLiveData.getOrAwaitValue().let {
            result= it.ndcInfoList.ndcInfo[0].conceptName
        }
        TestCase.assertEquals(result, expected)

    }

    /**
     * this test for getting the  drug name  from input rxcui code.
     *we are passing input rxcui and observing the suggestions
     */
    @Test
    fun getPrescribeTest()= runBlocking {
        val input="1020026"
        var result: String
        val expected="cetirizine hydrochloride 10 MG Oral Tablet [Zyrtec]"
        viewModel.getPrescribe(input)
        viewModel._suggestionList.getOrAwaitValue().let {
            result= it.suggestionGroup.suggestionList.suggestion.toString()
            result=result.drop(1).dropLast(1)
        }
        TestCase.assertEquals(result.uppercase(), expected.uppercase())
    }
}