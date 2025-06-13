package com.example.algorithmssimulationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.algorithmssimulationapp.search.BFSInteractiveScreen
import com.example.algorithmssimulationapp.sort.QuickSortStepScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavHost(navHostController, startDestination = "home") {
                composable("home") {
                    UIChoose(navHostController)
                }
                composable("sort") {
                    SortScreen(navHostController)
                }
                composable("search") {
                    SearchScreen(navHostController)
                }
                composable("bfs") {
                    BFSInteractiveScreen(navHostController)
                }
                composable("quickSort") {
                    QuickSortStepScreen(navHostController)
                }
            }
        }
    }
}
    @Composable
    fun UIChoose(navHostController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Sort", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Button(
                onClick = {
                    navHostController.navigate("sort")
                }
            ) {
                Text("Sort Algorithm")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Search", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Button(
                onClick = {
                    navHostController.navigate("search")
                }
            ) {
                Text("Search DFS and BFS")
            }

        }
    }




