package com.app.videodownloader.presentation.screens.downloader.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.data.download.VideoDownloader
import com.app.videodownloader.data.remote.DownloaderApi
import com.app.videodownloader.presentation.screens.downloader.states.DownloaderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloaderViewModel(
    private val api: DownloaderApi,
    private val downloader: VideoDownloader,
) : ViewModel() {

    private val _state = MutableStateFlow(DownloaderState())
    val state = _state.asStateFlow()

    fun onUrlChange(value: String) {
        _state.update { it.copy(url = value) }
    }

    fun onDownloadClick() {

        val url = state.value.url

        if (url.isBlank()) {
            _state.update { it.copy(error = "Paste a valid URL") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = api.download(url)

                if (!result.status) {
                    val error = result.error_message?.firstOrNull()
                        ?: "Something went wrong"

                    throw Exception(error)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        title = result.title,
                        thumbnail = result.image_url,
                        downloadOptions = result.downloadables
                    )
                }

            } catch (e: Exception) {
                Log.d("result", "the result is ${e.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onQualitySelected(url: String) {
        viewModelScope.launch {

            _state.update { it.copy(isDownloading = true) }

            downloader.downloadVideo(url)

            _state.update { it.copy(isDownloading = false) }
        }
    }
}