package com.example.algorithmssimulationapp.sort

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.algorithmssimulationapp.showList

@Composable
fun showQuickSort(arr: MutableList<Int>, option:String) {
    if (option == "normalquicksort") {

    }
    else if (option == "betterquicksort") {
        var sortedList by remember { mutableStateOf(quicksortbetter(arr, 0, arr.size - 1)) }
        showList(sortedList.toMutableList())
    }
}

fun quicksortbetter(arr: MutableList<Int>, leftInput: Int, rightInput: Int): MutableList<Int> {
    var pivot = ((leftInput + rightInput) / 2)
    var left = leftInput
    var right = rightInput

    do {
        while (arr[left] < arr[pivot]) {
            left++
        }
        while (arr[right] > arr[pivot]) {
            right--
        }
        if (left <= right) {
            var temp = arr[left]
            arr[left] = arr[right]
            arr[right] = temp
            left++
            right--
        }
    } while (left <= right)
    if (leftInput < right) {
        quicksortbetter(arr, leftInput, right)
    }
    if (left < rightInput) {
        quicksortbetter(arr, left, rightInput)
    }
    return arr
}

fun splitRuns(arr: MutableList<Int>, start: Int, end: Int) {

}