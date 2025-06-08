import SwiftUI
import Shared

@main
struct iOSApp: App {
    private let root: RootComponent
    
    init() {
        self.root = RootComponentImpl(componentContext: DefaultComponentContext(lifecycle: ApplicationLifecycle()))
    }

	var body: some Scene {
		WindowGroup {
			RootView(root)
		}
	}
}
