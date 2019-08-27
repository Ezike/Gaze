package dev.sasikanth.nasa.apod.data.source.remote

import dev.sasikanth.nasa.apod.data.APod
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APodApiService {

    @GET("planetary/apod")
    suspend fun getAPods(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<APod>>

    @GET("planetary/apod")
    suspend fun getAPod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Response<APod>
}
