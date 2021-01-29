package com.rohitthebest.projectplanner.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.rohitthebest.projectplanner.Constants.NO_INTERNET_MESSAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class Functions {

    companion object {

        private const val TAG = "Functions"
        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            try {
                Log.d(TAG, message)
                Toast.makeText(context, message, duration).show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun isInternetAvailable(context: Context): Boolean {

            return CheckNetworkConnection().isInternetAvailable(context)
        }

        fun showNoInternetMessage(context: Context) {

            showToast(context, NO_INTERNET_MESSAGE)
        }

        fun checkUrl(url: String): String {

            var urll = ""
            try {
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    urll = url
                } else if (url.isNotEmpty()) {
                    urll = "https://www.google.com/search?q=$url"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return urll

        }

        fun openLinkInBrowser(url: String?, context: Context) {

            if (isInternetAvailable(context)) {
                url?.let {

                    try {
                        Log.d(TAG, "Loading Url in default browser.")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkUrl(it)))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        showToast(context, e.message.toString())
                        e.printStackTrace()
                    }
                }
            } else {
                showToast(context,NO_INTERNET_MESSAGE)
            }
        }

        fun shareAsText(message: String?, subject: String?, context: Context) {

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(Intent.createChooser(intent, "Share Via"))

        }

        fun copyToClipBoard(activity: Activity, text: String) {

            val clipboardManager =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = ClipData.newPlainText("url", text)

            clipboardManager.setPrimaryClip(clipData)

        }

        fun hideKeyBoard(activity: Activity) {

            try {

                GlobalScope.launch {

                    closeKeyboard(activity)
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }

        private suspend fun closeKeyboard(activity: Activity) {
            try {
                withContext(Dispatchers.IO) {

                    val view = activity.currentFocus

                    if (view != null) {

                        val inputMethodManager =
                            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun EditText.showKeyboard(activity: Activity) {
            try {

                this.requestFocus()

                val inputMethodManager =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun applyColor(
                context: Context,
                view: View,
                textView: TextView? = null,
                editText: EditText? = null,
                hexCode: String
        ) {

            try {

                view.setBackgroundColor(Color.parseColor(hexCode))
                textView?.let { it.text = hexCode }
                editText?.setText(hexCode)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()

                Functions.showToast(context, "Incorrect hex code")
            }
        }

        fun generateKey(): String {

            return "${System.currentTimeMillis().toStringM(69)}_${Random.nextLong(1000, 99999999).toStringM(69)}"
        }
    }

}