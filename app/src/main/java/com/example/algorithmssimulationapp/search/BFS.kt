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

val graph = mapOf(
    1 to listOf(2, 3),
    2 to listOf(4),
    3 to listOf(5),
    4 to listOf(),
    5 to listOf()
)
val edges = listOf(
    1 to 2,
    1 to 3,
    2 to 4,
    3 to 5,
    1 to 6
)


