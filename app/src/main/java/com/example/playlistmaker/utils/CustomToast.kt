package com.example.playlistmaker.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.playlistmaker.R

object CustomToast {

    fun show(context: Context, message: String) {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
        val textView = layout.findViewById<TextView>(R.id.tv_toast_message)
        textView.text = message

        Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}