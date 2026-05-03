package com.app.videodownloader.presentation.ads.nativeAd

import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.presentation.ads.nativeAd.componants.NativeAdDrawableFactory
import com.app.videodownloader.presentation.ads.nativeAd.componants.toAndroidColor
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

fun NativeAdView.applyNativeAdStyle(
    config: NativeAdConfig
) {
    findViewById<View?>(R.id.ad_root)?.background =
        NativeAdDrawableFactory.roundedStrokeBackground(
            fillColorString = config.containerBackgroundColor,
            strokeColorString = config.containerBorderColor,
            strokeWidthDp = config.containerBorderWidthDp,
            cornerRadiusDp = config.cornerRadiusDp,
            context = context
        )

    findViewById<FrameLayout?>(R.id.ad_media_container)?.background =
        NativeAdDrawableFactory.roundedBackground(
            colorString = config.mediaBackgroundColor,
            cornerRadiusDp = config.mediaCornerRadiusDp,
            context = context
        )

    findViewById<TextView?>(R.id.ad_badge)?.apply {
        text = "Ad"
        visibility = View.VISIBLE
        setTextColor(config.adAttributionTextColor.toAndroidColor())
        background = NativeAdDrawableFactory.roundedStrokeBackground(
            fillColorString = config.adAttributionBackgroundColor,
            strokeColorString = config.adAttributionBorderColor,
            strokeWidthDp = 1,
            cornerRadiusDp = config.adBadgeCornerRadiusDp,
            context = context
        )
    }

    findViewById<TextView?>(R.id.ad_headline)?.apply {
        setTextColor(config.headlineTextColor.toAndroidColor())
        setTypeface(typeface, Typeface.BOLD)
    }

    findViewById<TextView?>(R.id.ad_body)?.apply {
        setTextColor(config.bodyTextColor.toAndroidColor())
    }

    findViewById<TextView?>(R.id.ad_advertiser)?.apply {
        setTextColor(config.bodyTextColor.toAndroidColor())
    }

    findViewById<Button?>(R.id.ad_call_to_action)?.apply {
        isAllCaps = false
        setTextColor(config.ctaTextColor.toAndroidColor())
        background = NativeAdDrawableFactory.roundedBackground(
            colorString = config.ctaBackgroundColor,
            cornerRadiusDp = config.ctaCornerRadiusDp,
            context = context
        )
    }

    findViewById<RatingBar?>(R.id.ad_stars)?.apply {
        progressDrawable?.setTint(config.starRatingColor.toAndroidColor())
    }
}

fun NativeAdView.registerNativeAdAssetViews() {
    /*
     * Every asset view is optional because your Small layout does not contain
     * every possible native asset, while Medium/Large layouts contain more.
     */

    findViewById<MediaView?>(R.id.ad_media)?.let { media ->
        mediaView = media
    }

    findViewById<ImageView?>(R.id.ad_icon)?.let { icon ->
        iconView = icon
    }

    findViewById<TextView?>(R.id.ad_headline)?.let { headline ->
        headlineView = headline
    }

    findViewById<TextView?>(R.id.ad_body)?.let { body ->
        bodyView = body
    }

    findViewById<Button?>(R.id.ad_call_to_action)?.let { callToAction ->
        callToActionView = callToAction
    }

    findViewById<TextView?>(R.id.ad_advertiser)?.let { advertiser ->
        advertiserView = advertiser
    }

    findViewById<RatingBar?>(R.id.ad_stars)?.let { rating ->
        starRatingView = rating
    }
}

fun NativeAdView.bindNativeAdFromXml(
    nativeAd: NativeAd
) {
    bindHeadline(nativeAd)
    bindBody(nativeAd)
    bindCallToAction(nativeAd)
    bindIcon(nativeAd)
    bindAdvertiser(nativeAd)
    bindStarRating(nativeAd)
    bindMedia(nativeAd)

    /*
     * Must be called after asset views are populated.
     * This lets Google Mobile Ads SDK handle click/impression tracking.
     */
    setNativeAd(nativeAd)
}

private fun NativeAdView.bindHeadline(
    nativeAd: NativeAd
) {
    val headlineTextView = headlineView as? TextView

    headlineTextView?.apply {
        text = nativeAd.headline.orEmpty()
        visibility = if (nativeAd.headline.isNullOrBlank()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}

private fun NativeAdView.bindBody(
    nativeAd: NativeAd
) {
    val bodyTextView = bodyView as? TextView

    bodyTextView?.apply {
        if (nativeAd.body.isNullOrBlank()) {
            text = ""
            visibility = View.GONE
        } else {
            text = nativeAd.body
            visibility = View.VISIBLE
        }
    }
}

private fun NativeAdView.bindCallToAction(
    nativeAd: NativeAd
) {
    val ctaButton = callToActionView as? Button

    ctaButton?.apply {
        if (nativeAd.callToAction.isNullOrBlank()) {
            text = ""
            visibility = View.GONE
        } else {
            text = nativeAd.callToAction
            visibility = View.VISIBLE
        }
    }
}

private fun NativeAdView.bindIcon(
    nativeAd: NativeAd
) {
    val iconImageView = iconView as? ImageView
    val icon = nativeAd.icon

    iconImageView?.apply {
        if (icon == null) {
            setImageDrawable(null)
            visibility = View.GONE
        } else {
            setImageDrawable(icon.drawable)
            visibility = View.VISIBLE
        }
    }
}

private fun NativeAdView.bindAdvertiser(
    nativeAd: NativeAd
) {
    val advertiserTextView = advertiserView as? TextView

    advertiserTextView?.apply {
        if (nativeAd.advertiser.isNullOrBlank()) {
            text = ""
            visibility = View.GONE
        } else {
            text = nativeAd.advertiser
            visibility = View.VISIBLE
        }
    }
}

private fun NativeAdView.bindStarRating(
    nativeAd: NativeAd
) {
    val ratingBar = starRatingView as? RatingBar
    val starRating = nativeAd.starRating

    ratingBar?.apply {
        if (starRating == null) {
            rating = 0f
            visibility = View.GONE
        } else {
            rating = starRating.toFloat()
            visibility = View.VISIBLE
        }
    }
}

private fun NativeAdView.bindMedia(
    nativeAd: NativeAd
) {
    val media = mediaView as? MediaView

    media?.apply {
        setMediaContent(nativeAd.mediaContent)
        visibility = if (nativeAd.mediaContent == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}