package com.example.algorithmssimulationapp.sort

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.algorithmssimulationapp.showList

@Composable
fun showMergeSort(arr: MutableList<Int>, option:String){
    if (option == "normalmergesort"){
        var sortedList by remember { mutableStateOf(mergeSort(arr)) }
        showList(sortedList.toMutableList())
    }
    else if (option == "naturalmergesort"){
        var sortedList = remember {mergeSortRuns(arr)}
        showList(sortedList.toMutableList())
    }
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


////NATURAL MERGE SORT
fun mergeSortRuns(arr: MutableList<Int>): List<Int> {

    var runs = splitRuns(arr)
    var result = mutableListOf<Int>()

    while (runs.size > 1){
        var currentRun = mutableListOf<List<Int>>()
        var index = 0
        while (index < runs.size){
            if (index +1 <runs.size){
                currentRun.add(mergeRuns(runs[index],runs[index+1]))
                index+=2
            }
            else {
                currentRun.add(runs[index])
                index++
            }
        }
        runs = currentRun
    }
    result = runs[0].toMutableList()
    return result

}

fun splitRuns(arr: MutableList<Int>): MutableList<List<Int>> {
    var runs = mutableListOf<List<Int>>()

    var index = 0
    var currentRun = mutableListOf<Int>(arr[index])
    for (index in 1 until arr.size){
        if (arr[index]>arr[index-1]){
            currentRun.add(arr[index])
        }
        else{
            runs.add(currentRun)
            currentRun = mutableListOf<Int>(arr[index])
        }
    }
    runs.add(currentRun)
    println(runs)
    return runs

}

fun mergeRuns(left: List<Int>, right: List<Int>):  MutableList<Int> {
    var result = mutableListOf<Int>()
    var i = 0
    var j = 0
    println("LeftRuns: "+left + "RightRuns: "+right)
    while(i<left.size && j<right.size){
        if (left[i] <= right[j]) {
            result.add(left[i])
            i++
            println("result add left: "+result)

        } else {
            result.add(right[j])
            j++
            println("result add right: " + result)
        }
        println(result)

    }
    for(u in i..left.size-1){
        result.add(left[u])
    }
    for(j in j..right.size-1) {
        result.add(right[j])
    }
    println("after merge: "+result)
    return result
}