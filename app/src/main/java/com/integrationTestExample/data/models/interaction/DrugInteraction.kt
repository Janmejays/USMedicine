package com.integrationTestExample.data.models.interaction

data class DrugInteraction(
    val fullInteractionTypeGroup: List<FullInteractionTypeGroup>,
    val nlmDisclaimer: String
)