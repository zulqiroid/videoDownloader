package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events.MainEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.BottomNavItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.MainState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.viewModel.MainViewModel

@Composable
fun TopBar(
    state: MainState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    selectedTab: BottomNavItem,
    onInfoClicked: () -> Unit,
    onPremiumClicked: () -> Unit,
    onPlayerSearchClick: () -> Unit,
    onPlayerSearchClosed: () -> Unit,
    onPlayerSearchQueryChanged: (String) -> Unit,
    onDownloadSearchClick: () -> Unit,
    onDownloadSearchClosed: () -> Unit,
    onDownloadSearchQueryChanged: (String) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (selectedTab) {
            BottomNavItem.Download -> {
                DownloadTopBar(
                    isPremiumIconVisible = state.isPremiumIconVisible,
                    isPremiumUser = state.isPremiumUser,
                    isSearchActive = state.isDownloadSearchActive,
                    searchQuery = state.downloadSearchQuery,
                    onPremiumClicked = onPremiumClicked,
                    onSearchClick = onDownloadSearchClick,
                    onSearchClosed = onDownloadSearchClosed,
                    onSearchQueryChanged = onDownloadSearchQueryChanged
                )
            }

            BottomNavItem.Home -> {
                HomeTopBar(
                    isPremiumIconVisible = state.isPremiumIconVisible,
                    isPremiumUser = state.isPremiumUser,
                    onPremiumClicked = onPremiumClicked,
                    onInfoClicked = onInfoClicked
                )
            }

            BottomNavItem.Player -> {
                PlayerTopBar(
                    isPremiumIconVisible = state.isPremiumIconVisible,
                    isPremiumUser = state.isPremiumUser,
                    isSearchActive = state.isPlayerSearchActive,
                    searchQuery = state.playerSearchQuery,
                    onPremiumClicked = onPremiumClicked,
                    onSearchClick = onPlayerSearchClick,
                    onSearchClosed = onPlayerSearchClosed,
                    onSearchQueryChanged = onPlayerSearchQueryChanged
                )
            }

            BottomNavItem.Reels -> {
                ReelsTopBar(

                    isPremiumIconVisible = state.isPremiumIconVisible,
                    isPremiumUser = state.isPremiumUser,
                    onPremiumClicked = onPremiumClicked,
                    onInfoClicked = onInfoClicked
                )
            }

            BottomNavItem.More -> {
                MoreTopBar(
                    isPremiumIconVisible = state.isPremiumIconVisible,
                    isPremiumUser = state.isPremiumUser,
                    onPremiumClicked = onPremiumClicked
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
    isPremiumIconVisible: Boolean,
    isPremiumUser: Boolean,
    onPremiumClicked : () -> Unit,
    onInfoClicked: () -> Unit
) {

    LaunchedEffect(Unit) {
        Log.d("HomeTopBar", "premum icon visible: $isPremiumIconVisible")
    }

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

        if (!isPremiumUser) {
            if (isPremiumIconVisible) {
                IconButton(
                    onClick = onPremiumClicked
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
    isPremiumIconVisible: Boolean,
    isPremiumUser: Boolean,
    isSearchActive: Boolean,
    searchQuery: String,
    onPremiumClicked: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchClosed: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    if (isSearchActive) {
        SearchTopBar(
            query = searchQuery,
            placeholder = stringResource(R.string.search_your_downloads_hint),
            onQueryChanged = onSearchQueryChanged,
            onBackClicked = onSearchClosed
        )
        return
    }

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

        if (!isPremiumUser) {
            if (isPremiumIconVisible) {

                IconButton(
                    onClick = onPremiumClicked
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

        IconButton(
            onClick = onSearchClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "search",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReelsTopBar(
    isPremiumIconVisible: Boolean,
    isPremiumUser: Boolean,
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
        if (!isPremiumUser) {
            if (isPremiumIconVisible) {
                IconButton(
                    onClick = { onPremiumClicked() },

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
    isPremiumIconVisible: Boolean,
    isPremiumUser: Boolean,
    isSearchActive: Boolean,
    searchQuery: String,
    onPremiumClicked: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchClosed: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    if (isSearchActive) {
        SearchTopBar(
            query = searchQuery,
            placeholder = stringResource(R.string.search_your_downloads_hint),
            onQueryChanged = onSearchQueryChanged,
            onBackClicked = onSearchClosed
        )
        return
    }

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

        if (!isPremiumUser) {
            if (isPremiumIconVisible) {
                IconButton(
                    onClick = onPremiumClicked
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

        IconButton(
            onClick = onSearchClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "search",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MoreTopBar(
    isPremiumIconVisible: Boolean,
    isPremiumUser: Boolean,
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
        if (!isPremiumUser) {
            if (isPremiumIconVisible) {
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
            text = stringResource(R.string.top_bar_social),
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )


    }
}



@Composable
private fun SearchTopBar(
    query: String,
    placeholder: String,
    onQueryChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(23.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF1F2F4)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color(0xFFE00004),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1F2937),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {}
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        if (query.isBlank()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFF374151),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400
                            )
                        }

                        innerTextField()
                    }
                )

                if (query.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6B7280))
                            .clickable {
                                onQueryChanged("")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "clear search",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}