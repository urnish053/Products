package com.ellingsengroup.products.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

var itemType: Int = 1
val MAX_SELECT_IMAGE = 5

fun View.show(): View {
    if (visibility != ViewGroup.VISIBLE) {
        visibility = ViewGroup.VISIBLE
    }
    return this
}

fun View.hide(isInvisible: Boolean = false): View {
    if (visibility == ViewGroup.VISIBLE) {
        visibility = if (isInvisible) {
            ViewGroup.INVISIBLE
        } else {
            ViewGroup.GONE
        }
    }
    return this
}

fun Context?.toast(text: String, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context
                .INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun ImageView.loadImageFromURL(url: String?, placeholder: Int? = null) {
    try {
        if (placeholder != null) {
            Glide.with(this).load(url).placeholder(placeholder).fitCenter().into(this)
        } else {
            Glide.with(this).load(url).fitCenter()
                .into(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun getPathFromUri(uri: Uri?, context: Context?): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        context?.contentResolver?.query(uri!!, projection, null, null, null) ?: return null
    val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(column_index)
    cursor.close()
    return s
}

inline val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)