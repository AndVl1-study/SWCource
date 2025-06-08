package ru.andvl.polytech.swcourse.shared.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T : Any> StateFlow<T>.asValue(lifecycle: Lifecycle): Value<T> =
    object : Value<T>() {
        override val value: T get() = this@asValue.value

        override fun subscribe(observer: (T) -> Unit): Cancellation {
            observer(value) // Send initial value synchronously

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

            this@asValue
                .onEach(observer)
                .launchIn(scope)

            val cancellation = Cancellation(scope::cancel)
            lifecycle.doOnDestroy(cancellation::cancel)

            return cancellation
        }
    }
