package com.example.yoospace_android.data.repository

import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.CommentsResponse
import com.example.yoospace_android.data.model.PostsResponse

class PostRepository {

    suspend fun getCurrentUserPosts(): PostsResponse {
        // This function will call the API to get the current user's posts
        return RetrofitInstance.api.getCurrentUserPosts()
    }

    suspend fun getCurrentUserLikedPosts(): PostsResponse {
        // This function will call the API to get the current user's liked posts
        return RetrofitInstance.api.getCurrentUserLikedPosts()
    }

    suspend fun getFeedPosts(): PostsResponse {
        // This function will call the API to get the feed posts
        return RetrofitInstance.api.getFeedPosts()
    }
    suspend fun getPostById(postId: String): PostsResponse {
        return RetrofitInstance.api.getPostById(postId)
    }
    suspend fun getCommentsByPostId(postId: String): CommentsResponse {
        return RetrofitInstance.api.getCommentsByPostId(postId)
    }
}