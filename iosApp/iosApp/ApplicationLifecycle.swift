import Shared
import Foundation

class ApplicationLifecycle: Lifecycle {
    private let lifecycle: LifecycleRegistry = LifecycleKt.lifecycleRegistry()

    init() {
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
    }

    func subscribe(callbacks: LifecycleCallbacks) {
        lifecycle.subscribe(callbacks: callbacks)
    }

    func unsubscribe(callbacks: LifecycleCallbacks) {
        lifecycle.unsubscribe(callbacks: callbacks)
    }

    var state: LifecycleState = .resumed
}
