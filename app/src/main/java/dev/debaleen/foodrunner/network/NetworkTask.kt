package dev.debaleen.foodrunner.network

import android.content.Context
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.debaleen.foodrunner.util.TOKEN
import org.json.JSONObject

class NetworkTask(private val listener: NetworkTaskListener) {

    fun makeNetworkRequest(context: Context, method: Int, url: String, jsonParam: JSONObject?) {
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest =
            object : JsonObjectRequest(
                method, url, jsonParam,
                Response.Listener {
                    listener.onSuccess(it)
                },
                Response.ErrorListener {
                    listener.onFailed(it)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = TOKEN
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }

    interface NetworkTaskListener {
        fun onSuccess(result: JSONObject)
        fun onFailed(error: VolleyError)
    }

}