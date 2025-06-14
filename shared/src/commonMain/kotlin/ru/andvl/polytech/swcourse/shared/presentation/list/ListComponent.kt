package ru.andvl.polytech.swcourse.shared.presentation.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.flow.stateIn
import ru.andvl.polytech.swcourse.shared.data.model.Person
import ru.andvl.polytech.swcourse.shared.domain.repository.PeopleRepository
import ru.andvl.polytech.swcourse.shared.util.asValue

interface ListComponent {

    val model: Value<Model>

    fun onPersonClicked(person: Person)
    fun onLoadNextPageClicked()
    fun onReloadClicked()

    data class Model(
        val items: List<Person>,
        val isLoading: Boolean,
        val error: String?
    )
}

class ListComponentImpl(
    componentContext: ComponentContext,
    peopleRepository: PeopleRepository,
    private val onPersonSelected: (personUrl: String) -> Unit
) : ListComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        ListStoreFactory(
            storeFactory = DefaultStoreFactory(), // In a real project, you would use a DI framework
            peopleRepository = peopleRepository
        ).create()
    }

    override val model: Value<ListComponent.Model> = store.stateFlow(lifecycle).asValue(lifecycle).map {
        ListComponent.Model(
            items = it.items,
            isLoading = it.isLoading,
            error = it.error
        )
    }

    override fun onPersonClicked(person: Person) {
        onPersonSelected(person.url)
    }

    override fun onLoadNextPageClicked() {
        store.accept(ListStore.Intent.LoadNextPage)
    }

    override fun onReloadClicked() {
        store.accept(ListStore.Intent.Reload)
    }
}
