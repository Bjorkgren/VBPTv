package com.bjorkgren.vbptv.model

class TVChannel {
    var programs: MutableList<TVProgram> = mutableListOf<TVProgram>()

    fun addProgram(begins: String, headline: String){
        programs.add(TVProgram(begins, headline))
    }
}