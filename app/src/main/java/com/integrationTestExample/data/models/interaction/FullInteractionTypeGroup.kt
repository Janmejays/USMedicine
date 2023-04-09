package com.integrationTestExample.data.models.interaction

data class FullInteractionTypeGroup(
    val fullInteractionType: List<FullInteractionType>,
    val sourceDisclaimer: String,
    val sourceName: String
)