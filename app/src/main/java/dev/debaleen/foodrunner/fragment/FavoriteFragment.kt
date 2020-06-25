package dev.debaleen.foodrunner.fragment

import android.content.Context
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
import dev.debaleen.foodrunner.adapter.RestaurantAdapter
import dev.debaleen.foodrunner.database.DBAsyncTask
import dev.debaleen.foodrunner.database.RestaurantEntity
import dev.debaleen.foodrunner.database.RetrieveFavourites
import dev.debaleen.foodrunner.database.toListRestaurantUIModel
import dev.debaleen.foodrunner.model.RestaurantUIModel
import dev.debaleen.foodrunner.util.FavouriteRestaurantsDBTasks
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

        RetrieveFavourites(activity as Context,
            object : RetrieveFavourites.AsyncListener {
                override fun publishFavourites(restaurants: List<RestaurantEntity>?) {
                    progressLayout.visibility = View.GONE
                    if (restaurants != null && restaurants.isNotEmpty()) {
                        restaurantList = ArrayList(restaurants.toListRestaurantUIModel())
                        restaurantList.forEach {
                            it.isFavourite = true
                        }
                        recyclerAdapter = RestaurantAdapter(restaurantList,
                            object : RestaurantAdapter.RestaurantClickListener {
                                override fun onRestaurantClick(position: Int, resId: String) {
                                    Toast.makeText(
                                        context,
                                        " ${restaurantList[position].resName} with id: $resId clicked.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFavouriteClick(position: Int, resId: String) {
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
                }
            }, userId!!).execute().get()

        return view
    }

    fun removeFromFavourites(restaurantEntity: RestaurantEntity, position: Int) {
        val result = DBAsyncTask(
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

}