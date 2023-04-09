package com.integrationTestExample.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ConceptProperty")
data class ConceptProperty(
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "rxcui")
    val rxcui: String,

    @ColumnInfo(name = "suppress")
    val suppress: String,
    @ColumnInfo(name = "interaction")
    val interaction: Boolean,

    @ColumnInfo(name = "ColumnInfo")
    val language: String,
    @ColumnInfo(name = "synonym")
    val synonym: String,
    @ColumnInfo(name = "tty")
    val tty: String,
    @ColumnInfo(name = "umlscui")
    val umlscui: String
)
