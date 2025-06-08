package ru.andvl.polytech.swcourse.shared.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.andvl.polytech.swcourse.shared.data.model.Page
import ru.andvl.polytech.swcourse.shared.data.model.Person

private const val BASE_URL = "https://swapi.py4e.com/api"

interface ApiService {
    suspend fun getPeople(page: Int): Page<Person>
    suspend fun getPerson(id: Int): Person
}

class ApiServiceImpl(
    private val httpClient: HttpClient
) : ApiService {

    override suspend fun getPeople(page: Int): Page<Person> {
        return httpClient.get("$BASE_URL/people") {
            parameter("page", page)
        }.body()
    }

    override suspend fun getPerson(id: Int): Person {
        return httpClient.get("$BASE_URL/people/$id").body()
    }
} 