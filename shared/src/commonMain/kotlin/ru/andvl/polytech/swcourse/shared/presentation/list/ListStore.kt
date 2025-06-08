package ru.andvl.polytech.swcourse.shared.presentation.list

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.andvl.polytech.swcourse.shared.data.model.Person
import ru.andvl.polytech.swcourse.shared.domain.repository.PeopleRepository

interface ListStore : Store<ListStore.Intent, ListStore.State, Nothing> {

    sealed interface Intent {
        data object LoadNextPage : Intent
    }

    data class State(
        val items: List<Person> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isLastPage: Boolean = false,
    )
}

class ListStoreFactory(
    private val storeFactory: StoreFactory,
    private val peopleRepository: PeopleRepository
) {
    fun create(): ListStore =
        object : ListStore, Store<ListStore.Intent, ListStore.State, Nothing> by storeFactory.create(
            name = "ListStore",
            initialState = ListStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object LoadFirstPage : Action
    }

    private sealed interface Msg {
        data class PageLoaded(val items: List<Person>, val isLastPage: Boolean) : Msg
        data class Loading(val isLoading: Boolean) : Msg
        data class Error(val error: String) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadFirstPage)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<ListStore.Intent, Action, ListStore.State, Msg, Nothing>() {
        private var currentPage = 1

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadFirstPage -> loadPage(1)
            }
        }

        override fun executeIntent(intent: ListStore.Intent) {
            when(intent) {
                is ListStore.Intent.LoadNextPage -> {
                    if (!state().isLastPage && !state().isLoading) {
                        loadPage(currentPage + 1)
                    }
                }
            }
        }

        private fun loadPage(page: Int) {
            scope.launch {
                dispatch(Msg.Loading(true))
                peopleRepository.getPeople(page)
                    .onSuccess {
                        currentPage = page
                        dispatch(Msg.PageLoaded(it.results, it.next == null))
                    }
                    .onFailure {
                        dispatch(Msg.Error(it.message ?: "Unknown Error"))
                    }
                dispatch(Msg.Loading(false))
            }
        }
    }

    private object ReducerImpl : Reducer<ListStore.State, Msg> {
        override fun ListStore.State.reduce(msg: Msg): ListStore.State =
            when (msg) {
                is Msg.Loading -> copy(isLoading = msg.isLoading, error = null)
                is Msg.Error -> copy(error = msg.error, isLoading = false)
                is Msg.PageLoaded -> copy(
                    items = items + msg.items,
                    isLastPage = msg.isLastPage,
                    isLoading = false,
                    error = null
                )
            }
    }
} 