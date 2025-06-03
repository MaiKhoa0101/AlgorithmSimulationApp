package com.example.algorithmssimulationapp.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
}

@Composable
fun BFSResultScreen(navController: NavHostController) {
    val bfsAlgorithm = remember {
        BFSAlgorithm().apply {
            // Thêm các cạnh theo dữ liệu từ code C++
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            // Input Graph
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Input Graph (Adjacency List)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(
                            bfsAlgorithm.getAdjacencyList().toList()
                                .sortedBy { it.first }) { (node, neighbors) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.onPrimary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = node.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = " → ",
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "[${neighbors.joinToString(", ")}]",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // BFS Result
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Result",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        bfsResult.chunked(5).forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                chunk.forEach { node ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.onPrimary,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = node.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Kết quả
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
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
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}