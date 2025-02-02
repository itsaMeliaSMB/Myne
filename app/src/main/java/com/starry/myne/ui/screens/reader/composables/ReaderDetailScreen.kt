package com.starry.myne.ui.screens.reader.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.starry.myne.MainActivity
import com.starry.myne.R
import com.starry.myne.navigation.Screens
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.ProgressDots
import com.starry.myne.ui.common.simpleVerticalScrollbar
import com.starry.myne.ui.screens.reader.viewmodels.ReaderDetailViewModel
import com.starry.myne.ui.screens.settings.viewmodels.ThemeMode
import com.starry.myne.ui.theme.figeronaFont
import com.starry.myne.utils.getActivity
import com.starry.myne.utils.toToast

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
fun ReaderDetailScreen(bookId: String, navController: NavController) {
    val viewModel: ReaderDetailViewModel = hiltViewModel()
    val state = viewModel.state

    LaunchedEffect(key1 = true) { viewModel.loadEbookData(bookId) }

    val context = LocalContext.current
    val settingsVM = (context.getActivity() as MainActivity).settingsViewModel

    Scaffold(topBar = {
        CustomTopAppBar(headerText = stringResource(id = R.string.reader_detail_header)) {
            navController.navigateUp()
        }
    }, floatingActionButton = {
        ExtendedFloatingActionButton(text = { Text(text = stringResource(id = if (state.readerItem != null) R.string.continue_reading_button else R.string.start_reading_button)) },
            onClick = { navController.navigate(Screens.ReaderScreen.withBookId(bookId)) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_fab_button),
                    contentDescription = null
                )
            })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressDots()
                }
            } else if (state.error != null) {
                stringResource(id = R.string.reader_file_not_found).toToast(context)
                navController.navigateUp()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = R.drawable.book_reader_bg,
                        contentDescription = null,
                        alpha = 0.35f,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background,
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    ), startY = 15f
                                )
                            )
                    )

                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val imageBackground =
                                if (settingsVM.getCurrentTheme() == ThemeMode.Dark) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                                }

                            Box(
                                modifier = Modifier
                                    .shadow(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(imageBackground)
                            ) {
                                if (state.ebookData!!.coverImage != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(state.ebookData.coverImage).crossfade(true)
                                            .build(),
                                        placeholder = painterResource(id = R.drawable.placeholder_cat),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(118.dp)
                                            .height(169.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (state.ebookData.epubBook.coverImage != null) {
                                    Image(
                                        bitmap = state.ebookData.epubBook.coverImage.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(118.dp)
                                            .height(169.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.placeholder_cat),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(118.dp)
                                            .height(169.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.ebookData!!.title,
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp, end = 8.dp, top = 20.dp
                                    )
                                    .fillMaxWidth(),
                                fontSize = 24.sp,
                                fontFamily = figeronaFont,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground,
                            )

                            Text(
                                text = state.ebookData.author,
                                modifier = Modifier.padding(
                                    start = 12.dp, end = 8.dp, top = 4.dp
                                ),
                                fontSize = 18.sp,
                                fontFamily = figeronaFont,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground,
                            )

                            if (state.readerItem != null) {
                                Text(
                                    text = "${state.readerItem.getProgressPercent(state.ebookData.epubBook.chapters.size)}% Completed",
                                    modifier = Modifier.padding(
                                        start = 12.dp, end = 8.dp, top = 8.dp
                                    ),
                                    fontSize = 16.sp,
                                    fontFamily = figeronaFont,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    thickness = 2.dp,
                    modifier = Modifier.padding(
                        start = 20.dp, end = 20.dp, top = 2.dp, bottom = 2.dp
                    )
                )

                val lazyListState = rememberLazyListState()
                LazyColumn(
                    state = lazyListState, modifier = Modifier.simpleVerticalScrollbar(
                        lazyListState, color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    items(state.ebookData!!.epubBook.chapters.size) { idx ->
                        val chapter = state.ebookData.epubBook.chapters[idx]
                        ChapterItem(chapterTitle = chapter.title) {
                            navController.navigate(
                                Screens.ReaderScreen.withBookId(
                                    bookId, idx = idx
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChapterItem(chapterTitle: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 12.dp),
                text = chapterTitle,
                fontFamily = figeronaFont,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Icon(
                modifier = Modifier
                    .size(15.dp)
                    .weight(0.4f),
                painter = painterResource(id = R.drawable.ic_right_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }


}


@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun EpubDetailScreenPV() {
    ReaderDetailScreen("", rememberNavController())
}