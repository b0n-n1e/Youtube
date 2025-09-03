package com.b0nn1e.youtube.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.b0nn1e.youtube.util.TAG
import com.b0nn1e.youtube.util.convertErrorBody
import com.b0nn1e.youtube.webs.bean.SearchListResponse
import com.b0nn1e.youtube.webs.bean.SearchRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val searchModel by lazy { SearchModel() }

    // 保存搜索結果
    private val _searchListResponse = MutableLiveData<SearchListResponse?>()
    val searchListResponse: LiveData<SearchListResponse?>
        get() = _searchListResponse

    // 保存搜索框字符
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String>
        get() = _searchQuery

    private val _scrollState = MutableStateFlow(Pair(0, 0)) // 保存 firstVisibleItemIndex 和 firstVisibleItemScrollOffset
    val scrollState = _scrollState.asStateFlow()

    fun saveScrollState(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        viewModelScope.launch {
            _scrollState.value = Pair(firstVisibleItemIndex, firstVisibleItemScrollOffset)
        }
    }


    // 更新搜索框字符
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search(searchRequestBody: SearchRequestBody) {
        viewModelScope.launch {
            searchModel.searchListRequest(searchRequestBody, object : Callback<SearchListResponse> {
                override fun onResponse(
                    call: Call<SearchListResponse>,
                    response: Response<SearchListResponse>
                ) {
                    _searchListResponse.value = if (response.isSuccessful) {
                        Log.d(TAG, "success")
                        response.body()
                    } else {
                        Log.e(TAG, "onResponse but unSuccess")
                        convertErrorBody<SearchListResponse>(response.errorBody())
                    }
                }

                override fun onFailure(
                    call: Call<SearchListResponse>,
                    t: Throwable
                ) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    _searchListResponse.value = null
                }
            })
        }
    }

}