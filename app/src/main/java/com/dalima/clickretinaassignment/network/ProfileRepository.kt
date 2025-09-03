package com.dalima.clickretinaassignment.network

import com.dalima.clickretinaassignment.network.RetrofitClient
import com.dalima.clickretinaassignment.data.ProfileResponse
import retrofit2.Response

class ProfileRepository {
    private val api = RetrofitClient.apiService

    suspend fun getProfile(): Response<ProfileResponse> {
        return api.fetchProfileRaw()
    }
}