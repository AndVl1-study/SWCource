package ru.andvl.polytech.swcourse.shared.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import ru.andvl.polytech.swcourse.shared.data.network.ApiService

class PeopleRepositoryImpl(
    private val apiService: ApiService
) : PeopleRepository {

    override suspend fun getPeople(page: Int) = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getPeople(page)
        }
    }

    override suspend fun getPerson(id: Int) = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getPerson(id)
        }
    }
}
