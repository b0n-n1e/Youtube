package com.b0nn1e.youtube.search

import com.b0nn1e.youtube.webs.ServiceBuilder
import com.b0nn1e.youtube.webs.bean.SearchListResponse
import com.b0nn1e.youtube.webs.bean.SearchRequestBody
import com.b0nn1e.youtube.webs.service.SearchService
import retrofit2.Callback

class SearchModel {

    fun searchListRequest(
        searchRequestBody: SearchRequestBody,
        callback: Callback<SearchListResponse>
    ) = ServiceBuilder.create<SearchService>()
        .search(q = searchRequestBody.q)
        .enqueue(callback)
}