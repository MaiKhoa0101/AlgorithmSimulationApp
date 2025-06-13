package com.example.algorithmssimulationapp.sort

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.algorithmssimulationapp.showList
import com.example.algorithmssimulationapp.showStepMerge
import com.example.algorithmssimulationapp.showStepNormalMerge
import kotlinx.coroutines.delay
import kotlin.collections.mutableListOf


// Data class để lưu thông tin về mỗi step (cho cả normal và natural merge sort)
data class MergeStep(
    val runs: MutableList<List<Int>> = mutableListOf(),
    val comparingIndices: List<CompareInfo> = emptyList(),
    val stepType: StepType = StepType.DISPLAY_RUNS,
    // Thêm fields cho normal merge sort
    val leftArray: List<Int> = emptyList(),
    val rightArray: List<Int> = emptyList(),
    val resultArray: List<Int> = emptyList(),
    val leftIndex: Int = -1,
    val rightIndex: Int = -1,
    val description: String = ""
)
data class CompareInfo(
    val leftRunIndex: Int,  // Index của run bên trái
    val rightRunIndex: Int, // Index của run bên phải
    val leftElementIndex: Int, // Index của element trong run trái
    val rightElementIndex: Int // Index của element trong run phải
)

enum class StepType {
    SPLIT,
    DISPLAY_RUNS,
    COMPARING,
    MERGE_COMPLETE,

    // Thêm cho normal merge sort
    DIVIDE,          // Chia mảng
    MERGE_ARRAYS,    // Merge 2 mảng con
    MERGE_STEP       // Từng bước merge
}

@Composable
fun showMergeSort(originArr: MutableList<Int>, option:String){
    val sortedList = remember { mutableStateListOf<Int>() }
    // Thay đổi structure để lưu thêm thông tin về index đang so sánh
    var stepList = remember { mutableStateListOf<MergeStep>() }
    var done by remember { mutableStateOf(false) }
    var a = remember { mutableStateOf(10)}

    LaunchedEffect(option) {
        stepList.clear()
        println("option: " + option)
        when (option) {
            "normalmergesort" -> {
                sortedList.clear()
                sortedList.addAll(
                    mergeSortWithTracking (
                        originArr,
                        addStepList = { step ->
                            stepList.add(step)
                        }
                    )
                )
            }

            "naturalmergesort" -> {
                sortedList.clear()
                sortedList.addAll(
                    mergeSortRuns(
                        originArr,
                        addStepList = { step ->
                            stepList.add(step)
                        },
                    )
                )
            }
        }
        done = true
    }

    if (done == true) {
        println("stepList: "+stepList)
        showList(sortedList, "Mảng sau khi sắp xếp")
        if (option == "normalmergesort") {
            showStepNormalMerge(stepList)
        } else {
            showStepMerge(stepList)
        }
    }
}
// Normal Merge Sort với tracking
fun mergeSortWithTracking(
    arr: MutableList<Int>,
    addStepList: (MergeStep) -> Unit,
    depth: Int = 0
): List<Int> {
    if (arr.size <= 1) {
        return arr
    }

    // Hiển thị bước chia mảng
    addStepList(MergeStep(
        stepType = StepType.DIVIDE,
        leftArray = arr,
        description = "Chia mảng: " +arr.toList()
    ))

    println(arr)
    val middle = arr.size / 2
    val leftArr = arr.subList(0, middle).toMutableList()
    val rightArr = arr.subList(middle, arr.size).toMutableList()

    val left = mergeSortWithTracking(leftArr, addStepList, depth + 1)
    val right = mergeSortWithTracking(rightArr, addStepList, depth + 1)

    // Hiển thị bước chuẩn bị merge
    addStepList(MergeStep(
        stepType = StepType.MERGE_ARRAYS,
        leftArray = left,
        rightArray = right,
        description = "Chuẩn bị merge: ${left} và ${right}"
    ))

    val result = mergeWithTracking(left, right, addStepList)
    return result
}

fun mergeWithTracking(
    left: List<Int>,
    right: List<Int>,
    addStepList: (MergeStep) -> Unit
): MutableList<Int> {
    val result = mutableListOf<Int>()
    var i = 0
    var j = 0

    while (i < left.size && j < right.size) {
        // Tạo step để show đang so sánh
        addStepList(MergeStep(
            stepType = StepType.MERGE_STEP,
            leftArray = left,
            rightArray = right,
            resultArray = result.toList(),
            leftIndex = i,
            rightIndex = j,
            description = "So sánh ${left[i]} và ${right[j]}"
        ))

        if (left[i] <= right[j]) {
            result.add(left[i])
            i++
            println("result add left: "+result)
        } else {
            result.add(right[j])
            j++
            println("result add right: "+result)
        }

        // Show result sau khi add
        addStepList(MergeStep(
            stepType = StepType.MERGE_STEP,
            leftArray = left,
            rightArray = right,
            resultArray = result.toList(),
            leftIndex = i,
            rightIndex = j,
            description = "Kết quả: ${result}"
        ))

        println("Sum result: "+result)
    }

    // Thêm phần tử còn lại từ left
    for(u in i until left.size){
        result.add(left[u])
        addStepList(MergeStep(
            stepType = StepType.MERGE_STEP,
            leftArray = left,
            rightArray = right,
            resultArray = result.toList(),
            leftIndex = u + 1,
            rightIndex = j,
            description = "Thêm phần tử còn lại từ mảng trái: ${left[u]}"
        ))
    }

    // Thêm phần tử còn lại từ right
    for(k in j until right.size){
        result.add(right[k])
        addStepList(MergeStep(
            stepType = StepType.MERGE_STEP,
            leftArray = left,
            rightArray = right,
            resultArray = result.toList(),
            leftIndex = i,
            rightIndex = k + 1,
            description = "Thêm phần tử còn lại từ mảng phải: ${right[k]}"
        ))
    }

    // Hiển thị kết quả cuối cùng của merge
    addStepList(MergeStep(
        stepType = StepType.MERGE_COMPLETE,
        leftArray = left,
        rightArray = right,
        resultArray = result.toList(),
        description = "Hoàn thành merge: ${result}"
    ))

    return result
}

fun mergeSortRuns(
    arr: MutableList<Int>,
    addStepList: (MergeStep) -> Unit
): List<Int> {
    var runs = splitRuns(arr, addStepList)
    var result = mutableListOf<Int>()

    while (runs.size > 1) {
        var currentRun = mutableListOf<List<Int>>()
        var index = 0

        while (index < runs.size) {
            if (index + 1 < runs.size) {
                // Merge 2 runs và track quá trình so sánh
                val mergedRun = mergeRunsWithTracking(
                    runs[index],
                    runs[index + 1],
                    index,
                    index + 1,
                    runs,
                    addStepList
                )
                currentRun.add(mergedRun)
                index += 2
                // Hiển thị kết quả sau mỗi vòng merge
            } else {
                currentRun.add(runs[index])
                index++
            }
        }
        runs = currentRun
        addStepList(MergeStep(currentRun, emptyList(), StepType.MERGE_COMPLETE))
    }

    result = runs[0].toMutableList()
    addStepList(MergeStep(runs, emptyList(), StepType.DISPLAY_RUNS))
    return result
}

fun splitRuns(
    arr: MutableList<Int>,
    addStepList: (MergeStep) -> Unit
): MutableList<List<Int>> {
    var runs = mutableListOf<List<Int>>()
    var index = 0
    var currentRun = mutableListOf<Int>(arr[index])

    for (index in 1 until arr.size) {
        if (arr[index] > arr[index - 1]) {
            currentRun.add(arr[index])
        } else {
            runs.add(currentRun)
            currentRun = mutableListOf<Int>(arr[index])
        }
    }
    runs.add(currentRun)

    // Add step cho việc split runs
    addStepList(MergeStep(runs, emptyList(), StepType.SPLIT))

    println("runs: " + runs)
    return runs
}

fun mergeRunsWithTracking(
    left: List<Int>,
    right: List<Int>,
    leftRunIndex: Int,
    rightRunIndex: Int,
    allRuns: MutableList<List<Int>>,
    addStepList: (MergeStep) -> Unit
): MutableList<Int> {
    var result = mutableListOf<Int>()
    var i = 0
    var j = 0

    println("LeftRuns: $left RightRuns: $right")

    while (i < left.size && j < right.size) {
        // Tạo step để hiển thị đang so sánh elements nào
        val compareInfo = CompareInfo(
            leftRunIndex = leftRunIndex,
            rightRunIndex = rightRunIndex,
            leftElementIndex = i,
            rightElementIndex = j
        )

        if (left[i] <= right[j]) {
            result.add(left[i])
            i++
            println("result add left: $result")
        } else {
            result.add(right[j])
            j++
            println("result add right: $result")
        }

        // Tạo snapshot của runs hiện tại để hiển thị
        val currentRuns = allRuns.toMutableList()
        addStepList(MergeStep(currentRuns, listOf(compareInfo), StepType.COMPARING))
        println("Sum result: $result")
    }

    // Thêm phần tử còn lại
    for (u in i until left.size) {
        result.add(left[u])
    }
    for (k in j until right.size) {
        result.add(right[k])
    }
    println("after merge: $result")
    return result
}


