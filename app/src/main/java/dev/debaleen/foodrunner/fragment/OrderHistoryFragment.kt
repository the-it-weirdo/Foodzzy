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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.adapter.OrderHistoryAdapter
import dev.debaleen.foodrunner.model.OrderHistoryItem
import dev.debaleen.foodrunner.model.toRestaurantFoodItemList
import dev.debaleen.foodrunner.util.*

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var emptyLayout: RelativeLayout
    private lateinit var recyclerAdapter: OrderHistoryAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var orderHistoryList = arrayListOf<OrderHistoryItem>()

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (activity != null) {
            sharedPreferences = activity!!.getSharedPreferences(
                getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
            )
        }
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        userId = sharedPreferences.getString(userIdKey, "")

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        recyclerOrderHistory.setHasFixedSize(true)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        emptyLayout = view.findViewById(R.id.emptyLayout)
        emptyLayout.visibility = View.GONE

        populateRecycler()
        fetchDataFromNetwork()
        return view
    }

    private fun populateRecycler() {
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
            makeNetworkRequest()
        } else {
            noInternetDialog(activity as Context)
        }
    }

    private fun makeNetworkRequest() {
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            "$FETCH_PREVIOUS_ORDERS$userId",
            null,
            Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    val returnObject = it.getJSONObject("data")
                    val success = returnObject.getBoolean("success")
                    if (success) {
                        val data = returnObject.getJSONArray("data")
                        if (data.length() == 0) {
                            emptyLayout.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until data.length()) {
                                val orderHistoryJsonObject = data.getJSONObject(i)
                                val orderHistoryItem = OrderHistoryItem(
                                    orderId = orderHistoryJsonObject.getString("order_id"),
                                    restaurantName = orderHistoryJsonObject.getString("restaurant_name"),
                                    totalCost = orderHistoryJsonObject.getString("total_cost"),
                                    orderPlacedAt = orderHistoryJsonObject.getString("order_placed_at"),
                                    orderFoodItems = orderHistoryJsonObject.getJSONArray("food_items")
                                        .toRestaurantFoodItemList("food_item_id", "name", "cost")
                                )
                                orderHistoryList.add(orderHistoryItem)
                            }
                            recyclerAdapter.updateList(orderHistoryList)
                        }
                    } else {
                        showToast("Some unexpected error occurred.")
                    }
                } catch (e: Exception) {
                    showToast("Exception occurred. ${e.localizedMessage}")
                }
            },
            Response.ErrorListener {
                showToast("Error occurred. ${it.localizedMessage}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = TOKEN
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}