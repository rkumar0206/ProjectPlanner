package com.rohitthebest.projectplanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants.EDIT_TEXT_EMPTY_MESSAGE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.utils.Functions.Companion.applyColor
import com.rohitthebest.projectplanner.utils.Functions.Companion.showKeyboard
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import es.dmoral.toasty.Toasty
import yuku.ambilwarna.AmbilWarnaDialog


enum class ToastyType {

    SUCCESS,
    ERROR,
    INFO,
    WARNING,
    NORMAL
}

fun View.showViewBySlidingAnimation(duration: Long = 600L) {

    this.show()

    val animation =
        AnimationUtils.loadAnimation(context, R.anim.add_topic_button_visible_anim).apply {

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

fun View.openColorPicker(context: Context, defaultColor: Int, textView: TextView? = null, editText: EditText? = null) {

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
                        editText?.setText(color.convertToHexString())
                    }
                }
        ).show()
    } catch (e: Exception) {

        e.printStackTrace()
    }
}

fun View.setBackgroundColorByHexCode(hexCode: String) {

    if (hexCode.isValidHexCode()) {

        this.setBackgroundColor(Color.parseColor(hexCode))
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

                            val hexCode = if (s.toString().trim().startsWith("#")) {

                                s.toString().trim()
                            } else {

                                "#${s.toString().trim()}"
                            }

                            if (hexCode.isValidHexCode()) {

                                input.setTextColor(Color.parseColor(hexCode))
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

            val hexCode = if (input?.text.toString().trim().startsWith("#")) {

                input?.text.toString().trim()
            } else {
                "#${input?.text.toString().trim()}"
            }

            if (hexCode.isValidHexCode()) {

                applyColor(activity, this@openDialogForWritingHexColor, textView, hexCode = hexCode)
            } else {

                showToast(context, "incorrect hex code")
            }
        }
    }
}

fun CheckBox.strikeThrough(textToBeStriked: String) {

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

fun TextView.boldSpan(textToBeBold: String, startIndex: Int = 0, endIndex: Int = textToBeBold.length - 1) {

    try {
        val spannableStringBuilder = SpannableStringBuilder(textToBeBold)

        val boldSpan = StyleSpan(Typeface.BOLD)

        spannableStringBuilder.setSpan(
                boldSpan,
                startIndex,
                endIndex + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        this.text = spannableStringBuilder

    } catch (e: IndexOutOfBoundsException) {

        e.printStackTrace()
    }
}

fun CheckBox.changeTextColor(context: Context, color: Int) {

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

fun String.isValidHexCode(): Boolean {

    val pattern = "^#[0-9A-F]{6}$"

    return Regex(pattern, RegexOption.IGNORE_CASE).matches(this)
}

fun String.showToasty(
    context: Context,
    type: ToastyType = ToastyType.SUCCESS,
    withIcon: Boolean = true,
    duration: Int = Toast.LENGTH_SHORT
) {

    when (type) {

        ToastyType.SUCCESS -> {
            Toasty.success(context, this, duration, withIcon).show()
        }
        ToastyType.ERROR -> {
            Toasty.error(context, this, duration, withIcon).show()
        }
        ToastyType.WARNING -> {
            Toasty.warning(context, this, duration, withIcon).show()
        }
        ToastyType.INFO -> {
            Toasty.info(context, this, duration, withIcon).show()
        }
        ToastyType.NORMAL -> {
            Toasty.normal(context, this, duration).show()
        }

    }
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








