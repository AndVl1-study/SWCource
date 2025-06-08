package ru.andvl.polytech.swcourse.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
) 