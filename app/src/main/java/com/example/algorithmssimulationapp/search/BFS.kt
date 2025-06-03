package com.example.algorithmssimulationapp.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Dữ liệu toàn cục của đồ thị
val nodePositions = listOf(
    Offset(200f, 200f),  // Node 1
    Offset(100f, 400f),  // Node 2
    Offset(300f, 400f),  // Node 3
    Offset(50f, 600f),   // Node 4
    Offset(350f, 600f),  // Node 5
    Offset(500f, 400f),  // Node 6
)

val edges = listOf(
    1 to 2,
    1 to 3,
    2 to 4,
    3 to 5,
    1 to 6
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bfsStart(navHostController: NavHostController) {
    val result = remember { mutableStateOf("") }

    // Tạo ma trận từ danh sách cạnh
    val graphMatrix = createGraphMatrixFromEdges(edges, 6)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navHostController.popBackStack() }) {
                Text("Back")
            }
            Text("BFS", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Biểu diễn đồ thị:", fontSize = 20.sp)
            GraphVisualizer()

            Spacer(modifier = Modifier.height(16.dp))

            Text("Kết quả BFS:", fontSize = 20.sp)

            LaunchedEffect(Unit) {
                val visited = BooleanArray(7) // 1..6
                val output = bfsMatrix(start = 1, graph = graphMatrix, visited = visited)
                result.value = output.joinToString(" → ")
            }

            Text(text = result.value, fontSize = 18.sp, color = Color.Blue)
        }
    }
}

fun createGraphMatrixFromEdges(edges: List<Pair<Int, Int>>, size: Int): Array<IntArray> {
    val graph = Array(size + 1) { IntArray(size + 1) } // 1-based index
    for ((u, v) in edges) {
        graph[u][v] = 1
        graph[v][u] = 1 // undirected
    }
    return graph
}


fun bfsMatrix(start: Int, graph: Array<IntArray>, visited: BooleanArray): List<Int> {
    val result = mutableListOf<Int>()
    val queue = ArrayDeque<Int>()
    queue.add(start)
    visited[start] = true

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        result.add(current)

        for (i in graph.indices) {
            if (graph[current][i] == 1 && !visited[i]) {
                queue.add(i)
                visited[i] = true
            }
            println(result)
        }
    }
    return result
}


@Composable
fun GraphVisualizer() {
    val radius = 40f

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Vẽ cạnh
        for ((from, to) in edges) {
            val start = nodePositions[from - 1]
            val end = nodePositions[to - 1]
            drawLine(
                color = Color.Black,
                start = start,
                end = end,
                strokeWidth = 4f
            )
        }

        // Vẽ node
        nodePositions.forEachIndexed { index, position ->
            drawCircle(
                color = Color.Cyan,
                center = position,
                radius = radius
            )
            drawContext.canvas.nativeCanvas.drawText(
                (index + 1).toString(),
                position.x,
                position.y + 10f,
                android.graphics.Paint().apply {
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
