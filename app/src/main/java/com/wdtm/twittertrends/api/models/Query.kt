package com.wdtm.twittertrends.api.models

import java.util.*

data class Query(val date: Date, val location: Location, val trends: List<Trend>)
