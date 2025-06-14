import SwiftUI
import Shared

struct StackView<C: AnyObject, T: AnyObject, V: View>: View {
    @ObservedObject
    private var stack: ObservableValue<ChildStack<C, T>>
    private let getTitle: (T) -> String
    private let onBack: () -> Void
    private let content: (T) -> V

    init(
        stackValue: Value<ChildStack<C, T>>,
        getTitle: @escaping (T) -> String,
        onBack: @escaping () -> Void,
        @ViewBuilder content: @escaping (T) -> V
    ) {
        self.stack = ObservableValue(stackValue)
        self.getTitle = getTitle
        self.onBack = onBack
        self.content = content
    }

    var body: some View {
        let stack = self.stack.value
        let activeChild = stack.active

        return NavigationView {
            VStack {
                self.content(activeChild.instance)
                    .id(String(describing: activeChild.configuration))
            }
            .navigationBarTitle(self.getTitle(activeChild.instance), displayMode: .inline)
            .navigationBarItems(
                leading: Button(action: { withAnimation { self.onBack() } }) {
                    if !stack.backStack.isEmpty {
                        Image(systemName: "chevron.left")
                    }
                }
            )
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}
