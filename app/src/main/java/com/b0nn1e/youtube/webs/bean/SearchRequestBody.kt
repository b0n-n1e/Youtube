package com.b0nn1e.youtube.webs.bean

/**
 * YouTube Data API v3 search.list 方法的请求体，封装所有查询参数以简化接口定义。
 * 参见：https://developers.google.com/youtube/v3/docs/search/list
 */
data class SearchRequestBody(
    // 指定 API 响应包含的搜索资源属性，逗号分隔列表，默认 "snippet"。
    val part: String = "snippet",

    //一下为过滤参数，0个或者1个
    // 限制搜索仅检索由内容所有者拥有的视频，需授权，仅限内容合作伙伴。
    val forContentOwner: Boolean? = null,
    // 指定内容所有者 ID，需与 forContentOwner 配合使用。
    val onBehalfOfContentOwner: String? = null,
    // 限制搜索仅检索通过开发者应用上传的视频，需授权。
    val forDeveloper: Boolean? = null,
    // 限制搜索仅检索由认证用户拥有的视频，需授权。
    val forMine: Boolean? = null,

    //可选参数
    // 限制响应仅包含指定频道创建的资源。
    val channelId: String? = null,
    // 限制搜索到特定频道类型，可选值："any"、"show"。
    val channelType: String? = null,
    // 限制搜索到广播事件，需 type 为 "video"，可选值："completed"、"live"、"upcoming"。
    val eventType: String? = null,
    // 定义圆形地理区域的中心点，格式："纬度,经度"，需与 locationRadius 配合使用。
    val location: String? = null,
    // 指定视频与位置点的最大距离，格式：浮点数加单位（如 "1500m"）。
    val locationRadius: String? = null,
    // 结果集最大项数，可选值：0 到 50，默认 5。
    val maxResults: Int? = null,
    // 指定响应中资源的排序方法，默认 "relevance"，可选值："date"、"rating"、"relevance"、"title"、"videoCount"、"viewCount"。
    val order: String? = null,
    // 标识结果集中的特定页面。
    val pageToken: String? = null,
    // 仅包含指定时间当天或之后创建的资源，格式：RFC 3339 日期时间。
    val publishedAfter: String? = null,
    // 仅包含指定时间之前或当天创建的资源，格式：RFC 3339 日期时间。
    val publishedBefore: String? = null,

    // 搜索查询词，支持布尔运算符 OR ("|") 和 NOT ("-")。
    val q: String? = null,

    // 返回指定国家/地区可观看的视频，格式：ISO 3166-1 alpha-2 国家/地区代码。
    val regionCode: String? = null,
    // 返回与指定语言最相关的结果，格式：ISO 639-1 语言代码（中文："zh-Hans"、"zh-Hant"）。
    val relevanceLanguage: String? = null,
    // 控制是否包含受限内容，可选值："moderate"（默认）、"none"、"strict"。
    val safeSearch: String? = "moderate",
    // 限制搜索到指定主题的资源，值为 Freebase 主题 ID（部分支持）。
    val topicId: String? = null,
    // 限制搜索到特定资源类型，逗号分隔列表，默认 "video,channel,playlist"。
    val type: String? = "video,channel,playlist",
    // 根据视频字幕过滤，需 type 为 "video"，可选值："any"、"closedCaption"、"none"。
    val videoCaption: String? = null,
    // 根据视频类别过滤，需 type 为 "video"。
    val videoCategoryId: String? = null,
    // 限制搜索到高清或标清视频，需 type 为 "video"，可选值："any"、"high"、"standard"。
    val videoDefinition: String? = null,
    // 限制搜索到 2D 或 3D 视频，需 type 为 "video"，可选值："2d"、"3d"、"any"。
    val videoDimension: String? = null,
    // 根据视频时长过滤，需 type 为 "video"，可选值："any"、"long"、"medium"、"short"。
    val videoDuration: String? = null,
    // 限制搜索到可嵌入网页的视频，需 type 为 "video"，可选值："any"、"true"。
    val videoEmbeddable: String? = null,
    // 过滤具有特定许可的视频，需 type 为 "video"，可选值："any"、"creativeCommon"、"youtube"。
    val videoLicense: String? = null,
    // 过滤包含付费宣传内容的视频，需 type 为 "video"，可选值："any"、"true"。
    val videoPaidProductPlacement: String? = null,
    // 限制搜索到可在 youtube.com 以外播放的视频，需 type 为 "video"，可选值："any"、"true"。
    val videoSyndicated: String? = null,
    // 限制搜索到特定视频类型，需 type 为 "video"，可选值："any"、"episode"、"movie"。
    val videoType: String? = null
)