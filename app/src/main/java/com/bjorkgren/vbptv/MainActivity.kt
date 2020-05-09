package com.bjorkgren.vbptv

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
                        // Display the first 500 characters of the response string.
                        /*Log.w("PARSE", "PARSING...")
                        val schedule = parseSchedule(response)
                        Log.e("PÅGÅENDE PROGRAM", "PÅGÅENDE PROGRAM")
                        for(wanted in wantedChannels){
                            Log.e("pågående in $wanted", schedule[wanted]?.first()?.headline)
                            Log.w("senare antal i kanalen", "antal: " + schedule[wanted]?.size)
                        }
                        */
                        //textView.text = "Response is: ${response.substring(0, 500)}"
                    },
                    Response.ErrorListener { error ->
                        Log.e("Error", error.message)
                    })
            )
        }

/*
// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.w("PARSE", "PARSING...")
                val schedule = parseSchedule(response)
                Log.e("PÅGÅENDE PROGRAM", "PÅGÅENDE PROGRAM")
                for(wanted in wantedChannels){
                    Log.e("pågående in $wanted", schedule[wanted]?.first()?.headline)
                    Log.w("senare antal i kanalen", "antal: " + schedule[wanted]?.size)
                }

                //textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { error ->
                Log.e("Error", error.message)
            })

// Add the request to the RequestQueue.
        Log.w("START", "STARTING....")
        queue.add(stringRequest)
        */
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
        if(line.contains("<span ")){
            val trimmed = line.trim()
            if(trimmed.startsWith("<span class=\"G\">   ")){
                //TODO: Append to latest added show headline
            }else{
                val time = trimmed.substring(0, line.indexOf(' '))
                val headlineStart = trimmed.indexOf('>') +1
                val headline = trimmed.substring(headlineStart, trimmed.indexOf('<', startIndex = headlineStart))
                val channelEnd = trimmed.lastIndexOf('<')
                val channel = trimmed.substring(trimmed.lastIndexOf('>', startIndex = channelEnd))
                Log.w("a find!", "$time^$headline^$channel")

            }

        }

    }
/*
    //Parsing from the api.textv.nu
    fun parseSchedule(textTvPages: String) : HashMap<String, MutableList<TVProgram>>{
        //var channels: MutableList<TVChannel> = mutableListOf<TVChannel>()
        val channels = HashMap<String, MutableList<TVProgram>>()
        for(wanted in wantedChannels){
            channels[wanted] = mutableListOf<TVProgram>()
        }

        var latestChannel = "XXX"
        // <span class=\"G\">      .
        val rows = textTvPages.split("\\n ").toTypedArray().drop(4)
        for(row in rows){
            Log.w("row", row)
            val items = row.split('<')
            if(items.size > 3){
                val channel = items[items.size-2].split('>')[1].trim()
                //Log.e("channel", channel)
                if(channel in wantedChannels) {

                    val time = items[0]
                    //Log.e("time", time)
                    val headline = items[1].split('>')[1]
                        .replace("\\u00e5", "å")
                        .replace("\\u00e4", "ä")
                        .replace("\\u00e6", "ö")
                        .replace("\\u00c5", "Å")
                        .replace("\\u00c4", "Ä")
                        .replace("\\u00d6", "Ö")
                    //Log.e("headline", headline)
                   Log.e("show", "Time: $time Headline: $headline @ $channel")
                    channels[channel]?.add(TVProgram(time,headline))
                }
            }
        }

        return channels
    }
    */
}
