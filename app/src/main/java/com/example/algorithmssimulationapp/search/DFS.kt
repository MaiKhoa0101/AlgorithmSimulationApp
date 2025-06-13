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


fun dfs(graph: List<List<Int>>, start: Int, visited: BooleanArray) {
    visited[start] = true
    println("Đang thăm đỉnh: $start")

    for (neighbor in graph[start]) {
        if (!visited[neighbor]) {
            dfs(graph, neighbor, visited)
        }
    }
}
