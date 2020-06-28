package dev.debaleen.foodrunner.activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import dev.debaleen.foodrunner.network.NetworkTask
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.adapter.CartAdapter
import dev.debaleen.foodrunner.database.AsyncTaskCompleteListener
import dev.debaleen.foodrunner.database.ClearCartAsyncTask
import dev.debaleen.foodrunner.database.RetrieveCartAsyncTask
import dev.debaleen.foodrunner.model.RestaurantFoodItem
import dev.debaleen.foodrunner.util.*
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userId: String
    private lateinit var restaurantId: String
    private lateinit var restaurantName: String

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerRestaurantDetails: RecyclerView
    private lateinit var btnPlaceOrder: Button
    private lateinit var txtTotalCost: TextView
    private lateinit var txtOrderingFrom: TextView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var emptyLayout: RelativeLayout

    private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CartAdapter

    private var cartList = arrayListOf<RestaurantFoodItem>()
    private var totalCost: Int = 0

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = loadSharedPreferences()

        userId = sharedPreferences.getString(userIdKey, "") ?: ""

        setContentView(R.layout.activity_cart)

        if (intent != null) {
            restaurantId = intent.getStringExtra(restaurantIdKey) ?: ""
            restaurantName = intent.getStringExtra(restaurantNameKey) ?: ""
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerRestaurantDetails = findViewById(R.id.recyclerCart)
        txtTotalCost = findViewById(R.id.txtTotalCost)
        txtOrderingFrom = findViewById(R.id.txtRestaurantName)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.show()
        emptyLayout = findViewById(R.id.emptyLayout)

        txtOrderingFrom.text = getString(R.string.ordering_from_template, restaurantName)

        recyclerAdapter = CartAdapter(cartList)
        recyclerLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerRestaurantDetails.layoutManager = recyclerLayoutManager
        recyclerRestaurantDetails.adapter = recyclerAdapter

        btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                networkRequest()
            } else {
                noInternetDialog(this@CartActivity)
            }
        }

        setupNetworkTaskListener()
        getCartFromDB()
    }

    private fun setupNetworkTaskListener() {
        networkTaskListener =
            object : NetworkTask.NetworkTaskListener {
                override fun onSuccess(result: JSONObject) {
                    try {
                        val returnObject = result.getJSONObject("data")
                        val success = returnObject.getBoolean("success")
                        if (success) {
                            ClearCartAsyncTask(this@CartActivity, userId, restaurantId,
                                object : AsyncTaskCompleteListener {
                                    override fun onTaskComplete() {
                                        showOrderResultDialog(true, "Order Placed.")
                                    }
                                }).execute()
                        } else {
                            showOrderResultDialog(false, "Couldn't place order.")
                        }
                    } catch (e: Exception) {
                        showOrderResultDialog(false, "Error: ${e.localizedMessage}")
                    }
                }

                override fun onFailed(error: VolleyError) {
                    showOrderResultDialog(false, "Error: ${error.localizedMessage}")
                }
            }
    }

    private fun getCartFromDB() {
        val resultFromDB = RetrieveCartAsyncTask(this@CartActivity,
            userId, restaurantId, object : AsyncTaskCompleteListener {
                override fun onTaskComplete() {
                    progressLayout.hide()
                }
            }).execute().get()
        for (element in resultFromDB) {
            val u =
                Gson().fromJson(element.foodItems, Array<RestaurantFoodItem>::class.java).asList()
            cartList.addAll(
                u
            )
        }
        if (cartList.isEmpty()) {
            emptyLayout.show()
        } else {
            emptyLayout.hide()
            for (i in 0 until cartList.size) {
                totalCost += cartList[i].cost.toInt()
            }
            txtTotalCost.text = getString(R.string.total_cost_template, totalCost)
            recyclerAdapter.updateList(cartList)
        }
    }

    private fun networkRequest() {
        progressLayout.show()

        val jsonParams = JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", restaurantId)
        jsonParams.put("total_cost", totalCost.toString())
        val foodIdArray = JSONArray()
        for (item in cartList) {
            val foodId = JSONObject()
            foodId.put("food_item_id", item.id)
            foodIdArray.put(foodId)
        }
        jsonParams.put("food", foodIdArray)

        NetworkTask(networkTaskListener).makeNetworkRequest(
            this@CartActivity, Request.Method.POST, PLACE_ORDER, jsonParams
        )
    }


    private fun showOrderResultDialog(success: Boolean, message: String) {
        val dialog = Dialog(
            this@CartActivity,
            android.R.style.Theme_Material_Light_NoActionBar_Fullscreen
        )
        dialog.setContentView(R.layout.order_result)
        val txtResult: TextView = dialog.findViewById(R.id.txtResult)
        val imgOrderResult: ImageView = dialog.findViewById(R.id.imgResult)
        val btnGoToDashboard: Button = dialog.findViewById(R.id.btnGoToDashboard)
        txtResult.text = message
        val tintColor: Int
        val drawableResId: Int
        tintColor = if (success) {
            // Since Picasso doesn't support loading Vector drawables, using .setImageResource()
            drawableResId = R.drawable.ic_check_filled
            ContextCompat.getColor(this@CartActivity, R.color.primaryColor)
        } else {
            drawableResId = R.drawable.ic_failed
            ContextCompat.getColor(this@CartActivity, R.color.error)
        }
        imgOrderResult.setImageResource(drawableResId)
        ImageViewCompat.setImageTintList(imgOrderResult, ColorStateList.valueOf(tintColor))
        dialog.show()
        dialog.setCancelable(false)

        btnGoToDashboard.setOnClickListener {
            dialog.dismiss()
            navigateToDashboard()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        ClearCartAsyncTask(this@CartActivity, userId, restaurantId,
            object : AsyncTaskCompleteListener {
                override fun onTaskComplete() {
                    super@CartActivity.onBackPressed()
                }
            }).execute()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this@CartActivity, DashboardActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this@CartActivity)
    }
}