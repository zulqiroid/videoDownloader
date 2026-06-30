package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadStatus
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.CancelDownloadUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetDownloadedFilesUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ObserveDownloadsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.PauseDownloadUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ResumeDownloadUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdSlotHelper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.events.DownloadEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadTab
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadUiItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.toUiItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.viewModel.PlayerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val observeDownloadsUseCase: ObserveDownloadsUseCase,
    private val getDownloadedFilesUseCase: GetDownloadedFilesUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val resumeDownloadUseCase: ResumeDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase,

    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadState())
    val state = _state.asStateFlow()

    init {
        loadLocalFiles()
        observeDownloads()

        observePremiumStatus()
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onEvent(event: DownloadEvents) {
        when (event) {
            is DownloadEvents.OnPauseDownloadingClicked -> {
                viewModelScope.launch {
                    pauseDownloadUseCase(event.id)
                }
            }

            is DownloadEvents.OnResumeDownloadingClicked -> {
                viewModelScope.launch {
                    resumeDownloadUseCase(event.id)
                }
            }

            is DownloadEvents.OnDeleteDownloadingClicked -> {
                viewModelScope.launch {
                    cancelDownloadUseCase(event.id)
                }
            }
        }
    }

 
    fun updateTab(tab: DownloadTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            observeDownloadsUseCase().collect { items ->
                val (downloading, activeCompleted) = mapToUi(items)

                _state.update { current ->
                    val mergedCompleted = (current.completed + activeCompleted)
                        .distinctBy { it.id }

                    current.copy(
                        downloading = downloading,
                        completed = mergedCompleted
                    )
                }
            }
        }
    }

    private fun loadLocalFiles() {
        viewModelScope.launch {
            val files = getDownloadedFilesUseCase()
            val uiItems = files.map { it.toUiItem() }

            _state.update {
                it.copy(
                    completed = uiItems
                )
            }

         }
    }
 
    private fun mapToUi(
        items: List<DownloadItem>
    ): Pair<List<DownloadUiItem>, List<DownloadUiItem>> {
        val downloading = mutableListOf<DownloadUiItem>()
        val completed = mutableListOf<DownloadUiItem>()

        items.forEach { item ->
            val totalMB = item.totalBytes / (1024f * 1024f)
            val speedMB = item.speedBytesPerSec / (1024f * 1024f)

            val etaText = formatTime(item.lastEtaSeconds)

            val finalTimeText = when (item.status) {
                DownloadStatus.DOWNLOADING -> {
                    etaText?.let { "Est. $it left" } ?: "Calculating..."
                }

                DownloadStatus.PAUSED -> {
                    "Paused"
                }

                DownloadStatus.FAILED -> {
                    "Failed"
                }

                DownloadStatus.SUCCESS -> {
                    "Completed"
                }
            }

            val ui = DownloadUiItem(
                id = item.id,
                title = item.fileName,
                progress = item.progress,
                status = item.status,
                sizeText = "${"%.1f".format(speedMB)} MB/s • ${"%.1f".format(totalMB)} MB",
                timeText = finalTimeText,
                filePath = item.filePath
            )

            when (item.status) {
                DownloadStatus.SUCCESS -> completed.add(ui)
                else -> downloading.add(ui)
            }
        }

        return downloading to completed
    }

    private fun formatTime(seconds: Long): String? {
        return when {
            seconds <= 0 -> null
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60} min"
            else -> "${seconds / 3600} hr"
        }
    }



    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _state.update { currentState ->
                        currentState.copy(
                            isPremiumUser = isPremium,
                            nativeAds = if (isPremium) emptyMap() else currentState.nativeAds
                        )
                    }

                    if (!isPremium) {
                        loadVisibleNativeAds()
                    } else {
                        Log.d(TAG, "More native ads skipped: premium user")
                    }
                }
        }
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { nativeAds ->
                _state.update { currentState ->
                    if (currentState.isPremiumUser) {
                        currentState.copy(nativeAds = emptyMap())
                    } else {
                        currentState.copy(nativeAds = nativeAds)
                    }
                }
            }
        }
    }

    private fun observeNativeAdConfig() {
        viewModelScope.launch {
            observeNativeAdConfigUseCase().collect { config ->
                _state.update {
                    it.copy(nativeAdConfig = config)
                }

                loadVisibleNativeAds()
            }
        }
    }

    private fun loadVisibleNativeAds() {
        if (_state.value.isPremiumUser) {
            Log.d(TAG, "More native ad load skipped: premium user")
            return
        }

        loadIfEnabled(NativeAdConfig.DOWNLOAD_LIST)
    }

    private fun loadIfEnabled(
        placementKey: String
    ) {
        val currentState = _state.value

        if (currentState.isPremiumUser) {
            Log.d(TAG, "More native ad skipped: premium user. placement=$placementKey")
            return
        }

        val config = currentState.nativeAdConfig

        if (config.placement(placementKey) == null) {
            return
        }

        loadNativeAdUseCase(
            placementKey = placementKey,
            onStateChanged = { state ->
                Log.d(TAG, "More native ad state. placement=$placementKey state=$state")

                if (state is AdState.LoadFailed) {
                    Log.d(TAG, "More native ad failed. placement=$placementKey")
                }
            }
        )
    }

    companion object {
        private const val TAG = "DownloadViewModel"
        const val EMPTY_STATE_SLOT_KEY = "empty"
    }
}