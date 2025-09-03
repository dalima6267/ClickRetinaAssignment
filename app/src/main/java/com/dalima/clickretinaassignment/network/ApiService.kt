package com.dalima.clickretinaassignment.network

import com.dalima.clickretinaassignment.data.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("data.json")
    suspend fun fetchProfileRaw(): Response<ProfileResponse>
}