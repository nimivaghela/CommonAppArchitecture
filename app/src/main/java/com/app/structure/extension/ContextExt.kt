package com.app.structure.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.app.structure.R
import java.io.FileDescriptor


fun Activity.resToast(@StringRes res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
}

fun Activity.callPhoneIntent(phoneNumber: String, errorMessage: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))

    if (intent.resolveActivity(packageManager) != null)
        startActivity(intent)
    else {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}

fun Activity.resToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Dialog.hideKeyboardFromDialog() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(context)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showMapForNavigation(latitude: String, longitude: String) {
    val latLongUri = Uri.parse(String.format("google.navigation:q=%1s,%2s", latitude, longitude))
    val mapIntent = Intent(Intent.ACTION_VIEW, latLongUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
    }
}

fun Activity.showAlert(
    title: String,
    msg: String?,
    positiveButtonText: String,
    negativeButtonText: String? = null,
    alertDialogTheme: Int,
    listener: DialogInterface.OnClickListener,
    cancelable: Boolean = true
) {
    val builder = AlertDialog.Builder(this, alertDialogTheme)
        .setTitle(title)
        .setMessage(msg)
        .setCancelable(cancelable)
    if (negativeButtonText == null) {
        builder.setNeutralButton(positiveButtonText, listener)
    } else {
        builder.setPositiveButton(positiveButtonText, listener)
            .setNegativeButton(negativeButtonText, listener)
    }

    val dialog = builder.create()

    dialog.setOnShowListener {
        val btnYes = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 0, 0, 0)
        btnYes.layoutParams = params

        val btnNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        btnNo.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        btnNo.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
    }
    if (!this.isFinishing)
        dialog.show()
}

fun Context.getWidthOfScreen(): Int {
    val displayMetrics = DisplayMetrics()
    val display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    display.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.bitmapFromUri(uri: Uri): Bitmap? {
    val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver
        .openFileDescriptor(uri, "r")
    parcelFileDescriptor?.let {
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        val bitmap: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return bitmap
    } ?: let { return null }
}

fun Activity.startActivityWithAnimation(intent: Intent) {
    hideKeyboard()
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}
fun Activity.startActivityWithAnimationLetToRight(intent: Intent) {
    hideKeyboard()
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun Activity.startActivityForResultWithAnimation(intent: Intent, reqCode: Int) {
    startActivityForResult(intent, reqCode)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun changeStatusBarColor(context: Context,window: Window){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(context,R.color.yellow)
    }
}


fun Context.getPrefInstance(prefName: String): SharedPreferences =
    this.getSharedPreferences(prefName, Context.MODE_PRIVATE)

@SuppressLint("HardwareIds")
fun Context.deviceId(): String =
    Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

