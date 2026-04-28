package com.app.videodownloader.presentation.screens.main.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.DownloadVideoUseCase
import com.app.videodownloader.domain.usecases.FetchVideoUseCase
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.states.MainState
import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class MainViewModel(
    private val policyUseCases: PolicyUseCases,
    private val fetchVideoUseCase: FetchVideoUseCase,
    private val downloadVideoUseCase: DownloadVideoUseCase,
) : ViewModel() {

    init {
        checkPolicy()
    }

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

            MainEvents.OnDownLoadInBottomSheetClicked -> {
                _state.update {
                    it.copy(showDownloadSheet = false)
                }
                onQualitySelected(
                    state.value.videoData?.downloadOptions?.firstOrNull()?.url ?: ""
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
            downloadVideoUseCase(url)
        }
    }
}