package com.dastan.profscore.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dastan.profscore.ViewModel

@Composable
fun AllSimilarProfesor(navController: NavController, viewModel: ViewModel) {
    val allSimilarProfessor = viewModel.allProfDataSimilar.value
    Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {
        IconButton(onClick = {
            //navController.popBackStack()
            navController.navigateUp()
        }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        LazyColumn(modifier = Modifier.padding(8.dp).padding(start = 8.dp).fillMaxWidth()) {
            allSimilarProfessor.forEach { (course, profList) ->
                items(profList) { profData ->
                    SimilarBox(profData, course, navController, viewModel)
                }
            }

        }
    }
}