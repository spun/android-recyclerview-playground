package com.spundev.commonresources.data

import androidx.annotation.DrawableRes

data class ItemModel(val description: String, @DrawableRes val image: Int, val transitionName: String)