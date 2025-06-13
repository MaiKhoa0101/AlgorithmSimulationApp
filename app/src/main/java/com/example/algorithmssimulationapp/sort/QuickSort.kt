package com.example.algorithmssimulationapp.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
            println("steps $steps")
            println("pivot value $pivotValue")
            println("working arr "+ workingArray.toList())

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

// Hàm parse mảng từ chuỗi
fun parseArrayInput(input: String): List<Int>? {
    return try {
        input.split(",", " ", ";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.toInt() }
            .takeIf { it.isNotEmpty() && it.size <= 15 } // Giới hạn tối đa 15 phần tử
    } catch (e: NumberFormatException) {
        null
    }
}

// Hàm tạo trạng thái từ bước
fun createVisualizationState(step: SortStep, array: List<Int>, baseState: VisualizationState): VisualizationState {
    return when (step) {
        is SortStep.SetPivot -> {
            baseState.copy(
                array = array.toMutableList(),
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
            baseState.copy(
                array = array.toMutableList(),
                leftPointer = step.leftPointer,
                rightPointer = step.rightPointer,
                message = "Khởi tạo con trỏ: i = ${step.leftPointer}, j = ${step.rightPointer}"
            )
        }
        is SortStep.MoveLeftPointer -> {
            baseState.copy(
                array = array.toMutableList(),
                leftPointer = step.to,
                message = "Di chuyển con trỏ i từ ${step.from} đến ${step.to}. Tìm phần tử >= pivot (${step.pivotValue}): ${step.value}"
            )
        }
        is SortStep.MoveRightPointer -> {
            baseState.copy(
                array = array.toMutableList(),
                rightPointer = step.to,
                message = "Di chuyển con trỏ j từ ${step.from} đến ${step.to}. Tìm phần tử <= pivot (${step.pivotValue}): ${step.value}"
            )
        }
        is SortStep.CheckSwapCondition -> {
            baseState.copy(
                array = array.toMutableList(),
                message = "Kiểm tra i <= j: ${step.leftIndex} <= ${step.rightIndex} = ${step.willSwap}. " +
                        if (step.willSwap) "Sẽ hoán đổi ${step.leftValue} và ${step.rightValue}" else "Dừng partition"
            )
        }
        is SortStep.Swap -> {
            baseState.copy(
                array = array.toMutableList(),
                swapIndices = step.leftIndex to step.rightIndex,
                message = "Hoán đổi a[${step.leftIndex}] = ${step.leftValue} với a[${step.rightIndex}] = ${step.rightValue}"
            )
        }
        is SortStep.UpdatePointers -> {
            baseState.copy(
                array = array.toMutableList(),
                leftPointer = step.newLeft,
                rightPointer = step.newRight,
                swapIndices = null,
                message = "Cập nhật con trỏ: i++ = ${step.newLeft}, j-- = ${step.newRight}"
            )
        }
        is SortStep.PartitionComplete -> {
            baseState.copy(
                array = array.toMutableList(),
                leftPointer = -1,
                rightPointer = -1,
                swapIndices = null,
                message = "Partition hoàn thành! i = ${step.finalLeft}, j = ${step.finalRight}"
            )
        }
        is SortStep.RecursiveCall -> {
            baseState.copy(
                array = array.toMutableList(),
                currentRange = step.left to step.right,
                message = "Gọi đệ quy cho mảng con bên ${step.side}: [${step.left}, ${step.right}]"
            )
        }
        is SortStep.Done -> {
            baseState.copy(
                array = array.toMutableList(),
                pivotIndex = -1,
                pivotValue = -1,
                leftPointer = -1,
                rightPointer = -1,
                swapIndices = null,
                completedIndices = array.indices.toSet(),
                currentRange = null,
                message = "QuickSort hoàn thành! Mảng đã được sắp xếp."
            )
        }
    }
}

// Màn hình chính
@Composable
fun QuickSortStepScreen(navController: NavHostController) {
    val defaultArray = listOf(10, 4, 8, 13, 9, 22, 2, 1)

    // States cho input
    var inputText by remember { mutableStateOf(defaultArray.joinToString(", ")) }
    var currentArray by remember { mutableStateOf(defaultArray) }
    var steps by remember { mutableStateOf(generateQuickSortSteps(defaultArray)) }
    var stepIndex by remember { mutableStateOf(0) }
    var isSimulationStarted by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    // Trạng thái hiển thị
    var visualState by remember {
        mutableStateOf(
            VisualizationState(
                array = currentArray.toMutableList(),
                message = "Nhập mảng số và nhấn 'Bắt đầu' để mô phỏng QuickSort"
            )
        )
    }

    // Hàm bắt đầu mô phỏng
    val startSimulation = {
        val parsedArray = parseArrayInput(inputText)
        if (parsedArray != null) {
            currentArray = parsedArray
            steps = generateQuickSortSteps(parsedArray)
            stepIndex = 0
            isSimulationStarted = true
            inputError = null
            visualState = VisualizationState(
                array = parsedArray.toMutableList(),
                message = "Nhấn 'Bước tiếp theo' để bắt đầu QuickSort với Hoare Partition"
            )
        } else {
            inputError = "Vui lòng nhập mảng hợp lệ (tối đa 15 số, cách nhau bởi dấu phẩy hoặc khoảng trắng)"
        }
    }

    // Hàm reset
    val resetSimulation = {
        stepIndex = 0
        isSimulationStarted = false
        visualState = VisualizationState(
            array = currentArray.toMutableList(),
            message = "Đã reset. Thay đổi mảng hoặc nhấn 'Bắt đầu' để mô phỏng lại"
        )
    }

    // Hàm đi tới bước tiếp theo
    val nextStep = {
        if (stepIndex < steps.size) {
            val (step, newArray) = steps[stepIndex]
            visualState = createVisualizationState(step, newArray, visualState)
            stepIndex++
        }
    }

    // Hàm lùi về bước trước
    val previousStep = {
        if (stepIndex > 0) {
            stepIndex--
            if (stepIndex == 0) {
                // Về trạng thái ban đầu
                visualState = VisualizationState(
                    array = currentArray.toMutableList(),
                    message = "Nhấn 'Bước tiếp theo' để bắt đầu QuickSort với Hoare Partition"
                )
            } else {
                // Lấy trạng thái của bước trước
                val (step, newArray) = steps[stepIndex - 1]
                visualState = createVisualizationState(step, newArray, visualState)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("QuickSort", style = MaterialTheme.typography.titleLarge)
            Button(onClick = {navController.popBackStack()}){
                Text("Back")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Phần nhập mảng
        if (!isSimulationStarted) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nhập mảng số:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            inputError = null
                        },
                        label = { Text("Ví dụ: 10, 4, 8, 13, 9") },
                        placeholder = { Text("Nhập các số cách nhau bởi dấu phẩy") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        isError = inputError != null
                    )

                    if (inputError != null) {
                        Text(
                            text = inputError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = startSimulation,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bắt đầu")
                        }

                        OutlinedButton(
                            onClick = {
                                inputText = defaultArray.joinToString(", ")
                                inputError = null
                            }
                        ) {
                            Text("Mảng mẫu")
                        }

                        OutlinedButton(
                            onClick = {
                                val randomArray = (1..8).map { (1..50).random() }
                                inputText = randomArray.joinToString(", ")
                                inputError = null
                            }
                        ) {
                            Text("Ngẫu nhiên")
                        }
                    }

                    Text(
                        text = "• Nhập tối đa 15 số\n• Các số cách nhau bởi dấu phẩy, khoảng trắng hoặc dấu chấm phẩy\n• Ví dụ: 10, 4, 8, 13 hoặc 10 4 8 13",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

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

        // Vẽ mảng
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
            if (isSimulationStarted) {
                // Nút Bước trước
                Button(
                    onClick = previousStep,
                    enabled = stepIndex > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("← Bước trước")
                }

                // Nút Bước tiếp theo
                Button(
                    onClick = nextStep,
                    enabled = stepIndex < steps.size
                ) {
                    Text("Bước tiếp theo →")
                }
            }

            Button(
                onClick = resetSimulation,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
            ) {
                Text("Reset")
            }
        }

        // Hiển thị thông tin bước hiện tại
        if (isSimulationStarted) {
            Text(
                text = "Bước ${stepIndex}/${steps.size}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (isSimulationStarted && stepIndex >= steps.size) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Text(
                    text = "🎉 Mô phỏng hoàn thành!\nMảng ban đầu: ${currentArray.joinToString(", ")}\nMảng sau khi sắp xếp: ${visualState.array.joinToString(", ")}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}