package ru.andvl.polytech.swcourse.composeApp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.andvl.polytech.swcourse.composeApp.ui.util.BackButtonHandler
import ru.andvl.polytech.swcourse.shared.presentation.details.DetailsComponent

@Composable
fun DetailsContent(component: DetailsComponent) {
    val model by component.model.subscribeAsState()
    BackButtonHandler(component.backHandler, true) {
        component.onBackClicked()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            model.isLoading -> CircularProgressIndicator()
            model.error != null -> Text(text = "Error: ${model.error}")
            model.person != null -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${model.person!!.name}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Height: ${model.person!!.height}")
                    Text("Mass: ${model.person!!.mass}")
                    Text("Hair Color: ${model.person!!.hairColor}")
                    Text("Skin Color: ${model.person!!.skinColor}")
                    Text("Eye Color: ${model.person!!.eyeColor}")
                    Text("Birth Year: ${model.person!!.birthYear}")
                    Text("Gender: ${model.person!!.gender}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = component::onBackClicked) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
