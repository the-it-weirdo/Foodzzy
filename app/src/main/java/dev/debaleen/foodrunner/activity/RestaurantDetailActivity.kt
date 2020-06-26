package dev.debaleen.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import dev.debaleen.foodrunner.R
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dev.debaleen.foodrunner.adapter.RestaurantDetailAdapter
import dev.debaleen.foodrunner.database.AsyncTaskCompleteListener
import dev.debaleen.foodrunner.database.CartDBAsyncTasks
import dev.debaleen.foodrunner.database.CartElementEntity
import dev.debaleen.foodrunner.database.ClearCartAsyncTask
import dev.debaleen.foodrunner.model.RestaurantFoodItem
import dev.debaleen.foodrunner.model.toRestaurantFoodItemList
import dev.debaleen.foodrunner.util.*

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userId: String
    private lateinit var restaurantId: String
    private lateinit var restaurantName: String

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerRestaurantDetails: RecyclerView
    private lateinit var btnGoToCart: Button
    private lateinit var progressLayout: RelativeLayout
    private lateinit var emptyLayout: RelativeLayout

    private lateinit var recyclerAdapter: RestaurantDetailAdapter
    private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager

    private var menuList = arrayListOf<RestaurantFoodItem>()
    private var orderList = arrayListOf<RestaurantFoodItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_restaurant_detail)
        userId = sharedPreferences.getString(userIdKey, "") ?: ""

        if (intent != null) {
            restaurantId = intent.getStringExtra(restaurantIdKey) ?: ""
            restaurantName = intent.getStringExtra(restaurantNameKey) ?: ""
        }

        toolbar = findViewById(R.id.toolbar)
        recyclerRestaurantDetails = findViewById(R.id.recyclerRestaurantDetails)
        btnGoToCart = findViewById(R.id.btnGoToCart)
        btnGoToCart.visibility = View.GONE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        emptyLayout = findViewById(R.id.emptyLayout)

        recyclerAdapter = RestaurantDetailAdapter(menuList,
            object : RestaurantDetailAdapter.CartButtonListener {
                override fun onAddToCartButtonClick(position: Int, foodItem: RestaurantFoodItem) {
                    orderList.add(foodItem)
                    if (orderList.isNotEmpty()) {
                        btnGoToCart.visibility = View.VISIBLE
                    }
                }

                override fun onRemoveFromButtonClicked(
                    position: Int,
                    foodItem: RestaurantFoodItem
                ) {
                    orderList.remove(foodItem)
                    if (orderList.isEmpty()) {
                        btnGoToCart.visibility = View.GONE
                    }
                }
            })
        recyclerLayoutManager = LinearLayoutManager(this@RestaurantDetailActivity)
        recyclerRestaurantDetails.layoutManager = recyclerLayoutManager
        recyclerRestaurantDetails.adapter = recyclerAdapter

        btnGoToCart.setOnClickListener {
            navigateToCart()
        }

        setUpToolbar()
        makeNetworkRequest()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun makeNetworkRequest() {
        if (ConnectionManager().checkConnectivity(this@RestaurantDetailActivity)) {
            val queue = Volley.newRequestQueue(this@RestaurantDetailActivity)

            val jsonRequest =
                object : JsonObjectRequest(Method.GET,
                    "$FETCH_RESTAURANT_DETAILS/$restaurantId",
                    null,
                    Response.Listener {
                        progressLayout.visibility = View.GONE
                        try {
                            val returnObject = it.getJSONObject("data")
                            val success = returnObject.getBoolean("success")
                            if (success) {
                                val foodsArray = returnObject.getJSONArray("data")
                                if (foodsArray.length() == 0) {
                                    emptyLayout.visibility = View.VISIBLE
                                } else {
                                    menuList = ArrayList(foodsArray.toRestaurantFoodItemList())
                                    recyclerAdapter.updateDataList(menuList)
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Some unexpected error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                e.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = TOKEN
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            noInternetDialog(this@RestaurantDetailActivity)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            if (orderList.isNotEmpty()) {
                clearCartDialog()
            } else {
                navigateToDashboardActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (orderList.isNotEmpty()) {
            clearCartDialog()
        } else {
            navigateToDashboardActivity()
        }
    }

    private fun clearCartDialog() {
        val dialog = MaterialAlertDialogBuilder(this@RestaurantDetailActivity)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Going back will clear the cart. Are you sure you want to go back?")

        dialog.setPositiveButton("YES") { text, listener ->
            ClearCartAsyncTask(this@RestaurantDetailActivity,
                userId, restaurantId,
                object : AsyncTaskCompleteListener {
                    override fun onTaskComplete() {
                        Toast.makeText(
                            this@RestaurantDetailActivity,
                            "Cart cleared.",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToDashboardActivity()
                    }
                }).execute()
        }
        dialog.setNegativeButton("NO") { text, listener ->
            // Do Nothing
        }
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    private fun navigateToCart() {
        val gson = Gson()

        val foodItems = gson.toJson(orderList)

        val result = CartDBAsyncTasks(
            this@RestaurantDetailActivity,
            CartElementEntity(userId, restaurantId, foodItems),
            CartDBTasks.INSERT
        ).execute().get()

        if (result) {
            orderList.clear()
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Going to cart.",
                Toast.LENGTH_SHORT
            ).show()
            // TODO("Create intent and put resName and resKey and navigate to Cart screen")
        } else {
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some unexpected error occurred.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun navigateToDashboardActivity() {
        val intent = Intent(this@RestaurantDetailActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}