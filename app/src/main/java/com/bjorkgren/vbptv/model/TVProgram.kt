package com.bjorkgren.vbptv.model

class TVProgram(duration: String, headline: String) {
    var begins: String = ""
    var ends: String = ""
    var headline: String = ""

    var progress: Int = -1 //if not calculated
    var beginHour: Int = 0
    var beginMinute: Int = 0
    var endHour: Int = 0
    var endMinute: Int = 0

    var totalMinutes: Int = 0

    init{
        val timeparts = duration.split('-')
        this.begins = timeparts[0]
        this.ends = timeparts[1]
        this.headline = headline

        //parse the timeparts:
        val beginparts = this.begins.split('.')
        this.beginHour = beginparts[0].toInt()
        this.beginMinute = beginparts[1].toInt()
        val endparts = this.ends.split('.')
        this.endHour = endparts[0].toInt()
        this.endMinute = endparts[1].toInt()

        if(endHour < beginHour)
            endHour += 24

        this.totalMinutes = (endHour - beginHour)*60 + (endMinute - beginMinute)
    }

    fun setProgress(currentHour: Int, currentMinute: Int) {
        var nowHour = currentHour
        if(nowHour < this.beginHour)
            nowHour += 24

        //räkna ut skillnaden i minuter mellan start-tid och nuvarande tid
        val difference = (nowHour - this.beginHour)*60 + (currentMinute - this.beginMinute)
        //sätt this.progress till det.
        this.progress = 100 * difference / this.totalMinutes
    }
}