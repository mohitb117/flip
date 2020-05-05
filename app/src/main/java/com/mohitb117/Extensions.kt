package com.mohitb117

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import com.afollestad.materialdialogs.MaterialDialog
import com.mohitb117.flip.R
import java.io.InputStream


/**
 * Creates a [MaterialDialog] with the correct theme applied.
 */
fun Context.createDialog() = MaterialDialog(themeContext(this))

private fun themeContext(context: Context) = ContextThemeWrapper(context, R.style.ThemeOverlay_AppCompat)

/**
 * Creates and shows a [MaterialDialog] with the correct theme applied.
 */
fun Context.showDialog(func: MaterialDialog.() -> Unit): MaterialDialog? {
    val activity = (this as? Activity)

    return activity?.executeIfRunning { createDialog().show(func) }
}

/**
 * Simple check to see if the activity is running for showing dialogs and using the UI.
 */
fun Activity.isRunning() = !this.let { it.isFinishing || it.isDestroyed }

/**
 * Execute a block of code when an activity is running and returns a value
 * from the block of code.
 */
inline fun <T : Any> Activity.executeIfRunning(block: () -> T?): T? =
    if (isRunning()) {
        block()
    } else {
        Log.e(Activity::class.java.simpleName, "Could not show dialog as the activity is being destroyed.")
        null
    }
/**
 * Convert the input stream into a string.
 */
fun InputStream.readText() = this.bufferedReader().use { it.readText() }
