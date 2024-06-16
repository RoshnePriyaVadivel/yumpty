package com.example.yumpty

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.platform.LocalContext
import com.example.yumpty.ui.theme.YumptyTheme
import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Yumpty()
        }
    }
}

@Composable
fun Yumpty(){
    val context = LocalContext.current
    val tabs = listOf("Food", "Fun")
    val tabContent: @Composable (String) -> Unit = { tab ->
        when (tab){
            "Food" -> FoodScreen()
            "Fun" -> FunScreen()
            else -> FoodScreen()
        }
    }
    Scaffold(
        topBar= {
            TopAppBar(
                title = { Text("TabLayout Example")}
            )
        },
        content = {
            Column {
                TabLayout(tabs, tabContent)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
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
        isFloatingActionButtonDocked =true
    )
}

@Composable
fun TabLayout(
    tabs: List<String>,
    tabContent: @Composable (String) ->Unit
){
    val tabState = remember { mutableStateOf(0)}
    val selectedTab = tabs[tabState.value]

    Column{
        TabRow(
            selectedTabIndex = tabState.value,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ){
            tabs.forEachIndexed{ index, title ->
                Tab(selected = tabState.value == index, onClick = { tabState.value = index }, text = { Text(title) })
            }
        }
        tabContent(selectedTab)
    }
}

@Composable
fun FoodScreen(){
    Column{
        Text(text = "Food Tab Content")
    }
}

@Composable
fun FunScreen(){
    Column{
        Text(text = "Fun Tab Content")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Yumpty()
}
