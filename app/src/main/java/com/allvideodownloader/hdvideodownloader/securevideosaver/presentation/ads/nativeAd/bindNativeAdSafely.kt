package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

fun NativeAdView.bindNativeAdSafely(
    nativeAd: NativeAd
) {
    val headlineTextView = headlineView as? TextView
    val bodyTextView = bodyView as? TextView
    val ctaButton = callToActionView as? Button
    val iconImageView = iconView as? ImageView
    val advertiserTextView = advertiserView as? TextView
    val ratingBar = starRatingView as? RatingBar
    val media = mediaView as? MediaView

    headlineTextView?.text = nativeAd.headline.orEmpty()

    if (nativeAd.body.isNullOrBlank()) {
        bodyTextView?.visibility = View.GONE
    } else {
        bodyTextView?.visibility = View.VISIBLE
        bodyTextView?.text = nativeAd.body
    }

    if (nativeAd.callToAction.isNullOrBlank()) {
        ctaButton?.visibility = View.GONE
    } else {
        ctaButton?.visibility = View.VISIBLE
        ctaButton?.text = nativeAd.callToAction
    }

    val icon = nativeAd.icon
    if (icon == null) {
        iconImageView?.visibility = View.GONE
    } else {
        iconImageView?.visibility = View.VISIBLE
        iconImageView?.setImageDrawable(icon.drawable)
    }

    if (nativeAd.advertiser.isNullOrBlank()) {
        advertiserTextView?.visibility = View.GONE
    } else {
        advertiserTextView?.visibility = View.VISIBLE
        advertiserTextView?.text = nativeAd.advertiser
    }

    val starRating = nativeAd.starRating
    if (starRating == null) {
        ratingBar?.visibility = View.GONE
    } else {
        ratingBar?.visibility = View.VISIBLE
        ratingBar?.rating = starRating.toFloat()
    }

    media?.setMediaContent(nativeAd.mediaContent)

    setNativeAd(nativeAd)
}