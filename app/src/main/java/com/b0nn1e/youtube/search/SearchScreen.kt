package com.b0nn1e.youtube.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.b0nn1e.youtube.search.widge.VideoCardList
import com.b0nn1e.youtube.webs.bean.SearchListResponse.SearchResult
import com.b0nn1e.youtube.webs.bean.SearchRequestBody

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onItemClick : (videoId : String) -> Unit
) {
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

    val searchQuery by viewModel.searchQuery.observeAsState("")
    //最新一次发起请求的response
    val searchResponse by viewModel.searchListResponse.observeAsState()
    //搜索的结果
    val searchResults = searchResponse?.items ?: emptyList<SearchResult>()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newString->
                viewModel.updateSearchQuery(newString)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text("搜索视频") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.search(SearchRequestBody(q = searchQuery))
                    }
                }
            )
        )

        // 搜索按鈕
        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    viewModel.search(SearchRequestBody(q = searchQuery))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = searchQuery.isNotBlank()
        ) {
            Text("搜索")
        }

        // 暂定两个状态
        when {
            searchResults.isEmpty() -> {
                // 無結果
                Text(
                    text = "无搜索结果",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // 有結果：展示列表
                VideoCardList(
                    modifier = Modifier.fillMaxSize(),
                    searchResults = searchResults,
                    lazyListState = lazyListState,
                    onItemClick = onItemClick
                )
            }
        }
    }
}
