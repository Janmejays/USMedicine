package com.integrationTestExample.data.network


import com.integrationTestExample.data.models.drugdetails.DrugDetails
import com.integrationTestExample.data.models.interaction.DrugInteraction
import com.integrationTestExample.data.models.ndcProperties.NDCRelation
import com.integrationTestExample.data.models.suggestionFromBarcode.BarcodeSuggestion
import com.integrationTestExample.data.models.suggestionFromCamera.CameraSuggestions
import com.integrationTestExample.data.models.suggestions.DrugSuggestions
import com.integrationTestExample.utils.Constants.Companion.MAX_ENTRIES
import com.integrationTestExample.utils.Constants.Companion.PRODUCT
import com.integrationTestExample.utils.Constants.Companion.SOURCES
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface DrugAPI {

    /**
     * this API is used to get suggestion from edit text---
    URL-https://rxnav.nlm.nih.gov/REST/spellingsuggestions.json?
    Query:
    i) name-"String"

    sample request: https://rxnav.nlm.nih.gov/REST/spellingsuggestions.json?name=alprazolam 0.25 MG Oral Tablet
     */
    @GET("spellingsuggestions.json")
    fun getSpellingSuggestionsDrugsRxKotlin(
        @Query("name") name: String
    ): Single<DrugSuggestions>

    /**
     * this API is used to get drug name from suggestion list text---
    URL-https://rxnav.nlm.nih.gov/REST/drugs.json?
    Query:
    i) name-"String"
    sample request: https://rxnav.nlm.nih.gov/REST/drugs.json?name=ambienn
     */
    @GET("drugs.json")
    fun getDrugByName(@Query("name") name: String): Single<DrugDetails>


    /**
     * this API is used to get drug interactions---
    URL-https://rxnav.nlm.nih.gov/REST/interaction/list.json?
    Query:
    i) name-"String",
    ii)sources-"String"
    sample request: https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=384455 &sources=drugbank
     */
    @GET("interaction/list.json") // @GET("interaction/interaction.json")
    fun getInteractionList(
        @Query("rxcuis") rxcui: String,
        @Query("sources") sources: String = SOURCES
    ): Single<DrugInteraction>


    /**
     * this API is used to get drug details from rxcui code---
    URL-https://rxnav.nlm.nih.gov/REST/Prescribe/rxcui/id.json
    Query:
    i) id-"String",
    sample request:https: https://rxnav.nlm.nih.gov/REST/Prescribe/rxcui/131725.json
     */
    //
    @GET("Prescribe/rxcui/{id}")
    fun getPrescribe(
        @Path("id") id: String?
    ): Single<BarcodeSuggestion>


    /**
     * this API is used to get drug details from barcode/qr code---
    URL-https://rxnav.nlm.nih.gov/REST/relatedndc.json
    Query:
    i) ndc-"String",
    ii)relation-"String"
    sample request : https://rxnav.nlm.nih.gov/REST/relatedndc.json?relation=product&ndc=0069-3060
     */
    //
    @GET("relatedndc.json")
    fun getNDrelation(
        @Query("ndc") ndc: String,
        @Query("relation") relation: String = PRODUCT
    ): Single<NDCRelation>

    //
    /**
     * this API is used for getting---
    URL-https://rxnav.nlm.nih.gov/REST/approximateTerm.json?
    Query:
    i) term-"String"
    ii)maxEntries-"Int"
     sample request : https://rxnav.nlm.nih.gov/REST/approximateTerm.json?term=to open with tigan&maxEntries=4
     */
    @GET("approximateTerm.json")
    fun getApproximateTerm(
        @Query("term") term: String,
        @Query("maxEntries") sources: Int = MAX_ENTRIES
    ): Single<CameraSuggestions>
}