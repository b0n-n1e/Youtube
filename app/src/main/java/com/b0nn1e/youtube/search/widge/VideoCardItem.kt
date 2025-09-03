package com.b0nn1e.youtube.search.widge

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.b0nn1e.youtube.webs.bean.SearchListResponse.SearchResult
import kotlinx.coroutines.flow.debounce

/**
 * 单个视频卡片布局，展示缩略图、标题、频道、发布时间和简短描述。
 * 如果是直播视频，显示“直播”标签。
 * 使用 Coil 加载图片，支持占位符。
 *
 * @param searchResult 搜索结果项
 * @param modifier Modifier
 */
@Composable
fun VideoCardItem(
    searchResult: SearchResult,
    modifier: Modifier = Modifier
) {
    // 确保 snippet 非空
    val snippet = searchResult.snippet ?: run {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "视频信息不可用",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // 缩略图
            val thumbnailUrl = snippet.thumbnails.medium?.url
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .crossfade(true)
                    .build()
            )

            Box {
                Image(
                    painter = painter,
                    contentDescription = "视频缩略图",
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                if (snippet.liveBroadcastContent == "live") {
                    Text(
                        text = "直播",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Red.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = snippet.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = snippet.channelTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = snippet.publishedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = snippet.description.take(100) + if (snippet.description.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 视频列表布局，使用 LazyColumn 垂直排列视频卡片，支持滚动和懒加载。
 * 只展示视频类型的搜索结果（kind == "youtube#video"）。
 *
 * @param searchResults 搜索结果列表。
 * @param modifier Modifier 用于自定义列表样式。
 */
@Composable
fun VideoCardList(
    modifier: Modifier = Modifier,
    searchResults: List<SearchResult>,
    lazyListState: LazyListState
) {
    // 缓存过滤后的视频列表
    val videoResults = remember(searchResults) {
        searchResults.filter {
            it.id.kind == "youtube#video" && it.id.videoId != null && it.snippet != null
        }
    }


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        state = lazyListState
    ) {
        items(
            count = videoResults.size,
            key = { index -> videoResults[index].id.videoId!! },
            contentType = { "VideoCard" }
        ) { index ->
            VideoCardItem(searchResult = videoResults[index])
        }
    }
}

