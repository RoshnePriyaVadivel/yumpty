package com.example.yumpty

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.Column
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.TabRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Tab
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.IconButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.runtime.LaunchedEffect


class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        setContent {
            YumptyApp()
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

@ExperimentalComposeUiApi
@Composable
fun YumptyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val foodItems = remember { mutableStateOf<List<FoodItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodItems.value = readFoodItemsFromFile(context, "food_items.csv")
    }

    NavHost(navController, startDestination = "main") {
        composable("main") { Yumpty(navController, foodItems.value) }
        composable("addItem") { AddItemScreen(navController) }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Yumpty(navController: NavController, foodItems: List<FoodItem>) {
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
                        "Food" -> FoodScreen(foodItems = foodItems)
                        "Fun" -> FunScreen()
                        else -> FoodScreen(foodItems = foodItems)
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
fun FoodScreen(foodItems: List<FoodItem>) {
    Column {
        RecyclerView(
            foodItems = foodItems,
            selectedItems = emptySet(),
            onItemCheckedChange = { _, _ -> }
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Fun Tab Content")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddItemScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }

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
                    value = name.text,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price.text,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val newItem = FoodItem(name = name.text, price = price.text.toIntOrNull() ?: 0)
                        writeFoodItemsToFile(LocalContext.current, listOf(newItem), "food_items.csv")
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Save")
                }
            }
        }
    )
}

private fun writeFoodItemsToFile(
    context: Context,
    foodItems: List<FoodItem>,
    fileName: String
) {
    val file = File(context.filesDir, fileName)
    try {
        FileOutputStream(file, true).bufferedWriter().use { writer ->
            foodItems.forEach { item ->
                writer.write("${item.name},${item.price}\n")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun readFoodItemsFromFile(context: Context, fileName: String): List<FoodItem> {
    val file = File(context.filesDir, fileName)
    val foodItems = mutableListOf<FoodItem>()
    try {
        file.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(",")
                if (parts.size == 2) {
                    val name = parts[0]
                    val price = parts[1].toIntOrNull() ?: 0
                    foodItems.add(FoodItem(name, price))
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return foodItems
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YumptyTheme {
        val navController = rememberNavController()
        Yumpty(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun FoodScreenPreview() {
    val foodItems = listOf(
        FoodItem("Pizza", 12),
        FoodItem("Burger", 8),
        FoodItem("Salad", 6)
    )
    YumptyTheme {
        FoodScreen(foodItems = foodItems)
    }
}

@Preview(showBackground = true)
@Composable
fun FunScreenPreview() {
    YumptyTheme {
        FunScreen()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    YumptyTheme {
        val navController = rememberNavController()
        AddItemScreen(navController = navController)
    }
}
