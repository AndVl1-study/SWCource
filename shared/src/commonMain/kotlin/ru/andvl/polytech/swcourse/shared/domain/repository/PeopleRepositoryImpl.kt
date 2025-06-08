package ru.andvl.polytech.swcourse.shared.domain.repository

import ru.andvl.polytech.swcourse.shared.data.network.ApiService

class PeopleRepositoryImpl(
    private val apiService: ApiService
) : PeopleRepository {

    override suspend fun getPeople(page: Int) = runCatching {
        apiService.getPeople(page)
    }

    override suspend fun getPerson(id: Int) = runCatching {
        apiService.getPerson(id)
    }
} 