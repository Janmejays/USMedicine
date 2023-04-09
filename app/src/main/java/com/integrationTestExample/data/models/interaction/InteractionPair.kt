package com.integrationTestExample.data.models.interaction

data class InteractionPair(
    val description: String,
    val interactionConcept: List<InteractionConcept>,
    val severity: String
)