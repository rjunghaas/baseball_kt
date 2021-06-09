package com.bignerdranch.android.baseball_kt

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

private const val TAG = "BaseballFragment"

// Fragment for player search and VORP calculation
class BaseballFragment : Fragment() {
    interface Callbacks {
        fun onPlayerSelected(playerName: String, playerId: Int)
    }

    private var callbacks: Callbacks? = null
    private lateinit var playerSearch: EditText
    private lateinit var playerName: TextView
    private lateinit var submitButton: Button
    private lateinit var detailButton: Button
    private lateinit var startDateField: EditText
    private lateinit var endDateField: EditText
    private lateinit var vorpResult: TextView

    private val baseballViewModel: BaseballViewModel by lazy {
        ViewModelProviders.of(this).get(BaseballViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.fragment_baseball, container, false)
        playerSearch = view.findViewById(R.id.player_name_search) as EditText
        playerName = view.findViewById(R.id.player_name_result) as TextView

        startDateField = view.findViewById(R.id.start_date_field)
        endDateField = view.findViewById(R.id.end_date_field)
        submitButton = view.findViewById(R.id.submit_button)
        detailButton = view.findViewById(R.id.detail_button)
        vorpResult = view.findViewById(R.id.vorp_result) as TextView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an observer on baseballViewModel.searchLiveData that will take any changes to LiveData, get name and id values, set current Player, and update UI
        baseballViewModel.searchLiveData.observe(viewLifecycleOwner, Observer { searchResponse ->
            // Separate searchResponse into name and id
            val resList = searchResponse.split(",")
            val name = resList[0]
            val id = resList[1].toInt()

            baseballViewModel.setPlayer(name, id)
            playerName.text = baseballViewModel.getPlayer()
        })

        // Set an observer on baseballViewModel.vorpLiveData that will take any changes to LiveData and get updated VORP value to make changes to UI
        baseballViewModel.vorpLiveData.observe(viewLifecycleOwner, Observer { vorpResponse ->
            vorpResult.text = vorpResponse
        })
    }

    // Set up a TextWatcher on the Player Search EditText that will call baseballViewModel.search() when it is changed
    override fun onStart() {
        super.onStart()

        // Add observer for searchText that will trigger baseballViewModel.search() when user enters text
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This space intentionally left blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                baseballViewModel.search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // This space intentionally left blank
            }
        }

        // setOnClickListeners for startDateField and endDateField to validate that text entered is a valid date, once it is valid, save it to BaseballViewModel
        val startDateTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This space intentionally left blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check that text is a valid date
                val startDateStr: String = s.toString()
                if(validateDate(startDateStr)){
                    // if text is valid date, add to startDate
                    baseballViewModel.setDate(startDateStr, true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // This space intentionally left blank
            }
        }

        val endDateTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This space intentionally left blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check that text is a valid date
                val endDateStr: String = s.toString()
                if(validateDate(endDateStr)){
                    // if text is valid date, add to startDate
                    baseballViewModel.setDate(endDateStr, false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // This space intentionally left blank
            }
        }

        submitButton.setOnClickListener {
            // Get values from startDateField and endDateField
            val startDate: String = baseballViewModel.getStartDate()
            val endDate: String = baseballViewModel.getEndDate()

            // Get current player id and pass id, startDate, and endDate to BaseballViewModel.getVorp()
            val id: String = baseballViewModel.getPlayerId().toString()
            baseballViewModel.getVorp(startDate, endDate, id)
        }

        detailButton.setOnClickListener {
            val playerName: String = baseballViewModel.getPlayer()
            val playerId: Int = baseballViewModel.getPlayerId()
            callbacks?.onPlayerSelected(playerName, playerId)
        }

        playerSearch.addTextChangedListener(searchTextWatcher)
        startDateField.addTextChangedListener(startDateTextWatcher)
        endDateField.addTextChangedListener(endDateTextWatcher)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    fun validateDate(dateStr: String): Boolean {
        val dateList: List<String> = dateStr.split("/")

        if(dateList.size == 3) {
            val month: String = dateList[0]
            val day: String = dateList[1]
            var year: String = dateList[2]

            // Handle if year is empty, so toInt() in if-clause does not crash app
            if(year == ""){
                year = "0"
            }

            // Check that there are 3 elements (month, day, year) and that each of their values makes sense
            if(dateList.size == 3 && year.toInt() >= 1900 && month.toInt() <= 12 && day.toInt() <= 31) {
                return true
            }
        }
        return false
    }
}