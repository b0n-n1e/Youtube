package com.b0nn1e.youtube.webs.bean

/**
 * 表示 YouTube Data API v3 search.list 方法的响应。
 * 此 API 端点返回与 API 请求参数匹配的搜索结果集合。
 * 默认情况下，搜索结果集会标识匹配的视频、频道和播放列表资源，
 * 但您也可以配置查询以仅检索特定类型的资源。
 *
 * 调用此方法的配额影响为 100 个单元。
 *
 * @see <a href="https://developers.google.com/youtube/v3/docs/search/list">YouTube Data API - 搜索列表</a>
 */
data class SearchListResponse(
    // API 资源类型，固定为 "youtube#searchListResponse"。
    val kind: String,
    // 资源的 ETag，用于版本控制。
    val etag: String,
    // 用于检索结果集下一页的 pageToken，可为空。
    val nextPageToken: String? = null,
    // 用于检索结果集上一页的 pageToken，可为空。
    val prevPageToken: String? = null,
    // 搜索查询的地区代码（ISO 3166-1 alpha-2），默认 US，可为空。
    val regionCode: String? = null,
    // 结果集的分页信息。
    val pageInfo: PageInfo,
    // 匹配搜索条件的资源列表。
    val items: List<SearchResult>
)

// 封装搜索结果集的分页信息。
data class PageInfo(
    // 结果总数（近似值，最大 1,000,000，不建议用于分页）。
    val totalResults: Int,
    // 当前页包含的结果数量。
    val resultsPerPage: Int
)

// 单个搜索结果项，指向视频、频道或播放列表等资源，包含 ID 和 snippet，无持久性数据。
// 参见：https://developers.google.com/youtube/v3/docs/search#resource
data class SearchResult(
    // API 资源类型，固定为 "youtube#searchResult"。
    val kind: String,
    // 资源的 ETag。
    val etag: String,
    // 引用的资源标识。
    val id: SearchResultId,
    // 资源的片段信息，可为空。
    val snippet: Snippet? = null
)

// 标识搜索结果引用的资源（视频、频道或播放列表）。
data class SearchResultId(
    // 资源类型，可能为 "youtube#video"、"youtube#channel" 或 "youtube#playlist"。
    val kind: String,
    // 视频 ID，仅当 kind 为 "youtube#video" 时有效。
    val videoId: String? = null,
    // 频道 ID，仅当 kind 为 "youtube#channel" 时有效。
    val channelId: String? = null,
    // 播放列表 ID，仅当 kind 为 "youtube#playlist" 时有效。
    val playlistId: String? = null
)

// 搜索结果的基本信息，包含标题、描述等元数据。
data class Snippet(
    // 资源创建时间（ISO 8601 格式）。
    val publishedAt: String,
    // 发布资源的频道 ID。
    val channelId: String,
    // 资源标题。
    val title: String,
    // 资源描述。
    val description: String,
    // 资源的缩略图集合。
    val thumbnails: Thumbnails,
    // 发布资源的频道标题。
    val channelTitle: String,
    // 视频直播状态，可能为 "live"、"none" 或 "upcoming"。
    val liveBroadcastContent: String
)

// 资源的缩略图集合，包含不同尺寸的缩略图。
data class Thumbnails(
    // 默认缩略图，可为空。
    val default: Thumbnail? = null,
    // 中等质量缩略图，可为空。
    val medium: Thumbnail? = null,
    // 高质量缩略图，可为空。
    val high: Thumbnail? = null
)

// 缩略图的详细信息。
data class Thumbnail(
    // 缩略图的 URL。
    val url: String,
    // 缩略图宽度（像素），可为空。
    val width: Int? = null,
    // 缩略图高度（像素），可为空。
    val height: Int? = null
)
