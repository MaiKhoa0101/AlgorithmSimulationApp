package com.example.algorithmssimulationapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.algorithmssimulationapp.sort.CompareInfo
import com.example.algorithmssimulationapp.sort.MergeStep
import com.example.algorithmssimulationapp.sort.StepType
import com.example.algorithmssimulationapp.sort.showMergeSort
import com.example.algorithmssimulationapp.sort.showQuickSort
import kotlin.random.Random

@Composable
fun SortScreen(navHostController: NavHostController){
    Column {
        addListFun(navHostController)
    }
}


@Composable
fun addListFun(navController: NavHostController) {
    var number by remember { mutableStateOf("") }
    var onSubmitted by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf("") }
    val list = remember { mutableStateListOf<Int>() }
    var reset by remember { mutableStateOf(false) }

    LaunchedEffect(reset) {
        list.clear()
        repeat(Random.nextInt(3, 9)) {
            list.add(Random.nextInt(10, 100))
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
            Text("Mảng đã cho: ${list.toList()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            // Nhập số và nút xử lý input
            if (!onSubmitted) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nhập số cần thêm vào mảng: ")
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OnclickButton(modifier, "Add") {
                            if (number.isNotEmpty() && isValidNumber(number)) {
                                list.add(number.toInt())
                                number = ""
                            } else {
                                println("Invalid input")
                            }
                        }

                        OnclickButton(modifier, "Random Array") {
                            reset = !reset
                        }
                    }
                }
            }

            // Nút clear
            OnclickButton(modifier.height(40.dp), "Clear array") {
                list.clear()
                onSubmitted = false
                selection = ""
            }

            // Nút chọn thuật toán
            if (!onSubmitted) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OnclickButton(modifier, "Natural Merge Sort") {
                            onSubmitted = true
                            selection = "naturalmergesort"
                        }

                        OnclickButton(modifier, "Normal Merge Sort") {
                            onSubmitted = true
                            selection = "normalmergesort"
                        }
                    }

                    OnclickButton(modifier, "Quick Sort normal") {
                        navController.navigate("quickSort")
                    }
                }
            }

            // Kết quả sau khi đã submit
            if (list.size > 1 && onSubmitted) {
                when (selection) {
                    "naturalmergesort", "normalmergesort" -> showMergeSort(list, selection)
                    "betterquicksort" -> showQuickSort(list, selection)
                }
            }
            // Hiển thị danh sách ban đầu
            showList(list, "Mảng ban đầu")
        }
    }
}

// Hàm kiểm tra hợp lệ của số nhập vào
fun isValidNumber(input: String): Boolean {
    return input.toIntOrNull() != null && (input.count { it == '-' } <= 1)
}

// Nút bấm tùy chỉnh
@Composable
fun OnclickButton(modifier: Modifier, nameButton: String, onClick: () -> Unit) {
    Button(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(Color.Unspecified),
        onClick = onClick
    ) {
        Text(
            nameButton,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}


@Composable
fun showStepMerge(stepList: MutableList<MergeStep>) {
    var i = remember { mutableIntStateOf(0) }

    Spacer(modifier = Modifier.height(30.dp))
    HorizontalDivider(color = Color.White, thickness = 2.dp)
    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Các bước thực hiện", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            OnclickButton(
                modifier = Modifier.height(40.dp),
                "Quay lại",
                onClick = {
                    if (i.value > 0) {
                        i.value--
                    }
                }
            )
            OnclickButton(
                modifier = Modifier.height(40.dp),
                "Tiến lên",
                onClick = {
                    if (i.value < stepList.size - 1) {
                        i.value++
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        HorizontalDivider(color = Color.White, thickness = 2.dp)
        Spacer(modifier = Modifier.height(30.dp))

        // Hiển thị loại step hiện tại
        val currentStep = stepList[i.value]
        Text(
            when (currentStep.stepType) {
                StepType.SPLIT -> "Chia thành các runs"
                StepType.DISPLAY_RUNS -> "Runs hiện tại"
                StepType.COMPARING -> "Đang so sánh"
                StepType.MERGE_COMPLETE -> "Hoàn thành merge"
                StepType.DIVIDE -> TODO()
                StepType.MERGE_ARRAYS -> TODO()
                StepType.MERGE_STEP -> TODO()
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Mảng:", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            itemsIndexed(currentStep.runs) { runIndex, run ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Run "+ runIndex)
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .border(2.dp, Color.Yellow),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        run.forEachIndexed { elementIndex, value ->
                            // Xác định màu cho element dựa trên comparing state
                            val backgroundColor = getElementColor(
                                runIndex,
                                elementIndex,
                                currentStep.comparingIndices
                            )

                            Column(
                                modifier = Modifier
                                    .height((2 * value).dp)
                                    .width(40.dp)
                                    .background(backgroundColor)
                                    .border(2.dp, Color.Black),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    value.toString(),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hiển thị thông tin so sánh nếu có
        if (currentStep.comparingIndices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            currentStep.comparingIndices.forEach { compareInfo ->
                Text(
                    "So sánh: Run ${compareInfo.leftRunIndex}[${compareInfo.leftElementIndex}] " +
                            "với Run ${compareInfo.rightRunIndex}[${compareInfo.rightElementIndex}]",
                    fontSize = 14.sp,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun showStepNormalMerge(stepList: MutableList<MergeStep>) {
    var i = remember { mutableIntStateOf(0) }

    Spacer(modifier = Modifier.height(30.dp))
    HorizontalDivider(color = Color.White, thickness = 2.dp)
    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Các bước Normal Merge Sort", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            OnclickButton(
                modifier = Modifier.height(40.dp),
                "Quay lại",
                onClick = {
                    if (i.value > 0) {
                        i.value--
                    }
                }
            )
            OnclickButton(
                modifier = Modifier.height(40.dp),
                "Tiến lên",
                onClick = {
                    if (i.value < stepList.size - 1) {
                        i.value++
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        HorizontalDivider(color = Color.White, thickness = 2.dp)
        Spacer(modifier = Modifier.height(30.dp))

        val currentStep = stepList[i.value]

        // Hiển thị loại step hiện tại
        Text(
            when (currentStep.stepType) {
                StepType.DIVIDE -> "Chia mảng"
                StepType.MERGE_ARRAYS -> "Chuẩn bị merge"
                StepType.MERGE_STEP -> "Đang merge"
                StepType.MERGE_COMPLETE -> "Hoàn thành merge"
                else -> "Bước thực hiện"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Hiển thị mô tả chi tiết
        if (currentStep.description.isNotEmpty()) {
            Text(
                currentStep.description,
                fontSize = 14.sp,
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
        }

        // Hiển thị theo từng loại step
        when (currentStep.stepType) {
            StepType.DIVIDE -> {
                // Hiển thị mảng cần chia
                showNormalMergeArray(currentStep.leftArray, "Mảng cần chia")
            }

            StepType.MERGE_ARRAYS -> {
                // Hiển thị 2 mảng con chuẩn bị merge
                showNormalMergeArray(currentStep.leftArray, "Mảng trái")
                Spacer(modifier = Modifier.height(15.dp))
                showNormalMergeArray(currentStep.rightArray, "Mảng phải")
            }

            StepType.MERGE_STEP -> {
                // Hiển thị quá trình merge với highlighting
                showMergeProcess(currentStep)
            }

            StepType.MERGE_COMPLETE -> {
                // Hiển thị kết quả cuối cùng
                showNormalMergeArray(currentStep.leftArray, "Mảng trái")
                Spacer(modifier = Modifier.height(10.dp))
                showNormalMergeArray(currentStep.rightArray, "Mảng phải")
                Spacer(modifier = Modifier.height(10.dp))
                showNormalMergeArray(currentStep.resultArray, "Kết quả merge", Color(0xFF00ac35))
            }

            else -> {
                // Default case
                if (currentStep.leftArray.isNotEmpty()) {
                    showNormalMergeArray(currentStep.leftArray, "Mảng")
                }
            }
        }

//        // Hiển thị thông tin so sánh nếu có
//        if (currentStep.leftIndex >= 0 && currentStep.rightIndex >= 0 &&
//            currentStep.stepType == StepType.MERGE_STEP) {
//            Spacer(modifier = Modifier.height(20.dp))
//            if (currentStep.leftIndex < currentStep.leftArray.size &&
//                currentStep.rightIndex < currentStep.rightArray.size) {
//                Text(
//                    "So sánh: ${currentStep.leftArray[currentStep.leftIndex]} " +
//                            "với ${currentStep.rightArray[currentStep.rightIndex]}",
//                    fontSize = 14.sp,
//                    color = Color.Magenta,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
    }
}

@Composable
fun showNormalMergeArray(
    array: List<Int>,
    title: String,
    baseColor: Color = Color.Red
) {
    if (array.isEmpty()) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            array.forEach { value ->
                Column(
                    modifier = Modifier
                        .height((2 * value + 20).dp)
                        .width(40.dp)
                        .background(baseColor)
                        .border(2.dp, Color.Black),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        value.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun showMergeProcess(step: MergeStep) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị mảng trái với highlighting
        Text("Mảng trái:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            step.leftArray.forEachIndexed { index, value ->
                val backgroundColor = when {
                    index == step.leftIndex -> Color(0xFF00ff10) // Element đang được xét
                    index < step.leftIndex -> Color.Gray   // Element đã được xử lý
                    else -> Color.Red                      // Element chưa xử lý
                }

                Column(
                    modifier = Modifier
                        .height((2 * value + 20).dp)
                        .width(40.dp)
                        .background(backgroundColor)
                        .border(2.dp, Color.Black),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        value.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Hiển thị mảng phải với highlighting
        Text("Mảng phải:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            step.rightArray.forEachIndexed { index, value ->
                val backgroundColor = when {
                    index == step.rightIndex -> Color.Blue // Element đang được xét
                    index < step.rightIndex -> Color.Gray  // Element đã được xử lý
                    else -> Color.Red                      // Element chưa xử lý
                }

                Column(
                    modifier = Modifier
                        .height((2 * value + 20).dp)
                        .width(40.dp)
                        .background(backgroundColor)
                        .border(2.dp, Color.Black),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        value.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Hiển thị mảng kết quả
        if (step.resultArray.isNotEmpty()) {
            Text("Kết quả:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                step.resultArray.forEach { value ->
                    Column(
                        modifier = Modifier
                            .height((2 * value + 20).dp)
                            .width(40.dp)
                            .background(Color.Magenta)
                            .border(2.dp, Color.Black),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            value.toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
fun showList(list: SnapshotStateList<Int>, title:String="Sắp xếp") {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            for (i in list) {
                Column(
                    modifier = Modifier
                        .height((2 * i).dp)
                        .width(40.dp)
                        .background(Color.Red),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        i.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}


// Hàm xác định màu cho element dựa trên trạng thái comparing
fun getElementColor(runIndex: Int, elementIndex: Int, comparingIndices: List<CompareInfo>): Color {
    for (compareInfo in comparingIndices) {
        when {
            runIndex == compareInfo.leftRunIndex && elementIndex == compareInfo.leftElementIndex -> {
                return Color.Green // Element đang được so sánh bên trái
            }
            runIndex == compareInfo.rightRunIndex && elementIndex == compareInfo.rightElementIndex -> {
                return Color.Blue // Element đang được so sánh bên phải
            }
        }
    }
    return Color.Red // Màu mặc định
}