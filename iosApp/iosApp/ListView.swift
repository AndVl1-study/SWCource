import SwiftUI
import Shared

struct ListView: View {
    @StateObject
    private var model: ObservableValue<ListComponentModel>
    private let component: ListComponent

    init(_ component: ListComponent) {
        self.component = component
        _model = StateObject(wrappedValue: ObservableValue(component.model))
    }

    var body: some View {
        let model = model.value
        ZStack {
            if let error = model.error, !model.isLoading {
                VStack(spacing: 16) {
                    Text(error)
                        .foregroundColor(.red)
                    Button("Reload", action: component.onReloadClicked)
                }
            } else {
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
                }
            }

            if model.items.isEmpty, model.isLoading {
                ProgressView()
            }
        }
        .navigationTitle("Star Wars People")
    }
}
