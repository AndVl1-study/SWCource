package ru.andvl.polytech.swcourse.composeApp.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.andvl.polytech.swcourse.shared.data.model.Person
import ru.andvl.polytech.swcourse.shared.presentation.list.ListComponent

@Composable
fun ListContent(component: ListComponent) {
    val model by component.model.subscribeAsState()
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize().safeContentPadding()) {
        if (model.error != null) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = model.error ?: "Error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(model.items) { person ->
                    PersonCard(
                        person = person,
                        onPersonClick = { component.onPersonClicked(person) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (model.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward) {
            component.onLoadNextPageClicked()
        }
    }
}

@Composable
fun PersonCard(person: Person, onPersonClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onPersonClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = person.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
