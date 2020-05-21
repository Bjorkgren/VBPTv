package com.bjorkgren.vbptv.model

class TVProgram(duration: String, headline: String) {
    var begins: String = ""
    var ends: String = ""
    var headline: String = ""
    var progress: Int = -1 //if not calculated

    init{
        val timeparts = duration.split('-')
        this.begins = timeparts[0]
        this.ends = timeparts[1]
        this.headline = headline

        //parse the timeparts:

    }
}