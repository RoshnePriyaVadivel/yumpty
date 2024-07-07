package com.example.yumpty

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yumpty.ui.theme.YumptyTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.Slider
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        setContent {
            YumptyTheme {
                YumptyApp()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with file writing
            } else {
                // Permission denied, show a message to the user
            }
        }
    }
}

data class FoodItem(val name: String, val price: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun YumptyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val foodItems = remember { mutableStateOf<List<FoodItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodItems.value = readFoodItemsFromFile(context, "food_items.csv")
    }

    NavHost(navController, startDestination = "main") {
        composable("main") { Yumpty(navController, foodItems.value) { newItem ->
            val updatedItems = foodItems.value + newItem
            foodItems.value = updatedItems
            writeFoodItemsToFile(context, updatedItems, "food_items.csv")
        } }
        composable("addItem") { AddItemScreen(navController) { newItem ->
            val updatedItems = foodItems.value + newItem
            foodItems.value = updatedItems
            writeFoodItemsToFile(context, updatedItems, "food_items.csv")
            navController.popBackStack()
        } }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Yumpty(navController: NavController, foodItems: List<FoodItem>, onAddItem: (FoodItem) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TabLayout Example") }
            )
        },
        content = {
            Column {
                TabLayout(listOf("Food", "Fun")) { tab ->
                    when (tab) {
                        "Food" -> FoodScreen(foodItems, onAddItem)
                        "Fun" -> FunScreen()
                        else -> FoodScreen(foodItems, onAddItem)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addItem") },
                backgroundColor = Color(0xFF74C931),
                contentColor = Color.White,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true
    )
}

@Composable
fun TabLayout(
    tabs: List<String>,
    tabContent: @Composable (String) -> Unit
) {
    val tabState = remember { mutableStateOf(0) }
    val selectedTab = tabs[tabState.value]

    Column {
        TabRow(
            selectedTabIndex = tabState.value,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabState.value == index,
                    onClick = { tabState.value = index },
                    text = { Text(title) }
                )
            }
        }
        tabContent(selectedTab)
    }
}

@Composable
fun FoodScreen(foodItems: List<FoodItem>, onAddItem: (FoodItem) -> Unit) {
    val context = LocalContext.current
    var selectedItems by remember { mutableStateOf(setOf<FoodItem>()) }
    val totalPrice by remember { derivedStateOf { selectedItems.sumOf { it.price } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Total Price: $$totalPrice",
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp, start = 25.dp),
            fontSize = 20.sp,
            color = Color.Black
        )
        RecyclerView(
            foodItems = foodItems,
            selectedItems = selectedItems,
            onItemCheckedChange = { item, isChecked ->
                selectedItems = if (isChecked) {
                    selectedItems + item
                } else {
                    selectedItems - item
                }
            }
        )
    }
}

@Composable
fun RecyclerView(
    foodItems: List<FoodItem>,
    selectedItems: Set<FoodItem>,
    onItemCheckedChange: (FoodItem, Boolean) -> Unit
) {
    LazyColumn {
        items(foodItems) { item ->
            ListItem(
                item = item,
                isChecked = selectedItems.contains(item),
                onCheckedChange = { isChecked ->
                    onItemCheckedChange(item, isChecked)
                }
            )
        }
    }
}

@Composable
fun ListItem(item: FoodItem, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        color = Color.White,
        elevation = 8.dp,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = 16.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = item.name)
                Text(text = "$${item.price}")
            }
        }
    }
}

@Composable
fun FunScreen() {
    var sliderValue by remember { mutableStateOf(0f) }
    var spinResult by remember { mutableStateOf(0) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val wheelImageRes = R.drawable.spinwheel // Use a single image resource

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = wheelImageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer(rotationZ = rotation.value)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            spinResult = Random.nextInt(1, 6)
            val spinDegrees = spinResult * 360 + (Random.nextInt(5) * 60)
            scope.launch {
                rotation.animateTo(
                    targetValue = spinDegrees.toFloat(),
                    animationSpec = tween(durationMillis = 2000)
                )
            }
        }) {
            Text("Spin the Wheel")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Spin Result: $spinResult")
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddItemScreen(navController: NavController, onAddItem: (FoodItem) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val newItem = FoodItem(name = name, price = price.toInt())
                        onAddItem(newItem)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Item")
                }
            }
        }
    )
}

fun writeFoodItemsToFile(context: Context, foodItems: List<FoodItem>, fileName: String) {
    val file = File(context.filesDir, fileName)
    try {
        FileOutputStream(file).use { stream ->
            stream.bufferedWriter().use { writer ->
                foodItems.forEach { item ->
                    writer.write("${item.name},${item.price}\n")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun readFoodItemsFromFile(context: Context, fileName: String): List<FoodItem> {
    val file = File(context.filesDir, fileName)
    if (!file.exists()) {
        return emptyList()
    }
    return try {
        file.bufferedReader().useLines { lines ->
            lines.map { line ->
                val parts = line.split(",")
                FoodItem(parts[0], parts[1].toInt())
            }.toList()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YumptyTheme {
        YumptyApp()
    }
}
