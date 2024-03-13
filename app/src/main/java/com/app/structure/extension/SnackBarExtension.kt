package com.app.structure.extension

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.structure.R
import com.google.android.material.snackbar.Snackbar


fun View.snackWithColor(msg: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snackbarview: Snackbar = Snackbar.make(this, msg, duration)
    val snckView: View = snackbarview.view
    val tv = snckView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    tv.setTextColor(Color.RED)
    tv.maxLines = 3
    snckView.setBackgroundColor(ContextCompat.getColor(this.context, android.R.color.white))
    snackbarview.show()
}