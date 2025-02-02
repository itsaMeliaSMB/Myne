/*
Copyright 2022 - 2023 Stɑrry Shivɑm

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.starry.myne.ui.screens.home.viewmodels

import android.annotation.SuppressLint
import android.os.Environment
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import com.starry.myne.MainActivity
import com.starry.myne.R
import com.starry.myne.api.BooksApi
import com.starry.myne.api.models.Book
import com.starry.myne.api.models.BookSet
import com.starry.myne.api.models.ExtraInfo
import com.starry.myne.database.library.LibraryDao
import com.starry.myne.database.library.LibraryItem
import com.starry.myne.others.BookDownloader
import com.starry.myne.others.Constants
import com.starry.myne.utils.BookUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailScreenState(
    val isLoading: Boolean = true,
    val item: BookSet = BookSet(0, null, null, emptyList()),
    val extraInfo: ExtraInfo = ExtraInfo(),
    val bookLibraryItem: LibraryItem? = null
)

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    val libraryDao: LibraryDao, val bookDownloader: BookDownloader
) : ViewModel() {
    var state by mutableStateOf(BookDetailScreenState())
    fun getBookDetails(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookItem = BooksApi.getBookById(bookId).getOrNull()!!
            val extraInfo = BooksApi.getExtraInfo(bookItem.books.first().title)
            state = if (extraInfo != null) {
                state.copy(item = bookItem, extraInfo = extraInfo)
            } else {
                state.copy(item = bookItem)
            }
            state = state.copy(
                bookLibraryItem = libraryDao.getItemById(bookId.toInt()), isLoading = false
            )
        }
    }

    @SuppressLint("Range")
    fun downloadBook(
        book: Book, activity: MainActivity, downloadProgressListener: (Float, Int) -> Unit
    ): String {
        return if (activity.checkStoragePermission()) {
            bookDownloader.downloadBook(book = book,
                downloadProgressListener = downloadProgressListener,
                onDownloadSuccess = {
                    insertIntoDB(book, bookDownloader.getFilenameForBook(book))
                    state = state.copy(bookLibraryItem = libraryDao.getItemById(book.id))
                })
            activity.getString(R.string.downloading_book)
        } else {
            activity.getString(R.string.storage_perm_error)
        }
    }

    private fun insertIntoDB(book: Book, filename: String) {
        val libraryItem = LibraryItem(
            book.id,
            book.title,
            BookUtils.getAuthorsAsString(book.authors),
            "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/${Constants.DOWNLOAD_DIR}/$filename",
            System.currentTimeMillis()
        )
        libraryDao.insert(libraryItem)
    }
}