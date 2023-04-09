package com.integrationTestExample.data.models.interaction

data class FullInteractionType(
    val comment: String,
    val interactionPair: List<InteractionPair>,
    val minConcept: List<MinConcept>
)