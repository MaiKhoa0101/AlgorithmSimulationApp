package com.example.algorithmssimulationapp

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.algorithmssimulationapp.search.BFSInteractiveScreen
import com.example.algorithmssimulationapp.search.dfsStart
import com.example.algorithmssimulationapp.sort.QuickSortStepScreen
import com.example.algorithmssimulationapp.sort.showMergeSort
import com.example.algorithmssimulationapp.sort.showQuickSort

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val list = mutableListOf(0)
            val graph = mapOf(
                1 to listOf(2, 3),
                2 to listOf(4),
                3 to listOf(5),
                4 to listOf(),
                5 to listOf()
            )
            val navHostController = rememberNavController()
            NavHost(navHostController, startDestination = "home") {
                composable("home") {
                    UIChoose(navHostController)
                }
                composable("sort") {
                    addListFun(list, navHostController)
                }
                composable("search") {
                    UIChoose(navHostController)
                }
                composable("dfs") {
                    dfsStart(navHostController)
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
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
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
                    navHostController.navigate("bfs")
                }
            ) {
                Text("BFS")
            }
            Button(
                onClick = {
                    navHostController.navigate("dfs")
                }
            ) {
                Text("DFS")
            }
        }
    }

    @Composable
    fun addListFun(list: MutableList<Int>, navController: NavHostController) {
        var number by remember { mutableStateOf("") }
        var onSubmitted by remember { mutableStateOf(false) }
        var selection by remember { mutableStateOf("") }

        val modifier = Modifier
            .padding(10.dp)
            .width(150.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, vertical = 50.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("Mảng hiện tại: $list")
            Text("Nhập số cần thêm vào mảng: ")
            OutlinedTextField(
                value = number,
                onValueChange = {
                    number = it
                }
            )
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            if (number.isNotEmpty()) {
                                for (i in number){
                                    if (!i.isDigit() && (i != '-' || number.length <=1)|| number.count( { it == '-' })>1) {
                                        println("Invalid input")
                                        return@Button
                                    }

                                }
                                list.add(number.toInt())
                                number = ""

                            } else {
                                println("Invalid input")
                            }
                        }
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = {
                            list.clear()
                            onSubmitted = false
                            selection = ""
                        }
                    ) {
                        Text("Clear")
                    }
                }
                Row {
                    Column {
                        //Submit button
                        Button(
                            modifier = modifier,
                            onClick = {
                                onSubmitted = true
                                selection = "naturalmergesort"
                                println("Mảng ban đầu: $list")
                            }
                        ) {
                            Text("Natural Merge Sort")
                        }
                        Button(
                            modifier = modifier,
                            onClick = {
                                onSubmitted = true
                                selection = "merge"

                                println("Mảng ban đầu: $list")
                            }
                        ) {
                            Text("Merge Sort")
                        }
                    }
                    Column {
                        Button(
                            modifier = modifier,
                            onClick = {
                                navController.navigate("quickSort")
                            }
                        ) {
                            Text("Quick Sort normal")
                        }
                        Button(
                            modifier = modifier,
                            onClick = {
                                onSubmitted = true
                                selection = "betterquicksort"
                            }
                        ) {
                            Text("Quick Sort better")
                        }
                    }
                }

                if (list.size > 1 && onSubmitted) {
                    if (selection == "normalmergesort") {
                        showMergeSort(list, "normalmergesort")
                    }
                    if (selection == "naturalmergesort") {
                        showMergeSort(list, "naturalmergesort")
                    }
                }
                if (list.size > 1 && onSubmitted ) {
                    if (selection == "betterquicksort") {
                        showQuickSort(list, "betterquicksort")
                    }
                }
            }
        }
    }

    @Composable
    fun showList(list: MutableList<Int>) {
        Text("Mảng sau khi sort: $list")
        println("Mảng sau khi sort: $list")
    }
