package dev.debaleen.foodrunner.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import dev.debaleen.foodrunner.util.FavouriteRestaurantsDBTasks

class DBAsyncTask(
    context: Context,
    private val restaurantEntity: RestaurantEntity,
    private val mode: FavouriteRestaurantsDBTasks
) :
    AsyncTask<Void, Void, Boolean>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {
            FavouriteRestaurantsDBTasks.INSERT -> {
                db.restaurantDao().insertFavouriteRestaurant(restaurantEntity)
                db.close()
                return true
            }
            FavouriteRestaurantsDBTasks.DELETE -> {
                db.restaurantDao().deleteFavouriteRestaurant(restaurantEntity)
                db.close()
                return true
            }
            FavouriteRestaurantsDBTasks.CHECK_FAVOURITE -> {
                val restaurant: RestaurantEntity? =
                    db.restaurantDao().getFavouriteRestaurantById(restaurantEntity.resId)
                db.close()
                return restaurant != null
            }
            else -> {
                // Do nothing
            }
        }
        return false
    }
}

class RetrieveFavourites(val context: Context, val listener: AsyncListener) :
    AsyncTask<Void, Void, List<RestaurantEntity>>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
        return db.restaurantDao().getAllFavouriteRestaurants()
    }

    override fun onPostExecute(result: List<RestaurantEntity>?) {
        db.close()
        listener.publishFavourites(result)
        super.onPostExecute(result)
    }

    public interface AsyncListener {
        fun publishFavourites(restaurants: List<RestaurantEntity>?)
    }
}

/*
class DBAsyncTasks(
    context: Context,
    private val restaurantEntity: RestaurantEntity,
    private val mode: FavouriteRestaurantsDBTasks
) :
    AsyncTask<Void, Void, Boolean>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {
            FavouriteRestaurantsDBTasks.INSERT -> {
                db.restaurantDao().insertFavouriteRestaurant(restaurantEntity)
                db.close()
                return true
            }
            FavouriteRestaurantsDBTasks.DELETE -> {
                db.restaurantDao().deleteFavouriteRestaurant(restaurantEntity)
                db.close()
                return true
            }
            FavouriteRestaurantsDBTasks.CHECK_FAVOURITE -> {
                val restaurant: RestaurantEntity? =
                    db.restaurantDao().getFavouriteRestaurantById(restaurantEntity.resId)
                db.close()
                return restaurant != null
            }
            else -> {
                // Do nothing
            }
        }
        return false
    }

    public interface DBAsyncTasksListener {
        fun insertTaskComplete(success: Boolean)
        fun deleteTaskComplete(success: Boolean)
        fun checkFavouriteTaskComplete(isFavourite: Boolean)
    }
}*/
