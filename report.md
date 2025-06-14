# Курсовая работа: Анализ и выбор архитектурного подхода для iOS-ориентированного проекта на Kotlin Multiplatform

## Введение

**Целью курсовой работы** является проведение сравнительного анализа архитектурных подходов для создания мультиплатформенных приложений на Kotlin Multiplatform (KMP) с особым акцентом на интеграцию с нативной платформой iOS. В рамках исследования будут рассмотрены и сопоставлены как традиционные паттерны iOS-разработки (MVVM, Redux), так и современные KMP-ориентированные стеки (MVIKotlin с Decompose, MVVM с Jetpack Navigation). Основная задача — выявить наиболее эффективное и удобное решение для проекта, где iOS является одной из ключевых платформ.

Для достижения поставленной цели предполагается решить следующие **задачи**:

1.  Проанализировать теоретические основы и практическое применение архитектурных паттернов MVVM и Redux в контексте нативной iOS-разработки.
2.  Исследовать доступные архитектурные решения для Kotlin Multiplatform, уделив внимание подходам к управлению состоянием (MVI, MVVM) и навигации (Decompose, Jetpack Navigation).
3.  Определить ключевые критерии для сравнения подходов: сложность интеграции со SwiftUI, объем переиспользуемого кода, простота тестирования, количество шаблонного кода и общий опыт разработчика.
4.  На основе теоретического анализа и сформулированных критериев выбрать и обосновать наиболее подходящий архитектурный стек для дальнейшей реализации.
5.  Разработать прототип приложения, чтобы на практике проверить гипотезы и оценить выбранную архитектуру.
6.  Сформулировать выводы и рекомендации по применению исследуемых подходов в проектах на KMP с фокусом на iOS.

**Актуальность исследования.** Данная работа является актуальной, поскольку технология Kotlin Multiplatform, несмотря на растущую популярность, еще не имеет устоявшихся архитектурных стандартов, в особенности для реализации навигации и управления состоянием. Это создает значительные трудности для разработчиков, особенно для тех, кто переходит с нативной iOS-разработки и оказывается перед выбором: адаптировать привычные паттерны, такие как MVVM, или осваивать новые, KMP-специфичные библиотеки, например MVIKotlin и Decompose. Отсутствие четких сравнительных анализов и практических рекомендаций усложняет принятие архитектурных решений и повышает риски при старте новых проектов. Таким образом, систематизация и сравнительный анализ ключевых архитектурных подходов для KMP-приложений с фокусом на iOS представляет собой важную научную и практическую задачу.

## Теоретический обзор

### Нативные подходы в iOS-разработке

#### Паттерн Model-View-ViewModel (MVVM)

**Model-View-ViewModel (MVVM)** — это архитектурный паттерн проектирования, который упрощает разделение разработки графического интерфейса пользователя (UI) от бизнес-логики или логики представления. Он был создан как ответ на недостатки таких паттернов, как MVC и MVP, и получил широкое распространение в разработке под iOS с появлением фреймворков Combine и SwiftUI, которые обеспечивают нативную поддержку для связывания данных (data binding).

Основные компоненты паттерна:
-   **Model** (Модель) — представляет данные и бизнес-логику приложения. Она не имеет прямого знания о `ViewModel` и `View`.
-   **View** (Представление) — отвечает за отображение данных пользователю и передачу его действий в `ViewModel`. В SwiftUI `View` декларативно описывает UI и подписывается на изменения в `ViewModel`.
-   **ViewModel** (Модель представления) — выступает посредником между `Model` и `View`. Она получает данные из `Model`, обрабатывает их и предоставляет в формате, удобном для отображения в `View`. `ViewModel` ничего не знает о конкретной реализации `View`.

Ключевой особенностью MVVM является механизм **связывания данных (Data Binding)**. В экосистеме Apple он реализуется через фреймворк Combine, который позволяет `View` автоматически обновляться при изменении данных в `ViewModel`.

**Пример реализации на Swift и SwiftUI:**

В качестве примера рассмотрим экран, отображающий информацию о пользователе.

**1. Model**

Это простая структура данных, представляющая бизнес-сущность.

```swift
// Model: Простая структура данных
struct User {
    let name: String
    let age: Int
}
```

**2. ViewModel**

`ViewModel` инкапсулирует логику представления, получая данные из `Model` и подготавливая их для `View`.

```swift
// ViewModel: Управляет логикой представления
import Combine

class UserViewModel: ObservableObject {
    @Published var userName: String = "Загрузка..."
    @Published var isPremium: Bool = false
    private var user: User?

    func fetchUser() {
        // Имитация асинхронной загрузки
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.user = User(name: "Иван", age: 30)
            self.userName = self.user?.name ?? ""
            self.isPremium = (self.user?.age ?? 0) > 25
        }
    }
}
```

**3. View**

`View` декларативно описывает интерфейс и подписывается на изменения в `ViewModel`.

```swift
// View: Отображает данные и взаимодействует с пользователем
import SwiftUI

struct UserView: View {
    @StateObject private var viewModel = UserViewModel()

    var body: some View {
        VStack(spacing: 16) {
            Text(viewModel.userName)
                .font(.title)
            if viewModel.isPremium {
                Text("Премиум-пользователь")
                    .foregroundColor(.green)
            }
        }
        .onAppear(perform: viewModel.fetchUser)
    }
}
```

**Преимущества MVVM:**
-   **Разделение логики:** Четко отделяет логику представления (`ViewModel`) от пользовательского интерфейса (`View`), что делает код более структурированным и легким для понимания.
-   **Высокая тестируемость:** Так как `ViewModel` не имеет прямой зависимости от UI-компонентов, ее логику можно легко покрыть автоматизированными юнит-тестами.
-   **Связывание данных (Data Binding):** Нативная поддержка со стороны SwiftUI и Combine позволяет автоматически синхронизировать данные между `ViewModel` и `View`, что сокращает количество шаблонного кода для обновления UI.

**Недостатки:**
-   **Проблема масштабирования:** На сложных экранах, где множество компонентов взаимодействуют друг с другом, `ViewModel` может стать слишком громоздкой (т.н. "Massive ViewModel"). Это затрудняет поддержку и навигацию по коду.
-   **Синхронизация состояния:** При дроблении логики на несколько `ViewModel` для одного экрана возникает проблема синхронизации состояния и обмена данными между ними, что усложняет поток данных.
-   **Неявный поток данных:** В отличие от однонаправленных потоков (как в Redux/MVI), логика обработки действий пользователя и изменения состояния может быть разбросана по разным методам `ViewModel`, что делает поток данных менее предсказуемым и сложным для отладки.

#### Паттерн Redux

**Redux** — это архитектурный паттерн и библиотека для управления состоянием приложения, основанная на принципах функционального программирования. Хотя он зародился в экосистеме JavaScript (React), его концепции были успешно адаптированы и для нативной iOS-разработки, часто с использованием таких библиотек, как ReSwift или The Composable Architecture (TCA), которая во многом следует идеям Redux.

Ключевые принципы Redux:
-   **Единый источник истины (Single Source of Truth):** Состояние всего приложения хранится в одном объекте — **Store**. Это упрощает отладку и инспектирование состояния.
-   **Состояние только для чтения (State is read-only):** Единственный способ изменить состояние — это отправить (dispatch) **Action** (действие), которое является простым объектом, описывающим намерение изменения.
-   **Изменения вносятся чистыми функциями:** Для обработки `Action` и обновления состояния используются **Reducers** (редьюсеры) — чистые функции, которые принимают текущее состояние и `Action`, а затем возвращают новое состояние.

**Пример реализации на Swift (концептуальный):**

Рассмотрим простой счетчик.

**1. State, Actions и Reducer**

Определим состояние, возможные действия и чистую функцию для их обработки.

```swift
// State, Actions, Reducer
struct CounterState {
    var count: Int = 0
}

enum CounterAction {
    case increment
    case decrement
}

func counterReducer(state: inout CounterState, action: CounterAction) {
    switch action {
    case .increment: state.count += 1
    case .decrement: state.count -= 1
    }
}
```

**2. Store**

`Store` управляет состоянием и является единственным источником истины.

```swift
// Store
import Combine

class Store<State, Action>: ObservableObject {
    @Published private(set) var state: State
    private var reducer: (inout State, Action) -> Void

    init(initialState: State, reducer: @escaping (inout State, Action) -> Void) {
        self.state = initialState
        self.reducer = reducer
    }

    func dispatch(_ action: Action) {
        reducer(&state, action)
    }
}
```

**3. View**

`View` отображает состояние из `Store` и отправляет действия при взаимодействии.

```swift
// View
import SwiftUI

struct CounterView: View {
    @StateObject private var store = Store(
        initialState: CounterState(),
        reducer: counterReducer
    )

    var body: some View {
        VStack {
            Text("Count: \(store.state.count)")
            HStack {
                Button("Increment") { store.dispatch(.increment) }
                Button("Decrement") { store.dispatch(.decrement) }
            }
        }
    }
}
```

**Преимущества Redux:**
-   **Предсказуемость:** Однонаправленный поток данных делает логику изменения состояния строгой и легкой для отслеживания.
-   **Централизованное состояние:** Упрощает передачу данных между разными частями приложения без необходимости пробрасывать их через множество слоев.
-   **Отличные инструменты для отладки:** Возможность "путешествия во времени" (time travel debugging), логирование всех действий.

**Недостатки:**
-   **Многословность (Boilerplate):** Требует описания `State`, `Actions` и `Reducer`, что может быть избыточным для простых экранов.
-   **Сложен для понимания:** Требует понимания принципов функционального программирования.
-   **Производительность:** В очень больших приложениях с частыми обновлениями состояния могут возникнуть проблемы с производительностью, если не применять оптимизации.

### Архитектурные подходы в Kotlin Multiplatform

#### Адаптация MVVM с Jetpack ViewModel

Стремление использовать привычный паттерн MVVM в KMP-проектах привело к появлению различных решений. Изначально `ViewModel` из Android Jetpack не была мультиплатформенной, что заставляло разработчиков создавать собственные реализации или использовать сторонние библиотеки (например, `moko-mvvm`).

Однако с недавнего времени Google официально поддерживает **Jetpack ViewModel в Kotlin Multiplatform** (`androidx.lifecycle:lifecycle-viewmodel-compose`). Это позволяет определять `ViewModel` в общем коде (`commonMain`), переиспользуя логику представления на всех платформах. На Android `ViewModel` автоматически интегрируется с жизненным циклом компонентов, а на iOS требует ручного создания и удержания.

**Пример реализации:**

**1. ViewModel в общем коде (`commonMain`)**

`ViewModel` определяется в общем модуле и использует `StateFlow` для предоставления состояния.

```kotlin
// shared/src/commonMain/kotlin/common/GreetingViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GreetingViewModel : ViewModel() {
    private val _greetingText = MutableStateFlow("Загрузка...")
    val greetingText = _greetingText.asStateFlow()

    fun greet() {
        viewModelScope.launch {
            _greetingText.value = "Привет из KMP ViewModel!"
        }
    }
}
```

**2. Интеграция с iOS и SwiftUI**

На стороне iOS для корректной интеграции со SwiftUI и подписки на `StateFlow` обычно создается класс-обертка, который является `ObservableObject`.

```swift
// iosApp/GreetingIOSViewModel.swift
import Foundation
import Combine
import shared

class GreetingIOSViewModel: ObservableObject {
    @Published var text: String = "..."
    
    private let kmpViewModel = GreetingViewModel()
    private var cancellable: AnyCancellable? = nil

    init() {
        // Установка подписки на StateFlow
        self.cancellable = kmpViewModel.greetingText
            .toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { self.text = $0 }
    }
    
    func greet() {
        kmpViewModel.greet()
    }
}
// Для toPublisher() требуется небольшая вспомогательная функция
```

**3. View в SwiftUI**

`View` работает уже с iOS-специфичной `ViewModel`.

```swift
// iosApp/ContentView.swift
import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = GreetingIOSViewModel()

    var body: some View {
        VStack {
            Text(viewModel.text)
            Button("Обновить", action: viewModel.greet)
        }
        .onAppear(perform: viewModel.greet)
    }
}
```

**Преимущества:**
-   **Официальная поддержка:** Подход поддерживается Google, что гарантирует его развитие и стабильность.
-   **Привычность для Android-разработчиков:** Позволяет использовать знакомые концепции `ViewModel` и `viewModelScope`.
-   **Разделение логики:** Сохраняется ключевое преимущество MVVM — отделение логики от UI.

**Недостатки:**
-   **Шаблонный код на iOS:** Требуется создавать классы-обертки для каждой `ViewModel`, чтобы связать их со SwiftUI.
-   **Отсутствие управления навигацией:** Данный подход решает только проблему `ViewModel`, но не предлагает общего решения для навигации между экранами.
-   **Управление жизненным циклом на iOS:** Необходимо вручную создавать и удерживать `ViewModel` на стороне iOS, в то время как на Android это происходит автоматически.

#### MVIKotlin с Decompose

**MVIKotlin** — это библиотека для Kotlin Multiplatform, реализующая архитектурный паттерн Model-View-Intent (MVI). В сочетании с **Decompose** — библиотекой для управления навигацией и жизненным циклом компонентов — она предоставляет полноценное решение для создания мультиплатформенных приложений.

Ключевые концепции MVI:
-   **Model** — состояние компонента
-   **View** — UI-слой, отображающий состояние
-   **Intent** — действия пользователя или системы
-   **Store** — компонент, обрабатывающий Intent'ы и обновляющий состояние

**Пример реализации:**

**1. Определение состояния и действий**

```kotlin
// shared/src/commonMain/kotlin/common/CounterState.kt
data class CounterState(
    val count: Int = 0
)

sealed class CounterIntent {
    object Increment : CounterIntent()
    object Decrement : CounterIntent()
}
```

**2. Создание Store**

```kotlin
// shared/src/commonMain/kotlin/common/CounterStore.kt
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

class CounterStore(
    storeFactory: DefaultStoreFactory
) : Store<CounterIntent, CounterState, Nothing> by storeFactory.create(
    name = "CounterStore",
    initialState = CounterState(),
    reducer = { intent, state ->
        when (intent) {
            is CounterIntent.Increment -> state.copy(count = state.count + 1)
            is CounterIntent.Decrement -> state.copy(count = state.count - 1)
        }
    }
)
```

**3. Создание компонента с Decompose**

```kotlin
// shared/src/commonMain/kotlin/common/CounterComponent.kt
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy

class CounterComponent(
    componentContext: ComponentContext,
    private val storeFactory: DefaultStoreFactory
) : ComponentContext by componentContext {
    private val store = CounterStore(storeFactory)
    
    val state = store.state
    
    fun increment() {
        store.accept(CounterIntent.Increment)
    }
    
    fun decrement() {
        store.accept(CounterIntent.Decrement)
    }
    
    init {
        lifecycle.doOnDestroy {
            store.dispose()
        }
    }
}
```

**4. Интеграция с iOS и SwiftUI**

Для элегантной интеграции Decompose со SwiftUI принято использовать универсальную обертку, которая превращает `Value` из мира Decompose в `ObservableObject` для мира SwiftUI. Это позволяет избежать написания повторяющегося кода для каждого экрана.

Пример такой обертки и ее использования:

```swift
// 1. Универсальная обертка ObservableValue
import SwiftUI
import shared

class ObservableValue<T>: ObservableObject {
    @Published var value: T
    private var cancellation: Cancellation?

    init(_ value: Value<T>) {
        self.value = value.value
        self.cancellation = value.subscribe { [weak self] in
            self?.value = $0
        }
    }

    deinit {
        cancellation?.cancel()
    }
}

// 2. SwiftUI View, использующее обертку
struct CounterView: View {
    @StateObject private var model: ObservableValue<CounterState>
    
    private let component: CounterComponent

    init(_ component: CounterComponent) {
        self.component = component
        _model = StateObject(wrappedValue: ObservableValue(component.state))
    }

    var body: some View {
        VStack(spacing: 16) {
            Text("Count: \(model.value.count)")
                .font(.title)
            
            HStack(spacing: 24) {
                Button("Increment") { component.increment() }
                Button("Decrement") { component.decrement() }
            }
        }
    }
}
```

**Преимущества MVIKotlin с Decompose:**
-   **Единый подход к навигации:** Decompose предоставляет унифицированное решение для управления навигацией на всех платформах.
-   **Предсказуемый поток данных:** Однонаправленный поток данных делает поведение приложения более предсказуемым и тестируемым.
-   **Мультиплатформенность:** Все компоненты определены в общем коде, что обеспечивает максимальное переиспользование.
-   **Управление жизненным циклом:** Decompose автоматически управляет жизненным циклом компонентов на всех платформах.

**Недостатки:**
-   **Сложность интеграции:** Требует более глубокого понимания архитектуры и дополнительных настроек по сравнению с MVVM.
-   **Объем шаблонного кода:** Необходимость определения состояний, интентов и редьюсеров для каждого компонента может привести к увеличению объема кода.
-   **Крутая кривая обучения:** Разработчикам, привыкшим к традиционным паттернам, может потребоваться время для освоения MVI и Decompose.

## Описание приложения

В рамках практической части работы было разработано мультиплатформенное приложение на Kotlin Multiplatform, демонстрирующее получение, отображение и навигацию по данным, полученным из открытого API.

### Функциональность

Приложение реализует базовый сценарий взаимодействия с удаленным сервером и предоставляет следующий функционал:

1.  **Отображение списка персонажей**: При запуске приложения открывается главный экран, на котором в виде списка отображаются персонажи, загруженные из Star Wars API (SWAPI). Реализована пагинация для постепенной подгрузки данных при прокрутке списка (см. рисунок 1).
2.  **Просмотр детальной информации**: При нажатии на элемент списка происходит переход на экран с подробной информацией о выбранном персонаже (см. рисунок 2).
3.  **Обработка состояний**: В приложении реализована обработка состояний загрузки и ошибок как для списка, так и для экрана с детальной информацией, что обеспечивает корректное отображение UI в различных сценариях.

### Структура проекта

Проект имеет стандартную для Kotlin Multiplatform архитектуру и состоит из перечисленных модулей:

-   `shared`: Общий модуль, содержащий бизнес-логику, логику представления, сетевой слой и доменные модели. Код в этом модуле компилируется как для Android, так и для iOS.
-   `iosApp`: Модуль, специфичный для платформы iOS, который содержит UI-слой, реализованный на SwiftUI, и обеспечивает интеграцию с общим кодом из модуля `shared`.
-   `composeApp`: Реализация Compose Multiplatform кода, который используется в Android и Desktop версиях приложения. В рамках данной курсовой работы не рассматривается.

Модуль `shared` внутри разделен на три логических слоя, что соответствует принципам чистой архитектуры:

-   `data`: Отвечает за получение данных из сети (с помощью Ktor) и их преобразование.
-   `domain`: Содержит бизнес-сущности (модели) и интерфейсы репозиториев.
-   `presentation`: Реализует логику представления с использованием MVIKotlin и управляет навигацией с помощью Decompose.

### Используемые технологии и библиотеки

Для реализации проекта был выбран следующий стек технологий:

-   **Kotlin Multiplatform**: Основа для создания переиспользуемого кода между iOS и Android.
-   **Decompose**: Библиотека для мультиплатформенной навигации и управления жизненным циклом компонентов. Она позволила реализовать всю навигационную логику в общем модуле `shared`.
-   **MVIKotlin**: Реализация паттерна MVI для управления состоянием. Использовалась для создания предсказуемого и тестируемого потока данных.
-   **Ktor**: Асинхронный сетевой фреймворк для выполнения HTTP-запросов к API.
-   **Kotlinx.serialization**: Библиотека для сериализации и десериализации данных в формате JSON.
-   **SwiftUI**: Декларативный фреймворк для построения UI на платформе iOS.

### Пример реализации навигации

Навигация в приложении управляется с помощью `RootComponent`, который содержит `StackNavigation` из библиотеки Decompose. В нем определяются возможные экраны (`Config`) и создаются дочерние компоненты для каждого из них.

```kotlin
// shared/src/commonMain/kotlin/ru/andvl/polytech/swcourse/shared/presentation/root/RootComponent.kt
class RootComponentImpl(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<RootComponent.Config>()

    override val stack: Value<ChildStack<RootComponent.Config, RootComponent.Child>> =
        childStack(
            source = navigation,
            // ...
            initialConfiguration = RootComponent.Config.List,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: RootComponent.Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is RootComponent.Config.List -> RootComponent.Child.List(
                ListComponentImpl(
                    componentContext = componentContext,
                    // ...
                    onPersonSelected = { personUrl ->
                        navigation.pushNew(RootComponent.Config.Details(personUrl))
                    }
                )
            )
            is RootComponent.Config.Details -> RootComponent.Child.Details(
               DetailsComponentImpl(
                   componentContext = componentContext,
                   // ...
                   onFinished = navigation::pop
               )
            )
        }
    }
}
```

Данный подход позволяет описать все навигационные переходы в общем коде, делая UI-слой на стороне iOS максимально "тонким" и ответственным только за отображение состояния.
