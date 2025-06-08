package ru.andvl.polytech.swcourse.composeApp.ui.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.andvl.polytech.swcourse.composeApp.ui.details.DetailsContent
import ru.andvl.polytech.swcourse.composeApp.ui.list.ListContent
import ru.andvl.polytech.swcourse.shared.presentation.root.RootComponent

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(slide())
    ) {
        when(val child = it.instance) {
            is RootComponent.Child.List -> ListContent(child.component)
            is RootComponent.Child.Details -> DetailsContent(child.component)
        }
    }
} 