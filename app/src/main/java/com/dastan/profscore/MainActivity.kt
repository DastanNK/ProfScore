package com.dastan.profscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dastan.profscore.ui.theme.ProfScoreTheme
import com.dastan.profscore.screen.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfScoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp(){

    val navController= rememberNavController()
    val viewModel = hiltViewModel<ViewModel>()
    if(viewModel.inProgress.value){
        CommonProgressSpiner()
    }
    NavHost(navController = navController, startDestination = Screens.Professor.route){
        composable(Screens.Professor.route){
            Professor(navController,viewModel)
        }
        composable(Screens.AddProfessor.route){
            AddProfessor(navController, viewModel)
        }
        composable(Screens.SingleProfessor.route){ navBackStackEntry ->
            SingleProfessor(navController, viewModel, navBackStackEntry.arguments?.getString("profId"))
        }
        composable(Screens.AddComment.route){ navBackStackEntry ->
            AddComment(navController, viewModel, navBackStackEntry.arguments?.getString("profId"))
        }
        composable(Screens.AllComments.route){ navBackStackEntry ->
            AllComments(navController, viewModel, navBackStackEntry.arguments?.getString("profId"))
        }
        composable(Screens.AllSimilarProfessor.route){
            AllSimilarProfesor(navController, viewModel)
        }
        composable(Screens.Syllabuses.route){
            Syllabus(navController, viewModel)
        }
    }
}

