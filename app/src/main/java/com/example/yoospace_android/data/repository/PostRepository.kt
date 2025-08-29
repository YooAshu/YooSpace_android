package com.example.yoospace_android.data.repository

import android.util.Log
import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.Comment
import com.example.yoospace_android.data.model.CommentCreateRequest
import com.example.yoospace_android.data.model.Like
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.model.Response
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor() {

    private var feedPosts: List<Post>? = null
    suspend fun getCurrentUserPosts(): Response<List<Post>> {
        // This function will call the API to get the current user's posts
        return RetrofitInstance.api.getCurrentUserPosts()
    }

    suspend fun getCurrentUserLikedPosts(): Response<List<Post>> {
        // This function will call the API to get the current user's liked posts
        return RetrofitInstance.api.getCurrentUserLikedPosts()
    }

    suspend fun getFeedPosts(forceRefresh: Boolean = false): List<Post> {
        Log.d("PostRepository", "Fetching feed posts Feed posts fetched: ${
            feedPosts?.size}")
        // This function will call the API to get the feed posts
        if (feedPosts == null || forceRefresh) {
            feedPosts = RetrofitInstance.api.getFeedPosts().data
            Log.d("PostRepository", "Feed posts fetched: ${feedPosts?.size}")
        }
        return feedPosts!!
    }
    suspend fun getPostById(postId: String): Response<List<Post>> {
        return RetrofitInstance.api.getPostById(postId)
    }
    suspend fun getCommentsByPostId(postId: String): Response<List<Comment>> {
        return RetrofitInstance.api.getCommentsByPostId(postId)
    }

    suspend fun getPostsByUserId(userId: String): Response<List<Post>> {
        // This function will call the API to get posts by a specific user ID
        return RetrofitInstance.api.getPostsByUserId(userId)
    }

    suspend fun likePost(postId: String) {
        // This function will call the API to like a post
        return RetrofitInstance.api.likePost(postId)
    }
    suspend fun unlikePost(postId: String) {
        // This function will call the API to unlike a post
        return RetrofitInstance.api.unlikePost(postId)
    }
    suspend fun likeComment(commentId: String) {
        // This function will call the API to like a comment
        return RetrofitInstance.api.likeComment(commentId)
    }
    suspend fun unlikeComment(commentId: String) {
        // This function will call the API to unlike a comment
        return RetrofitInstance.api.unlikeComment(commentId)
    }
    suspend fun commentOnPost(postId: String, request: CommentCreateRequest): Response<Comment> {
        // This function will call the API to comment on a post
        return RetrofitInstance.api.commentOnPost(postId,request )
    }

    suspend fun getWhoLikedPost(postId: String): Response<List<Like>> {
        // This function will call the API to get the list of users who liked the post
        return RetrofitInstance.api.getWhoLiked(postId)
    }

    suspend fun createPost(body: MultipartBody) {
        // This function will call the API to create a new post
        return RetrofitInstance.api.createPost(body)
    }
}