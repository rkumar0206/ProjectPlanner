package com.rohitthebest.projectplanner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.rohitthebest.projectplanner.R


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

fun Int.convertToHexString(): String {

    return java.lang.String.format("#%06X", 0xFFFFFF and this)
}






