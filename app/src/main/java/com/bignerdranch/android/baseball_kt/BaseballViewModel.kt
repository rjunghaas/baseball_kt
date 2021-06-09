package com.bignerdranch.android.baseball_kt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "BaseballViewModel"

// ViewModel class to store player data
class BaseballViewModel : ViewModel() {
    private val baseballFetchr = BaseballFetchr()
    private var player: Player = Player()
    private var searchQuery = MutableLiveData<String>()
    var searchLiveData: LiveData<String>
    private var vorpQuery = MutableLiveData<String>()
    var vorpLiveData: LiveData<String>

    // Properties for PlayerDetailFragment
    private var careerVorpQuery = MutableLiveData<String>()
    var careerLiveData: LiveData<String>
    private var threeSeasonsVorpQuery = MutableLiveData<String>()
    var threeSeasonsLiveData: LiveData<String>
    private var thisYearVorpQuery = MutableLiveData<String>()
    var thisYearLiveData: LiveData<String>
    private var thisMonthVorpQuery = MutableLiveData<String>()
    var thisMonthLiveData: LiveData<String>
    private var twoWeeksAgoVorpQuery = MutableLiveData<String>()
    var twoWeeksAgoLiveData: LiveData<String>

    // Set up Transformation that will listen for changes to query LiveData, call baseballFetchr.searchPlayer() on any changes, and set result to searchLiveData
    init {
        searchLiveData = Transformations.switchMap(searchQuery) { searchStr ->
            baseballFetchr.searchPlayer(searchStr)
        }

        vorpLiveData = Transformations.switchMap(vorpQuery) {  vorpQueryStr ->
            val vorpQueryList: List<String> = vorpQueryStr.split(",")
            val startDate: String = vorpQueryList[0]
            val endDate: String = vorpQueryList[1]
            val id: String = vorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }

        careerLiveData = Transformations.switchMap(careerVorpQuery) { careerVorpQueryStr ->
            val careerVorpQueryList: List<String> = careerVorpQueryStr.split(",")
            val startDate: String = careerVorpQueryList[0]
            val endDate: String = careerVorpQueryList[1]
            val id: String = careerVorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }

        threeSeasonsLiveData = Transformations.switchMap(threeSeasonsVorpQuery) { threeSeasonsVorpQueryStr ->
            val threeSeasonsVorpQueryList: List<String> = threeSeasonsVorpQueryStr.split(",")
            val startDate: String = threeSeasonsVorpQueryList[0]
            val endDate: String = threeSeasonsVorpQueryList[1]
            val id: String = threeSeasonsVorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }

        thisYearLiveData = Transformations.switchMap(thisYearVorpQuery) { thisYearVorpQueryStr ->
            val thisYearVorpQueryList: List<String> = thisYearVorpQueryStr.split(",")
            val startDate: String = thisYearVorpQueryList[0]
            val endDate: String = thisYearVorpQueryList[1]
            val id: String = thisYearVorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }

        thisMonthLiveData = Transformations.switchMap(thisMonthVorpQuery) { thisMonthVorpQueryStr ->
            val thisMonthVorpQueryList: List<String> = thisMonthVorpQueryStr.split(",")
            val startDate: String = thisMonthVorpQueryList[0]
            val endDate: String = thisMonthVorpQueryList[1]
            val id: String = thisMonthVorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }

        twoWeeksAgoLiveData = Transformations.switchMap(twoWeeksAgoVorpQuery) { twoWeeksAgoVorpQueryStr ->
            val twoWeeksAgoVorpQueryList: List<String> = twoWeeksAgoVorpQueryStr.split(",")
            val startDate: String = twoWeeksAgoVorpQueryList[0]
            val endDate: String = twoWeeksAgoVorpQueryList[1]
            val id: String = twoWeeksAgoVorpQueryList[2]

            baseballFetchr.calcVorp(startDate, endDate, id)
        }
    }

    // When search is called, we set the latest searchStr that user has provided to update value of query which will trigger Transformations listener
    fun search(searchStr: String) {
        searchQuery.value = searchStr
    }

    fun getVorp(startDate: String, endDate: String, id: String) {
        val vorpQueryStr: String = startDate + "," + endDate + "," + id
        vorpQuery.value = vorpQueryStr
    }

    // Update name, id for current Player
    fun setPlayer(str: String, id: Int) {
        player.name = str
        player.num = id
    }

    // Return name of current Player
    fun getPlayer(): String {
        return player.name
    }

    // Return id of current Player
    fun getPlayerId(): Int {
        return player.num
    }

    // Set player.startDate or player.endDate; if startOrEnd is true, set startDate.  Assumes we have already checked that it is a valid date.
    fun setDate(dateStr: String, startOrEnd: Boolean) {
        // convert to Date format
        val dateList = dateStr.split("/")
        val month: Int = dateList[0].toInt()
        val day: Int = dateList[1].toInt()
        val year: Int = dateList[2].toInt()
        val date: LocalDate = LocalDate.of(year, month, day)

        if(startOrEnd){
            player.startDate = date
        } else if(!startOrEnd) {
            // Make sure endDate is after startDate.  If so, set player.endDate
            val startDate: LocalDate = player.startDate
            if(date.isAfter(startDate)){
                player.endDate = date
            }
        }
        return
    }

    fun getStartDate(): String {
        val startDate: LocalDate = player.startDate
        return(startDate.format(DateTimeFormatter.ofPattern("MM/dd/y")))
    }

    fun getEndDate(): String {
        val endDate: LocalDate = player.endDate
        return(endDate.format(DateTimeFormatter.ofPattern("MM/dd/y")))
    }

    fun getCareerVorp(startDate: String, endDate: String, id: String) {
        val careerVorpQueryStr: String = startDate + "," + endDate + "," + id
        careerVorpQuery.value = careerVorpQueryStr
    }

    fun getThreeSeasonsVorp(startDate: String, endDate: String, id: String) {
        val threeSeasonsVorpQueryStr: String = startDate + "," + endDate + "," + id
        threeSeasonsVorpQuery.value = threeSeasonsVorpQueryStr
    }

    fun getThisYearVorp(startDate: String, endDate: String, id: String) {
        val thisYearVorpQueryStr: String = startDate + "," + endDate + "," + id
        thisYearVorpQuery.value = thisYearVorpQueryStr
    }

    fun getThisMonthVorp(startDate: String, endDate: String, id: String) {
        val thisMonthVorpQueryStr: String = startDate + "," + endDate + "," + id
        thisMonthVorpQuery.value = thisMonthVorpQueryStr
    }

    fun getTwoWeeksAgoVorp(startDate: String, endDate: String, id: String) {
        val twoWeeksAgoVorpQueryStr: String = startDate + "," + endDate + "," + id
        twoWeeksAgoVorpQuery.value = twoWeeksAgoVorpQueryStr
    }
}