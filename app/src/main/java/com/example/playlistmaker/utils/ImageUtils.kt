package com.example.playlistmaker.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    sealed class ImageCopyResult {
        data class Success(val path: String) : ImageCopyResult()
        data class Error(val message: String) : ImageCopyResult()
    }

    fun copyImageToPrivateStorage(context: Context, imageUri: Uri): ImageCopyResult {
        return try {

            val size = getFileSize(context.contentResolver, imageUri)
            if (size > 10 * 1024 * 1024) { // 10MB
                return ImageCopyResult.Error("Файл слишком большой (макс. 10MB)")
            }


            val mimeType = getMimeType(context.contentResolver, imageUri)
            if (mimeType == null || !mimeType.startsWith("image/")) {
                return ImageCopyResult.Error("Выбранный файл не является изображением")
            }

            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return ImageCopyResult.Error("Не удалось открыть файл")

            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                return ImageCopyResult.Error("Не удалось загрузить изображение")
            }


            val optimizedBitmap = optimizeBitmap(bitmap)

            val extension = getFileExtension(mimeType)
            val fileName = "playlist_cover_${System.currentTimeMillis()}.$extension"
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { outputStream ->
                when (extension) {
                    "png" -> optimizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    else -> optimizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
            }

            ImageCopyResult.Success(file.absolutePath)
        } catch (e: SecurityException) {
            ImageCopyResult.Error("Нет доступа к файлу")
        } catch (e: IOException) {
            ImageCopyResult.Error("Ошибка чтения файла: ${e.message}")
        } catch (e: Exception) {
            ImageCopyResult.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    private fun optimizeBitmap(original: Bitmap): Bitmap {
        val maxSize = 1024
        val width = original.width
        val height = original.height

        if (width <= maxSize && height <= maxSize) {
            return original
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }

    private fun getFileSize(contentResolver: ContentResolver, uri: Uri): Long {
        return try {
            contentResolver.openFileDescriptor(uri, "r")?.use {
                it.statSize
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun getMimeType(contentResolver: ContentResolver, uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
    }

    private fun getFileExtension(mimeType: String): String {
        return when {
            mimeType.contains("png") -> "png"
            mimeType.contains("gif") -> "gif"
            else -> "jpg"
        }
    }

    fun deleteImageFromPrivateStorage(context: Context, imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}