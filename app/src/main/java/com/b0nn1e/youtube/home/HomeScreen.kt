package com.b0nn1e.youtube.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.b0nn1e.youtube.webs.bean.PopularVideosResponse
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onVideoClick: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value


    // 创建 LazyListState
    val lazyListState = rememberLazyListState()
    val scrollState by viewModel.scrollState.collectAsState()
    // 恢复滑动状态
    LaunchedEffect(scrollState) {
        val (index, offset) = scrollState
        if (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0) {
            lazyListState.scrollToItem(index, offset)
        }
    }
    // 监听滑动状态变化并保存
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            Pair(
                lazyListState.firstVisibleItemIndex,
                lazyListState.firstVisibleItemScrollOffset
            )
        }.collect { (index, offset) ->
            viewModel.saveScrollState(index, offset)
        }
    }


    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        viewModel.getPopularVideos()
    }

    // 监听 isRefreshing 变化，确保刷新完成后动画隐藏
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            state.animateToHidden()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = state,
        onRefresh = onRefresh
    ) {
        when (uiState) {
            is HomeViewModel.UiState.Initial,
            is HomeViewModel.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeViewModel.UiState.Success -> {
                VideoList(
                    listState = lazyListState,
                    videos = uiState.videos,
                    nextPageToken = uiState.nextPageToken,
                    isLoadingMore = uiState.isLoadingMore,
                    onVideoClick = onVideoClick,
                    onLoadMore = {
                        viewModel.getPopularVideos(uiState.nextPageToken)
                    }
                )
            }
            is HomeViewModel.UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun VideoList(
    listState: LazyListState,
    videos: List<PopularVideosResponse.Item>,
    nextPageToken: String?,
    isLoadingMore: Boolean,
    onVideoClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 2
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos.size) { index ->
            VideoCard(
                video = videos[index],
                onClick = { videos[index].id?.let { onVideoClick(it) } }
            )
        }
        if (isLoadingMore || nextPageToken != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && nextPageToken != null && !isLoadingMore) {
            onLoadMore()
        }
    }
}

@Composable
fun VideoCard(video: PopularVideosResponse.Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = video.snippet?.thumbnails?.high?.url,
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = video.snippet?.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = video.snippet?.channelTitle ?: "Unknown Channel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${video.statistics?.viewCount ?: "0"} views",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}