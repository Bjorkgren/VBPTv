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
        val url = "http://api.texttv.nu/api/get/650-655?app=com.bjorkgren.vbptv"

// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                parseSchedule(response)
                //textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { Log.e("Error", "ERROR!!") })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun parseSchedule(textTvPages: String) : MutableList<TVChannel>{
        var channels: MutableList<TVChannel> = mutableListOf<TVChannel>()
        Log.w("result", textTvPages)
        //do magic
        return channels
    }

}
