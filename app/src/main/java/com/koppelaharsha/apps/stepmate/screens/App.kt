package com.koppelaharsha.apps.stepmate.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koppelaharsha.apps.stepmate.R
import com.koppelaharsha.apps.stepmate.data.BtConnStatus
import com.koppelaharsha.apps.stepmate.navigation.AppScreen

@Composable
fun App() {
    Home()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Dashboard.name
    )
    Scaffold(
        topBar = {
            AppBar(
                viewModel = viewModel,
                currentScreen = currentScreen,
                canNavigateUp = navController.previousBackStackEntry != null,
                navController = navController
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = AppScreen.Dashboard.name,
            ) {
                composable(route = AppScreen.PairedDevices.name) {
                    BluetoothDevicesList(
                        viewModel = viewModel,
                        deviceSelected = { btDevice ->
                            viewModel.setBtDevice(btDevice)
                            navController.navigateUp()
                        }
                    )
                }
                composable(route = AppScreen.Dashboard.name) {
                    DashboardScreen(
                        viewModel = viewModel,
                        viewPairedDevices = {
                            navController.navigate(AppScreen.PairedDevices.name)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: AppViewModel,
    currentScreen: AppScreen,
    canNavigateUp: Boolean,
    navController: NavController
) {
    val btConnStatus = viewModel.btConnStatus.collectAsState().value
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { Text(stringResource(currentScreen.title)) },
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            if (!canNavigateUp) {
                if (viewModel.btDevice.collectAsState().value != null) {
                    if (btConnStatus == BtConnStatus.connected) {
                        IconButton(onClick = { viewModel.disconnectDevice() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.disconnect)
                            )
                        }
                    }
                    if (btConnStatus is BtConnStatus.disconnected && btConnStatus.error.isEmpty()) {
                        IconButton(onClick = { viewModel.connectDevice() }) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = stringResource(R.string.connect)
                            )
                        }
                    }
                }
                IconButton(onClick = { navController.navigate(AppScreen.PairedDevices.name) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        }
    )
}
