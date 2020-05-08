package com.bjorkgren.vbptv.model

class TVChannel(name: String) {
    var programs: MutableList<TVProgram> = mutableListOf<TVProgram>()
    var name: String = ""

    init{
        this.name = name
    }
    fun addProgram(begins: String, headline: String){
        programs.add(TVProgram(begins, headline))
    }
}