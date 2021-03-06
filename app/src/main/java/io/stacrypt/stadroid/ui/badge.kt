package io.stacrypt.stadroid.ui

import com.matrixxun.starry.badgetextview.MaterialBadgeTextView
import io.stacrypt.stadroid.R
import org.jetbrains.anko.backgroundColorResource

fun MaterialBadgeTextView.showError(message: String) {
    text = message
    backgroundColorResource = R.color.real_red
}

fun MaterialBadgeTextView.showWarning(message: String) {
    text = message
    backgroundColorResource = R.color.yellow
}

fun MaterialBadgeTextView.showSuccess(message: String) {
    text = message
    backgroundColorResource = R.color.real_green
}