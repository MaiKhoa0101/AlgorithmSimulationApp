package com.example.algorithmssimulationapp.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Các bước của thuật toán
sealed class SortStep {
    data class SetPivot(val pivotIndex: Int, val pivotValue: Int, val left: Int, val right: Int) : SortStep()
    data class InitPointers(val leftPointer: Int, val rightPointer: Int) : SortStep()
    data class MoveLeftPointer(val from: Int, val to: Int, val value: Int, val pivotValue: Int) : SortStep()
    data class MoveRightPointer(val from: Int, val to: Int, val value: Int, val pivotValue: Int) : SortStep()
    data class CheckSwapCondition(val leftIndex: Int, val rightIndex: Int, val leftValue: Int, val rightValue: Int, val willSwap: Boolean) : SortStep()
    data class Swap(val leftIndex: Int, val rightIndex: Int, val leftValue: Int, val rightValue: Int) : SortStep()
    data class UpdatePointers(val newLeft: Int, val newRight: Int) : SortStep()
    data class PartitionComplete(val finalLeft: Int, val finalRight: Int, val left: Int, val right: Int) : SortStep()
    data class RecursiveCall(val left: Int, val right: Int, val side: String) : SortStep()
    object Done : SortStep()
}

// Trạng thái hiển thị
data class VisualizationState(
    val array: MutableList<Int>,
    val pivotIndex: Int = -1,
    val pivotValue: Int = -1,
    val leftPointer: Int = -1,
    val rightPointer: Int = -1,
    val swapIndices: Pair<Int, Int>? = null,
    val completedIndices: Set<Int> = emptySet(),
    val currentRange: Pair<Int, Int>? = null,
    val message: String = ""
)

// thuật toán QuickSort Hoare
fun generateQuickSortSteps(array: List<Int>): List<Pair<SortStep, List<Int>>> {
    val steps = mutableListOf<Pair<SortStep, List<Int>>>()
    val workingArray = array.toMutableList()

    fun quickSort(left: Int, right: Int) {
        if (left < right) {
            // Chọn pivot ở giữa
            val pivotIndex = (left + right) / 2
            val pivotValue = workingArray[pivotIndex]
            steps.add(SortStep.SetPivot(pivotIndex, pivotValue, left, right) to workingArray.toList())

            // Khởi tạo con trỏ
            var i = left
            var j = right
            steps.add(SortStep.InitPointers(i, j) to workingArray.toList())

            do {
                val startI = i
                while (workingArray[i] < pivotValue) {
                    i++
                }
                if (startI != i) {
                    steps.add(SortStep.MoveLeftPointer(startI, i, workingArray[i], pivotValue) to workingArray.toList())
                }

                val startJ = j
                while (workingArray[j] > pivotValue) {
                    j--
                }
                if (startJ != j) {
                    steps.add(SortStep.MoveRightPointer(startJ, j, workingArray[j], pivotValue) to workingArray.toList())
                }

                // Kiểm tra điều kiện hoán đổi
                val willSwap = i <= j
                steps.add(SortStep.CheckSwapCondition(i, j, workingArray[i], workingArray[j], willSwap) to workingArray.toList())

                if (willSwap) {
                    // Hoán đổi
                    if (i != j) {
                        steps.add(SortStep.Swap(i, j, workingArray[i], workingArray[j]) to workingArray.toList())
                        workingArray.swap(i, j)
                    }

                    // Cập nhật con trỏ
                    i++
                    j--
                    steps.add(SortStep.UpdatePointers(i, j) to workingArray.toList())
                }

            } while (i <= j)

            steps.add(SortStep.PartitionComplete(i, j, left, right) to workingArray.toList())

            // Đệ quy bên trái
            if (left < j) {
                steps.add(SortStep.RecursiveCall(left, j, "trái") to workingArray.toList())
                quickSort(left, j)
            }

            // Đệ quy bên phải
            if (i < right) {
                steps.add(SortStep.RecursiveCall(i, right, "phải") to workingArray.toList())
                quickSort(i, right)
            }
        }
    }

    quickSort(0, array.size - 1)
    steps.add(SortStep.Done to workingArray.toList())
    return steps
}

// Hàm hoán đổi
fun MutableList<Int>.swap(i: Int, j: Int) {
    if (i != j && i in 0 until size && j in 0 until size) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }
}

// Màn hình chính
@Composable
fun QuickSortStepScreen(navController: NavHostController) {
    val originalArray = listOf(10, 4, 8, 13, 9, 22, 2, 1)
    val steps = remember { generateQuickSortSteps(originalArray) }
    var stepIndex by remember { mutableStateOf(0) }

    // Trạng thái hiển thị
    var visualState by remember {
        mutableStateOf(
            VisualizationState(
                array = originalArray.toMutableList(),
                message = "Nhấn 'Bước tiếp theo' để bắt đầu QuickSort với Hoare Partition"
            )
        )
    }

    // Hàm reset
    val resetSimulation = {
        stepIndex = 0
        visualState = VisualizationState(
            array = originalArray.toMutableList(),
            message = "Đã reset. Nhấn 'Bước tiếp theo' để bắt đầu lại"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("QuickSort - Hoare Partition", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Thông tin bước hiện tại
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Text(
                text = visualState.message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // Hiển thị pivot và con trỏ
        if (visualState.pivotValue != -1) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Pivot: ${visualState.pivotValue} (vị trí ${visualState.pivotIndex})")
                    if (visualState.leftPointer >= 0 && visualState.rightPointer >= 0) {
                        Text("Con trỏ trái (i): ${visualState.leftPointer}, Con trỏ phải (j): ${visualState.rightPointer}")
                    }
                }
            }
        }

        // vẽ
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            visualState.array.forEachIndexed { index, value ->
                val color = when {
                    index in visualState.completedIndices -> Color(0xFF4CAF50) // Xanh lá - đã hoàn thành
                    index == visualState.pivotIndex -> Color(0xFFFF9800) // Cam - pivot
                    index == visualState.leftPointer -> Color(0xFFE91E63) // Hồng - con trỏ trái
                    index == visualState.rightPointer -> Color(0xFF9C27B0) // Tím - con trỏ phải
                    visualState.swapIndices?.let { index == it.first || index == it.second } == true -> Color(0xFFFFEB3B) // Vàng - đang swap
                    visualState.currentRange?.let { index in it.first..it.second } == true -> Color(0xFF2196F3) // Xanh dương - vùng đang xử lý
                    else -> Color(0xFFE0E0E0) // Xám - chưa xử lý
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .height(maxOf(20, value * 3).dp)
                            .width(28.dp)
                            .background(color)
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Chú thích
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFFF9800))
                            .border(0.5.dp, Color.Black)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pivot", style = MaterialTheme.typography.labelSmall)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFE91E63))
                            .border(0.5.dp, Color.Black)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Con trỏ i", style = MaterialTheme.typography.labelSmall)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF9C27B0))
                            .border(0.5.dp, Color.Black)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Con trỏ j", style = MaterialTheme.typography.labelSmall)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFFFEB3B))
                            .border(0.5.dp, Color.Black)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hoán đổi", style = MaterialTheme.typography.labelSmall)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF4CAF50))
                            .border(0.5.dp, Color.Black)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hoàn thành", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút điều khiển
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (stepIndex < steps.size) {
                        val (step, newArray) = steps[stepIndex]
                        visualState.array.clear()
                        visualState.array.addAll(newArray)

                        visualState = when (step) {
                            is SortStep.SetPivot -> {
                                visualState.copy(
                                    pivotIndex = step.pivotIndex,
                                    pivotValue = step.pivotValue,
                                    leftPointer = -1,
                                    rightPointer = -1,
                                    swapIndices = null,
                                    currentRange = step.left to step.right,
                                    message = "Chọn pivot = ${step.pivotValue} tại vị trí giữa ${step.pivotIndex} cho vùng [${step.left}, ${step.right}]"
                                )
                            }
                            is SortStep.InitPointers -> {
                                visualState.copy(
                                    leftPointer = step.leftPointer,
                                    rightPointer = step.rightPointer,
                                    message = "Khởi tạo con trỏ: i = ${step.leftPointer}, j = ${step.rightPointer}"
                                )
                            }
                            is SortStep.MoveLeftPointer -> {
                                visualState.copy(
                                    leftPointer = step.to,
                                    message = "Di chuyển con trỏ i từ ${step.from} đến ${step.to}. Tìm phần tử >= pivot (${step.pivotValue}): ${step.value}"
                                )
                            }
                            is SortStep.MoveRightPointer -> {
                                visualState.copy(
                                    rightPointer = step.to,
                                    message = "Di chuyển con trỏ j từ ${step.from} đến ${step.to}. Tìm phần tử <= pivot (${step.pivotValue}): ${step.value}"
                                )
                            }
                            is SortStep.CheckSwapCondition -> {
                                visualState.copy(
                                    message = "Kiểm tra i <= j: ${step.leftIndex} <= ${step.rightIndex} = ${step.willSwap}. " +
                                            if (step.willSwap) "Sẽ hoán đổi ${step.leftValue} và ${step.rightValue}" else "Dừng partition"
                                )
                            }
                            is SortStep.Swap -> {
                                visualState.copy(
                                    swapIndices = step.leftIndex to step.rightIndex,
                                    message = "Hoán đổi a[${step.leftIndex}] = ${step.leftValue} với a[${step.rightIndex}] = ${step.rightValue}"
                                )
                            }
                            is SortStep.UpdatePointers -> {
                                visualState.copy(
                                    leftPointer = step.newLeft,
                                    rightPointer = step.newRight,
                                    swapIndices = null,
                                    message = "Cập nhật con trỏ: i++ = ${step.newLeft}, j-- = ${step.newRight}"
                                )
                            }
                            is SortStep.PartitionComplete -> {
                                visualState.copy(
                                    leftPointer = -1,
                                    rightPointer = -1,
                                    swapIndices = null,
                                    message = "Partition hoàn thành! i = ${step.finalLeft}, j = ${step.finalRight}"
                                )
                            }
                            is SortStep.RecursiveCall -> {
                                visualState.copy(
                                    currentRange = step.left to step.right,
                                    message = "Gọi đệ quy cho mảng con bên ${step.side}: [${step.left}, ${step.right}]"
                                )
                            }
                            is SortStep.Done -> {
                                visualState.copy(
                                    pivotIndex = -1,
                                    pivotValue = -1,
                                    leftPointer = -1,
                                    rightPointer = -1,
                                    swapIndices = null,
                                    completedIndices = visualState.array.indices.toSet(),
                                    currentRange = null,
                                    message = "QuickSort hoàn thành! Mảng đã được sắp xếp."
                                )
                            }
                        }
                        stepIndex++
                    }
                },
                enabled = stepIndex < steps.size
            ) {
                Text("Bước tiếp theo (${stepIndex + 1}/${steps.size})")
            }

            Button(
                onClick = resetSimulation,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
            ) {
                Text("Reset")
            }
        }

        if (stepIndex >= steps.size) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Text(
                    text = "🎉 Mô phỏng hoàn thành!\nMảng ban đầu: ${originalArray.joinToString(", ")}\nMảng sau khi sắp xếp: ${visualState.array.joinToString(", ")}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
