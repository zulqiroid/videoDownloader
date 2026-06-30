package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DeleteMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MoveMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.NotificationSettings
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RenameMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.DeleteMediaFileUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.FetchVideoUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPremiumIconVisibility
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPrivacyPolicyLink
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.MoveMediaFileUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.NotificationSettingsUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.RenameMediaFileUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.StartDownloadUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.PolicyUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ObserveMediaPlaybackStateUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.FloatingVideoPlayerCoordinator
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events.FileDialogIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events.MainEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events.MainNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events.NotificationEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.BottomNavItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.MainState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.utils.toProfessionalSearchQuery
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.firstOrNull

class MainViewModel(
    private val policyUseCases: PolicyUseCases,
    private val fetchVideoUseCase: FetchVideoUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val notificationSettingsUseCases: NotificationSettingsUseCases,
    private val renameMediaFileUseCase: RenameMediaFileUseCase,
    private val deleteMediaFileUseCase: DeleteMediaFileUseCase,
    private val moveMediaFileUseCase: MoveMediaFileUseCase,
    private val loadInterstitialAd: LoadInterstitialAdUseCase,
    private val showInterstitialAd: ShowInterstitialAdUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    private val getRCPremiumIconVisibility: GetRCPremiumIconVisibility,
    private val getRCPrivacyPolicyLink: GetRCPrivacyPolicyLink,
    private val observeMediaPlaybackStateUseCase: ObserveMediaPlaybackStateUseCase,
    private val mediaPlaybackController: MediaPlaybackController,
    private val floatingVideoPlayerCoordinator: FloatingVideoPlayerCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MainNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    init {
        observePlaybackState()
        observeFloatingVideoMiniPlayerState()
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .collect { isPremiumUser ->
                    _state.update {
                        it.copy(isPremiumUser = isPremiumUser)
                    }
                }
        }
        viewModelScope.launch {
            getRCPremiumIconVisibility().let { isPremiumIconVisible ->
                _state.update { state ->
                    state.copy(isPremiumIconVisible = isPremiumIconVisible)
                }
            }
        }


        observeNotificationSettings()
        viewModelScope.launch {
            delay(100)

            if (!_state.value.isPremiumUser) {
                preloadAd()
            }
        }

        viewModelScope.launch {
            getRCPrivacyPolicyLink().let { privacyPolicyLink ->
                _state.update { state ->
                    state.copy(privacyPolicyLink = privacyPolicyLink)
                }
            }
        }
        checkPolicy()
    }


    fun onEvent(event: MainEvents) {
        when (event) {
            is MainEvents.OnTabSelected -> {
                switchTabWithInterstitial(
                    tab = event.tab,
                    activity = event.activity
                )
            }

            MainEvents.OnPolicyDialogueAcceptClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            showPolicyDialogue = false
                        )
                    }
                    policyUseCases.setPolicyAcceptedUseCase(true)
                }
            }

            is MainEvents.FetchUrl -> {
                fetchVideo(event.url)
            }

            MainEvents.OnDismissSheet -> {
                _state.update {
                    it.copy(showDownloadSheet = false)
                }
            }

            is MainEvents.OnOptionSelected -> {
                _state.update {
                    it.copy(selectedOptionIndex = event.index)
                }
            }

            is MainEvents.OnDownLoadInBottomSheetClicked -> {
                _state.update {
                    it.copy(showDownloadSheet = false)
                }
                onQualitySelected(
                    event.url
                )
            }

            MainEvents.OnDismissProgressDialogue -> {
                _state.update {
                    it.copy(
                        showDownloadProgressDialogue = false
                    )
                }
            }

            MainEvents.OnViewProgressInProgressDialogueCLicked -> {
                _state.update {
                    it.copy(
                        selectedTab = BottomNavItem.Download,
                        showDownloadProgressDialogue = false
                    )
                }
            }

            MainEvents.OnBackClicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = true
                    )
                }
            }

            MainEvents.OnDialogueCancelCLicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = false
                    )
                }
            }

            MainEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            showExitDialogue = false
                        )
                    }
                    _navEvents.emit(MainNavEvents.ExitApp)
                }
            }

            is MainEvents.OnReelSelected -> {
                viewModelScope.launch {

                    _state.update {
                        it.copy(selectedTab = BottomNavItem.Reels)
                    }
                    delay(1000)

                    _state.update {
                        it.copy(selectedReel = null)
                    }
                }

            }

            is MainEvents.OnSocialPlatformSelected -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(selectedTab = BottomNavItem.Social)
                    }
                    delay(1000)
                    _state.update {
                        it.copy(

                        )
                    }
                }

            }

            is MainEvents.OnMediaItemInPLayerClick -> {
                _state.update {
                    it.copy(
                        showPlayerDialogue = true,
                        playerMediaItem = event.item
                    )
                }
            }

            MainEvents.OnFeedbackClicked -> {
                openFeedbackDialog()
            }

            MainEvents.OnFeedbackDismissed -> {
                dismissFeedbackDialog()
            }

            is MainEvents.OnFeedbackValueChanged -> {
                _state.update {
                    it.copy(
                        feedbackMessage = event.value,
                        feedbackError = null
                    )
                }
            }

            MainEvents.OnFeedbackSubmitClicked -> {
                submitFeedback()
            }

            MainEvents.OnRateUsClicked -> {
                openRateUsDialog()
            }

            is MainEvents.OnRateUsSelected -> {
                _state.update {
                    it.copy(
                        selectedRating = event.rating.coerceIn(1, 5),
                        rateUsError = null
                    )
                }
            }

            MainEvents.OnRateUsDismissed -> {
                dismissRateUsDialog()
            }

            MainEvents.OnRateNowClicked -> {
                submitRating()
            }

            MainEvents.OnFetchFailedRetryClicked -> {
                retryFailedFetch()
            }

            MainEvents.OnFetchFailedPasteNewLinkClicked -> {
                dismissFetchFailedDialog()
            }

            MainEvents.OnFetchFailedHelpClicked -> {
                _state.update {
                    it.copy(showFetchFailedDialog = false)
                }

                // Optional: navigate to your guide later.
                // You can emit nav event if you want.
            }

            MainEvents.OnFetchFailedDismissed -> {
                dismissFetchFailedDialog()
            }

            is MainEvents.OnOpenMediaPlayerClicked -> {
                openMediaPlayerWithInterstitial(
                    mediaList = event.mediaList,
                    startIndex = event.startIndex,
                    activity = event.activity
                )
            }

            is MainEvents.OnBackNavigationClicked -> {
                navigateBackToHomeWithInterstitial(
                    activity = event.activity
                )
            }

            MainEvents.OnAppLanguageCLicked -> {
                viewModelScope.launch {

                    _navEvents.emit(MainNavEvents.NavigateToAppLanguageSRC)
                }
            }

            is MainEvents.OnMediaPermissionResult -> {
                _state.update {
                    it.copy(
                        isMediaPermissionGranted = event.granted,
                        showMediaPermissionDialog = !event.granted,
                        shouldOpenMediaPermissionSettings = event.permanentlyDenied
                    )
                }
            }

            MainEvents.OnDownloadSearchClicked -> {
                _state.update {
                    it.copy(
                        isDownloadSearchActive = true
                    )
                }
            }

            MainEvents.OnDownloadSearchClosed -> {
                _state.update {
                    it.copy(
                        isDownloadSearchActive = false,
                        downloadSearchQuery = ""
                    )
                }
            }

            is MainEvents.OnDownloadSearchQueryChanged -> {
                _state.update {
                    it.copy(
                        downloadSearchQuery = event.query.toProfessionalSearchQuery()
                    )
                }
            }

            MainEvents.OnMediaPermissionDialogDismissed -> {
                _state.update {
                    it.copy(
                        showMediaPermissionDialog = false
                    )
                }
            }

            MainEvents.OnMediaPermissionRequestClicked -> {
                _state.update {
                    it.copy(
                        showMediaPermissionDialog = false
                    )
                }
            }

            MainEvents.OnMediaPermissionSettingsClicked -> {
                _state.update {
                    it.copy(
                        showMediaPermissionDialog = false
                    )
                }
            }

            is MainEvents.OnNotificationPermissionResult -> {
                _state.update {
                    it.copy(
                        isNotificationPermissionGranted = event.granted
                    )
                }
            }

            MainEvents.OnPlayerSearchClicked -> {
                _state.update {
                    it.copy(
                        isPlayerSearchActive = true
                    )
                }
            }

            MainEvents.OnPlayerSearchClosed -> {
                _state.update {
                    it.copy(
                        isPlayerSearchActive = false,
                        playerSearchQuery = ""
                    )
                }
            }

            is MainEvents.OnPlayerSearchQueryChanged -> {
                _state.update {
                    it.copy(
                        playerSearchQuery = event.query.toProfessionalSearchQuery()
                    )
                }
            }

            MainEvents.OnPrivacyPolicyClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(MainNavEvents.NavigateToPrivacyPolicy)
                }
            }

            MainEvents.OnAudioMiniPlayerPlayPauseClicked -> {
                mediaPlaybackController.togglePlayPause()
            }

            MainEvents.OnAudioMiniPlayerPreviousClicked -> {
                mediaPlaybackController.skipToPrevious()
            }

            MainEvents.OnAudioMiniPlayerNextClicked -> {
                mediaPlaybackController.skipToNext()
            }

            MainEvents.OnAudioMiniPlayerCloseClicked -> {
                mediaPlaybackController.stopAndClear()
            }

            MainEvents.OnAudioMiniPlayerClicked -> {
                openCurrentMiniPlayerInFullPlayer()
            }

            MainEvents.OnFloatingVideoMiniPlayerClicked -> {
                openFloatingVideoInFullPlayer()
            }

            MainEvents.OnFloatingVideoMiniPlayerPlayPauseClicked -> {
                floatingVideoPlayerCoordinator.togglePlayPause()
            }

            MainEvents.OnFloatingVideoMiniPlayerCloseClicked -> {
                floatingVideoPlayerCoordinator.closeAndRelease()
            }
        }
    }


    private fun observePlaybackState() {
        viewModelScope.launch {
            observeMediaPlaybackStateUseCase()
                .collect { playbackState ->
                    _state.update { currentState ->
                        currentState.copy(
                            mediaPlaybackState = playbackState
                        )
                    }
                }
        }
    }

    private fun openCurrentMiniPlayerInFullPlayer() {
        val playbackState = _state.value.mediaPlaybackState

        if (!playbackState.isAudio) return

        val currentMedia = playbackState.currentMedia ?: return
        val queue = playbackState.mediaList

        if (queue.isEmpty()) return

        val resolvedIndex = queue.indexOfFirst { media ->
            media.id == currentMedia.id || media.filePath == currentMedia.filePath
        }.takeIf { index ->
            index >= 0
        } ?: playbackState.currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = queue.lastIndex
        )

        viewModelScope.launch {
            _navEvents.emit(
                MainNavEvents.OpenMediaPlayer(
                    mediaList = queue,
                    startIndex = resolvedIndex
                )
            )
        }
    }


    fun fileDialogueEvent(dialogue: FileDialogIntent) {
        when (dialogue) {
            FileDialogIntent.OnAddToQueueClicked -> {

            }

            FileDialogIntent.OnDeleteClicked -> {
                openDeleteDialog()
            }

            FileDialogIntent.OnDismiss -> {
                _state.update {
                    it.copy(
                        playerMediaItem = null,
                        showPlayerDialogue = false,
                    )
                }
            }

            FileDialogIntent.OnInfoClicked -> {
                val selectedFile = _state.value.playerMediaItem

                _state.update {
                    it.copy(
                        showPlayerDialogue = false,
                        playerMediaItem = null,

                        showFileInfoDialog = selectedFile != null,
                        fileInfoMediaItem = selectedFile
                    )
                }
            }

            FileDialogIntent.OnMoveClicked -> {
                openMoveDestinationPicker()
            }

            FileDialogIntent.OnPlayClicked -> {
                playSelectedFile()
            }

            FileDialogIntent.OnRenameClicked -> {
                openRenameDialog()
            }

            FileDialogIntent.OnShareClicked -> {
                shareSelectedFile()
            }

            FileDialogIntent.OnFileInfoDismiss -> {
                _state.update {
                    it.copy(
                        showFileInfoDialog = false,
                        fileInfoMediaItem = null
                    )
                }
            }

            is FileDialogIntent.OnRenameValueChanged -> {
                _state.update {
                    it.copy(
                        renameDraftName = dialogue.value,
                        renameError = null
                    )
                }
            }

            FileDialogIntent.OnRenameDismiss -> {
                dismissRenameDialog()
            }

            FileDialogIntent.OnRenameConfirmClicked -> {
                renameSelectedFile()
            }

            FileDialogIntent.OnDeleteDismiss -> {
                dismissDeleteDialog()
            }

            FileDialogIntent.OnDeleteConfirmClicked -> {
                deleteSelectedFile()
            }

            FileDialogIntent.OnDeletePermissionGranted -> {
                onDeleteSuccess()
            }

            FileDialogIntent.OnDeletePermissionDenied -> {
                _state.update {
                    it.copy(
                        isDeletingFile = false,
                        deleteFileError = "Delete permission was denied"
                    )
                }
            }

            is FileDialogIntent.OnMoveDestinationSelected -> {
                moveSelectedFile(dialogue.destinationTreeUri)
            }

            FileDialogIntent.OnMoveDestinationSelectionCancelled -> {
                clearMoveState()
            }

            FileDialogIntent.OnMoveDeletePermissionGranted -> {
                onMoveSuccess()
            }

            FileDialogIntent.OnMoveDeletePermissionDenied -> {
                _state.update {
                    it.copy(
                        isMovingFile = false,
                        moveFileError = "File was copied, but original file could not be removed"
                    )
                }
            }
        }
    }

    fun onNotificationEvent(event: NotificationEvents) {
        when (event) {
            NotificationEvents.OnNotificationClicked -> {
                openNotificationDialog()
            }

            NotificationEvents.OnNotificationDialogDismissed -> {
                dismissNotificationDialog()
            }

            NotificationEvents.OnNotificationSaveClicked -> {
                saveNotificationSettings()
            }

            is NotificationEvents.OnDraftDownloadCompleteNotificationChanged -> {
                _state.update {
                    it.copy(
                        draftDownloadCompleteNotificationEnabled = event.enabled
                    )
                }
            }

            is NotificationEvents.OnDraftDownloadFailedNotificationChanged -> {
                _state.update {
                    it.copy(
                        draftDownloadFailedNotificationEnabled = event.enabled
                    )
                }
            }

            is NotificationEvents.OnDraftAppUpdatesNotificationChanged -> {
                _state.update {
                    it.copy(
                        draftAppUpdatesNotificationEnabled = event.enabled
                    )
                }
            }
        }
    }

    private fun checkPolicy() {
        viewModelScope.launch {
            val isPolicyAccepted = policyUseCases.getPolicyAcceptedUseCase().firstOrNull()

            Log.d("MainSRC", " the policy value is : $isPolicyAccepted")

            if (isPolicyAccepted != null && !isPolicyAccepted) {
                _state.update {
                    it.copy(
                        showPolicyDialogue = true
                    )
                }
            }
        }
    }

    private fun fetchVideo(url: String) {
        viewModelScope.launch {
            val cleanUrl = url.trim()

            if (cleanUrl.isBlank()) {
                _state.update {
                    it.copy(
                        urlFetchingLoading = false,
                        showFetchFailedDialog = true,
                        failedFetchUrl = "",
                        fetchErrorMessage = "Please paste a valid video link",
                        error = "Please paste a valid video link"
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    urlFetchingLoading = true,
                    showFetchFailedDialog = false,
                    failedFetchUrl = cleanUrl,
                    fetchErrorMessage = null,
                    error = null
                )
            }

            try {
                val result = fetchVideoUseCase(cleanUrl)

                if (!result.status) {
                    val message = result.errorMessage?.firstOrNull()
                        ?: "We couldn’t fetch this video. Please check the link or try another source."

                    _state.update {
                        it.copy(
                            urlFetchingLoading = false,
                            videoData = null,
                            showDownloadSheet = false,
                            showFetchFailedDialog = true,
                            failedFetchUrl = cleanUrl,
                            fetchErrorMessage = message,
                            error = message
                        )
                    }

                    return@launch
                }

                _state.update {
                    it.copy(
                        urlFetchingLoading = false,
                        videoData = result,
                        showDownloadSheet = true,
                        selectedOptionIndex = 0,
                        showFetchFailedDialog = false,
                        fetchErrorMessage = null,
                        error = null
                    )
                }
            } catch (exception: Exception) {
                val message = exception.message
                    ?: "We couldn’t fetch this video. Please check the link or try another source."

                _state.update {
                    it.copy(
                        urlFetchingLoading = false,
                        videoData = null,
                        showDownloadSheet = false,
                        showFetchFailedDialog = true,
                        failedFetchUrl = cleanUrl,
                        fetchErrorMessage = message,
                        error = message
                    )
                }
            }
        }
    }

    fun onQualitySelected(url: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showDownloadProgressDialogue = true
                )
            }
            startDownloadUseCase(url)
        }
    }


    private fun preloadAd() {
        if (_state.value.isPremiumUser) {
            _adState.value = AdState.Skipped(
                reason = "Interstitial preload skipped: premium user"
            )
            return
        }

        loadInterstitialAd(AdsScreens.Default) { state ->
            _adState.value = state
        }
    }

    private fun observeFloatingVideoMiniPlayerState() {
        viewModelScope.launch {
            floatingVideoPlayerCoordinator.state.collect { floatingVideoState ->
                _state.update { currentState ->
                    currentState.copy(
                        floatingVideoMiniPlayerState = floatingVideoState
                    )
                }
            }
        }
    }

    private fun openFloatingVideoInFullPlayer() {
        val floatingState = _state.value.floatingVideoMiniPlayerState

        if (floatingState.currentMedia?.isVideo != true) return

        val mediaList = floatingState.mediaList
        if (mediaList.isEmpty()) return

        val safeIndex = floatingState.currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = mediaList.lastIndex
        )

        /*
         * Important:
         * Navigation se pehle mini-player UI hide kar rahe hain.
         * Is se mini PlayerView old video surface release kar deta hai,
         * warna full screen PlayerView blank show kar sakta hai.
         */
        floatingVideoPlayerCoordinator.hideForFullPlayerOpening()

        viewModelScope.launch {
            _navEvents.emit(
                MainNavEvents.OpenMediaPlayer(
                    mediaList = mediaList,
                    startIndex = safeIndex
                )
            )
        }
    }


    private fun observeNotificationSettings() {
        viewModelScope.launch {
            notificationSettingsUseCases
                .observeNotificationSettingsUseCase()
                .distinctUntilChanged()
                .collect { settings ->
                    _state.update { currentState ->
                        currentState.copy(
                            downloadCompleteNotificationEnabled = settings.downloadCompleteEnabled,
                            downloadFailedNotificationEnabled = settings.downloadFailedEnabled,
                            appUpdatesNotificationEnabled = settings.appUpdatesEnabled,

                            draftDownloadCompleteNotificationEnabled =
                                if (currentState.showNotificationDialog) {
                                    currentState.draftDownloadCompleteNotificationEnabled
                                } else {
                                    settings.downloadCompleteEnabled
                                },

                            draftDownloadFailedNotificationEnabled =
                                if (currentState.showNotificationDialog) {
                                    currentState.draftDownloadFailedNotificationEnabled
                                } else {
                                    settings.downloadFailedEnabled
                                },

                            draftAppUpdatesNotificationEnabled =
                                if (currentState.showNotificationDialog) {
                                    currentState.draftAppUpdatesNotificationEnabled
                                } else {
                                    settings.appUpdatesEnabled
                                }
                        )
                    }
                }
        }
    }


    private fun openNotificationDialog() {
        _state.update {
            it.copy(
                showNotificationDialog = true,
                draftDownloadCompleteNotificationEnabled = it.downloadCompleteNotificationEnabled,
                draftDownloadFailedNotificationEnabled = it.downloadFailedNotificationEnabled,
                draftAppUpdatesNotificationEnabled = it.appUpdatesNotificationEnabled
            )
        }
    }

    private fun dismissNotificationDialog() {
        _state.update {
            it.copy(
                showNotificationDialog = false,
                draftDownloadCompleteNotificationEnabled = it.downloadCompleteNotificationEnabled,
                draftDownloadFailedNotificationEnabled = it.downloadFailedNotificationEnabled,
                draftAppUpdatesNotificationEnabled = it.appUpdatesNotificationEnabled
            )
        }
    }

    private fun saveNotificationSettings() {
        viewModelScope.launch {
            val currentState = _state.value

            val settings = NotificationSettings(
                downloadCompleteEnabled = currentState.draftDownloadCompleteNotificationEnabled,
                downloadFailedEnabled = currentState.draftDownloadFailedNotificationEnabled,
                appUpdatesEnabled = currentState.draftAppUpdatesNotificationEnabled
            )

            notificationSettingsUseCases.updateNotificationSettingsUseCase(settings)

            _state.update {
                it.copy(
                    showNotificationDialog = false,
                    downloadCompleteNotificationEnabled = settings.downloadCompleteEnabled,
                    downloadFailedNotificationEnabled = settings.downloadFailedEnabled,
                    appUpdatesNotificationEnabled = settings.appUpdatesEnabled
                )
            }
        }
    }


    private fun openRenameDialog() {
        val selectedFile = _state.value.playerMediaItem ?: return
        val file = File(selectedFile.filePath)

        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,

                showRenameFileDialog = true,
                renameMediaItem = selectedFile,
                renameDraftName = file.nameWithoutExtension.ifBlank {
                    selectedFile.fileName.substringBeforeLast(".")
                },
                renameError = null,
                isRenamingFile = false
            )
        }
    }

    private fun dismissRenameDialog() {
        _state.update {
            it.copy(
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false
            )
        }
    }

    private fun validateRenameFileName(
        value: String,
    ): String? {
        if (value.isBlank()) {
            return "File name cannot be empty"
        }

        val invalidChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        if (value.any { it in invalidChars }) {
            return "File name contains invalid characters"
        }

        return null
    }

    private fun renameSelectedFile() {
        viewModelScope.launch {
            val currentState = _state.value
            val selectedFile = currentState.renameMediaItem

            if (selectedFile == null) {
                dismissRenameDialog()
                return@launch
            }

            val newName = currentState.renameDraftName.trim()

            val validationError = validateRenameFileName(newName)
            if (validationError != null) {
                _state.update {
                    it.copy(renameError = validationError)
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isRenamingFile = true,
                    renameError = null
                )
            }

            when (
                val result = renameMediaFileUseCase(
                    mediaFile = selectedFile,
                    newNameWithoutExtension = newName
                )
            ) {
                is RenameMediaFileResult.Success -> {
                    onRenameSuccess(result.mediaFile)
                }

                is RenameMediaFileResult.RequiresWritePermission -> {
                    _state.update {
                        it.copy(
                            isRenamingFile = false,
                            renameError = null
                        )
                    }

                    _navEvents.emit(
                        MainNavEvents.RequestMediaWritePermission(
                            uri = result.uri,
                            pendingIntent = result.pendingIntent
                        )
                    )
                }

                is RenameMediaFileResult.Failure -> {
                    _state.update {
                        it.copy(
                            isRenamingFile = false,
                            renameError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun onRenameSuccess(
        renamedFile: MediaFile,
    ) {
        _state.update {
            it.copy(
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                fileInfoMediaItem = if (it.fileInfoMediaItem?.id == renamedFile.id) {
                    renamedFile
                } else {
                    it.fileInfoMediaItem
                }
            )
        }
    }


    private fun openDeleteDialog() {
        val selectedFile = _state.value.playerMediaItem ?: return

        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,

                showDeleteFileDialog = true,
                deleteMediaItem = selectedFile,
                isDeletingFile = false,
                deleteFileError = null
            )
        }
    }

    private fun dismissDeleteDialog() {
        _state.update {
            it.copy(
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null
            )
        }
    }

    private fun deleteSelectedFile() {
        viewModelScope.launch {
            val selectedFile = _state.value.deleteMediaItem

            if (selectedFile == null) {
                dismissDeleteDialog()
                return@launch
            }

            _state.update {
                it.copy(
                    isDeletingFile = true,
                    deleteFileError = null
                )
            }

            when (
                val result = deleteMediaFileUseCase(selectedFile)
            ) {
                DeleteMediaFileResult.Success -> {
                    onDeleteSuccess()
                }

                is DeleteMediaFileResult.RequiresDeletePermission -> {
                    _state.update {
                        it.copy(
                            isDeletingFile = false,
                            deleteFileError = null
                        )
                    }

                    _navEvents.emit(
                        MainNavEvents.RequestMediaDeletePermission(
                            uri = result.uri,
                            pendingIntent = result.pendingIntent
                        )
                    )
                }

                is DeleteMediaFileResult.Failure -> {
                    _state.update {
                        it.copy(
                            isDeletingFile = false,
                            deleteFileError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun onDeleteSuccess() {
        _state.update {
            it.copy(
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,

                showFileInfoDialog = false,
                fileInfoMediaItem = null,

                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false
            )
        }
    }

    private fun openMoveDestinationPicker() {
        val selectedFile = _state.value.playerMediaItem ?: return

        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null,

                moveMediaItem = selectedFile,
                isMovingFile = false,
                moveFileError = null
            )
        }

        viewModelScope.launch {
            _navEvents.emit(MainNavEvents.PickMoveDestinationFolder)
        }
    }

    private fun moveSelectedFile(
        destinationTreeUri: Uri,
    ) {
        viewModelScope.launch {
            val selectedFile = _state.value.moveMediaItem

            if (selectedFile == null) {
                clearMoveState()
                return@launch
            }

            _state.update {
                it.copy(
                    isMovingFile = true,
                    moveFileError = null
                )
            }

            when (
                val result = moveMediaFileUseCase(
                    mediaFile = selectedFile,
                    destinationTreeUri = destinationTreeUri
                )
            ) {
                is MoveMediaFileResult.Success -> {
                    onMoveSuccess()
                }

                is MoveMediaFileResult.RequiresDeletePermission -> {
                    _state.update {
                        it.copy(
                            isMovingFile = false,
                            moveFileError = null
                        )
                    }

                    _navEvents.emit(
                        MainNavEvents.RequestMoveDeletePermission(
                            uri = result.uri,
                            pendingIntent = result.pendingIntent
                        )
                    )
                }

                is MoveMediaFileResult.Failure -> {
                    _state.update {
                        it.copy(
                            isMovingFile = false,
                            moveFileError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun onMoveSuccess() {
        _state.update {
            it.copy(
                moveMediaItem = null,
                isMovingFile = false,
                moveFileError = null,

                showFileInfoDialog = false,
                fileInfoMediaItem = null,

                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,

                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null
            )
        }
    }

    private fun clearMoveState() {
        _state.update {
            it.copy(
                moveMediaItem = null,
                isMovingFile = false,
                moveFileError = null
            )
        }
    }

    private fun shareSelectedFile() {
        val selectedFile = _state.value.playerMediaItem ?: return

        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null
            )
        }

        viewModelScope.launch {
            _navEvents.emit(
                MainNavEvents.ShareMediaFile(
                    mediaFile = selectedFile
                )
            )
        }
    }

    private fun playSelectedFile() {
        val selectedFile = _state.value.playerMediaItem ?: return

        _state.update {
            it.copy(
                showPlayerDialogue = false,
                playerMediaItem = null
            )
        }

        viewModelScope.launch {
            _navEvents.emit(
                MainNavEvents.PlayMediaFile(
                    mediaFile = selectedFile
                )
            )
        }
    }

    private fun openFeedbackDialog() {
        _state.update {
            it.copy(
                showFeedbackDialog = true,
                feedbackMessage = "",
                feedbackError = null,
                isSubmittingFeedback = false
            )
        }
    }

    private fun dismissFeedbackDialog() {
        _state.update {
            it.copy(
                showFeedbackDialog = false,
                feedbackMessage = "",
                feedbackError = null,
                isSubmittingFeedback = false
            )
        }
    }

    private fun submitFeedback() {
        val message = _state.value.feedbackMessage.trim()

        if (message.isBlank()) {
            _state.update {
                it.copy(
                    feedbackError = "Feedback cannot be empty"
                )
            }
            return
        }

        if (message.length < MIN_FEEDBACK_LENGTH) {
            _state.update {
                it.copy(
                    feedbackError = "Please write at least $MIN_FEEDBACK_LENGTH characters"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmittingFeedback = true,
                    feedbackError = null
                )
            }

            val subject = "Feedback for Video Downloader"

            _state.update {
                it.copy(
                    showFeedbackDialog = false,
                    feedbackMessage = "",
                    feedbackError = null,
                    isSubmittingFeedback = false
                )
            }

            _navEvents.emit(
                MainNavEvents.SendFeedbackEmail(
                    subject = subject,
                    message = message
                )
            )
        }
    }

    private fun openRateUsDialog() {
        _state.update {
            it.copy(
                showRateUsDialog = true,
                selectedRating = DEFAULT_SELECTED_RATING,
                rateUsError = null,
                isSubmittingRating = false
            )
        }
    }

    private fun dismissRateUsDialog() {
        _state.update {
            it.copy(
                showRateUsDialog = false,
                selectedRating = DEFAULT_SELECTED_RATING,
                rateUsError = null,
                isSubmittingRating = false
            )
        }
    }

    private fun retryFailedFetch() {
        val failedUrl = _state.value.failedFetchUrl

        if (failedUrl.isBlank()) {
            _state.update {
                it.copy(
                    showFetchFailedDialog = false,
                    fetchErrorMessage = null,
                    error = null
                )
            }
            return
        }

        fetchVideo(failedUrl)
    }

    private fun dismissFetchFailedDialog() {
        _state.update {
            it.copy(
                showFetchFailedDialog = false,
                fetchErrorMessage = null,
                error = null
            )
        }
    }

    private fun switchTabWithInterstitial(
        tab: BottomNavItem,
        activity: android.app.Activity?,
    ) {
        if (tab == _state.value.selectedTab) return

        runAfterInterstitial(
            activity = activity,
            placement = InterstitialAdPlacement.TabSwitch
        ) {
            _state.update {
                it.copy(
                    selectedTab = tab,
                    isPlayerSearchActive = false,
                    playerSearchQuery = "",
                    isDownloadSearchActive = false,
                    downloadSearchQuery = ""
                )
            }
        }
    }

    private fun openMediaPlayerWithInterstitial(
        mediaList: List<MediaFile>,
        startIndex: Int,
        activity: android.app.Activity?,
    ) {
        runAfterInterstitial(
            activity = activity,
            placement = InterstitialAdPlacement.PlayMedia
        ) {
            viewModelScope.launch {
                _navEvents.emit(
                    MainNavEvents.OpenMediaPlayer(
                        mediaList = mediaList,
                        startIndex = startIndex
                    )
                )
            }
        }
    }

    private fun navigateBackToHomeWithInterstitial(
        activity: android.app.Activity?,
    ) {
        if (_state.value.selectedTab == BottomNavItem.Home) {
            _state.update {
                it.copy(showExitDialogue = true)
            }
            return
        }

        runAfterInterstitial(
            activity = activity,
            placement = InterstitialAdPlacement.BackNavigation
        ) {
            _state.update {
                it.copy(selectedTab = BottomNavItem.Home)
            }
        }
    }

    private fun runAfterInterstitial(
        activity: android.app.Activity?,
        placement: InterstitialAdPlacement,
        forceShow: Boolean = false,
        action: () -> Unit,
    ) {
        if (_state.value.isPremiumUser) {
            _adState.value = AdState.Skipped(
                reason = "Interstitial skipped: premium user"
            )
            action()
            return
        }

        if (activity == null) {
            action()
            return
        }

        showInterstitialAd(
            activity = activity,
            placement = placement,
            forceShow = forceShow,
            onStateChanged = { state ->
                _adState.value = state
            },
            onComplete = action
        )
    }

    private fun submitRating() {
        val rating = _state.value.selectedRating

        if (rating !in 1..5) {
            _state.update {
                it.copy(rateUsError = "Please select a rating")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmittingRating = true,
                    rateUsError = null
                )
            }

            delay(300)

            _state.update {
                it.copy(
                    showRateUsDialog = false,
                    selectedRating = DEFAULT_SELECTED_RATING,
                    rateUsError = null,
                    isSubmittingRating = false
                )
            }

            _navEvents.emit(MainNavEvents.OpenAppStoreForRating)
        }
    }

    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _state.update {
                        it.copy(isPremiumUser = isPremium)
                    }

                    if (isPremium) {
                        _adState.value = AdState.Skipped(
                            reason = "Interstitial skipped: premium user"
                        )
                    } else {
                        preloadAd()
                    }
                }
        }
    }


    companion object {
        private const val MIN_FEEDBACK_LENGTH = 5
        private const val DEFAULT_SELECTED_RATING = 4
        const val MAX_PLAYER_SEARCH_QUERY_LENGTH = 80
    }
}