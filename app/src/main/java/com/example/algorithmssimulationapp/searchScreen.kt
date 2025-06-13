package com.example.algorithmssimulationapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.random.Random


data class Node(
    val name: String = "0",
    val value: Int = name.toInt(),
    val neighbors: MutableList<Node> = emptyList<Node>().toMutableList()
)

data class Edge(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)


var ExampleListNode = mutableListOf(
    Node(
        "1",
        1,
        mutableListOf(
            Node(
                "2",
                2,
                mutableListOf(
                    Node(
                        "3",
                        3
                    )
                )
            )
        )
    )
)

@Composable
fun SearchScreen(navHostController: NavHostController){
    Column {
        ShowGraph(ExampleListNode)
    }
}

@Composable
fun ShowGraph(ListNode: MutableList<Node> = mutableListOf() ){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ListNode.forEach {
            Text(it.name)
        }
    }
}

@Composable
fun addGraphFun(navController: NavHostController) {
    var node by remember { mutableStateOf<Node>(null)}
    var onSubmitted by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf("") }
    val listNode = remember { mutableStateListOf<Node>() }
    var reset by remember { mutableStateOf(false) }

    LaunchedEffect(reset) {
        listNode.clear()
        repeat(Random.nextInt(3, 9)) {
            listNode.add(
                Node(
                    Random.nextInt(10, 100),
                    Random.nextInt(10, 100),
                    Random.nextInt(10,100)

                )
            )
        }
    }

    val modifier = Modifier.padding(10.dp).width(150.dp)

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Hiển thị mảng hiện tại
            Text("Đồ thị đã cho: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            // Nhập số và nút xử lý input
            if (!onSubmitted) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text("Nhập giá trị nút: ")
//                    OutlinedTextField(
//                        value = number,
//                        onValueChange = { number = it }
//                    )
//                    Text("Nhâp số cạnh: ")
//                    OutlinedTextField(
//                        value = number,
//                        onValueChange = { number = it }
//                    )
//
//                    Text("Nhập số cần thêm vào mảng: ")
//                    OutlinedTextField(
//                        value = number,
//                        onValueChange = { number = it }
//                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OnclickButton(modifier, "Add") {
//                            if (number.isNotEmpty() && isValidNumber(number)) {
//                                list.add(number.toInt())
//                                number = ""
//                            } else {
//                                println("Invalid input")
//                            }
                        }

                        OnclickButton(modifier, "Random Array") {
                            reset = !reset
                        }
                    }
                }
            }

            // Nút clear
            OnclickButton(modifier.height(40.dp), "Reset") {
                listNode.clear()
                onSubmitted = false
                selection = ""
            }

            // Nút chọn thuật toán
            if (!onSubmitted) {
                Column {
                    OnclickButton(modifier, "BFS") {
                        onSubmitted = true
                        selection = "bfs"
                    }
                    OnclickButton(modifier, "DFS") {
                        navController.navigate("dfs")
                    }
                }
            }

            // Kết quả sau khi đã submit
            if (listNode.size > 1 && onSubmitted) {
                when (selection) {
                    "bfs"-> ShowGraph()
                    "dfs" -> ShowGraph()
                }
            }
            // Hiển thị danh sách ban đầu
            ShowGraph()
        }
    }
}
