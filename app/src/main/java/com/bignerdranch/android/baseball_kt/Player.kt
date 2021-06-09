package com.bignerdranch.android.baseball_kt

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Month

data class Player (
    var name: String = "",
    var num: Int = 0,
    var startDate: LocalDate = LocalDate.of(1900, Month.JANUARY,1),
    var endDate: LocalDate = LocalDate.now(),
    //var vorp: Double = 0.00
)