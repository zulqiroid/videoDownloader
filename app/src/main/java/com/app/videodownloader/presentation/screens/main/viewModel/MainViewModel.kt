package com.app.videodownloader.presentation.screens.main.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.AdState
import com.app.videodownloader.domain.repository.ads.AdManager
import com.app.videodownloader.domain.usecases.FetchVideoUseCase
import com.app.videodownloader.domain.usecases.StartDownloadUseCase
import com.app.videodownloader.domain.usecases.ads.LoadAppOpenAdUseCase
import com.app.videodownloader.domain.usecases.ads.LoadInterstitialAdUseCase
import com.app.videodownloader.domain.usecases.ads.ShowAppOpenAdUseCase
import com.app.videodownloader.domain.usecases.ads.ShowInterstitialAdUseCase
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.presentation.screens.main.events.FileDialogIntent
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.MainState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class MainViewModel(
    private val policyUseCases: PolicyUseCases,
    private val fetchVideoUseCase: FetchVideoUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val loadInterstitialAd: LoadInterstitialAdUseCase,
    private val showInterstitialAd: ShowInterstitialAdUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(100)   // 👈 ensure init complete
            preloadAd()
        }
        checkPolicy()
    }

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MainNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: MainEvents) {
        when (event) {
            is MainEvents.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTab = event.tab)
                }

                event.activity?.let {
                    showInterstitialAd(it) { state ->
                        _adState.value = state
                    }
                }
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

            is MainEvents.OnMediaItemInPLayerClick ->{
                _state.update {
                    it.copy(
                        showPlayerDialogue = true,
                        playerMediaItem = event.item
                    )
                }
            }
        }
    }

    fun fileDialogueEvent(dialogue: FileDialogIntent){
        when(dialogue){
            FileDialogIntent.OnAddToQueueClicked -> {

            }
            FileDialogIntent.OnDeleteClicked ->{

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

            }
            FileDialogIntent.OnMoveClicked -> {

            }
            FileDialogIntent.OnPlayClicked -> {

            }
            FileDialogIntent.OnRenameClicked -> {

            }
            FileDialogIntent.OnShareClicked -> {

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

            _state.update { it.copy(urlFetchingLoading = true, error = null) }

            try {
                try {
                    val result = fetchVideoUseCase(url)

                    if (!result.status) {
                        val error = result.errorMessage?.firstOrNull()
                            ?: "Something went wrong"

                        throw Exception(error)
                    }

                    _state.update {
                        it.copy(
                            urlFetchingLoading = false,
                            videoData = result,
                            showDownloadSheet = true,
                            selectedOptionIndex = 0
                        )
                    }

                } catch (e: Exception) {
                    Log.d("result", "the result is ${e.message}")
                    _state.update {
                        it.copy(
                            urlFetchingLoading = false,
                            error = e.message
                        )
                    }
                }

                _state.update { it.copy(urlFetchingLoading = false) }

            } catch (e: Exception) {
                _state.update { it.copy(urlFetchingLoading = false) }
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
        loadInterstitialAd { state ->
            _adState.value = state
        }
    }

}