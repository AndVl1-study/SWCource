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
            List {
                ForEach(model.items, id: \.url) { item in
                    Button(action: { withAnimation { component.onPersonClicked(person: item) } }) {
                        Text(item.name)
                            .padding(.horizontal)
                            .padding(.vertical, 12)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .contentShape(Rectangle())
                    .onAppear {
                        if item == model.items.last {
                            component.onLoadNextPageClicked()
                        }
                    }
                }
                .listRowInsets(EdgeInsets())
            }

            if model.isLoading {
                ProgressView()
            }
        }
    }
}
