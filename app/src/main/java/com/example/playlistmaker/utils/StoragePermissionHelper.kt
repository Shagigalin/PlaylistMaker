package com.example.playlistmaker.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class StoragePermissionHelper(private val fragment: Fragment) {

    private var onPermissionGranted: (() -> Unit)? = null

    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted?.invoke()
        } else {
            android.widget.Toast.makeText(
                fragment.requireContext(),
                "Для выбора изображения необходимо разрешение",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        onPermissionGranted = null
    }

    fun checkStoragePermission(onGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }
            else -> {
                onPermissionGranted = onGranted
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}