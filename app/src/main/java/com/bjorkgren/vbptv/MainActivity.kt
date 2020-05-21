package com.bjorkgren.vbptv

import android.app.ActionBar
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bjorkgren.vbptv.model.TVChannel
import com.bjorkgren.vbptv.model.TVProgram

class MainActivity : AppCompatActivity() {

    val wantedChannels = listOf("SVT1", "SVT2", "TV3", "TV4", "Kanal 5")
    var textTvPages = listOf(650, 651, 652, 653)
    val schedule = hashMapOf<String, MutableList<TVProgram>>()
    var lastAddedChannel = ""
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<ProgressBar>(R.id.progress)
        colorizeUI()
        setupClickEvents()
    }

    override fun onResume() {
        super.onResume()
        //Initiate the schedule
        for(wanted in wantedChannels){
            schedule[wanted] = mutableListOf<TVProgram>()
        }
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        //Fetch pages in order to avoid the async sort of lists n stuff
        //Warning, this is a bit recursive...
        progressBar.visibility = ProgressBar.VISIBLE
        fetchPages(queue, textTvPages)
    }

    fun fetchPages(queue: RequestQueue, pages: List<Int>){
        Log.e("fetches", "FETCH ${pages.first()}")
        queue.add(
            StringRequest(
                Request.Method.GET, "https://www.svt.se/svttext/webL/pages/${pages.first()}.html",
                Response.Listener<String> { response ->
                    parseSVTTextPage(response)
                    progressBar.setProgress(5 - pages.size, true)
                    updateUI(0)
                    if(pages.size > 1){
                        fetchPages(queue, pages.drop(1))
                    }else{
                        progressBar.visibility = ProgressBar.GONE
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error", error.message)
                })
        )
    }

    //TODO: Skapa en linear layout per kanal, som bara visar program 0, med tid,
    // håller man ner nånstans i uit så visas nästkommande tills man släpper

    //TODO: framtida funktionalitet:
    //Första tab:en är "pågående program", ett program per kanal, precis som de ser ut på SVT text 650
    //Nästa tab:en är för SVT1, sen en tab per kanal
    //      pga att vissa kanaler har många korta program (SVT2) o vissa har få o långa...


    //Parsing from svt.se/svttext
    fun parseSVTTextPage(resultFromSvtPage: String){
        val rows = resultFromSvtPage.split('\n')
        var parsing = false
        for(row in rows){
            if(row.contains("Fortsättning följer på nästa")) {
                break
            }
            if(!parsing){
                parsing = row.contains("SVT Texts programguide")
            }else{
                parseLine(row)
            }
        }
    }

    fun parseLine(line: String){
        //  14.20-14.50 <span class="G">Husdrömmar             </span><span class="W">SVT1</span>
        //  <span class="G">           Sicilien <a href="199.html">199</a></span><span class="Y">               </span>
        //  16.05-16.15 <span class="G">Sverige idag på        </span><span class="W">SVT2</span>
        //  <span class="G">           meänkieli                  </span>

        if(line.contains("<span ")){
            val trimmed = line.trim()
            Log.w("LINE", trimmed)
            val continuation = "<span class=\"G\">"
            if(trimmed.startsWith(continuation)){
                //Append to latest added show headline
                var ending = trimmed.substring(continuation.length)
                val endOfLine = ending.indexOf('<')
                if(endOfLine > 1){
                    ending = ending.substring(0, endOfLine)
                }
                ending = ending.trim()

                //Om det finns en till:
                val secondPos = trimmed.indexOf(continuation, endOfLine)
                if(secondPos > 0){
                    var secondPart = trimmed.substring(secondPos + continuation.length)
                    val secondEnd = secondPart.indexOf('<')
                    if(secondEnd > 1){
                        secondPart = secondPart.substring(0, secondEnd).trim()
                        ending += " $secondPart"
                    }
                }
                Log.e("got END for" + lastAddedChannel, "\"$ending\"")
                schedule[lastAddedChannel]!!.last().headline += " $ending"

            }else{
                val time = trimmed.substring(0, line.indexOf('<') - 1).trim()
                val headlineStart = trimmed.indexOf('>') +1
                val headline = trimmed.substring(headlineStart, trimmed.indexOf('<', startIndex = headlineStart)).trim()
                val channelEnd = trimmed.lastIndexOf('<')
                val channel = trimmed.substring(trimmed.lastIndexOf('>', startIndex = channelEnd) + 1, channelEnd).trim()
                schedule[channel]?.add(TVProgram(time, headline))
                Log.e("added", channel)
                lastAddedChannel = channel
                //Log.w("a find!", "$time^$headline^$channel")
            }
        }
    }

    fun setupClickEvents(){
        val uiChannels = listOf(R.id.svt1, R.id.svt2, R.id.tv3, R.id.tv4, R.id.kanal5)
        for(x in 0 until uiChannels.size) {
            val channelLayout = findViewById<View>(uiChannels[x])
            channelLayout.setOnTouchListener { v, event ->
                val upOrDown = event.action == MotionEvent.ACTION_DOWN
                        || event.action == MotionEvent.ACTION_UP
                if(upOrDown)
                    updateUI(if(event.action == MotionEvent.ACTION_DOWN) 1 else 0)
                upOrDown
            }
        }
    }

    fun colorizeUI(){
        val uiChannels = listOf(R.id.svt1, R.id.svt2, R.id.tv3, R.id.tv4, R.id.kanal5)
        //val bgBlack = ContextCompat.getColor(this, R.color.black)
        val uiColors = listOf(R.color.svt1, R.color.svt2, R.color.tv3, R.color.tv4, R.color.kanal5)

        for(x in 0 until uiChannels.size) {
            val channelColor = ContextCompat.getColor(this, uiColors[x])

            val channelLayout = findViewById<ConstraintLayout>(uiChannels[x])

            val channelNo = channelLayout.findViewById<TextView>(R.id.txtChannelNo)
            channelNo.text = (x+1).toString()
            channelNo.setBackgroundColor(channelColor)
            //channelLayout.setBackgroundColor(bgBlack)
            val time = channelLayout.findViewById<TextView>(R.id.txtTime)
            time.setTextColor(channelColor)
            val headline = channelLayout.findViewById<TextView>(R.id.txtHeadline)
            headline.setTextColor(channelColor)
        }
    }
    fun updateUI(index: Int){
      // val listLayout = findViewById<LinearLayout>(R.id.channels)
        val uiChannels = listOf(R.id.svt1, R.id.svt2, R.id.tv3, R.id.tv4, R.id.kanal5)
        for(x in 0 until uiChannels.size){
            val channelLayout = findViewById<ConstraintLayout>(uiChannels[x])
            val program = schedule[wantedChannels[x]]?.get(index)//.first()
            if(program != null){
                val time = channelLayout.findViewById<TextView>(R.id.txtTime)
                val headline = channelLayout.findViewById<TextView>(R.id.txtHeadline)
                time.text = program.begins
                headline.text = program.headline
            }
        }
    }
}