package com.example.algorithmssimulationapp.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import java.util.LinkedList
import java.util.Queue

class BFSAlgorithm(private val isDirected: Boolean = false) {
    private val adj = mutableMapOf<Int, MutableList<Int>>()

    fun addEdge(u: Int, v: Int) {
        adj.getOrPut(u) { mutableListOf() }.add(v)
        if (!isDirected) {
            adj.getOrPut(v) { mutableListOf() }.add(u)
        }
    }

    fun bfs(start: Int): Pair<List<Int>, List<List<Int>>> {
        val visited = mutableSetOf<Int>()
        val queue: Queue<Int> = LinkedList()
        val result = mutableListOf<Int>()
        val queueStates = mutableListOf<List<Int>>()

        queue.offer(start)
        visited.add(start)
        queueStates.add(queue.toList())

        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            result.add(cur)

            adj[cur]?.forEach { nxt ->
                if (nxt !in visited) {
                    visited.add(nxt)
                    queue.offer(nxt)
                }
            }
            queueStates.add(queue.toList())
        }
        return result to queueStates
    }
}

@Composable
fun BFSInteractiveScreen(navController: NavHostController) {
    var edges = remember { mutableStateListOf<Pair<Int, Int>>() }
    var uInput by remember { mutableStateOf("") }
    var vInput by remember { mutableStateOf("") }
    var startInput by remember { mutableStateOf("1") }
    var bfsResult by remember { mutableStateOf<List<Int>>(emptyList()) }
    var queueStates by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var currentStepIdx by remember { mutableStateOf(-1) }
    var isDirected by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back")
                }
                Text(
                    "BFS Interactive Demo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Directed Graph?")
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = isDirected,
                    onCheckedChange = { isDirected = it }
                )
                Spacer(Modifier.width(16.dp))
                Text(if (isDirected) "(Directed)" else "(Undirected)")
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uInput,
                            onValueChange = { uInput = it.filter(Char::isDigit) },
                            label = { Text("u") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = vInput,
                            onValueChange = { vInput = it.filter(Char::isDigit) },
                            label = { Text("v") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val u = uInput.toIntOrNull()
                            val v = vInput.toIntOrNull()
                            if (u != null && v != null && u != v) {
                                if (u to v !in edges && v to u !in edges) {
                                    edges.add(u to v)
                                }
                                uInput = ""
                                vInput = ""
                            }
                        }) { Text("Add") }
                    }

                    if (edges.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Edges: " + edges.joinToString { "(${it.first},${it.second})" },
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = {
                            edges.clear()
                            bfsResult = emptyList()
                            queueStates = emptyList()
                            currentStepIdx = -1
                        }) { Text("Clear edges") }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = startInput,
                            onValueChange = { startInput = it.filter(Char::isDigit) },
                            label = { Text("Start node") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val start = startInput.toIntOrNull() ?: return@Button
                            val algo = BFSAlgorithm(isDirected = isDirected).apply {
                                edges.forEach { (u, v) -> addEdge(u, v) }
                            }
                            val (result, states) = algo.bfs(start)
                            bfsResult = result
                            queueStates = states
                            currentStepIdx = -1
                        }) { Text("Run BFS") }
                    }
                }
            }
        }

        if (bfsResult.isNotEmpty()) {
            item {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Traversal order:", fontWeight = FontWeight.Medium)
                        Text(
                            if (currentStepIdx >= 0) {
                                bfsResult.take(currentStepIdx + 1).joinToString(" → ")
                            } else {
                                "None"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Queue at step ${currentStepIdx + 1}:", fontWeight = FontWeight.Medium)
                        Text(
                            queueStates.getOrNull(currentStepIdx + 1)?.joinToString(", ") ?: "Empty",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Nút Previous
                    Button(
                        onClick = {
                            if (currentStepIdx > -1) {
                                currentStepIdx -= 1
                            }
                        },
                        enabled = currentStepIdx > -1
                    ) {
                        Text("Previous")
                    }

                    // Nút Next
                    Button(
                        onClick = {
                            if (currentStepIdx < bfsResult.lastIndex) {
                                currentStepIdx += 1
                            }
                        },
                        enabled = currentStepIdx < bfsResult.lastIndex
                    ) {
                        Text("Next")
                    }

                    // Nút Reset
                    Button(
                        onClick = {
                            currentStepIdx = -1
                        }
                    ) {
                        Text("Reset")
                    }
                }
            }
        }

        if (edges.isNotEmpty()) {
            item {
                Spacer(Modifier.height(12.dp))
                val maxNode = edges.flatMap { listOf(it.first, it.second) }.maxOrNull() ?: 0
                val nodePositions by remember(maxNode) {
                    mutableStateOf(
                        (1..maxNode).mapIndexed { idx, _ ->
                            val r = 200f
                            val cx = 300f
                            val cy = 300f
                            val angle = 2 * PI * idx / maxNode
                            Offset(
                                cx + (r * cos(angle)).toFloat(),
                                cy + (r * sin(angle)).toFloat()
                            )
                        }
                    )
                }

                GraphVisualizer(
                    edges = edges.sortedBy { it.first },
                    nodePositions = nodePositions,
                    nodeCount = nodePositions.size,
                    visited = bfsResult.take(currentStepIdx.coerceAtLeast(0)).toSet(),
                    current = bfsResult.getOrNull(currentStepIdx),
                    isDirected = isDirected
                )
            }
        }
    }
}

@Composable
fun GraphVisualizer(
    edges: List<Pair<Int, Int>>,
    nodePositions: List<Offset>,
    nodeCount: Int,
    visited: Set<Int>,
    current: Int?,
    isDirected: Boolean
) {
    val radius = 40f
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(16.dp)
    ) {
        // Vẽ cạnh
        edges.forEach { (from, to) ->
            drawLine(
                color = Color.Gray,
                start = nodePositions[from - 1],
                end = nodePositions[to - 1],
                strokeWidth = 4f
            )
        }

        // Vẽ node
        nodePositions.take(nodeCount).forEachIndexed { idx, pos ->
            val nodeId = idx + 1
            val fillColor = when {
                nodeId == current -> Color.Red
                nodeId in visited -> Color(0xFF4CAF50)
                else -> Color.Cyan
            }

            drawCircle(color = fillColor, center = pos, radius = radius)

            drawContext.canvas.nativeCanvas.drawText(
                nodeId.toString(),
                pos.x,
                pos.y + 12f,
                android.graphics.Paint().apply {
                    textSize = 40f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

