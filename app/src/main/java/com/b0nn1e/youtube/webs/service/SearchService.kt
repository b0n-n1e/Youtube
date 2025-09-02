package com.b0nn1e.youtube.webs.service

import com.b0nn1e.youtube.webs.WebConstant
import com.b0nn1e.youtube.webs.bean.SearchListResponse
import com.b0nn1e.youtube.webs.bean.SearchRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * 用于与 YouTube Data API v3 搜索端点交互的 Retrofit 服务接口。
 *
 * @see <a href="https://developers.google.com/youtube/v3/docs/search/list">YouTube Data API - 搜索列表</a>
 */
interface SearchService {

    /**
     * 返回与 API 请求中指定的查询参数匹配的搜索结果集合。
     * 默认情况下，搜索结果集会标识匹配的视频、频道和播放列表资源，
     * 但您也可以配置查询以仅检索特定类型的资源。
     *
     * 必需参数：part（设置为 "snippet"）。
     *
     * @param part 指定 API 响应将包含的一个或多个搜索资源属性的逗号分隔列表。参数值应设为 "snippet"。
     * @param forContentOwner 限制搜索仅检索由 onBehalfOfContentOwner 参数指定的内容所有者拥有的视频。
     *                        此参数只能在适当授权的请求中使用，仅适用于 YouTube 内容合作伙伴。
     * @param forDeveloper 限制搜索仅检索通过开发者的应用或网站上传的视频。
     *                     此参数只能在适当授权的请求中使用。
     * @param forMine 限制搜索仅检索由经过身份验证的用户拥有的视频。
     *                此参数只能在适当授权的请求中使用。
     * @param channelId 表示 API 响应应仅包含由指定频道创建的资源。
     * @param channelType 将搜索限制为特定类型的频道。可接受的值："any"、"show"。
     * @param eventType 将搜索限制为广播事件。如果指定，必须将 type 参数设置为 "video"。
     *                  可接受的值："completed"、"live"、"upcoming"。
     * @param location 定义圆形地理区域，与 locationRadius 结合使用，限制搜索到元数据中指定该区域内地理位置的视频。
     *                  格式："纬度,经度"。
     * @param locationRadius 指定视频与位置点的最大距离，超出此距离的视频不会出现在搜索结果中。
     *                       格式：浮点数加单位（如 "1500m"、"5km"）。
     * @param maxResults 结果集中应返回的项的最大数量。可接受的值：0 到 50。默认值：5。
     * @param onBehalfOfContentOwner 表示请求的授权凭据标识代表参数值中指定的内容所有者执行操作的 YouTube CMS 用户。
     *                               此参数适用于 YouTube 内容合作伙伴。
     * @param order 指定在 API 响应中对资源排序的方法。默认值："relevance"。
     *              可接受的值："date"、"rating"、"relevance"、"title"、"videoCount"、"viewCount"。
     * @param pageToken 标识结果集中应返回的特定页面。
     * @param publishedAfter 表示 API 响应应仅包含指定时间当天或之后创建的资源。
     *                       格式：RFC 3339 日期时间（如 "1970-01-01T00:00:00Z"）。
     * @param publishedBefore 表示 API 响应应仅包含在指定时间之前或当天创建的资源。
     *                        格式：RFC 3339 日期时间。
     * @param q 指定要搜索的查询词。支持布尔运算符，如 OR ("|") 和 NOT ("-")。
     * @param regionCode 指示 API 返回可在指定国家/地区观看的视频的搜索结果。
     *                   格式：ISO 3166-1 alpha-2 国家/地区代码。
     * @param relevanceLanguage 指示 API 返回与指定语言最相关的搜索结果。
     *                          格式：ISO 639-1 两字母语言代码（中文例外："zh-Hans"、"zh-Hant"）。
     * @param safeSearch 指示搜索结果是否应包含受限内容及标准内容。
     *                   可接受的值："moderate"（默认）、"none"、"strict"。
     * @param topicId 表示 API 响应应仅包含与指定主题相关联的资源。值为 Freebase 主题 ID。
     *                注意：自 Freebase 弃用后，仅支持部分精选主题 ID。
     * @param type 将搜索查询限制为仅检索特定类型的资源。
     *             逗号分隔的资源类型列表。默认值："video,channel,playlist"。可接受的值："channel"、"playlist"、"video"。
     * @param videoCaption 根据视频是否有字幕过滤视频搜索结果。
     *                     如果指定，type 必须为 "video"。可接受的值："any"、"closedCaption"、"none"。
     * @param videoCategoryId 根据视频类别过滤视频搜索结果。
     *                         如果指定，type 必须为 "video"。
     * @param videoDefinition 将搜索结果限制为仅包含高清 (HD) 或标清 (SD) 视频。
     *                        如果指定，type 必须为 "video"。可接受的值："any"、"high"、"standard"。
     * @param videoDimension 将搜索限制为仅检索 2D 或 3D 视频。
     *                       如果指定，type 必须为 "video"。可接受的值："2d"、"3d"、"any"。
     * @param videoDuration 根据时长过滤视频搜索结果。
     *                      如果指定，type 必须为 "video"。可接受的值："any"、"long"、"medium"、"short"。
     * @param videoEmbeddable 将搜索限制为可嵌入网页的视频。
     *                        如果指定，type 必须为 "video"。可接受的值："any"、"true"。
     * @param videoLicense 过滤搜索结果以包含具有特定许可的视频。
     *                     如果指定，type 必须为 "video"。可接受的值："any"、"creativeCommon"、"youtube"。
     * @param videoPaidProductPlacement 过滤搜索结果以仅包含标示为提供付费宣传内容的视频。
     *                                   如果指定，type 必须为 "video"。可接受的值："any"、"true"。
     * @param videoSyndicated 将搜索限制为可在 youtube.com 以外播放的视频。
     *                        如果指定，type 必须为 "video"。可接受的值："any"、"true"。
     * @param videoType 将搜索限制为特定类型的视频。
     *                  如果指定，type 必须为 "video"。可接受的值："any"、"episode"、"movie"。
     * @return 用于执行请求并检索 SearchListResponse 的 Call 对象。
     */
    @GET(WebConstant.SEARCH)
    fun search(
        @Query("key") key : String = WebConstant.KEY,

        @Query("part") part: String = "snippet",

        //过滤参数
        @Query("forContentOwner") forContentOwner: Boolean? = null,
        @Query("forDeveloper") forDeveloper: Boolean? = null,
        @Query("forMine") forMine: Boolean? = null,

        //可选参数（重点q字段和maxResults字段）
        @Query("channelId") channelId: String? = null,
        @Query("channelType") channelType: String? = null,
        @Query("eventType") eventType: String? = null,
        @Query("location") location: String? = null,
        @Query("locationRadius") locationRadius: String? = null,
        @Query("maxResults") maxResults: Int? = null,
        @Query("onBehalfOfContentOwner") onBehalfOfContentOwner: String? = null,
        @Query("order") order: String? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("publishedAfter") publishedAfter: String? = null,
        @Query("publishedBefore") publishedBefore: String? = null,

        @Query("q") q: String? = null,

        @Query("regionCode") regionCode: String? = null,
        @Query("relevanceLanguage") relevanceLanguage: String? = null,
        @Query("safeSearch") safeSearch: String? = null,
        @Query("topicId") topicId: String? = null,
        @Query("type") type: String? = null,
        @Query("videoCaption") videoCaption: String? = null,
        @Query("videoCategoryId") videoCategoryId: String? = null,
        @Query("videoDefinition") videoDefinition: String? = null,
        @Query("videoDimension") videoDimension: String? = null,
        @Query("videoDuration") videoDuration: String? = null,
        @Query("videoEmbeddable") videoEmbeddable: String? = null,
        @Query("videoLicense") videoLicense: String? = null,
        @Query("videoPaidProductPlacement") videoPaidProductPlacement: String? = null,
        @Query("videoSyndicated") videoSyndicated: String? = null,
        @Query("videoType") videoType: String? = null
    ): Call<SearchListResponse>
}