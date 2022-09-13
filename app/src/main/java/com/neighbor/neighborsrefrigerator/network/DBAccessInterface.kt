package com.neighbor.neighborsrefrigerator.network

import com.neighbor.neighborsrefrigerator.data.*
import retrofit2.http.*

interface DBAccessInterface {
    @GET("/post/getPostByUserId")
    fun getPostByUserId(
        @Query("userId") userId: Int
    ): retrofit2.Call<ReturnObject<ArrayList<PostData>>>

    @GET("/post/getPostOrderByTime")
    suspend fun getPostOrderByTime(
        @Query("page") page: Int,
        @Query("reqType") reqType: Int,
        @Query("postType") postType: Int,
        @Query("categoryId") categoryId: Int?,
        @Query("title") title: String?,
        @Query("currentTime") currentTime: String,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): ReturnObject<ArrayList<PostData>>

    @POST("/user/updateFcmToken")
    fun updateFcmToken(
        @Body dataTransferObject: DataTransferObject<String>
    ): retrofit2.Call<ReturnObject<Int>>

    @GET("/post/getInfoById")
    fun getPostById(
        @Query("id") id : Int
    ): retrofit2.Call<ReturnObject<ArrayList<PostData>>>

    @POST("/post/completeTrade")
    fun completeTrade(
        @Body postData : PostData
    ): retrofit2.Call<ReturnObject<Int>>

    @POST("/post/sendMail")
    fun sendMail(
        @Body reportData : MailData
    ): ReturnObject<Int>


    @POST("/post/create")
    suspend fun entryPost(
        @Body postData: PostData
    ): ReturnObject<Int>

    @POST("/post/review")
    fun reviewPost(
        @Body reviewData: ReviewData
    ): retrofit2.Call<ReturnObject<Int>>

    @GET("/user/getInfoById")
    suspend fun getUserInfoById(
        @Query("id") id : Int
    ) : ReturnObject<ArrayList<UserData>>

    @GET("/user/getInfoByFbId")
    suspend fun getUserInfoByFbId(
        @Query("fbId") id : String
    ) : ReturnObject<ArrayList<UserData>>

    @POST("/user/join")
    suspend fun userJoin(
        @Body userData: UserData
    ): ReturnObject<Int>

    @GET("/user/checkNickname")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): ReturnObject<Boolean>

    @GET("/user/hasFbId")
    suspend fun hasFbId(
        @Query("id") id: String
    ): ReturnObject<Boolean>

    @POST("post/updateFcmToken")
    fun updateFcmToken(
        @Body userdata : UserData
    ) : retrofit2.Call<ReturnObject<Int>>

    @POST("/user/updateNickname")
    fun updateNickname(
        @Body dataTransferObject: DataTransferObject<String>
    ) : retrofit2.Call<ReturnObject<Int>>

    @DELETE("/user")
    suspend fun deleteUser(
        @Query("id") id: Int
    ): ReturnObject<Boolean>


}