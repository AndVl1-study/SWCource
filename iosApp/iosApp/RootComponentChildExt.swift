import Foundation
import Shared

extension RootComponentChild {
    func onBack() {
        switch self {
        case let child as RootComponentChild.Details:
            child.component.onBackClicked()
        default:
            break
        }
    }
}
