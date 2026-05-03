package com.app.videodownloader.presentation.ads.nativeAd

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdStyle
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun NativeAdCard(
    nativeAd: NativeAd,
    config: NativeAdConfig,
    style: NativeAdStyle,
    modifier: Modifier = Modifier
) {
    /*
     * Important:
     * If style changes from Small -> Medium -> Large through Remote Config,
     * the XML layout must be recreated.
     */
    key(style) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val nativeAdView = LayoutInflater
                    .from(context)
                    .inflate(
                        style.toNativeAdLayoutRes(),
                        null,
                        false
                    ) as NativeAdView

                nativeAdView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                /*
                 * Register views once for this inflated XML.
                 * Binding the actual NativeAd happens inside update.
                 */
                nativeAdView.registerNativeAdAssetViews()
                nativeAdView.applyNativeAdStyle(config)

                nativeAdView
            },
            update = { nativeAdView ->
                nativeAdView.applyNativeAdStyle(config)

                /*
                 * AndroidView.update can run many times during recomposition.
                 * Avoid rebinding the same NativeAd again and again.
                 */
                val currentAdIdentity = System.identityHashCode(nativeAd)
                val alreadyBoundAdIdentity =
                    nativeAdView.getTag(R.id.tag_bound_native_ad) as? Int

                if (alreadyBoundAdIdentity != currentAdIdentity) {
                    nativeAdView.bindNativeAdFromXml(nativeAd)
                    nativeAdView.setTag(
                        R.id.tag_bound_native_ad,
                        currentAdIdentity
                    )
                }
            }
        )
    }
}

@LayoutRes
private fun NativeAdStyle.toNativeAdLayoutRes(): Int {
    return when (this) {
        NativeAdStyle.Small -> R.layout.native_ad_small
        NativeAdStyle.Medium -> R.layout.native_ad_medium
        NativeAdStyle.Large -> R.layout.native_ad_large_full_page
    }
}