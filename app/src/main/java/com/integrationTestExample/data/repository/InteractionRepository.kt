package com.integrationTestExample.data.repository

import com.integrationTestExample.room.AppDatabase

class InteractionRepository( private val db: AppDatabase) {
/**
 * this class is used for getting the interaction details from Room DB
 * for specific medicines

 */
    fun getInteractionData(rxcui:String) = db.DrugDao().getInteractionData(rxcui)
}