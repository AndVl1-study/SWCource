import SwiftUI
import Shared

struct RootView: View {
    private let root: RootComponent

    @ObservedObject
    private var stack: ObservableValue<ChildStack<RootComponentConfig, RootComponentChild>>

    init(_ root: RootComponent) {
        self.root = root
        self.stack = ObservableValue(root.stack)
    }

    var body: some View {
        StackView(
            stackValue: root.stack,
            getTitle: { _ in "" },
            onBack: { root.stack.value.active.instance.onBack() }
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
