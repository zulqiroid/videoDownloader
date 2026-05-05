package com.app.videodownloader.presentation.screens.download.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.CancelDownloadUseCase
import com.app.videodownloader.domain.usecases.GetDownloadedFilesUseCase
import com.app.videodownloader.domain.usecases.ObserveDownloadsUseCase
import com.app.videodownloader.domain.usecases.PauseDownloadUseCase
import com.app.videodownloader.domain.usecases.ResumeDownloadUseCase
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.app.videodownloader.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
import com.app.videodownloader.presentation.screens.download.events.DownloadEvents
import com.app.videodownloader.presentation.screens.download.states.DownloadState
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.states.toUiItem
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
    private val observeNativeAdPoolsUseCase: ObserveNativeAdPoolsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadState())
    val state = _state.asStateFlow()

    init {
        observePremiumStatus()
        loadLocalFiles()
        observeDownloads()
        observeNativeAdPools()
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

    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _state.update { currentState ->
                        currentState.copy(
                            isPremiumUser = isPremium,
                            nativeAds = if (isPremium) emptyMap() else currentState.nativeAds,
                            nativeAdPools = if (isPremium) emptyMap() else currentState.nativeAdPools
                        )
                    }

                    if (!isPremium) {
                        loadNativeSlotsForAllTabs()
                    } else {
                        Log.d(TAG, "Download native ads skipped: premium user")
                    }
                }
        }
    }

    fun updateTab(tab: DownloadTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }

        loadNativeSlotsForTab(tab)
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

                loadNativeSlotsForAllTabs()
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

            loadNativeSlotsForAllTabs()
        }
    }

    private fun observeNativeAdPools() {
        viewModelScope.launch {
            observeNativeAdPoolsUseCase().collect { nativeAdPools ->
                _state.update { currentState ->
                    if (currentState.isPremiumUser) {
                        currentState.copy(nativeAdPools = emptyMap())
                    } else {
                        currentState.copy(nativeAdPools = nativeAdPools)
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

                loadNativeSlotsForAllTabs()
            }
        }
    }

    private fun loadNativeSlotsForAllTabs() {
        loadNativeSlotsForTab(DownloadTab.DOWNLOADING)
        loadNativeSlotsForTab(DownloadTab.COMPLETED)
    }

    private fun loadNativeSlotsForTab(
        tab: DownloadTab
    ) {
        val currentState = _state.value

        if (currentState.isPremiumUser) {
            Log.d(TAG, "Download native ad load skipped: premium user. tab=$tab")
            return
        }

        val placementKey = when (tab) {
            DownloadTab.DOWNLOADING -> NativeAdConfig.DOWNLOAD_DOWNLOADING_LIST
            DownloadTab.COMPLETED -> NativeAdConfig.DOWNLOAD_COMPLETED_LIST
        }

        val totalItems = when (tab) {
            DownloadTab.DOWNLOADING -> currentState.downloading.size
            DownloadTab.COMPLETED -> currentState.completed.size
        }

        val placementConfig = currentState.nativeAdConfig.placement(placementKey)

        if (placementConfig == null) {
            Log.d(TAG, "Download native skipped: placement disabled or missing. placement=$placementKey")
            return
        }

        val slotKeys = if (totalItems <= 0) {
            listOf(EMPTY_STATE_SLOT_KEY)
        } else {
            NativeAdSlotHelper.insertionSlotKeys(
                totalItems = totalItems,
                config = placementConfig
            )
        }

        Log.d(
            TAG,
            "Download native slots to load. placement=$placementKey slots=$slotKeys totalItems=$totalItems"
        )

        slotKeys.forEach { slotKey ->
            loadNativeAdUseCase(
                placementKey = placementKey,
                slotKey = slotKey,
                onStateChanged = { adState ->
                    Log.d(
                        TAG,
                        "Download native ad state. placement=$placementKey slot=$slotKey state=$adState"
                    )
                }
            )
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

    companion object {
        private const val TAG = "DownloadViewModel"
        const val EMPTY_STATE_SLOT_KEY = "empty"
    }
}