package com.bjorkgren.vbptv

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bjorkgren.vbptv.model.TVChannel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.texttv.nu/api/get/650-655?app=com.bjorkgren.vbptv"

// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                parseSchedule(response)
                //textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { error ->
                Log.e("Error", error.message)
            })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    //Första tab:en är "pågående program", ett program per kanal, precis som de ser ut på SVT text 650
    //Nästa tab:en är "Kommande program", program nr 2 per kanal,
    //Nästa tab:en är "Kommande program, sid 2", program nr 3 per kanal
    //Sista tab:en är "Kommande program, sid 3", program nr 4 per kanal

    fun parseSchedule(textTvPages: String) : MutableList<TVChannel>{
        val wantedChannels = listOf("SVT1", "SVT2", "TV3", "TV4", "Kanal 5")
        var channels: MutableList<TVChannel> = mutableListOf<TVChannel>()
        val rows = textTvPages.split("\\n ").toTypedArray().drop(4)
        for(row in rows){
            Log.w("row", row)
            val items = row.split('<')
            if(items.size > 3){
                val channel = items[3].split('>')[1].trim()
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
                }
            }
        }

        return channels
    }
}
