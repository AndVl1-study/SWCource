package ru.andvl.polytech.swcourse.shared.presentation.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.andvl.polytech.swcourse.shared.data.model.Person
import ru.andvl.polytech.swcourse.shared.domain.repository.PeopleRepository
import ru.andvl.polytech.swcourse.shared.util.asValue

interface DetailsComponent : BackHandlerOwner {
    val model: Value<Model>

    fun onBackClicked()
    fun onReloadClicked()

    data class Model(
        val person: Person? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )
}

class DetailsComponentImpl(
    componentContext: ComponentContext,
    personUrl: String,
    peopleRepository: PeopleRepository,
    private val onFinished: () -> Unit
) : DetailsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        DetailsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            peopleRepository = peopleRepository,
            personUrl = personUrl
        ).create()
    }

    override val model: Value<DetailsComponent.Model> = store
        .stateFlow(lifecycle)
        .asValue(lifecycle)
        .map {
            DetailsComponent.Model(
                person = it.person,
                isLoading = it.isLoading,
                error = it.error
            )
        }

    override fun onBackClicked() {
        onFinished()
    }

    override fun onReloadClicked() {
        store.accept(DetailsStore.Intent.Reload)
    }
}
