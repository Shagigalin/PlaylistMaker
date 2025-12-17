
package com.example.playlistmaker.util.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.playlistmaker.R

fun NavController.navigateWithBottomNav(destinationId: Int) {
    val navOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(R.id.main_nav_graph, inclusive = false, saveState = true)
        .build()

    navigate(destinationId, null, navOptions)
}