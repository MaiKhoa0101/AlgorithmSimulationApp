package com.example.algorithmssimulationapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.algorithmssimulationapp.search.dfsWithSteps
import kotlinx.coroutines.delay
import kotlin.random.Random

// Data classes cho DFS steps
data class DFSStep(
    val currentNode: Int,
    val stepType: DFSStepType,
    val visitedNodes: List<Int>,
    val stack: List<Int>,
    val message: String,
    val exploringFrom: Int? = null,
    val exploringTo: Int? = null
)

enum class DFSStepType {
    START,
    VISIT,
    EXPLORE,
    BACKTRACK,
    COMPLETE
}

data class Node(
    val name: String = "0",
    val value: Int = name.toInt(),
)

@Composable
fun SearchScreen(navHostController: NavHostController){
    Column {
        addGraphFun(navHostController)
    }
}

@Composable
fun addGraphFun(navController: NavHostController) {
    var onSubmitted by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf("") }
    val listNode = remember { mutableMapOf<Node, MutableList<Int>>() }
    var reset by remember { mutableStateOf(false) }

    LaunchedEffect(reset) {
        listNode.clear()
        val max = Random.nextInt(4, 7)

        // Tạo sẵn tất cả node
        val nodes = (1..max).map { Node(it.toString()) }
        nodes.forEach { listNode[it] = mutableListOf() }
        println("listNode sau khi bi xoa la: $listNode")

        for (i in nodes.indices) {
            val nodeA = nodes[i]
            val edgeCount = Random.nextInt(1, 2) // mỗi node có 1–2 cạnh
            val neighbors = mutableSetOf<Int>()

            while (neighbors.size < edgeCount) {
                val randomIndex = Random.nextInt(0, max)
                if (randomIndex != i) {
                    val nodeB = nodes[randomIndex]

                    // Thêm cạnh hai chiều nếu chưa tồn tại
                    if (!listNode[nodeA]!!.contains(nodeB.name.toInt())) {
                        listNode[nodeA]!!.add(nodeB.name.toInt())
                        listNode[nodeB]!!.add(nodeA.name.toInt())
                    }

                    neighbors.add(randomIndex)
                }
            }
        }

        // In ra để debug
        listNode.forEach { (node, edges) ->
            println("${node.name} -> $edges")
        }
    }


    val modifier = Modifier
        .padding(10.dp)
        .width(150.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Hiển thị mảng hiện tại
            Text("Đồ thị đã cho: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            // Nhập số và nút xử lý input
            if (!onSubmitted) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OnclickButton(modifier, "Random Array") {
                        reset = !reset
                        onSubmitted = false
                    }

                }
            }

            // Nút chọn thuật toán
            if (!onSubmitted) {
                Column {
                    OnclickButton(modifier, "BFS") {
                        onSubmitted = true
                        selection = "bfs"
                    }
                    OnclickButton(modifier, "DFS") {
                        onSubmitted = true
                        selection = "dfs"
                    }
                }
                ShowGraph(listNode)
            }


            // Kết quả sau khi đã submit
            if (listNode.size > 1 && onSubmitted) {
                when (selection) {
                    "bfs" -> {

                    }
                    "dfs" -> {
                        println("dfs dc goi")
                        showDFS(listNode)
                    }
                }
            }
        }
    }
}

// Hàm tính toán level của các node (giữ nguyên từ code gốc)
fun calculateNodeLevels(listNode: Map<Node, List<Int>>): Map<Int, List<Node>> {
    val levelMap = mutableMapOf<Int, MutableList<Node>>()
    val visited = mutableSetOf<Node>()
    val queue = mutableListOf<Pair<Node, Int>>()

    val startNode = listNode.keys.find { it.name == "1" } ?: return levelMap
    queue.add(startNode to 1)
    visited.add(startNode)

    while (queue.isNotEmpty()) {
        val (currentNode, level) = queue.removeAt(0)
        levelMap.getOrPut(level) { mutableListOf() }.add(currentNode)

        val neighbors = listNode[currentNode] ?: emptyList()
        for (neighborId in neighbors) {
            val neighborNode = listNode.keys.find { it.name == neighborId.toString() }
            if (neighborNode != null && neighborNode !in visited) {
                visited.add(neighborNode)
                queue.add(neighborNode to level + 1)
            }
        }
    }

    return levelMap
}

@Composable
fun ShowGraph( listNode: MutableMap<Node, MutableList<Int>> = mutableMapOf()){
    var level = calculateNodeLevels(listNode)
    val nodePositions = remember { mutableStateMapOf<Node, Offset>() }
    var readyToDraw by remember { mutableStateOf(false) }

    println("Vao dc day ne")

    LaunchedEffect(nodePositions) {
        if (nodePositions.size == listNode.size) {
            delay(50) // Cho layout hoàn thành
            readyToDraw = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(500.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (listNode==emptyMap<Node, MutableList<Int>>()){
            Text("Chưa có đồ thị")
        }
        else{
            if (readyToDraw) {

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    listNode.forEach { (fromNode, neighbors) ->
                        val fromPosition = nodePositions[fromNode] ?: return@forEach
                        neighbors.forEach { toId ->
                            val toNode =
                                listNode.keys.find { it.name == toId.toString() } ?: return@forEach
                            val toPosition = nodePositions[toNode] ?: return@forEach

                            drawLine(
                                color = Color.Black,
                                start = fromPosition,
                                end = toPosition,
                                strokeWidth = 5f
                            )
                        }
                    }
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                for (u in 1..level.keys.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        level[u]?.forEach { node ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .onGloballyPositioned { coordinates ->
                                        val position = coordinates.localToWindow(Offset(10f,-580f))
                                        nodePositions[node] = position
                                    }
                                    .clip(CircleShape)
                                    .background(Color.Green),
                                contentAlignment = Alignment.Center,

                                ) {
                                Text(node.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OnclickButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color.Blue else Color.Gray
        )
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun showDFS(listNode: MutableMap<Node, MutableList<Int>>) {
    val dfsSteps = remember { mutableStateListOf<DFSStep>() }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    // Tính toán các bước DFS
    LaunchedEffect(listNode) {
        if (listNode.isNotEmpty()) {
            val steps = dfsWithSteps(listNode, 1)
            dfsSteps.clear()
            dfsSteps.addAll(steps)
            currentStepIndex = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp)
    ) {
        // Hiển thị đồ thị
        ShowGraphWithHighlight(listNode, if (dfsSteps.isNotEmpty()) dfsSteps[currentStepIndex] else null)

        Spacer(modifier = Modifier.height(20.dp))

        if (dfsSteps.isNotEmpty()) {
            // Điều khiển bước
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OnclickButton(
                    modifier = Modifier.width(120.dp),
                    text = "⬅ Lùi",
                    enabled = currentStepIndex > 0,
                    onClick = {
                        if (currentStepIndex > 0) currentStepIndex--
                    }
                )

                Text(
                    "Bước ${currentStepIndex + 1}/${dfsSteps.size}",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                OnclickButton(
                    modifier = Modifier.width(120.dp),
                    text = "Tiến ➡",
                    enabled = currentStepIndex < dfsSteps.size - 1,
                    onClick = {
                        if (currentStepIndex < dfsSteps.size - 1) currentStepIndex++
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White, thickness = 2.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // Hiển thị thông tin bước hiện tại
            val currentStep = dfsSteps[currentStepIndex]

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    currentStep.message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (currentStep.stepType) {
                        DFSStepType.START -> Color.Blue
                        DFSStepType.VISIT -> Color(0xFF2aad00)
                        DFSStepType.EXPLORE -> Color(0xFFe67600)
                        DFSStepType.BACKTRACK -> Color.Red
                        DFSStepType.COMPLETE -> Color.Unspecified
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Danh sách đã thăm
                    Column {
                        Text("Đã thăm:", fontWeight = FontWeight.Bold)
                        Text(
                            if (currentStep.visitedNodes.isEmpty()) "Chưa có"
                            else currentStep.visitedNodes.joinToString(", "),
                            color = Color(0xFF2aad00)
                        )
                    }

                    // Stack hiện tại
                    Column {
                        Text("Stack:", fontWeight = FontWeight.Bold)
                        Text(
                            if (currentStep.stack.isEmpty()) "Rỗng"
                            else currentStep.stack.joinToString(", "),
                            color = Color.Blue
                        )
                    }
                }

                // Hiển thị cạnh đang khám phá
                if (currentStep.exploringFrom != null && currentStep.exploringTo != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Cạnh đang khám phá: ${currentStep.exploringFrom} → ${currentStep.exploringTo}",
                        color = Color(0xFFe67600),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ShowGraphWithHighlight(
    listNode: MutableMap<Node, MutableList<Int>>,
    currentStep: DFSStep?
) {
    val level = calculateNodeLevels(listNode)
    val nodePositions = remember { mutableStateMapOf<Node, Offset>() }

    Box(
        modifier = Modifier
            .height(500.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Vẽ các cạnh
        Canvas(modifier = Modifier.fillMaxSize()) {
            listNode.forEach { (fromNode, neighbors) ->
                val fromPosition = nodePositions[fromNode] ?: return@forEach
                neighbors.forEach { toId ->
                    val toNode = listNode.keys.find { it.name == toId.toString() } ?: return@forEach
                    val toPosition = nodePositions[toNode] ?: return@forEach

                    // Highlight cạnh đang khám phá
                    val isExploringEdge = currentStep?.exploringFrom == fromNode.value &&
                            currentStep?.exploringTo == toId

                    drawLine(
                        color = if (isExploringEdge) Color.Red else Color.Black,
                        start = fromPosition,
                        end = toPosition,
                        strokeWidth = if (isExploringEdge) 8f else 3f
                    )
                }
            }
        }

        // Vẽ các đỉnh
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            for (levelNum in 1..level.keys.size) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    level[levelNum]?.forEach { node ->
                        val nodeValue = node.value
                        val nodeColor = when {
                            currentStep?.currentNode == nodeValue -> Color.Red // Đỉnh hiện tại
                            currentStep?.visitedNodes?.contains(nodeValue) == true -> Color(0xFF2aad00) // Đã thăm
                            currentStep?.stack?.contains(nodeValue) == true -> Color.Yellow // Trong stack
                            else -> Color.LightGray // Chưa thăm
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .onGloballyPositioned { coordinates ->
                                    val position = coordinates.localToWindow(Offset(-20f, -150f))
                                    nodePositions[node] = position
                                }
                                .clip(CircleShape)
                                .background(nodeColor)
                                .border(2.dp, Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                node.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (nodeColor == Color.Yellow) Color.Black else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

