package com.integrationTestExample.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : TestCase() {

    /**
     * this for room data base testing
     * we have 2 tables in AppDatabase (druginteractr.db)
     * i)ConceptProperty//same name used as in api response
     * ii)Table_interaction//table created frm specific values from api response
     *
     * here we are going to test
     * insertion of these 2 table works
     * deletion of these 2 tables works
     * selection of these 2 table works or not.
     */

    private lateinit var db: AppDatabase
    private lateinit var dao: DrugDao
    private lateinit var drugValues: DrugValues
    private lateinit var interactionValues: InteractionValues

    /**
     * setting up the database for testing
     */
    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>() //getting context
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build() //room instance
        dao = db.DrugDao() //dao instance
        drugValues = DrugValues()
        interactionValues = InteractionValues()
    }

    /**
     * clear the db after all tests completed
     */
    @After
    fun closeDb() {
        db.close()
    }

    /**
     * test for insertion into concept property(drug) table works
     */
    @Test
    fun ensure_appDatabase_ConceptProperty_insertion_works() = runBlocking {
        val id = drugValues.id
        val name = drugValues.name
        val rxcui = drugValues.rxcui
        val suppress = drugValues.suppress
        val interaction = drugValues.interaction
        val language = drugValues.language
        val synonym = drugValues.synonym
        val tty = drugValues.tty
        val umlsciu = drugValues.umlscui
        val conceptProperty =
            ConceptProperty(id, name, rxcui, suppress, interaction, language, synonym, tty, umlsciu)
        var resultRxcuid = ""

        /**
        //saving dummy data
         */
        dao.saveDrug(conceptProperty)
        /**
        //// checking dummy data is returned from ROOM
         */
        val drugs = dao.getDrug()
            .subscribe { drug ->
                resultRxcuid = drug[0].rxcui
            }
        drugs.dispose()
        assertEquals(resultRxcuid, rxcui)
    }

    /**
     * test for interaction table save works
     */
    @Test
    fun ensure_appDatabase_Table_Interaction_insertion_works() = runBlocking {
        val id = interactionValues.id.toLong()
        val minConceptId1 = interactionValues.minConceptId1
        val minConceptName1 = interactionValues.minConceptName1
        val minConceptId2 = interactionValues.minConceptId2
        val minConceptName2 = interactionValues.minConceptName2
        val severity = interactionValues.severity
        val description = interactionValues.description
        val interaction =
            Interaction(
                id,
                minConceptId1,
                minConceptName1,
                minConceptId2,
                minConceptName2,
                severity,
                description
            )
        var resultminConceptId2 = ""
        /**
        //saving dummy data
         */
        dao.saveInteraction(interaction = arrayListOf(interaction))
        /**
        //// checking dummy data is returned from ROOM
         */
        val interactions = dao.getInt()
            .subscribe { interactions ->
                resultminConceptId2 = interactions[0].minConceptId2
            }
        interactions.dispose()
        assertEquals(resultminConceptId2, minConceptId2)
    }

    /**
     * test for deleting the drug table works or not
     */
    @Test
    fun ensure_ConceptProperty_delete_works() = runBlocking {
        val id = drugValues.id
        val name = drugValues.name
        val rxcui = drugValues.rxcui
        val suppress = drugValues.suppress
        val interaction = drugValues.interaction
        val language = drugValues.language
        val synonym = drugValues.synonym
        val tty = drugValues.tty
        val umlsciu = drugValues.umlscui
        val conceptProperty =
            ConceptProperty(id, name, rxcui, suppress, interaction, language, synonym, tty, umlsciu)
        var resultRxcuid = ""
        dao.saveDrug(conceptProperty)//saving
        dao.deleteDrug(rxcui)//deleting
        val result = dao.getDrug().subscribe { drug ->
            resultRxcuid = if(drug.isNotEmpty()) {
                drug[0].rxcui
            }else{
                ""
            }
        }
        result.dispose()
        Assert.assertEquals(resultRxcuid, "")
    }

    /**
     * test for deleting from the interaction table
     */
    @Test
    fun ensure_Table_interaction_delete_works() = runBlocking {
        val id = interactionValues.id.toLong()
        val minConceptId1 = interactionValues.minConceptId1
        val minConceptName1 = interactionValues.minConceptName1
        val minConceptId2 = interactionValues.minConceptId2
        val minConceptName2 = interactionValues.minConceptName2
        val severity = interactionValues.severity
        val description = interactionValues.description
        val interaction =
            Interaction(
                id,
                minConceptId1,
                minConceptName1,
                minConceptId2,
                minConceptName2,
                severity,
                description
            )
        var resultInteraction= ""
        dao.saveInteraction(arrayListOf(interaction))//saving
        dao.deleteInteractionById(minConceptId1)//deleting
        val result = dao.getInt().subscribe { interactions ->
            resultInteraction = if(interactions.isNotEmpty()) {
                interactions[0].minConceptId2
            }else{
                ""
            }
        }
        result.dispose()
        Assert.assertEquals(resultInteraction, "")
    }

}

