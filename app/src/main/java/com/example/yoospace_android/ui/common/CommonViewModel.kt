package com.example.yoospace_android.ui.common

import androidx.lifecycle.ViewModel
import com.example.yoospace_android.data.repository.PostRepository

class CommonViewModel: ViewModel() {
    val postRepository = PostRepository()
}