package dev.debaleen.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import dev.debaleen.foodrunner.network.NetworkTask
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.activity.RestaurantDetailActivity
import dev.debaleen.foodrunner.adapter.RestaurantAdapter
import dev.debaleen.foodrunner.database.FavouriteDBAsyncTask
import dev.debaleen.foodrunner.database.entity.RestaurantEntity
import dev.debaleen.foodrunner.model.RestaurantUIModel
import dev.debaleen.foodrunner.model.toRestaurantEntity
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var progressLayout: RelativeLayout
    private lateinit var recyclerHome: RecyclerView
    private lateinit var emptyView: RelativeLayout
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: RestaurantAdapter

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    private val restaurantList = arrayListOf<RestaurantUIModel>()

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = loadSharedPreferences()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.show()
        recyclerHome = view.findViewById(R.id.recyclerHome)
        emptyView = view.findViewById(R.id.emptyLayout)
        emptyView.hide()

        userId = sharedPreferences.getString(userIdKey, "")
        if (userId == null) {
            userId = ""
        }

        setupNetworkListener()
        setupRecyclerAdapter()

        layoutManager = LinearLayoutManager(activity)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager

        fetchDataFromNetwork()
        return view
    }

    private fun setupRecyclerAdapter() {
        recyclerAdapter = RestaurantAdapter(restaurantList,
            object : RestaurantAdapter.RestaurantClickListener {
                override fun onRestaurantClick(position: Int, resId: String) {
                    navigateToRestaurantDetailsActivity(restaurantList[position].resName, resId)
                }

                override fun onFavouriteClick(position: Int, resId: String) {
                    if (!restaurantList[position].isFavourite) {
                        val async = FavouriteDBAsyncTask(
                            activity as Context,
                            restaurantList[position].toRestaurantEntity(userId!!),
                            FavouriteRestaurantsDBTasks.INSERT
                        ).execute()
                        val result = async.get()
                        if (result) {
                            restaurantList[position].isFavourite = true
                            recyclerAdapter.notifyItemChanged(position)
                            showToast("${restaurantList[position].resName} added to favourites")
                        } else {
                            showToast("Some error occurred.")
                        }
                    } else {
                        val async = FavouriteDBAsyncTask(
                            activity as Context,
                            restaurantList[position].toRestaurantEntity(userId!!),
                            FavouriteRestaurantsDBTasks.DELETE
                        ).execute()
                        val result = async.get()
                        if (result) {
                            restaurantList[position].isFavourite = false
                            recyclerAdapter.notifyItemChanged(position)
                            showToast("${restaurantList[position].resName} removed from favourites")
                        } else {
                            showToast("Some error occurred.")
                        }
                    }
                }
            })
    }

    private fun setupNetworkListener() {
        networkTaskListener = object : NetworkTask.NetworkTaskListener {
            override fun onSuccess(result: JSONObject) {
                try {
                    // Response from network obtained. Hiding progress layout.
                    progressLayout.hide()
                    val returnObject = result.getJSONObject("data")
                    val success = returnObject.getBoolean("success")

                    if (success) {
                        emptyView.hide()
                        val data = returnObject.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = RestaurantUIModel(
                                resId = restaurantJsonObject.getString("id"),
                                resName = restaurantJsonObject.getString("name"),
                                resRating = restaurantJsonObject.getString("rating"),
                                resCostForOne = restaurantJsonObject.getString("cost_for_one"),
                                resImageUrl = restaurantJsonObject.getString("image_url")
                            )
                            restaurantObject.isFavourite =
                                checkFavourite(restaurantObject.toRestaurantEntity(userId!!))
                            restaurantList.add(restaurantObject)
                            recyclerAdapter.updateList(restaurantList)
                        }
                    } else {
                        // Not success
                        emptyView.show()
                    }
                } catch (e: Exception) {
                    if (activity != null) {
                        showToast("Exception occurred. ${e.localizedMessage}")
                    }
                }
            }

            override fun onFailed(error: VolleyError) {
                if (activity != null) {
                    showToast("Error occurred. ${error.localizedMessage}")
                }
            }
        }
    }

    private fun fetchDataFromNetwork() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            NetworkTask(networkTaskListener).makeNetworkRequest(
                activity as Context,
                Request.Method.GET, FETCH_RESTAURANTS, null
            )
        } else {
            // No internet
            noInternetDialog(activity as Context)
        }
    }

    private fun checkFavourite(restaurantEntity: RestaurantEntity): Boolean {
        return FavouriteDBAsyncTask(
            activity as Context,
            restaurantEntity,
            FavouriteRestaurantsDBTasks.CHECK_FAVOURITE
        ).execute().get()
    }

    private fun navigateToRestaurantDetailsActivity(resName: String, resId: String) {
        val intent = Intent(activity, RestaurantDetailActivity::class.java)
        intent.putExtra(restaurantNameKey, resName)
        intent.putExtra(restaurantIdKey, resId)
        startActivity(intent)
        activity?.finish()
    }
}