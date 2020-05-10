package com.bjorkgren.vbptv

import android.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bjorkgren.vbptv.model.TVChannel
import com.bjorkgren.vbptv.model.TVProgram

class MainActivity : AppCompatActivity() {

    val wantedChannels = listOf("SVT1", "SVT2", "TV3", "TV4", "Kanal 5")
    val textTvPages = listOf(650, 651, 652)
    val schedule = hashMapOf<String, MutableList<TVProgram>>()
    var lastAddedChannel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        //Initiate the schedule
        for(wanted in wantedChannels){
            schedule[wanted] = mutableListOf<TVProgram>()
        }
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        for(page in textTvPages){
            //add a request to queue
            queue.add(
                StringRequest(
                    Request.Method.GET, "https://www.svt.se/svttext/webL/pages/$page.html",
                    Response.Listener<String> { response ->
                        parseSVTTextPage(response)
                        updateUI()
                    },
                    Response.ErrorListener { error ->
                        Log.e("Error", error.message)
                    })
            )
        }
    }

    //TODO: Programmatically create the "Pågående program"-tab
        //this will also have the channel...
    //TODO: loop and Programatically create the following specific channel tabs
        //becase this layout will be more like a table with just time + name
    //Första tab:en är "pågående program", ett program per kanal, precis som de ser ut på SVT text 650
    //Nästa tab:en är för SVT1, sen en tab per kanal
    //      pga att vissa kanaler har många korta program (SVT2) o vissa har få o långa...


    //Parsing from svt.se/svttext
    fun parseSVTTextPage(resultFromSvtPage: String){
        val rows = resultFromSvtPage.split('\n')
        var parsing: Boolean = false
        for(row in rows){
            if(row.contains("Fortsättning följer på nästa")) {
                break;
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
            val continuation = "<span class=\"G\">   "
            if(trimmed.startsWith(continuation)){
                //Append to latest added show headline
                var ending = trimmed.substring(continuation.length)
                val endOfLine = ending.indexOf('<')
                if(endOfLine > 1){
                    ending = ending.substring(0, endOfLine)
                }
                ending = ending.trim()
                //Log.e("got END for" + lastAddedChannel, "\"$ending\"")
                schedule[lastAddedChannel]!!.last()!!.headline += " $ending"

            }else{
                val time = trimmed.substring(0, line.indexOf('<') - 2)
                val headlineStart = trimmed.indexOf('>') +1
                val headline = trimmed.substring(headlineStart, trimmed.indexOf('<', startIndex = headlineStart)).trim()
                val channelEnd = trimmed.lastIndexOf('<')
                val channel = trimmed.substring(trimmed.lastIndexOf('>', startIndex = channelEnd) + 1, channelEnd)
                schedule[channel]?.add(TVProgram(time, headline))
                lastAddedChannel = channel
                //Log.w("a find!", "$time^$headline^$channel")
            }
        }
    }

    fun updateUI(){
        var i = 0
      // val listLayout = findViewById<LinearLayout>(R.id.channels)
        for(wanted in wantedChannels){
            //Skapa en linear layout per kanal, som bara visar program 0, håller man ner nånstans i uit så visas nästkommande tills man släpper
            //schedule[wanted] = mutableListOf<TVProgram>()

            //TODO: DO not add UI, just update the items..
            val itemLayout = LinearLayout(this)
            itemLayout.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f))
            itemLayout.orientation = LinearLayout.HORIZONTAL
            val txtChannel = TextView(this)
            txtChannel.text = (++i).toString()
            itemLayout.addView(txtChannel)
           // listLayout.addView(itemLayout)
        }
    }
}