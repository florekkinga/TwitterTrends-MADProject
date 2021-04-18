package com.wdtm.twittertrends.models

import java.util.*

data class Query(val date: Date, val location: Location, val trends: List<Trend>)
