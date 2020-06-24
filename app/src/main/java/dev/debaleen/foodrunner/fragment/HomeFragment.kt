package dev.debaleen.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.adapter.RestaurantAdapter
import dev.debaleen.foodrunner.database.DBAsyncTask
import dev.debaleen.foodrunner.model.RestaurantUIModel
import dev.debaleen.foodrunner.util.ConnectionManager
import dev.debaleen.foodrunner.util.FavouriteRestaurantsDBTasks
import org.json.JSONException

class HomeFragment : Fragment() {

    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantAdapter

    val restaurantList = arrayListOf<RestaurantUIModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null,
                    Response.Listener {

                        try {
                            // Response from network obtained. Hiding progress layout.
                            progressLayout.visibility = View.GONE
                            val success = it.getJSONObject("data").getBoolean("success")

                            if (success) {
                                val data = it.getJSONObject("data").getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)
                                    val restaurantObject = RestaurantUIModel(
                                        resId = restaurantJsonObject.getString("id"),
                                        resName = restaurantJsonObject.getString("name"),
                                        resRating = restaurantJsonObject.getString("rating"),
                                        resCostForOne = restaurantJsonObject.getString("cost_for_one"),
                                        resImageUrl = restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantObject.isFavourite = DBAsyncTask(
                                        activity as Context,
                                        restaurantObject.toRestaurantEntity(),
                                        FavouriteRestaurantsDBTasks.CHECK_FAVOURITE
                                    ).execute().get()
                                    restaurantList.add(restaurantObject)

                                    recyclerAdapter =
                                        RestaurantAdapter(
                                            restaurantList,
                                            object : RestaurantAdapter.RestaurantClickListener {
                                                override fun onRestaurantClick(
                                                    position: Int,
                                                    resId: String
                                                ) {
                                                    Toast.makeText(
                                                        context,
                                                        " ${restaurantList[position].resName} with id: $resId clicked.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                override fun onFavouriteClick(
                                                    position: Int,
                                                    resId: String
                                                ) {
                                                    Toast.makeText(
                                                        context,
                                                        " ${restaurantList[position].resName} with id: $resId clicked for favourite.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    if (!DBAsyncTask(
                                                            activity as Context,
                                                            restaurantList[position].toRestaurantEntity(),
                                                            FavouriteRestaurantsDBTasks.CHECK_FAVOURITE
                                                        ).execute().get()
                                                    ) {
                                                        val async = DBAsyncTask(
                                                            activity as Context,
                                                            restaurantList[position].toRestaurantEntity(),
                                                            FavouriteRestaurantsDBTasks.INSERT
                                                        ).execute()
                                                        val result = async.get()
                                                        if (result) {
                                                            restaurantList[position].isFavourite =
                                                                true
                                                            recyclerAdapter.notifyItemChanged(
                                                                position
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                "Restaurant added to favourites",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Some error occurred.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    } else {
                                                        val async = DBAsyncTask(
                                                            activity as Context,
                                                            restaurantList[position].toRestaurantEntity(),
                                                            FavouriteRestaurantsDBTasks.DELETE
                                                        ).execute()
                                                        val result = async.get()
                                                        if (result) {
                                                            restaurantList[position].isFavourite =
                                                                false
                                                            recyclerAdapter.notifyItemChanged(
                                                                position
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                "Restaurant removed from favourites",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Some error occurred.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            })

                                    recyclerHome.adapter = recyclerAdapter
                                    recyclerHome.layoutManager = layoutManager
                                }

                            } else {
                                // Not success
                                if (activity != null) {
                                    Toast.makeText(
                                        activity as Context,
                                        "Error occurred",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        } catch (e: JSONException) {
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    "JSOn Exception occurred. ${e.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                    },
                    Response.ErrorListener {
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Error occurred. ${it.localizedMessage}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "141f8efeca1968"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            // No internet
            noInternetDialog()
        }
        return view
    }

    private fun noInternetDialog() {
        val dialog = MaterialAlertDialogBuilder(activity as Context)
        dialog.setTitle("Error")
        dialog.setMessage("Internet connection not found.")

        dialog.setPositiveButton("Open Settings") { text, listener ->
            // Opens settings
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            activity?.finish()
        }
        dialog.setNegativeButton("Exit App") { text, listener ->
            // Closes app safely
            ActivityCompat.finishAffinity(activity as Activity)
        }
        dialog.create()
        dialog.show()
    }

}

