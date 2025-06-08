package ru.andvl.polytech.swcourse.shared.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.andvl.polytech.swcourse.shared.data.network.ApiServiceImpl
import ru.andvl.polytech.swcourse.shared.data.network.createHttpClient
import ru.andvl.polytech.swcourse.shared.domain.repository.PeopleRepositoryImpl
import ru.andvl.polytech.swcourse.shared.presentation.details.DetailsComponent
import ru.andvl.polytech.swcourse.shared.presentation.details.DetailsComponentImpl
import ru.andvl.polytech.swcourse.shared.presentation.list.ListComponent
import ru.andvl.polytech.swcourse.shared.presentation.list.ListComponentImpl

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class List(val component: ListComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
    }
}

class RootComponentImpl(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    // DI happening here. In a real app, you would use a DI framework.
    private val httpClient = createHttpClient()
    private val apiService = ApiServiceImpl(httpClient)
    private val peopleRepository = PeopleRepositoryImpl(apiService)

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.List -> RootComponent.Child.List(
                ListComponentImpl(
                    componentContext = componentContext,
                    peopleRepository = peopleRepository,
                    onPersonSelected = { personUrl ->
                        navigation.push(Config.Details(personUrl))
                    }
                )
            )
            is Config.Details -> RootComponent.Child.Details(
               DetailsComponentImpl(
                   componentContext = componentContext,
                   personUrl = config.personUrl,
                   peopleRepository = peopleRepository,
                   onFinished = navigation::pop
               )
            )
        }


    @Serializable
    private sealed interface Config {
        @Serializable
        data object List : Config
        @Serializable
        data class Details(val personUrl: String) : Config
    }
} 