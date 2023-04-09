package com.integrationTestExample.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import io.reactivex.Single


@Dao
interface DrugDao {

    /**
     * this is used to insert drug to room
    i) ConceptProperty-ConceptProperty
     */

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun saveDrug(ConceptProperty: ConceptProperty)

    /**
     * this is used to select items from table
    i) ConceptProperty-ConceptProperty
     */
    @Query("SELECT * FROM ConceptProperty")
    fun getDrug(): Single<List<ConceptProperty>>

    /**
     * this is used to select items from concept property where interaction is true
    i) ConceptProperty-ConceptProperty
     */
    @Query("SELECT * FROM ConceptProperty where interaction=1")
    fun getIntercationDrugList(): Single<List<ConceptProperty>>

    /**
     * this is used to select rxcui code from ConceptProperty
    i) ConceptProperty-ConceptProperty
     */
    @Query("SELECT rxcui FROM ConceptProperty")
    fun getDrugId(): Single<List<String>>

    /**
     * this is used to delete from ConceptProperty based on rxcui code
    i) rxcui-String
     */
    @Query("DELETE FROM ConceptProperty WHERE rxcui = :rxcui")
    fun deleteDrug(rxcui: String) //dlete from room

    /**
     * this is used to insert data to interaction table.
    i) interaction-List<Interaction>
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveInteraction(interaction: List<Interaction>)

    /**
     * this is used to delete interaction table
     */
    @Query("DELETE FROM table_interaction")
    fun deleteInteraction() //dlete from room

    /**
     * this is used select all from interaction table
     */
    @Query("SELECT * FROM table_interaction")
    fun getInt(): Single<List<Interaction>>

    /**
     * this is used  to select count of interaction from interaction table.
     * i)rxcui:=String
     */
    @Query("SELECT count(*) FROM table_interaction where   minConceptId1= :rxcui or minConceptId2=:rxcui")
    fun checkInteraction(rxcui: String): Single<Int>

    /**
     * this is used to update interaction flag in Concept Property table
     * i)rxcui:=String
     */
    @Query("update ConceptProperty set interaction=:flag where rxcui = :rxcui")
    fun updateDrug(rxcui: String, flag: Boolean)

    /**
     * this is used to delete from interaction table based on minConceptId1 or minConceptId2
     * i)rxcui:=String
     */
    @Query("DELETE FROM table_interaction WHERE minConceptId1= :rxcui or minConceptId2=:rxcui")
    fun deleteInteractionById(rxcui: String) //dlete from room

    /**
     * this is used to select interaction based on excui code.
     * i)rxcui:=String
     */
    @Query("SELECT distinct * FROM table_interaction where   minConceptId1= :rxcui or minConceptId2=:rxcui")
    fun getInteractionData(rxcui: String): Single<List<Interaction>>

    /**
     * this is used to test the database
     * i)rxcui:=String
     */
    @Query("SELECT * FROM ConceptProperty WHERE id = :id")
    fun getDrugById(id: String): List<ConceptProperty> //get by specific id, for unit testing


}