package com.bignerdranch.android.baseball_kt.api

import com.bignerdranch.android.baseball_kt.SearchResponse
import com.bignerdranch.android.baseball_kt.VorpResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BaseballApi {
    @GET("search/{searchStr}")
    fun searchPlayer(@Path("searchStr") searchString: String): Call<SearchResponse>

    @GET("scrape/{startDate}/{endDate}/{id}")
    fun calcVorp(@Path("startDate", encoded=true) startDate: String, @Path("endDate", encoded=true) endDate: String, @Path("id") id: String): Call<VorpResponse>
}