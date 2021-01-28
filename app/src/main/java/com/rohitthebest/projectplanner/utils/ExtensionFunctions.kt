package com.rohitthebest.projectplanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants.EDIT_TEXT_EMPTY_MESSAGE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.utils.Functions.Companion.showKeyboard
import yuku.ambilwarna.AmbilWarnaDialog


fun View.showViewBySlidingAnimation(duration: Long = 600L) {

    this.show()

    val animation = AnimationUtils.loadAnimation(context, R.anim.add_topic_button_visible_anim).apply {

        this.duration = duration
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = 0
    }
    startAnimation(animation)
}

fun View.hideViewBySlidingAnimation(visibilityMode: Int = View.INVISIBLE, duration: Long = 600L) {

    val animation = AnimationUtils.loadAnimation(context, R.anim.add_topic_button_invisible_anim).apply {

        this.duration = duration
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = 0
    }

    startAnimation(animation)

    this.visibility = visibilityMode
}

fun View.show() {

    try {
        this.visibility = View.VISIBLE

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.hide() {

    try {
        this.visibility = View.GONE

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.invisible() {

    try {
        this.visibility = View.INVISIBLE

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.openColorPicker(context: Context, defaultColor: Int, textView: TextView? = null) {

    try {
        AmbilWarnaDialog(
                context,
                defaultColor,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {
                        //TODO("Not yet implemented")
                    }

                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                        this@openColorPicker.setBackgroundColor(color)
                        textView?.let { it.text = color.convertToHexString() }
                    }
                }
        ).show()
    } catch (e: Exception) {

        e.printStackTrace()
    }
}

private fun applyColor(context: Context, view: View, textView: TextView?, hexCode: String) {

    try {

        view.setBackgroundColor(Color.parseColor(hexCode))
        textView?.let { it.text = hexCode }

    } catch (e: java.lang.Exception) {
        e.printStackTrace()

        Functions.showToast(context, "Incorrect hex code")
    }
}

@SuppressLint("SetTextI18n")
fun View.openDialogForWritingHexColor(activity: Activity, textView: TextView? = null) {

    MaterialDialog(activity).show {

        title(text = "Write hex string")

        customView(
                R.layout.editext_input_layout
        )

        val input = getCustomView().findViewById<TextInputLayout>(R.id.inputEditText).editText

        input?.showKeyboard(activity)

        input?.apply {

            hint = "write hex code e.g. #000000"
            inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            setText(textView?.text?.toString())
            setTextColor(Color.parseColor(textView?.text?.toString()))
            textView?.text?.length?.let { setSelection(0, it) }
        }

        input?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    getCustomView().findViewById<TextInputLayout>(R.id.inputEditText).error = EDIT_TEXT_EMPTY_MESSAGE
                } else {

                    getCustomView().findViewById<TextInputLayout>(R.id.inputEditText).error = null

                    try {
                        if (s.length >= 6) {

                            if (s.toString().trim().startsWith("#")) {

                                input.setTextColor(Color.parseColor(s.toString().trim()))

                            } else {

                                input.setTextColor(Color.parseColor("#${s.toString().trim()}"))

                            }
                        } else if (s.length < 7) {

                            input.setTextColor(Color.parseColor("#212121"))
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        positiveButton(text = "Ok") {

            val hexCode = input?.text.toString().trim()

            if (hexCode.startsWith("#")) {

                applyColor(activity, this@openDialogForWritingHexColor, textView, hexCode)
            } else {

                applyColor(activity, this@openDialogForWritingHexColor, textView, "#$hexCode")
            }

        }
    }
}


fun TextView.strikeThrough(textToBeStriked: String) {

    val spannableStringBuilder = SpannableStringBuilder(textToBeStriked)
    val strikeThroughSpan = StrikethroughSpan()

    spannableStringBuilder.setSpan(
            strikeThroughSpan,
            0,
            textToBeStriked.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannableStringBuilder
}

fun TextView.changeTextColor(context: Context, color: Int) {

    this.setTextColor(ContextCompat.getColor(context, color))
}

@SuppressLint("SetTextI18n")
fun TextView.setDateInTextView(
    timeStamp: Long?,
    pattern: String = "dd-MM-yyyy",
    startingText: String = ""
) {

    this.text = "$startingText${
        WorkingWithDateAndTime().convertMillisecondsToDateAndTimePattern(
            timeStamp, pattern
        )
    }"

}

fun EditText.removeFocus() {

    if (this.hasFocus()) {

        this.clearFocus()
    }

}

fun Int.convertToHexString(): String {

    return java.lang.String.format("#%06X", 0xFFFFFF and this)
}

fun Long.toStringM(radix: Int = 0): String {

    val values = arrayOf(
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z",
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "!",
            "@",
            "#",
            "$",
            "%",
            "^",
            "&"
    )
    var str = ""
    var d = this
    var r: Int

    if (radix in 1..69) {

        if (d <= 0) {
            return d.toString()
        }

        while (d != 0L) {

            r = (d % radix).toInt()
            d /= radix
            str = values[r] + str
        }

        return str
    }

    return d.toString()
}







