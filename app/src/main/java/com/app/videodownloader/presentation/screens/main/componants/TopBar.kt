package com.app.videodownloader.presentation.screens.main.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.MainState
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel

@Composable
fun TopBar(
    state: MainState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    selectedTab: BottomNavItem,
    onInfoClicked: () -> Unit,
    onPremiumClicked : () -> Unit,
) {

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        when (selectedTab) {
            BottomNavItem.Download -> {
                DownloadTopBar(
                    onPremiumClicked = {
                        onPremiumClicked()
                    }
                )
            }

            BottomNavItem.Home -> {
                HomeTopBar(
                    onPremiumClicked = {
                        onPremiumClicked()
                    },
                    onInfoClicked = {
                        onInfoClicked()
                    }
                )
            }

            BottomNavItem.Player -> {
                PlayerTopBar(
                    onPremiumClicked = {
                        onPremiumClicked()
                    }
                )
            }

            BottomNavItem.Reels -> {
                ReelsTopBar(
                    onPremiumClicked = {
                        onPremiumClicked()
                    },
                    onInfoClicked = {
                        onInfoClicked()
                    }
                )
            }

            BottomNavItem.More -> {
                MoreTopBar(
                    onPremiumClicked = {
                        onPremiumClicked()
                    }
                )
            }

            BottomNavItem.Social -> {
                SocialTopBar(
                    onBackClicked = {
                        viewModel.onEvent(
                            MainEvents.OnTabSelected(BottomNavItem.Home, null)
                        )
                    }
                )
            }
        }
    }

}

@Composable
fun HomeTopBar(
    onPremiumClicked : () -> Unit,
    onInfoClicked: () -> Unit
) {


    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp,)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE00004)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_download1),
                contentDescription = "download icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = stringResource(R.string.top_bar_app_name),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onPremiumClicked()
            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = "premium icon",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = {
                onInfoClicked()

            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "info icon",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }

    }

}

@Composable
fun PlayerTopBar(
    onPremiumClicked : () -> Unit,
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.top_bar_my_player),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onPremiumClicked()
            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = "premium icon",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = {},

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "info icon",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }

    }
}

@Composable
fun ReelsTopBar(
    onPremiumClicked : () -> Unit,
    onInfoClicked: () -> Unit
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.top_bar_trending_reels),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {onPremiumClicked()},

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = "premium icon",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = {
                onInfoClicked()
            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "info icon",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }

    }
}

@Composable
fun DownloadTopBar(
    onPremiumClicked : () -> Unit,
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.top_bar_downloads),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onPremiumClicked()
            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = "premium icon",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = {},

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "info icon",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }

    }
}

@Composable
fun MoreTopBar(
    onPremiumClicked : () -> Unit,
){

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.top_bar_more_options),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onPremiumClicked()
            },

            ) {
            Icon(
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = "premium icon",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }

    }
}

@Composable
fun SocialTopBar(
    onBackClicked:() -> Unit
){

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                onBackClicked()
            },
            ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "premium icon",
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Social",
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )


    }
}

