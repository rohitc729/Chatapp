package com.rohitchauhan.hiichat.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rohitchauhan.hiichat.utils.bottomBarItems


@Composable
fun MyBottomBar(subNavController: NavHostController, onItemClick: (String) -> Unit) {

    NavigationBar(
        containerColor = Color.Transparent
    ) {
        val backStackEntry = subNavController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry.value?.destination?.route

        bottomBarItems.forEach { item ->
            val selected = currentRoute == item.rout
            NavigationBarItem(
                selected = selected,
                onClick = {
                    onItemClick(item.level)
                    subNavController.navigate(item.rout) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(subNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                icon = {
                    val icon = if(selected) item.selectedIcon else item.unSelectedIcon
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "bottom bar icon"
                    )
                },
                label = { Text(item.level) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xff2196F3)
                )
            )
        }
    }
}