package com.bignerdranch.android.baseball_kt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.baseball_kt.api.BaseballApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "BaseballFetchr"

// Repository class to handle interactions with API
class BaseballFetchr {
    private val baseballApi: BaseballApi

    // Set up Retrofit object
    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("http://xxx.xx.xx.xxx:3308/").addConverterFactory(GsonConverterFactory.create()).build()
        baseballApi = retrofit.create(BaseballApi::class.java)
    }

    // Send request to search API and handle response
    fun searchPlayer(searchStr: String): LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val baseballRequest: Call<SearchResponse> = baseballApi.searchPlayer(searchStr)

        // Send request to API endpoint (wrapped in BaseballApi)
        baseballRequest.enqueue(object : Callback<SearchResponse> {

            // Handle failed requests
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, "Failed to search text", t)
            }

            // Handle successful responses
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                // Parse out name and id from response and return it
                val searchResponse: SearchResponse? = response.body()
                val name: String = searchResponse?.Name ?: "Yoenis Cespedes"
                val id: Int = searchResponse?.Id ?: 13110

                // Construct concatenated string of name,id to return via LiveData
                val searchRes = name + "," + id.toString()
                responseLiveData.value = searchRes
            }
        })
        return responseLiveData
    }

    // Add function to calculate player VORP
    fun calcVorp(startDate: String, endDate: String, id: String): LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()

        // Replace forward slash ("/") in startDate and endDate with "%2F, then pass into baseballApi.calcVorp()
        val startDateEscaped: String = startDate.replace("/", "%2F")
        val endDateEscaped: String = endDate.replace("/", "%2F")
        val vorpRequest: Call<VorpResponse> = baseballApi.calcVorp(startDateEscaped, endDateEscaped, id)

        vorpRequest.enqueue(object : Callback<VorpResponse> {

            override fun onFailure(call: Call<VorpResponse>, t: Throwable) {
                Log.e(TAG, "Failed to calculate VORP", t)
            }

            override fun onResponse(call: Call<VorpResponse>, response: Response<VorpResponse>) {
                val vorpResponse: VorpResponse? = response.body()
                val vorp: String = vorpResponse?.Vorp ?: "0.00"

                responseLiveData.value = vorp
            }
        })
        return responseLiveData
    }
}