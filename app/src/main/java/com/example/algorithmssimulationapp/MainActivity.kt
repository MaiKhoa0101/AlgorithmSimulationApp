package com.example.algorithmssimulationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.algorithmssimulationapp.sort.showMergeSort
import com.example.algorithmssimulationapp.sort.showQuickSort

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var list = mutableListOf(0)
            var list2 by remember { mutableStateOf(list) }
            list = list2
            list2 = list
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
                    addListFun(list)
                }
                composable("search") {
                    UIChoose(navHostController)
                }
                composable("bfs") {
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
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Sort", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Button(
                onClick = {
                    navHostController.navigate("sort")
                }
            ) {
                Text("Merge Sort")
            }
            Button(
                onClick = {
                    navHostController.navigate("sort")
                }
            ) {
                Text("Quick Sort")
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
                }
            ) {
                Text("DFS")
            }
        }
    }

    @Composable
    fun addListFun(list: MutableList<Int>) {
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
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround
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
                                onSubmitted = true
                                println("Mảng ban đầu: $list")
                            }
                        ) {
                            Text("Quick Sort normal")
                        }
                        Button(
                            modifier = modifier ,
                            onClick = {
                                onSubmitted = true
                                println("Mảng ban đầu: $list")
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
                if (list.size > 1 && onSubmitted) {
                    showQuickSort(list, "betterquicksort")
                }
            }
        }
    }

    @Composable
    fun showList(list: MutableList<Int>) {
        Text("Mảng sau khi sort: $list")
        println("Mảng sau khi sort: $list")
    }
