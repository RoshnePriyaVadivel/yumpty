package com.example.yumpty

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.material.FloatingActionButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.yumpty.ui.theme.YumptyTheme
import androidx.compose.material.icons.filled.ArrowDropDown
import android.content.Context
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

@ExperimentalComposeUiApi
@Composable
fun YumptyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { Yumpty(navController) }
        composable("addItem") { AddItemScreen(navController) }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Yumpty(navController: NavController) {
    val context = LocalContext.current
    val tabs = listOf("Food", "Fun")
    val tabContent: @Composable (String) -> Unit = { tab ->
        when (tab) {
            "Food" -> FoodScreen()
            "Fun" -> FunScreen()
            else -> FoodScreen()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TabLayout Example") }
            )
        },
        content = {
            Column {
                TabLayout(tabs, tabContent)
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
fun FoodScreen() {
    Column {
        Text(text = "Food Tab Content")
    }
}

@Composable
fun FunScreen() {
    Column {
        Text(text = "Fun Tab Content")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddItemScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var canteen by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    var cuisine by remember { mutableStateOf(TextFieldValue()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Main Course") }

    val categories = listOf(
        "Main Course",
        "Beverage",
        "Snacks"
    )

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
                    onValueChange = { name = name.copy(text = it) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = canteen.text,
                    onValueChange = { canteen = canteen.copy(text = it) },
                    label = { Text("Canteen") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price.text,
                    onValueChange = { price = price.copy(text = it) },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = cuisine.text,
                    onValueChange = { cuisine = cuisine.copy(text = it) },
                    label = { Text("Cuisine") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                /*DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        ) {
                            Text(text = category)
                        }
                    }
                }*/
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Category: $selectedCategory",
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category
                                expanded = false // Close the dropdown after selection
                            }
                        ) {
                            Text(text = category)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        saveDataToCsv(
                            name.text,
                            canteen.text,
                            price.text,
                            cuisine.text,
                            selectedCategory
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Save")
                }
            }
        }
    )
}

fun saveDataToCsv(name: String, canteen: String, price: String, cuisine: String, category: String) {
    val fileName = "yumpty_data.csv"
    val csvHeader = "Name,Canteen,Price,Cuisine,Category\n"
    val csvRow = "$name,$canteen,$price,$cuisine,$category\n"

    val file = File(Environment.getExternalStorageDirectory(), fileName)
    if (!file.exists()) {
        file.createNewFile()
        file.appendText(csvHeader)
    }
    file.appendText(csvRow)
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
