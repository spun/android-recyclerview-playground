package com.spundev.fixedheader.model

import java.text.SimpleDateFormat
import java.util.*


data class FixHeaderItemModel(val id: Int, val name: String, private val stringDate: String) {
    val calendar: Calendar
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val cal = Calendar.getInstance()
            sdf.parse(stringDate)?.let { date ->
                cal.time = date
            }
            return cal
        }
}