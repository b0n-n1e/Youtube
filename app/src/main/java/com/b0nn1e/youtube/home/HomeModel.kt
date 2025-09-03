package com.b0nn1e.youtube.home

import com.b0nn1e.youtube.webs.ServiceBuilder
import com.b0nn1e.youtube.webs.bean.PopularVideosRequestBody
import com.b0nn1e.youtube.webs.bean.PopularVideosResponse
import com.b0nn1e.youtube.webs.service.PopularVideoService
import retrofit2.Callback

class HomeModel {

    fun getPopularVideos(
        pageToken: String? = null,
        callback: Callback<PopularVideosResponse>
    ) = ServiceBuilder.create<PopularVideoService>()
        .getRecommendedVideos(pageToken = pageToken)
        .enqueue(callback)
}