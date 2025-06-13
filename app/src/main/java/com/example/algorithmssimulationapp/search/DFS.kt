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
import com.example.algorithmssimulationapp.DFSStep
import com.example.algorithmssimulationapp.DFSStepType
import com.example.algorithmssimulationapp.Node
import kotlin.collections.find
import kotlin.collections.get

// Hàm DFS được cải tiến để ghi lại các bước
fun dfsWithSteps(
    graph: MutableMap<Node, MutableList<Int>>,
    startNodeId: Int
): List<DFSStep> {
    val steps = mutableListOf<DFSStep>()
    val visited = mutableSetOf<Int>()
    val stack = mutableListOf<Int>()
    val visitOrder = mutableListOf<Int>()

    // Bước khởi tạo
    stack.add(startNodeId)
    steps.add(
        DFSStep(
            currentNode = startNodeId,
            stepType = DFSStepType.START,
            visitedNodes = visitOrder.toList(),
            stack = stack.toList(),
            message = "Bắt đầu DFS từ đỉnh $startNodeId"
        )
    )

    while (stack.isNotEmpty()) {
        val current = stack.removeAt(stack.size - 1) // Pop từ stack

        if (current !in visited) {
            // Thăm đỉnh hiện tại
            visited.add(current)
            visitOrder.add(current)

            steps.add(
                DFSStep(
                    currentNode = current,
                    stepType = DFSStepType.VISIT,
                    visitedNodes = visitOrder.toList(),
                    stack = stack.toList(),
                    message = "Thăm đỉnh $current"
                )
            )

            // Tìm tất cả neighbors chưa thăm
            val currentNode = graph.keys.find { it.name == current.toString() }
            val neighbors = graph[currentNode]?.filter { it !in visited } ?: emptyList()

            // Thêm neighbors vào stack (thêm theo thứ tự ngược để DFS đúng)
            neighbors.sortedDescending().forEach { neighbor ->
                if (neighbor !in visited && neighbor !in stack) {
                    stack.add(neighbor)
                    steps.add(
                        DFSStep(
                            currentNode = current,
                            stepType = DFSStepType.EXPLORE,
                            visitedNodes = visitOrder.toList(),
                            stack = stack.toList(),
                            message = "Khám phá đỉnh $neighbor từ đỉnh $current",
                            exploringFrom = current,
                            exploringTo = neighbor
                        )
                    )
                }
            }

            // Nếu không có neighbors mới, đây là backtrack
            if (neighbors.isEmpty() && stack.isNotEmpty()) {
                steps.add(
                    DFSStep(
                        currentNode = current,
                        stepType = DFSStepType.BACKTRACK,
                        visitedNodes = visitOrder.toList(),
                        stack = stack.toList(),
                        message = "Quay lại từ đỉnh $current (không còn đỉnh nào để thăm)"
                    )
                )
            }
        }
    }

    // Bước hoàn thành
    steps.add(
        DFSStep(
            currentNode = -1,
            stepType = DFSStepType.COMPLETE,
            visitedNodes = visitOrder.toList(),
            stack = emptyList(),
            message = "Hoàn thành DFS. Thứ tự thăm: ${visitOrder.joinToString(" → ")}"
        )
    )

    return steps
}