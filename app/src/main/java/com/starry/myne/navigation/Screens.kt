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

package com.starry.myne.navigation

const val BOOK_ID_ARG_KEY = "bookId"
const val CATEGORY_DETAIL_ARG_KEY = "category"
const val READER_CHAPTER_INDEX_KEY = "readerChapterIndex"

sealed class Screens(val route: String) {

    object OSLScreen : Screens("osl_screen")

    object AboutScreen : Screens("about_screen")

    object BookDetailScreen : Screens("book_detail_screen/{$BOOK_ID_ARG_KEY}") {
        fun withBookId(id: String): String {
            return this.route.replace("{$BOOK_ID_ARG_KEY}", id)
        }
    }

    object CategoryDetailScreen : Screens("category_detail_screen/{$CATEGORY_DETAIL_ARG_KEY}") {
        fun withCategory(category: String): String {
            return this.route.replace("{$CATEGORY_DETAIL_ARG_KEY}", category)
        }
    }

    object ReaderDetailScreen : Screens("reader_detail_screen/{$BOOK_ID_ARG_KEY}") {
        fun withBookId(id: String): String {
            return this.route.replace("{$BOOK_ID_ARG_KEY}", id)
        }
    }

    object ReaderScreen : Screens("reader_screen/{$BOOK_ID_ARG_KEY}/{$READER_CHAPTER_INDEX_KEY}") {
        fun withBookId(id: String, idx: Int = -1): String {
            return this.route.replace("{$BOOK_ID_ARG_KEY}", id)
                .replace("{$READER_CHAPTER_INDEX_KEY}", idx.toString())
        }
    }
}
