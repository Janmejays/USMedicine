package com.integrationTestExample.data.models.drugdetails

import com.integrationTestExample.room.entities.ConceptProperty

data class ConceptGroup(
    val conceptProperties: List<ConceptProperty>,
    val tty: String
)