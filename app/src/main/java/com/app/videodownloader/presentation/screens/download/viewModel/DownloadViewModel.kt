    package com.app.videodownloader.presentation.screens.download.viewModel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.app.videodownloader.domain.model.DownloadItem
    import com.app.videodownloader.domain.model.DownloadStatus
    import com.app.videodownloader.domain.usecases.CancelDownloadUseCase
    import com.app.videodownloader.domain.usecases.GetDownloadedFilesUseCase
    import com.app.videodownloader.domain.usecases.ObserveDownloadsUseCase
    import com.app.videodownloader.presentation.screens.download.events.DownloadEvents
    import com.app.videodownloader.presentation.screens.download.states.DownloadState
    import com.app.videodownloader.presentation.screens.download.states.DownloadTab
    import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
    import com.app.videodownloader.presentation.screens.download.states.toUiItem
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch

    class DownloadViewModel(
        private val observeDownloadsUseCase: ObserveDownloadsUseCase,
        private val getDownloadedFilesUseCase: GetDownloadedFilesUseCase,
        private val cancelDownloadUseCase: CancelDownloadUseCase
    ) : ViewModel() {

        private val _state = MutableStateFlow(DownloadState())
        val state = _state.asStateFlow()

        init {
            loadLocalFiles()
            observeDownloads()
        }

        fun onEvent(event: DownloadEvents){
            when(event){
                is DownloadEvents.OnDeleteDownloadingClicked -> {
                    viewModelScope.launch {
                        cancelDownloadUseCase(event.id)
                    }
                }
                is DownloadEvents.OnPauseDownloadingClicked -> {
                    viewModelScope.launch {
                        cancelDownloadUseCase(event.id)
                    }
                }
            }
        }

        private fun observeDownloads() {
            viewModelScope.launch {

                observeDownloadsUseCase().collect { items ->

                    val (downloading, activeCompleted) = mapToUi(items)

                    _state.update { current ->

                        // merge existing completed (from folder) with active completed
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

        private fun mapToUi(items: List<DownloadItem>): Pair<List<DownloadUiItem>, List<DownloadUiItem>> {

            val downloading = mutableListOf<DownloadUiItem>()
            val completed = mutableListOf<DownloadUiItem>()

            items.forEach { item ->

                val totalMB = item.totalBytes / (1024f * 1024f)
                val speedMB = item.speedBytesPerSec / (1024f * 1024f)

                val etaText = formatTime(item.lastEtaSeconds)

                val finalTimeText = if (item.status == DownloadStatus.DOWNLOADING) {
                    etaText?.let { "Est. $it left" } ?: "Calculating..."
                } else {
                    "Completed"
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
        fun updateTab(tab: DownloadTab) {
            _state.update {
                it.copy(selectedTab = tab)
            }
        }
    }