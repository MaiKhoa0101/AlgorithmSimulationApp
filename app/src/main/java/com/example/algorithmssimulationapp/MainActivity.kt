package com.example.algorithmssimulationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.example.algorithmssimulationapp.sort.mergeSort
import com.example.algorithmssimulationapp.sort.showMergeSort
import com.example.algorithmssimulationapp.ui.theme.AlgorithmsSimulationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val list = mutableListOf(0)
            AlgorithmsSimulationAppTheme{
                addListFun(list)
            }
        }
    }

    @Composable
    fun addListFun(list: MutableList<Int>){
        var number by remember { mutableStateOf("") }
        var onSubmitted by remember { mutableStateOf(false) }
        Column {
            Text("Mảng hiện tại: $list")
            Text("Nhập số cần thêm vào mảng: ")
            OutlinedTextField(
                value = number,
                onValueChange = {
                    number = it
                }
            )
            Button(
                onClick = {
                    if (number.isNotEmpty() && number.isDigitsOnly()) {
                        list.add(number.toInt())
                        number = ""
                    }
                    else{
                        println("Invalid input")
                    }
                }
            ) {
                Text("Add")
            }
            Button(
                onClick = {
                    list.clear()
                    onSubmitted = false
                }
            ) {
                Text("Clear")
            }
            //Submit button
            Button(
                onClick = {
                    onSubmitted = true
                    println("Mảng ban đầu: $list")
                }
            ) {
                Text("Submit")
            }
            if (list.size > 1 && onSubmitted) {
                showMergeSort(list)
            }
        }
    }
}

@Composable
fun showList(list: MutableList<Int>){
    Text("Mảng sau khi sort: $list")
}