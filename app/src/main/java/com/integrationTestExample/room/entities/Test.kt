package com.integrationTestExample.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
this class is for room db encryption testing
 */
@Entity(tableName = "test_table")
data class Test(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "test_value")
    val secretValue: String
)