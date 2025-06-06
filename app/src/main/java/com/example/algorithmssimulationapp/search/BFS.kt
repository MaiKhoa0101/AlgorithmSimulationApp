package com.example.algorithmssimulationapp.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.util.*

class BFSAlgorithm {
    private val adj = mutableMapOf<Int, MutableList<Int>>()

    fun addEdge(u: Int, v: Int) {
        adj.getOrPut(u) { mutableListOf() }.add(v)
        adj.getOrPut(v) { mutableListOf() }.add(u)
    }

    fun bfs(start: Int): List<Int> {
        val visited = mutableSetOf<Int>()
        val queue: Queue<Int> = LinkedList()
        val result = mutableListOf<Int>()

        queue.offer(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            result.add(current)

            adj[current]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    queue.offer(neighbor)
                    visited.add(neighbor)
                }
            }
        }

        return result
    }

    fun getAdjacencyList(): Map<Int, List<Int>> = adj.toMap()

    fun getEdges(): List<Pair<Int, Int>> {
        val edges = mutableListOf<Pair<Int, Int>>()
        adj.forEach { (node, neighbors) ->
            neighbors.forEach { neighbor ->
                if (node < neighbor) { // Avoid duplicate edges (undirected graph)
                    edges.add(node to neighbor)
                }
            }
        }
        return edges.sortedBy { it.first }
    }
}

@Composable
fun BFSResultScreen(navController: NavHostController) {
    val bfsAlgorithm = remember {
        BFSAlgorithm().apply {
            // Thêm các cạnh theo dữ liệu từ code 1
            addEdge(1, 2)
            addEdge(1, 3)
            addEdge(1, 5)
            addEdge(1, 10)
            addEdge(2, 4)
            addEdge(3, 6)
            addEdge(3, 7)
            addEdge(3, 9)
            addEdge(6, 7)
            addEdge(5, 8)
            addEdge(8, 9)
        }
    }

    val bfsResult = remember { bfsAlgorithm.bfs(1) }

    // Vị trí mới – đã canh gần giống layout của hình 2
    val nodePositions = listOf(
        Offset(100f,  60f),   // 1 – trên cùng hơi lệch trái
        Offset( 60f, 180f),   // 2
        Offset(260f, 190f),   // 3
        Offset( 20f, 300f),   // 4 – trái dưới
        Offset(300f,  90f),   // 5 – giữa trên (nằm giữa 1 & 8)
        Offset(220f, 340f),   // 6
        Offset(320f, 340f),   // 7
        Offset(380f, 190f),   // 8
        Offset(460f, 320f),   // 9 – phải dưới
        Offset(520f,  60f)    // 10 – trên cùng bên phải, nối ngang với 1
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BFS Algorithm Result",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Breadth-First Search từ node 1",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            // Input Graph Visualization
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Input Graph (Visualization)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    GraphVisualizer(
                        edges = bfsAlgorithm.getEdges(),
                        nodePositions = nodePositions,
                        nodeCount = 10
                    )
                }
            }
        }

        item {
            // Kết quả
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Thứ tự duyệt:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = bfsResult.joinToString(" → "),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GraphVisualizer(edges: List<Pair<Int, Int>>, nodePositions: List<Offset>, nodeCount: Int) {
    val radius = 40f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(16.dp)
    ) {
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
        nodePositions.take(nodeCount).forEachIndexed { index, position ->
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