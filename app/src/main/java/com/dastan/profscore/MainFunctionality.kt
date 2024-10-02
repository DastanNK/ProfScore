package com.dastan.profscore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProfessorSearch(
    viewModel: ViewModel
) {
    val focusManager = LocalFocusManager.current
    val text = viewModel.queryText.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusState = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 4.dp).border(
            width = 1.dp,
            color = if (focusState.value) colorScheme.scrim else colorScheme.onPrimary,
            shape = RoundedCornerShape(50)
        )
            .background(Color.Transparent).clickable {
            focusManager.clearFocus() // Clear focus on tap outside the TextField
            keyboardController?.hide() // Hide keyboard
        }
    ) {
        TextField(value = text.value,
            onValueChange = {
                viewModel.onQueryUpdate(it)
            },
            maxLines = 1,
            leadingIcon = {
                if (text.value.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.onQueryUpdate("")
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                    }
                }else{
                    Icon(Icons.Default.Search, contentDescription = "search")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search, // Set the IME action to Done
                keyboardType = KeyboardType.Text // Specify the type of keyboard
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel::onQueryUpdate
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.focusTarget().onFocusChanged { focusState.value = it.isFocused },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledTextColor = colorScheme.onSurface,
                disabledIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent,
                cursorColor = colorScheme.scrim,
                textColor = colorScheme.onPrimary
            )
        )
    }
}

@Composable
fun CommonProgressSpiner() {
    Row(
        modifier = Modifier.fillMaxSize().clickable(enabled = false) {},
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}