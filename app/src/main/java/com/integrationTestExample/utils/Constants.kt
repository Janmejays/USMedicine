package com.integrationTestExample.utils

class Constants {
    companion object {
        const val BASE_URL = "https://rxnav.nlm.nih.gov/REST/"
        const val DATABASE_NAME = "druginteractr.db"
        const val SHARED_PREFS_NAME = "druginteractr.encryptedroomdb.shared_prefs"
        const val PREFS_KEY_PASSPHRASE = "PREFS_KEY_PASSPHRASE"
        const val ALGORITHM_AES = "AES"
        const val KEY_SIZE = 256
        const val SOURCES="drugbank"
        const val PRODUCT="product"
        const val TEXT_DELAY:Long=1500
        const val MAX_ENTRIES=1
        const val SEARCH_LIMIT:Int=200
        const val DELETED= "deleted"
        const val NO_DRUGS_FOUND=  "Oops no records found!!"
        const val INSERTED_SUCCESSFULLY=  "Inserted Successfully"
        // Testing barcode and text analyzer
        const val MANUAL_TESTING_LOG = "LogTagForTest"
        const val EXISTS = "Ooops drug is already added in your list!!!!"
    }
}