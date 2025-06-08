package ru.andvl.polytech.swcourse.shared.presentation.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.andvl.polytech.swcourse.shared.data.model.Person
import ru.andvl.polytech.swcourse.shared.domain.repository.PeopleRepository

interface DetailsStore : Store<Nothing, DetailsStore.State, Nothing> {

    data class State(
        val person: Person? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}

class DetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val peopleRepository: PeopleRepository,
    private val personUrl: String
) {
    fun create(): DetailsStore =
        object : DetailsStore, Store<Nothing, DetailsStore.State, Nothing> by storeFactory.create(
            name = "DetailsStore",
            initialState = DetailsStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object LoadDetails : Action
    }

    private sealed interface Msg {
        data class DetailsLoaded(val person: Person) : Msg
        data class Loading(val isLoading: Boolean) : Msg
        data class Error(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadDetails)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Nothing, Action, DetailsStore.State, Msg, Nothing>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadDetails -> loadDetails()
            }
        }

        private fun loadDetails() {
            scope.launch {
                dispatch(Msg.Loading(true))
                val id = personUrl.trimEnd('/').substringAfterLast('/').toInt()
                peopleRepository.getPerson(id)
                    .onSuccess { dispatch(Msg.DetailsLoaded(it)) }
                    .onFailure { dispatch(Msg.Error(it.message ?: "Unknown Error")) }
                dispatch(Msg.Loading(false))
            }
        }
    }

    private object ReducerImpl : Reducer<DetailsStore.State, Msg> {
        override fun DetailsStore.State.reduce(msg: Msg): DetailsStore.State =
            when (msg) {
                is Msg.Loading -> copy(isLoading = msg.isLoading)
                is Msg.Error -> copy(error = msg.error, isLoading = false)
                is Msg.DetailsLoaded -> copy(person = msg.person, isLoading = false)
            }
    }
} 