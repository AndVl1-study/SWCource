# Kotlin Multiplatform - Сравнение архитектурных подходов (Курсовая работа)

Это проект-прототип, разработанный в рамках курсовой работы по теме "Анализ и выбор архитектурного подхода для iOS-ориентированного проекта на Kotlin Multiplatform".

Проект представляет собой простое приложение для просмотра списка персонажей из Star Wars API (SWAPI) с возможностью перехода на экран с детальной информацией.

## Архитектура

*   **Общий код (`shared`):** Kotlin Multiplatform
*   **Архитектурный паттерн:** MVI (с библиотекой MVIKotlin)
*   **Навигация:** Decompose
*   **Сеть:** Ktor
*   **UI (iOS):** SwiftUI

## Запуск на iOS

Для запуска iOS-приложения требуется **Xcode** и **Android Studio** (или IntelliJ IDEA) с установленным плагином [**Kotlin Multiplatform**](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform).

### Способ 1: Через Android Studio / IntelliJ IDEA

Это самый простой способ, так как среда разработки автоматически выполнит все необходимые шаги по сборке общего кода.

1.  Откройте проект в Android Studio или IntelliJ IDEA.
2.  Дождитесь, пока Gradle синхронизирует все зависимости.
3.  Выберите конфигурацию запуска `iosApp` в выпадающем списке конфигураций.
4.  Нажмите кнопку "Run" (▶️). Среда автоматически соберет KMP-модуль, запустит симулятор и установит на него приложение.

### Способ 2: Через Xcode

Этот способ также работает, но может потребовать предварительной сборки общего модуля.

1.  **(Если необходимо)** Сначала откройте проект в Android Studio / IntelliJ IDEA, чтобы Gradle мог скачать зависимости и сгенерировать фреймворк для iOS (`shared.framework`). В качестве альтернативы можно один раз выполнить команду `./gradlew build` в терминале из корневой папки проекта.
2.  Откройте файл `iosApp/iosApp.xcodeproj` в Xcode.
3.  Выберите симулятор и нажмите "Build and run" (▶️).

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that's common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple's CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you're sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
