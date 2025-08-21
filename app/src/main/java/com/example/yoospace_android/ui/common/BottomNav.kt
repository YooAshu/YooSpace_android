package com.example.yoospace_android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yoospace_android.R
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.theme.LocalExtraColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.FluentMaterials
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNav(navController: NavHostController, modifier: Modifier = Modifier,hazeState: HazeState) {
    Row(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .clip(RoundedCornerShape(50.dp))
            .hazeEffect(state = hazeState, style = CupertinoMaterials.ultraThin())
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavItems.forEach { navItem ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        if (currentRoute == navItem.route) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable {
                        if (
                            currentRoute != navItem.route
                        ) {
                            navController.navigate(navItem.route) {
                                popUpTo(navItem.route) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(navItem.icon),
                    contentDescription = navItem.label,
                    tint = if (currentRoute == navItem.route) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
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
        route = Routes.ADD_POST,
        icon = R.drawable.round_add_24,
        label = "Add Post"
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