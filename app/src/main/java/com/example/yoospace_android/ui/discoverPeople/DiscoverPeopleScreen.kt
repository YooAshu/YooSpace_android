package com.example.yoospace_android.ui.discoverPeople

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.R
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.DiscoverUser
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun DiscoverPeopleScreen(
    navController: NavController,
    viewModel: DiscoverPeopleViewModel = viewModel(),
) {
    ProtectedRoute(navController) {

        LaunchedEffect(Unit) {
            viewModel.fetchDiscoverUsers()
        }
        val isDiscoverPeopleLoading = viewModel.isDiscoverPeopleLoading
        val discoverUsersList: List<DiscoverUser> = viewModel.discoverUsersList
        var searchQuery by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(.8f)
                ) {
                    FormInputField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isEmpty() && viewModel.hasSearched) {
                                viewModel.fetchDiscoverUsers()
                            }
                        },
                        label = "Search Users",
                    )
                }

                Button(
                    onClick = {
                        if (searchQuery.isNotEmpty()) {
                            viewModel.searchDiscoverUsers(searchQuery)
//                            searchQuery = ""
                        }
                    },
                    enabled = !viewModel.hasSearched,
                    modifier = Modifier.padding(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = MaterialTheme.colorScheme.run {
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = LocalExtraColors.current.textSecondary
                        )
                    },

                    ) {
                    Icon(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }
            }
            if (viewModel.errorMessage == "408") {
                if (!viewModel.hasSearched)
                    RequestTimedOut {
                        viewModel.fetchDiscoverUsers()
                    }
                else{
                    RequestTimedOut {
                        viewModel.searchDiscoverUsers(searchQuery)
                    }
                }
            }
            LazyColumn {
                items(discoverUsersList, key = { it._id }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(LocalExtraColors.current.item_bg)
                            .padding(10.dp)
                            .clickable {
                                navController.navigate(Routes.userProfile(user._id))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var isFollowing by remember { mutableStateOf(user.isFollowing) }
                        Row {
                            ProfileImage(
                                profileImage = ImageSource.Url(user.profile_image),
                                userId = user._id,
                                size = 35,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                "@${user.userName}",
                                color = LocalExtraColors.current.textSecondary,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            )
                        }
                        Button(
                            onClick = {
                                if (!viewModel.followLoading)
                                    if (isFollowing) {
                                        viewModel.unfollowUser(user._id)
                                        TokenManager.updateUserFollowingNo(-1)
                                    } else {
                                        viewModel.followUser(user._id)
                                        TokenManager.updateUserFollowingNo(1)
                                    }
                                isFollowing = !isFollowing
                            },
                            modifier = Modifier
                                .border(1.dp, Color(0xFF6B38F8), RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .height(30.dp)
                                .width(80.dp)
                                .background(
                                    if (!isFollowing)
                                        SolidColor(Color.Transparent)
                                    else
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF8208FC),
                                                Color(0xFF3725D7)
                                            )
                                        )
                                ),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                            ),
                        ) {
                            Text(
                                if (isFollowing) "Following" else "Follow",
                                modifier = Modifier
                                    .padding(5.dp),
                                color = LocalExtraColors.current.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}