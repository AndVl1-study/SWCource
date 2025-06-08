package ru.andvl.polytech.swcourse.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val name: String,
    val height: String,
    val mass: String,
    @SerialName("hair_color")
    val hairColor: String,
    @SerialName("skin_color")
    val skinColor: String,
    @SerialName("eye_color")
    val eyeColor: String,
    @SerialName("birth_year")
    val birthYear: String,
    val gender: String,
    val url: String
) 