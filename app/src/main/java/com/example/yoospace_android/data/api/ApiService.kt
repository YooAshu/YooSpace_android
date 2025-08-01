package com.example.yoospace_android.data.api

import com.example.yoospace_android.data.model.CommentsResponse
import com.example.yoospace_android.data.model.PostsResponse
import com.example.yoospace_android.data.model.CurrentUserResponse
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.LoginResponse
import com.example.yoospace_android.data.model.RefreshTokenResponse
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/api/users/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("/api/users/current-user")
    suspend fun getCurrentUser(): CurrentUserResponse

    @POST("/api/refreshToken")
    fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Call<RefreshTokenResponse>

    @POST("/api/users/logout")
    suspend fun logoutUser()

    @POST("/api/users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): RegisterResponse

//    dynamic user route

    @GET("/api/users/profile/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: String
    ): CurrentUserResponse

//    posts-route

    @GET("/api/posts/currentUser")
    suspend fun getCurrentUserPosts(): PostsResponse

    @GET("/api/posts/user-likes")
    suspend fun getCurrentUserLikedPosts(): PostsResponse

    @GET("/api/posts/feed")
    suspend fun getFeedPosts(): PostsResponse

//    dynamic posts route

    @GET("/api/posts/post/{postId}")
    suspend fun getPostById(
        @Path("postId") postId: String
    ): PostsResponse

    @GET("/api/posts/user/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") userId: String
    ): PostsResponse

    @GET("/api/posts/comments/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: String
    ): CommentsResponse

    @PATCH("/api/posts/like/{postId}")
    suspend fun likePost(
        @Path("postId") postId: String
    )

    @PATCH("/api/posts/unlike/{postId}")
    suspend fun unlikePost(
        @Path("postId") postId: String
    )


}