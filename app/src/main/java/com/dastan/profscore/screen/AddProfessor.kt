package com.dastan.profscore.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dastan.profscore.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfessor(navController: NavController, viewModel: ViewModel) {
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    val isFormFilled = name.isNotEmpty() && department.isNotEmpty()


    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Text("Add a Professor", fontWeight = FontWeight.Bold, fontSize = 32.sp)
        Text("Please use the search bar above to make sure that the professor does not already exist at this school.", modifier = Modifier.padding(top=16.dp, bottom = 16.dp))
        Text("Professor's Name", modifier = Modifier.padding(top=16.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                // Set the underline color to transparent
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                textColor = colorScheme.onPrimary,
                cursorColor = colorScheme.onPrimary
            ),
            modifier = Modifier.padding(top=16.dp, bottom = 16.dp).border(2.dp, Color.Black, RoundedCornerShape(12.dp))
        )
        Text("Field of Study")
        TextField(
            value = department,
            onValueChange = { department = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                // Set the underline color to transparent
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                textColor = colorScheme.onPrimary,
                cursorColor = colorScheme.onPrimary
            ),
            modifier = Modifier.padding(top=16.dp, bottom = 16.dp).border(2.dp, Color.Black, RoundedCornerShape(12.dp))
        )

        Button(
            onClick = {
                viewModel.addProf(profName = name, department = department)
                name=""
                department=""
                //navController.navigateUp()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormFilled) colorScheme.scrim else colorScheme.onSurface
            ),
            enabled = isFormFilled
        ) {
            Text("Add Professor", color = colorScheme.onPrimary)
        }
    }
}