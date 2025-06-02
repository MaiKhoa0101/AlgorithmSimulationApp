package com.example.algorithmssimulationapp.sort

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.algorithmssimulationapp.showList

@Composable
fun showMergeSort(arr: MutableList<Int>){
    var sortedList by remember { mutableStateOf(mergeSort(arr)) }
    sortedList = mergeSort(arr)
    showList(sortedList.toMutableList())
}


fun mergeSort(arr: MutableList<Int>): List<Int> {
    if (arr.size <= 1) {
        return arr
    }
    println(arr)
    val middle = arr.size / 2
    val left = mergeSort(arr.subList(0, middle).toMutableList())
    val right = mergeSort(arr.subList(middle, arr.size).toMutableList())
    val result = merge(left, right)
    return result
}

fun merge(left: List<Int>, right: List<Int>): MutableList<Int> {
    val result = mutableListOf<Int>()
    var i = 0
    var j = 0

    while (i < left.size && j < right.size) {
        if (left[i] <= right[j]) {
            result.add(left[i])
            i++
            println("result add left: "+result)

        } else {
            result.add(right[j])
            j++
            println("result add right: "+result)
        }
        println("Sum result: "+result)
    }
    for(u in i..left.size-1){
        result.add(left[u])
    }
    for(j in j..right.size-1){
        result.add(right[j])
    }

    return result
}
