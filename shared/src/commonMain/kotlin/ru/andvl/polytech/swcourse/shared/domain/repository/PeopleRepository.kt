package ru.andvl.polytech.swcourse.shared.domain.repository

import ru.andvl.polytech.swcourse.shared.data.model.Page
import ru.andvl.polytech.swcourse.shared.data.model.Person

interface PeopleRepository {
    suspend fun getPeople(page: Int): Result<Page<Person>>
    suspend fun getPerson(id: Int): Result<Person>
} 