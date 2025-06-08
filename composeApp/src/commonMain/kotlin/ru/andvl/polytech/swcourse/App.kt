package ru.andvl.polytech.swcourse

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.andvl.polytech.swcourse.composeApp.ui.root.RootContent
import ru.andvl.polytech.swcourse.shared.presentation.root.RootComponentImpl

@Composable
@Preview
fun App() {
    val root = rememberComponentContext { RootComponentImpl(it) }
    MaterialTheme {
        RootContent(root)
    }
}

@Composable
fun rememberComponentContext(factory: (ComponentContext) -> RootComponentImpl): RootComponentImpl {
    val lifecycle = remember { LifecycleRegistry() }
    val context = remember { DefaultComponentContext(lifecycle) }
    return remember { factory(context) }
}
