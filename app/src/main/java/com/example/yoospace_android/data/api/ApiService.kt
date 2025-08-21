package com.example.yoospace_android.data.api

import com.example.yoospace_android.data.model.Comment
import com.example.yoospace_android.data.model.CommentCreateRequest
import com.example.yoospace_android.data.model.Conversation
import com.example.yoospace_android.data.model.ConversationItemModel
import com.example.yoospace_android.data.model.Group
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.DiscoverUserData
import com.example.yoospace_android.data.model.FollowDetail
import com.example.yoospace_android.data.model.GroupConversationData
import com.example.yoospace_android.data.model.GroupInvite
import com.example.yoospace_android.data.model.Like
import com.example.yoospace_android.data.model.LoginData
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.Message
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.model.RefreshTokenResponse
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisteredUser
import com.example.yoospace_android.data.model.Response
import com.example.yoospace_android.data.model.SearchBody
import com.example.yoospace_android.data.model.SendMessageRequest
import okhttp3.MultipartBody
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
    ): Response<LoginData>

    @GET("/api/users/current-user")
    suspend fun getCurrentUser(): Response<CurrentUser>

    @POST("/api/refreshToken")
    fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Call<RefreshTokenResponse>

    @POST("/api/users/logout")
    suspend fun logoutUser()

    @POST("/api/users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisteredUser>

    @GET("/api/users/followers")
    suspend fun getFollowers(): Response<List<FollowDetail>>

    @GET("/api/users/followings")
    suspend fun getFollowing(): Response<List<FollowDetail>>

    @GET("/api/users/discover")
    suspend fun discoverUsers(): Response<DiscoverUserData>

    @POST("/api/users/search")
    suspend fun searchUsers(
        @Body inputValue: SearchBody
    ): Response<DiscoverUserData>

    @PATCH("/api/users/update-profile")
    suspend fun updateProfile(@Body body: MultipartBody): Response<CurrentUser>

//    dynamic user route

    @GET("/api/users/profile/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: String
    ): Response<CurrentUser>

    @GET("/api/users/followers/{userId}")
    suspend fun getUserFollowers(
        @Path("userId") userId: String
    ): Response<List<FollowDetail>>

    @GET("/api/users/followings/{userId}")
    suspend fun getUserFollowing(
        @Path("userId") userId: String
    ): Response<List<FollowDetail>>

    @PATCH("/api/users/follow/{targetId}")
    suspend fun followUser(
        @Path("targetId") targetId: String
    )

    @PATCH("/api/users/unfollow/{targetId}")
    suspend fun unfollowUser(
        @Path("targetId") targetId: String
    )

//    posts-route

    @GET("/api/posts/currentUser")
    suspend fun getCurrentUserPosts(): Response<List<Post>>

    @GET("/api/posts/user-likes")
    suspend fun getCurrentUserLikedPosts(): Response<List<Post>>

    @GET("/api/posts/feed")
    suspend fun getFeedPosts(): Response<List<Post>>

    @POST("/api/posts/create")
    suspend fun createPost(@Body body: MultipartBody)
//    dynamic posts route

    @GET("/api/posts/post/{postId}")
    suspend fun getPostById(
        @Path("postId") postId: String
    ): Response<List<Post>>

    @GET("/api/posts/user/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") userId: String
    ): Response<List<Post>>

    @GET("/api/posts/comments/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: String
    ): Response<List<Comment>>

    @PATCH("/api/posts/like/{postId}")
    suspend fun likePost(
        @Path("postId") postId: String
    )

    @PATCH("/api/posts/unlike/{postId}")
    suspend fun unlikePost(
        @Path("postId") postId: String
    )

    @GET("/api/posts/likes-on/{postId}")
    suspend fun getWhoLiked(
        @Path("postId") postId: String
    ): Response<List<Like>>

    //comments route
    @PATCH("/api/posts/comment/like/{commentId}")
    suspend fun likeComment(
        @Path("commentId") commentId: String
    )

    @PATCH("/api/posts/comment/unlike/{commentId}")
    suspend fun unlikeComment(
        @Path("commentId") commentId: String
    )

    @POST("/api/posts/comment-on/{postId}")
    suspend fun commentOnPost(
        @Path("postId") postId: String,
        @Body request: CommentCreateRequest
    ): Response<Comment>


    //messages route
    @GET("/api/messages/all-conversation")
    suspend fun getAllConversations(): Response<List<ConversationItemModel>>

    @GET("/api/messages/conversation/{targetId}")
    suspend fun getDirectConversationById(
        @Path("targetId") targetId: String
    ): Response<Conversation>
    @GET("/api/messages/conversation-id/{conversationId}")
    suspend fun getGroupConversationById(
        @Path("conversationId") conversationId: String
    ): Response<GroupConversationData>


    @GET("/api/messages/all-messages/{conversationId}")
    suspend fun getAllMessages(
        @Path("conversationId") conversationId: String
    ): Response<List<Message>>

    @POST("/api/messages/send/{conversationId}")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body request: SendMessageRequest
    ): Response<Message>

    @PATCH("/api/messages/set-read/{messageId}")
    suspend fun setMessageRead(
        @Path("messageId") messageId: String
    )
    @POST("/api/messages/create-group")
    suspend fun createGroupConversation(
        @Body body: MultipartBody
    ): Response<Group>

    @GET("/api/messages/group-invites")
    suspend fun getGroupInvites(): Response<List<GroupInvite>>
    @PATCH("/api/messages/group-invite-accept/{conversationId}")
    suspend fun acceptGroupInvite(
        @Path("conversationId") conversationId: String
    ): Response<Group>
}