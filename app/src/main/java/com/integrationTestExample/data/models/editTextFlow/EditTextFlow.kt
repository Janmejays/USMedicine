package com.integrationTestExample.data.models.editTextFlow

data class EditTextFlow(
    val query: String,
    val type: Type
) {
    enum class Type { ON }
}
