package dev.debaleen.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import java.util.*
import kotlin.Comparator

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

    private val ratingComparator = Comparator<RestaurantUIModel> { restaurant1, restaurant2 ->
        if (restaurant1.resRating.compareTo(restaurant2.resRating, true) == 0) {
            restaurant1.resCostForOne.compareTo(restaurant2.resCostForOne, true)
        } else {
            restaurant1.resRating.compareTo(restaurant2.resRating, true)
        }
    }

    private val priceComparator = Comparator<RestaurantUIModel> { restaurant1, restaurant2 ->
        if (restaurant1.resCostForOne.compareTo(restaurant2.resCostForOne, true) == 0) {
            restaurant1.resRating.compareTo(restaurant2.resRating, true)
        } else {
            restaurant1.resCostForOne.compareTo(restaurant2.resCostForOne, true)
        }
    }

    private var checkedItem: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = loadSharedPreferences()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {
            showSortDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortRestaurant(sortOn: RestaurantSortOn) {
        when (sortOn) {
            RestaurantSortOn.RATING -> {
                Collections.sort(restaurantList, ratingComparator)
                restaurantList.reverse()
            }
            RestaurantSortOn.PRICE_HIGH_TO_LOW -> {
                Collections.sort(restaurantList, priceComparator)
                restaurantList.reverse()
            }
            RestaurantSortOn.PRICE_LOW_TO_HIGH -> {
                Collections.sort(restaurantList, priceComparator)
            }
            RestaurantSortOn.NONE -> {
                // Do nothing
            }
        }
        recyclerAdapter.updateList(restaurantList)
    }

    private fun showSortDialog() {
        val singleItems = arrayOf("Rating", "Cost(High to Low)", "Cost(Low to High")
        var sortOn: RestaurantSortOn = when (checkedItem) {
            0 -> {
                RestaurantSortOn.RATING
            }
            1 -> {
                RestaurantSortOn.PRICE_HIGH_TO_LOW
            }
            else -> {
                RestaurantSortOn.PRICE_LOW_TO_HIGH
            }
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Sort By?")
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Okay") { _, _ ->
                sortRestaurant(sortOn)
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                sortOn = when (which) {
                    0 -> {
                        checkedItem = 0
                        RestaurantSortOn.RATING
                    }
                    1 -> {
                        checkedItem = 1
                        RestaurantSortOn.PRICE_HIGH_TO_LOW
                    }
                    else -> {
                        checkedItem = 2
                        RestaurantSortOn.PRICE_LOW_TO_HIGH
                    }
                }
            }.show()
    }

    private fun navigateToRestaurantDetailsActivity(resName: String, resId: String) {
        val intent = Intent(activity, RestaurantDetailActivity::class.java)
        intent.putExtra(restaurantNameKey, resName)
        intent.putExtra(restaurantIdKey, resId)
        startActivity(intent)
        activity?.finish()
    }
}