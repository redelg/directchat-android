package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codergang.chatdirecto.R

@Composable
internal fun MainScaffold(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    topBarActions: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { MainTopBar(actions = topBarActions) },
        bottomBar = {
            MainBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(actions: @Composable () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                letterSpacing = 0.2.sp
            )
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BrandPrimary,
            titleContentColor = ComposeColor.White,
            actionIconContentColor = ComposeColor.White
        )
    )
}

@Composable
private fun MainBottomNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = ComposeColor.White,
        tonalElevation = 6.dp
    ) {
        MainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(id = tab.iconRes),
                        contentDescription = null,
                        tint = if (selectedTab == tab) BrandPrimary else ComposeColor(0xFF94A3B8)
                    )
                },
                label = {
                    Text(
                        text = stringResource(tab.labelRes),
                        fontFamily = AvenirFamily,
                        fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimary,
                    selectedTextColor = BrandPrimary,
                    indicatorColor = BrandLavenderBg,
                    unselectedIconColor = ComposeColor(0xFF94A3B8),
                    unselectedTextColor = ComposeColor(0xFF94A3B8)
                )
            )
        }
    }
}
