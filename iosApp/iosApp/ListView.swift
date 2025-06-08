import SwiftUI
import Shared

struct ListView: View {
    private let component: ListComponent

    @ObservedObject
    private var model: ObservableValue<ListComponentModel>

    init(_ component: ListComponent) {
        self.component = component
        self.model = ObservableValue(component.model)
    }

    var body: some View {
        let model = self.model.value

        return ZStack {
            List(model.items, id: \.url) { item in
                Text(item.name)
                    .onTapGesture {
                        component.onPersonClicked(person: item)
                    }
            }
            .onAppear(perform: {
                // This is a workaround to detect when the list is scrolled to the end.
                // A better solution would be to use a custom View that wraps UIScrollView.
                Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { _ in
                    component.onLoadNextPageClicked()
                }
            })

            if model.isLoading {
                ProgressView()
            }
        }
    }
}
