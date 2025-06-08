import SwiftUI
import Shared

struct DetailsView: View {
    private let component: DetailsComponent

    @ObservedObject
    private var model: ObservableValue<DetailsComponentModel>

    init(_ component: DetailsComponent) {
        self.component = component
        self.model = ObservableValue(component.model)
    }

    var body: some View {
        let model = self.model.value

        return VStack {
            if model.isLoading {
                ProgressView()
            } else if let person = model.person {
                Text("Name: \(person.name)")
                Text("Height: \(person.height)")
                Text("Mass: \(person.mass)")
                Text("Hair Color: \(person.hairColor)")
                Text("Skin Color: \(person.skinColor)")
                Text("Eye Color: \(person.eyeColor)")
                Text("Birth Year: \(person.birthYear)")
                Text("Gender: \(person.gender)")
            } else if let error = model.error {
                Text("Error: \(error)")
            }
        }
    }
}
