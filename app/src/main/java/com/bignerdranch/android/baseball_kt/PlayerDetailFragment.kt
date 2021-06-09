package com.bignerdranch.android.baseball_kt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter

private const val TAG = "PlayerDetailFragment"
private const val ARG_NAME = "player_name"
private const val ARG_ID = "player_id"

class PlayerDetailFragment : Fragment() {
    private lateinit var playerName: TextView
    private lateinit var playerId: String
    private lateinit var careerVorp: TextView
    private lateinit var threeSeasonsVorp: TextView
    private lateinit var thisYearVorp: TextView
    private lateinit var thisMonthVorp: TextView
    private lateinit var twoWeeksAgoVorp: TextView

    private val baseballViewModel: BaseballViewModel by lazy {
        ViewModelProviders.of(this).get(BaseballViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player_detail, container, false)
        playerName = view.findViewById(R.id.player_name)

        val pName: String = arguments?.getCharSequence(ARG_NAME) as String
        val pId: Int? = arguments?.getInt(ARG_ID)
        playerId = pId.toString()

        careerVorp = view.findViewById(R.id.career_vorp)
        threeSeasonsVorp = view.findViewById(R.id.three_season_vorp)
        thisYearVorp = view.findViewById(R.id.this_year_vorp)
        thisMonthVorp = view.findViewById(R.id.this_month_vorp)
        twoWeeksAgoVorp = view.findViewById(R.id.two_weeks_ago_vorp)

        playerName.text = pName

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val todayDate: LocalDate = LocalDate.now()

        // Calculate careerDate, 3 years ago, Jan 1 of this year, start of this month, 2 weeks ago's date
        val careerDate: LocalDate = LocalDate.of(1900,1, 1)
        val threeSeasonsDate: LocalDate = calcThreeSeasonsDate(todayDate)
        val thisYearDate: LocalDate = calcThisYear(todayDate)
        val thisMonthDate: LocalDate = calcThisMonth(todayDate)
        val twoWeeksAgoDate: LocalDate = todayDate.minusDays(14)

        // Convert dates to Strings
        val todayDateStr: String = todayDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))
        val careerDateStr: String = careerDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))
        val threeSeasonsDateStr: String = threeSeasonsDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))
        val thisYearDateStr: String = thisYearDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))
        val thisMonthDateStr: String = thisMonthDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))
        val twoWeeksAgoDateStr: String = twoWeeksAgoDate.format(DateTimeFormatter.ofPattern("MM/dd/y"))

        // Set observer for career VORP and pass in careerDateStr (startDate), todayDateStr (endDate), playerId (id)
        baseballViewModel.careerLiveData.observe(viewLifecycleOwner, Observer { careerVorpResponse ->
            careerVorp.text = careerVorpResponse
        })

        baseballViewModel.threeSeasonsLiveData.observe(viewLifecycleOwner, Observer { threeSeasonsVorpResponse ->
            threeSeasonsVorp.text = threeSeasonsVorpResponse
        })

        baseballViewModel.thisYearLiveData.observe(viewLifecycleOwner, Observer { thisYearVorpResponse ->
            thisYearVorp.text = thisYearVorpResponse
        })

        baseballViewModel.thisMonthLiveData.observe(viewLifecycleOwner, Observer { thisMonthVorpResponse ->
            thisMonthVorp.text = thisMonthVorpResponse
        })

        baseballViewModel.twoWeeksAgoLiveData.observe(viewLifecycleOwner, Observer { twoWeeksAgoVorpResponse ->
            twoWeeksAgoVorp.text = twoWeeksAgoVorpResponse
        })

        // Trigger observers
        baseballViewModel.getCareerVorp(careerDateStr, todayDateStr, playerId)
        baseballViewModel.getThreeSeasonsVorp(threeSeasonsDateStr, todayDateStr, playerId)
        baseballViewModel.getThisYearVorp(thisYearDateStr, todayDateStr, playerId)
        baseballViewModel.getThisMonthVorp(thisMonthDateStr, todayDateStr, playerId)
        baseballViewModel.getTwoWeeksAgoVorp(twoWeeksAgoDateStr, todayDateStr, playerId)
    }

    fun calcThreeSeasonsDate(todayDate: LocalDate): LocalDate {
        val currYear: Int = todayDate.year
        val threeYearsAgo: Int = currYear - 3
        return (LocalDate.of(threeYearsAgo, 1, 1))
    }

    fun calcThisYear(todayDate: LocalDate): LocalDate {
        val currYear: Int = todayDate.year
        return(LocalDate.of(currYear, 1, 1))
    }

    fun calcThisMonth(todayDate: LocalDate): LocalDate {
        val currYear: Int = todayDate.year
        val thisMonth: Int = todayDate.monthValue
        return(LocalDate.of(currYear, thisMonth, 1))
    }

    companion object {
        fun newInstance(playerName: String, playerId: Int): PlayerDetailFragment {
            val args = Bundle().apply {
                putCharSequence(ARG_NAME, playerName)
                putInt(ARG_ID, playerId)
            }
            return PlayerDetailFragment().apply {
                arguments = args
            }
        }
    }
}