package com.b0nn1e.youtube.webs.bean

data class PopularVideosRequestBody (
    val part : String = "snippet,contentDetails,statistics",
    val chart : String? = "mostPopular",
    val regionCode : String? = "CN",
    val maxResults : Int? = 20,
    val pageToken : String? = null
)
