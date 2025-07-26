package com.example.yoospace_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yoospace_android.R
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun BottomNav(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = LocalExtraColors.current.cardBackground,
        modifier = modifier
            .height(70.dp)
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .clip(RoundedCornerShape(30.dp)),
        windowInsets = WindowInsets(0)
    ) {
        // observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // observe current route to change the icon
        // color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route
        BottomNavItems.forEach { navItem ->
            // Place the bottom nav items
            NavigationBarItem(
                // it currentRoute is equal then its selected route
                selected = currentRoute == navItem.route,

                // navigate on click
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navItem.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(navItem.icon),
                        contentDescription = navItem.label,
                        modifier = Modifier
                            .size(28.dp)
                    )
                },
//                label = {
//                    Text(text = navItem.label)
//                },
                alwaysShowLabel = false,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (currentRoute == navItem.route) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
            )
        }

    }
}

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val label: String
)

val BottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        icon = R.drawable.home_icon,
        label = "Home"
    ),
    BottomNavItem(
        route = Routes.DISCOVER,
        icon = R.drawable.discover_icon,
        label = "Discover"
    ),
    BottomNavItem(
        route = Routes.MESSAGES,
        icon = R.drawable.messages_icon,
        label = "Messages"
    ),
    BottomNavItem(
        route = Routes.PROFILE,
        icon = R.drawable.profile_icon,
        label = "Profile"
    ),
)