package dev.debaleen.foodrunner
/*
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NetworkTask {
    private var method:Int
    private var queue: RequestQueue
    private var url :String
    private var listener:NetworkTaskListener

    constructor(context: Context, mode: NetworkGET) {
        this.method = Request.Method.GET
        this.queue = Volley.newRequestQueue(context)
        this.url = mode.url
    }

    constructor(context: Context, mode:NetworkPOST, jsonParams: JSONObject) {
        this.method = Request.Method.POST
        this.queue = Volley.newRequestQueue(context)
        this.url = mode.url
    }

    init {
        val jsonObjectRequest =
            object: JsonObjectRequest(this.method, this.url, JSONObject(),
                Response.Listener {
                    listener.onSuccessfulCompletion(it)
                },
                Response.ErrorListener {
                    listener.onFailure(it)
                }){}
        jsonObjectRequest.
    }
}

interface NetworkTaskListener {
    fun onSuccessfulCompletion(jsonResultObject: JSONObject)
    fun onFailure(error:VolleyError)
}

enum class NetworkTaskMode {
    LOGIN, REGISTER, FETCH_ALL_RESTAURANTS, FETCH_RESTAURANT_DETAILS, PLACE_ORDER, RETRIEVE_PREVIOUS_ORDERS
}

sealed class NetworkGET(val url:String) {

    object FETCH_ALL_RESTAURANTS : NetworkGET("")
}

sealed class NetworkPOST(val url: String) {
    object LOGIN: NetworkPOST("")
}*/

/*
@Entity
data class Food(
        var foodName: String,
        var foodDesc: String,
        var protein: Double,
        var carbs: Double,
        var fat: Double
){
    @PrimaryKey(autoGenerate = true)
    var foodId: Int = 0 // or foodId: Int? = null
    var calories: Double = 0.toDouble()
}
 */