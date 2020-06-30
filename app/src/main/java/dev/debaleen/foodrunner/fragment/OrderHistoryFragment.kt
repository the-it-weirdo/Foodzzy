package dev.debaleen.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.adapter.OrderHistoryAdapter
import dev.debaleen.foodrunner.model.OrderHistoryItem
import dev.debaleen.foodrunner.model.toRestaurantFoodItemList
import dev.debaleen.foodrunner.network.ConnectionManager
import dev.debaleen.foodrunner.network.NetworkTask
import dev.debaleen.foodrunner.network.noInternetDialog
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var emptyLayout: RelativeLayout
    private lateinit var errorLayout: RelativeLayout
    private lateinit var recyclerAdapter: OrderHistoryAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var orderHistoryList = arrayListOf<OrderHistoryItem>()

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = ""

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = loadSharedPreferences()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        userId = sharedPreferences.getString(userIdKey, "")

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        recyclerOrderHistory.setHasFixedSize(true)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.show()
        emptyLayout = view.findViewById(R.id.emptyLayout)
        emptyLayout.hide()
        errorLayout = view.findViewById(R.id.errorLayout)
        errorLayout.hide()

        setupRecycler()
        fetchDataFromNetwork()
        return view
    }

    private fun setupRecycler() {
        recyclerAdapter = OrderHistoryAdapter(orderHistoryList)
        layoutManager = LinearLayoutManager(activity as Context)
        recyclerOrderHistory.adapter = recyclerAdapter
        recyclerOrderHistory.layoutManager = layoutManager

        recyclerOrderHistory.addItemDecoration(
            DividerItemDecoration(
                recyclerOrderHistory.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
    }

    private fun fetchDataFromNetwork() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            setupNetworkTaskListener()
            NetworkTask(networkTaskListener).makeNetworkRequest(
                activity as Context, Request.Method.GET, "$FETCH_PREVIOUS_ORDERS$userId", null
            )
        } else {
            noInternetDialog(activity as Context)
        }
    }

    private fun setupNetworkTaskListener() {
        networkTaskListener =
            object : NetworkTask.NetworkTaskListener {
                override fun onSuccess(result: JSONObject) {
                    try {
                        progressLayout.hide()
                        val returnObject = result.getJSONObject("data")
                        val success = returnObject.getBoolean("success")
                        if (success) {
                            val data = returnObject.getJSONArray("data")
                            if (data.length() == 0) {
                                emptyLayout.show()
                            } else {
                                for (i in 0 until data.length()) {
                                    val orderHistoryJsonObject = data.getJSONObject(i)
                                    val orderHistoryItem = OrderHistoryItem(
                                        orderId = orderHistoryJsonObject.getString("order_id"),
                                        restaurantName = orderHistoryJsonObject.getString("restaurant_name"),
                                        totalCost = orderHistoryJsonObject.getString("total_cost"),
                                        orderPlacedAt = orderHistoryJsonObject.getString("order_placed_at"),
                                        orderFoodItems = orderHistoryJsonObject.getJSONArray("food_items")
                                            .toRestaurantFoodItemList(
                                                "food_item_id",
                                                "name",
                                                "cost"
                                            )
                                    )
                                    orderHistoryList.add(orderHistoryItem)
                                }
                                recyclerAdapter.updateList(orderHistoryList)
                            }
                        } else {
                            errorLayout.show()
                            showToast("Some unexpected error occurred.")
                        }
                    } catch (e: Exception) {
                        errorLayout.show()
                        showToast("Exception occurred. ${e.localizedMessage}")
                    }
                }

                override fun onFailed(error: VolleyError) {
                    progressLayout.hide()
                    errorLayout.show()
                    showToast("Error occurred. $error")
                }
            }
    }
}