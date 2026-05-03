package com.app.videodownloader.presentation.ads.nativeAd.componants

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.app.videodownloader.domain.model.ads.NativeAdConfig

object NativeAdDrawableFactory {

    fun roundedBackground(
        colorString: String,
        cornerRadiusDp: Int,
        context: Context
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(colorString.toAndroidColor())
            cornerRadius = cornerRadiusDp.dpToPx(context).toFloat()
        }
    }

    fun roundedStrokeBackground(
        fillColorString: String,
        strokeColorString: String,
        strokeWidthDp: Int,
        cornerRadiusDp: Int,
        context: Context
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fillColorString.toAndroidColor())
            cornerRadius = cornerRadiusDp.dpToPx(context).toFloat()
            setStroke(
                strokeWidthDp.dpToPx(context),
                strokeColorString.toAndroidColor()
            )
        }
    }
}

fun String.toAndroidColor(): Int {
    return runCatching {
        trim().toColorInt()
    }.getOrDefault(Color.WHITE)
}

fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

fun Int.dp(context: Context): Int {
    return dpToPx(context)
}

fun createAdBadgeTextView(
    context: Context,
    config: NativeAdConfig
): TextView {
    return TextView(context).apply {
        text = "Ad"
        textSize = 10f
        gravity = Gravity.CENTER
        includeFontPadding = false
        setPadding(
            7.dp(context),
            3.dp(context),
            7.dp(context),
            3.dp(context)
        )
        setTextColor(config.adAttributionTextColor.toAndroidColor())
        background = NativeAdDrawableFactory.roundedStrokeBackground(
            fillColorString = config.adAttributionBackgroundColor,
            strokeColorString = config.adAttributionBorderColor,
            strokeWidthDp = 1,
            cornerRadiusDp = config.adBadgeCornerRadiusDp,
            context = context
        )
    }
}

fun createHeadlineTextView(
    context: Context,
    config: NativeAdConfig,
    textSizeSp: Float,
    maxLines: Int
): TextView {
    return TextView(context).apply {
        textSize = textSizeSp
        this.maxLines = maxLines
        includeFontPadding = false
        setTextColor(config.headlineTextColor.toAndroidColor())
        setTypeface(typeface, Typeface.BOLD)
    }
}

fun createBodyTextView(
    context: Context,
    config: NativeAdConfig,
    textSizeSp: Float,
    maxLines: Int
): TextView {
    return TextView(context).apply {
        textSize = textSizeSp
        this.maxLines = maxLines
        includeFontPadding = false
        setTextColor(config.bodyTextColor.toAndroidColor())
    }
}

fun createCtaButton(
    context: Context,
    config: NativeAdConfig,
    textSizeSp: Float,
    horizontalPaddingDp: Int,
    verticalPaddingDp: Int,
    cornerRadiusDp: Int
): Button {
    return Button(context).apply {
        textSize = textSizeSp
        minHeight = 0
        minWidth = 0
        minimumHeight = 0
        minimumWidth = 0
        includeFontPadding = false
        isAllCaps = false
        gravity = Gravity.CENTER
        setPadding(
            horizontalPaddingDp.dp(context),
            verticalPaddingDp.dp(context),
            horizontalPaddingDp.dp(context),
            verticalPaddingDp.dp(context)
        )
        setTextColor(config.ctaTextColor.toAndroidColor())
        background = NativeAdDrawableFactory.roundedBackground(
            colorString = config.ctaBackgroundColor,
            cornerRadiusDp = cornerRadiusDp,
            context = context
        )
    }
}

fun createIconImageView(
    context: Context,
    sizeDp: Int
): ImageView {
    return ImageView(context).apply {
        visibility = View.GONE
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
}