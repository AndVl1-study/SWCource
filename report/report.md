# Курсовая работа по дисциплине "Разработка мобильных приложений под iOS"

## Тема: Сравнительный анализ подходов к построению архитектуры в мультиплатформенных приложениях на базе Kotlin Multiplatform

### 1. Введение

**Цель работы:** Исследовать и реализовать современный подход к построению клиент-серверных мультиплатформенных приложений с использованием Kotlin Multiplatform, разделяя бизнес-логику и сохраняя нативный UI для каждой платформы.

**Задачи:**
- Разработать приложение с использованием общей бизнес-логики на Kotlin Multiplatform.
- Реализовать нативный UI для Android/Desktop (Jetpack Compose) и iOS (SwiftUI).
- Применить современный архитектурный стек: Decompose для навигации и декомпозиции на компоненты, MVIKotlin для управления состоянием.
- Организовать сетевое взаимодействие с публичным API.
- Проанализировать преимущества и недостатки выбранного подхода.

### 2. Теоретический раздел

#### 2.1. Выбранный стек технологий

- **Kotlin Multiplatform:** Технология, позволяющая переиспользовать код между различными платформами (iOS, Android, Desktop, Web). В данном проекте используется для написания общей бизнес-логики.
- **Decompose:** Библиотека от `Arkadii Ivanov`, предоставляющая компоненто-ориентированный подход к разработке UI и управлению навигацией в мультиплатформенных проектах. Идеально сочетается с декларативными UI-фреймворками.
- **MVIKotlin:** Реализация паттерна MVI (Model-View-Intent) для Kotlin Multiplatform от того же автора, что и Decompose. Обеспечивает однонаправленный поток данных, что делает состояние приложения предсказуемым и легко отлаживаемым.
- **Ktor:** Асинхронный фреймворк от JetBrains для создания сетевых клиентов и серверов. Используется для взаимодействия с REST API.
- **Jetpack Compose:** Современный декларативный UI-фреймворк от Google для Android и Desktop.
- **SwiftUI:** Декларативный UI-фреймворк от Apple для iOS.

#### 2.2. API

В качестве источника данных будет использоваться [Star Wars API (SWAPI)](https://swapi.py4e.com/), предоставляющее информацию о персонажах, планетах и других сущностях вселенной "Звездных Войн".

### 3. Описание приложения

#### 3.1. Структура `shared` модуля

Общий код организован в `shared` модуле и разделен на три основных слоя, что соответствует принципам чистой архитектуры:

- **Data Layer (`shared/data`):** Отвечает за получение данных.
    - `model`: DTO-классы (`Person`, `Page`), аннотированные `@Serializable` для парсинга JSON.
    - `network`: `KtorClient` для настройки HTTP-клиента и `ApiService` с suspend-функциями для запросов к SWAPI.
- **Domain Layer (`shared/domain`):** Содержит бизнес-логику.
    - `repository`: `PeopleRepository` — интерфейс, который абстрагирует слой данных. Его реализация `PeopleRepositoryImpl` использует `ApiService` и оборачивает вызовы в `Result` для обработки ошибок.
- **Presentation Layer (`shared/presentation`):** Отвечает за логику UI и управление состоянием.
    - `root`: `RootComponent` — корневой компонент, управляющий навигацией между экранами с помощью `ChildStack` из Decompose.
    - `list`, `details`: `ListComponent` и `DetailsComponent` — компоненты для каждого экрана. Они не содержат UI-кода, только логику. Состояние каждого компонента управляется собственным MVI-стором (`ListStore`, `DetailsStore`) на базе MVIKotlin.

#### 3.2. Архитектура iOS-приложения

Интеграция общего `shared` модуля с нативным iOS-приложением на SwiftUI выполняется с помощью нескольких ключевых техник.

**1. Экспорт зависимостей:**
Чтобы Swift мог "видеть" классы из библиотек, используемых в `shared`, их API необходимо экспортировать. В `shared/build.gradle.kts` мы явно указываем, что `decompose` и `essenty` должны быть частью публичного API фреймворка:

```kotlin
// ...
iosTarget.binaries.framework {
    baseName = "Shared"
    isStatic = true
    export(libs.decompose)
    export(libs.essenty.lifecycle)
}
// ...
```

**2. Жизненный цикл (Lifecycle):**
Decompose-компоненты привязаны к жизненному циклу. Для iOS мы создаем `ApplicationLifecycle.swift`, который предоставляет `Lifecycle` для корневого компонента. Чтобы обойти проблемы с доступностью конструктора `LifecycleRegistry` из Swift, была создана фабричная функция в `shared/src/iosMain`:

```kotlin
// shared/src/iosMain/kotlin/.../Lifecycle.kt
@file:ObjCName("IosUtils")
package ru.andvl.polytech.swcourse.shared

import com.arkivanov.essenty.lifecycle.LifecycleRegistry

fun lifecycleRegistry(): LifecycleRegistry = LifecycleRegistry()
```

В Swift мы используем `IosUtils.lifecycleRegistry()` для создания экземпляра.

**3. Связывание Decompose и SwiftUI (`Value` -> `ObservableObject`):**
SwiftUI не может напрямую работать с `Value` из Decompose. Мы создаем обертку `ObservableValue.swift`, которая подписывается на изменения `Value` и публикует их через `@Published`, что позволяет SwiftUI отслеживать состояние.

```swift
// ObservableValue.swift
class ObservableValue<T: AnyObject>: ObservableObject {
    @Published
    var value: T
    // ...
    init(_ value: Value<T>) {
        self.value = value.value
        self.cancellation = value.subscribe { [weak self] value in
            self?.value = value
        }
    }
    // ...
}
```

**4. Навигация:**
Для отображения навигационного стека (`ChildStack`) из Decompose в SwiftUI был создан универсальный `StackView.swift`. Он использует `NavigationView` и отслеживает активный дочерний компонент, отображая для него соответствующий `View`.

```swift
// RootView.swift
struct RootView: View {
    // ...
    var body: some View {
        StackView(
            stackValue: root.stack,
            // ...
        ) { child in
            switch child {
            case let child as RootComponentChild.List:
                ListView(child.component)
            case let child as RootComponentChild.Details:
                DetailsView(child.component)
            default:
                EmptyView()
            }
        }
    }
}
```

### 4. Анализ и выводы

Выбранный архитектурный подход (Kotlin Multiplatform + Decompose + MVIKotlin) позволяет эффективно переиспользовать код бизнес-логики, сохраняя при этом полностью нативный пользовательский опыт на каждой платформе.

**Преимущества:**
- **Высокая степень переиспользования кода:** Вся бизнес-логика, включая сетевые запросы, управление состоянием и навигацию, написана один раз.
- **Нативный UI/UX:** Пользователи получают привычный и отзывчивый интерфейс, так как UI написан на нативных фреймворках (SwiftUI, Jetpack Compose).
- **Типобезопасность и предсказуемость:** MVI-архитектура с однонаправленным потоком данных упрощает отладку и делает состояние приложения предсказуемым.
- **Разделение ответственностей:** Четкое разделение на слои (Data, Domain, Presentation) и компоненты (Decompose) делает код модульным и легко тестируемым.

**Недостатки и сложности:**
- **Интероперабельность Kotlin/Swift:** Требуются дополнительные усилия для обеспечения гладкого взаимодействия между двумя языками: создание оберток (`ObservableValue`), экспорт зависимостей, решение проблем с видимостью конструкторов.
- **Порог вхождения:** Стек технологий (KMP, Decompose, MVIKotlin) является достаточно новым и требует времени на изучение.
- **Отладка:** Отладка общего кода, особенно в контексте iOS, может быть менее удобной, чем при полностью нативной разработке.
