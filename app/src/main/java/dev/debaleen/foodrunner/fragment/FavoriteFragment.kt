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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.activity.RestaurantDetailActivity
import dev.debaleen.foodrunner.adapter.RestaurantAdapter
import dev.debaleen.foodrunner.database.*
import dev.debaleen.foodrunner.model.RestaurantUIModel
import dev.debaleen.foodrunner.util.FavouriteRestaurantsDBTasks
import dev.debaleen.foodrunner.util.restaurantIdKey
import dev.debaleen.foodrunner.util.restaurantNameKey
import dev.debaleen.foodrunner.util.userIdKey

class FavoriteFragment : Fragment() {

    lateinit var progressLayout: RelativeLayout
    lateinit var emptyLayout: RelativeLayout
    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantAdapter

    private var restaurantList = arrayListOf<RestaurantUIModel>()

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
        userId = sharedPreferences.getString(userIdKey, "")
        if (userId == null) {
            userId = ""
        }
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        emptyLayout = view.findViewById(R.id.emptyLayout)
        emptyLayout.visibility = View.GONE
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        recyclerFavourite.setHasFixedSize(true)

        val restaurantsListFromDB = RetrieveFavouritesAsyncTask(
            activity as Context,
            object : AsyncTaskCompleteListener {
                override fun onTaskComplete() {
                    progressLayout.visibility = View.GONE
                }
            }, userId!!
        ).execute().get()

        if (restaurantsListFromDB.isNotEmpty()) {
            restaurantList = ArrayList(restaurantsListFromDB.toListRestaurantUIModel())
            restaurantList.forEach {
                it.isFavourite = true
            }
            recyclerAdapter = RestaurantAdapter(restaurantList,
                object : RestaurantAdapter.RestaurantClickListener {
                    override fun onRestaurantClick(position: Int, resId: String) {
                        navigateToRestaurantDetailsActivity(
                            restaurantList[position].resName,
                            resId
                        )
                    }

                    override fun onFavouriteClick(position: Int, resId: String) {
                        /* Since we are in Favourites Fragment, Clicking in Favourite Button can only mean removing
                         from favourites list. On removal from favourites list, we also remove the Restaurant
                         from the RecyclerView to ensure that we cannot add this Restaurant to the favourites list
                         from the Favourite fragment. We can only add favourite fragments from the Home Fragment.
                         */
                        removeFromFavourites(
                            restaurantList[position].toRestaurantEntity(userId!!),
                            position
                        )
                    }
                })

            layoutManager = LinearLayoutManager(activity as Context)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        } else {
            emptyLayout.visibility = View.VISIBLE
        }
        return view
    }

    fun removeFromFavourites(restaurantEntity: RestaurantEntity, position: Int) {
        val result = FavouriteDBAsyncTask(
            activity as Context,
            restaurantEntity,
            FavouriteRestaurantsDBTasks.DELETE
        ).execute().get()
        if (result) {
            restaurantList.removeAt(position)
            recyclerAdapter.notifyItemRemoved(position)
            Toast.makeText(
                context,
                "${restaurantEntity.resName} removed from favourites.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Something went wrong.",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restaurantList.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun navigateToRestaurantDetailsActivity(resName: String, resId: String) {
        val intent = Intent(activity, RestaurantDetailActivity::class.java)
        intent.putExtra(restaurantNameKey, resName)
        intent.putExtra(restaurantIdKey, resId)
        startActivity(intent)
        activity?.finish()
    }
}