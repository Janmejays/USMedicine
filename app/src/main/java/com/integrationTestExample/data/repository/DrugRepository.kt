package com.integrationTestExample.data.repository

import com.integrationTestExample.data.network.DrugAPI
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction


class DrugRepository(
    private val api: DrugAPI,
    private val db: AppDatabase
) {

    /**
    for inserting drug details to table_ConceptProperty(drug table/main table)
    on duplicate it will fail and error live data will get invoked.
     */
    fun saveDrug(ConceptProperty: ConceptProperty) =
        db.DrugDao().saveDrug(ConceptProperty)//save to room db

    /**
    //for getting all rxcui code from main table for checking the
    interactions while adding new item.
    for each entries passing whole exsiting rxcui to get the updated result
     */
    fun getrxcui() = db.DrugDao().getDrugId()

    /**
    //// to check if there is any interaction for each drug
     */
    fun checkInteraction(rxcui: String) = db.DrugDao().checkInteraction(rxcui)

    /**
    //// for saving interactions while adding new drug
     */
    fun saveInteraction(interacton: List<Interaction>) = db.DrugDao().saveInteraction(interacton)

    /**
    //// for updating drug list with interactions is true or false
     */
    fun updateDrug(rxcui: String, flag: Boolean) = db.DrugDao().updateDrug(rxcui, flag)

    /**
    //// for getting all drugs
     */
    fun getData() = db.DrugDao().getDrug()


    /**
    //// for getting drug if they have any interaction to others (interaction=1)
     */
    fun getIntercationDrugList() = db.DrugDao().getIntercationDrugList()

    /**
    //// for deleting drugs from main table with id
     */
    fun deleteDrug(id: String) = db.DrugDao().deleteDrug(id)

    /**
    //// for deleting interaction drugs from interaction table with id
     */
    fun deleteInteractionDrug(rxcui: String) = db.DrugDao().deleteInteractionById(rxcui)

    /**
    //// for deleting interaction table before adding new records
     */
    fun deleteInteraction() = db.DrugDao().deleteInteraction()

    /**
    //// for getting suggestion from edit text if it is mis spelled or not
     */
    fun getspellingsuggestionsDrugsRxKotlin(text: String) =
        api.getSpellingSuggestionsDrugsRxKotlin(text)

    /**
    //// for drug details of selected text from suf=ggestion list
     */
    fun getDrugByName(text: String) = api.getDrugByName(text)

    /**
    //// for getting interaction details the ids are passed as string with spaces
     */
    fun getInteractionList(rxcui: String) = api.getInteractionList(rxcui)

    // fun drugs(text: String) = api.drugs(text)

    /**
    //// for getting drug suggestions from text reader
    //// for getting drug suggestions from text reader
     */
    fun getapproximateTerm(term: String) = api.getApproximateTerm(term)

    // fun getndcproperties(id: String) = api.getndcproperties(id)
    /**
    //// for getting drug details from rxcui code.
     */
    fun getPrescribe(text: String) = api.getPrescribe(text)

    /**
    //// for getting drug details from rxcui code.
     */
    fun getNDrelation(ndc: String) = api.getNDrelation(ndc)


//    fun getInteraction() = db.DrugDao().getInt()

}