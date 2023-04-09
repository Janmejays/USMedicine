package com.integrationTestExample.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
this class is for room db interaction
 */
@Entity(tableName = "table_interaction")
data class Interaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "minConceptId1")
    val minConceptId1: String,
    @ColumnInfo(name = "minConceptName1")
    val minConceptName1: String,
    @ColumnInfo(name = "minConceptId2")
    val minConceptId2: String,
    @ColumnInfo(name = "minConceptName2")
    val minConceptName2: String,
    @ColumnInfo(name = "severity")
    val severity: String,
    @ColumnInfo(name = "description")
    val description: String

)