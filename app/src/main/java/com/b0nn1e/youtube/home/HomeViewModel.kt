package com.b0nn1e.youtube.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.b0nn1e.youtube.util.TAG
import com.b0nn1e.youtube.util.convertErrorBody
import com.b0nn1e.youtube.webs.bean.PopularVideosResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {

    private val model by lazy { HomeModel() }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _scrollState = MutableStateFlow(Pair(0, 0))
    val scrollState = _scrollState.asStateFlow()

    // 新增刷新状态的 StateFlow
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun saveScrollState(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        viewModelScope.launch {
            _scrollState.value = Pair(firstVisibleItemIndex, firstVisibleItemScrollOffset)
        }
    }

    init {
        getPopularVideos()
    }

    fun getPopularVideos(pageToken: String? = null) {
        if (pageToken == null) {
            _isRefreshing.value = true // 明确在下拉刷新时设置为 true
            _uiState.value = UiState.Loading
        } else {
            _uiState.update { current ->
                if (current is UiState.Success) {
                    current.copy(isLoadingMore = true)
                } else current
            }
        }

        viewModelScope.launch {
            model.getPopularVideos(pageToken, object : Callback<PopularVideosResponse> {
                override fun onResponse(
                    call: Call<PopularVideosResponse>,
                    response: Response<PopularVideosResponse>
                ) {
                    _isRefreshing.value = false
                    if (response.isSuccessful) {
                        val newData = response.body()
                        _uiState.update { current ->
                            if (current is UiState.Success) {
                                UiState.Success(
                                    videos = current.videos + (newData?.items?.filterNotNull()
                                        ?: emptyList()),
                                    nextPageToken = newData?.nextPageToken,
                                    isLoadingMore = false
                                )
                            } else {
                                UiState.Success(
                                    videos = newData?.items?.filterNotNull() ?: emptyList(),
                                    nextPageToken = newData?.nextPageToken
                                )
                            }
                        }
                    } else {
                        val error = convertErrorBody<PopularVideosResponse>(response.errorBody())
                        _uiState.value =
                            UiState.Error("API Error: ${error?.toString() ?: "Unknown error"}")
                        Log.e(TAG, "onResponse: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<PopularVideosResponse>, t: Throwable) {
                    _isRefreshing.value = false
                    _uiState.value = UiState.Error("Network Error: ${t.message}")
                    Log.e(TAG, "onFailure: ${t.message}", t)
                }
            })
        }
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(
            val videos: List<PopularVideosResponse.Item>,
            val nextPageToken: String?,
            val isLoadingMore: Boolean = false
        ) : UiState()

        data class Error(val message: String) : UiState()
    }
}