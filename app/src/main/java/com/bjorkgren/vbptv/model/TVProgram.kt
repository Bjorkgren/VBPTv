package com.bjorkgren.vbptv.model

class TVProgram(begins: String, headline: String) {
    var begins: String = ""
    var headline: String = ""

    init{
        this.begins = begins
        this.headline = headline
    }
}