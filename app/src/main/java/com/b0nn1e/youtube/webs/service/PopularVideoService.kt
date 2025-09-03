package com.b0nn1e.youtube.webs.service

import com.b0nn1e.youtube.webs.WebConstant
import retrofit2.http.GET
import com.b0nn1e.youtube.webs.WebConstant.VIDEOS
import com.b0nn1e.youtube.webs.bean.PopularVideosResponse
import retrofit2.Call
import retrofit2.http.Query

interface PopularVideoService {

    @GET(VIDEOS)
    fun getRecommendedVideos(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") regionCode: String = "US",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") key: String = WebConstant.KEY
    ): Call<PopularVideosResponse>
}