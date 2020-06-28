package dev.debaleen.foodrunner.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import dev.debaleen.foodrunner.database.entity.CartElementEntity
import dev.debaleen.foodrunner.database.entity.RestaurantEntity
import dev.debaleen.foodrunner.util.CartDBTasks
import dev.debaleen.foodrunner.util.FavouriteRestaurantsDBTasks

class FavouriteDBAsyncTask(
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
                    db.restaurantDao()
                        .getFavouriteRestaurantById(restaurantEntity.resId, restaurantEntity.userId)
                db.close()
                return restaurant != null
            }
        }
    }
}

class RetrieveFavouritesAsyncTask(
    val context: Context,
    private val listener: AsyncTaskCompleteListener,
    private val userId: String
) :
    AsyncTask<Void, Void, List<RestaurantEntity>>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
        return db.restaurantDao().getAllFavouriteRestaurants(userId)
    }

    override fun onPostExecute(result: List<RestaurantEntity>?) {
        db.close()
        listener.onTaskComplete()
        super.onPostExecute(result)
    }
}


class CartDBAsyncTasks(
    context: Context,
    private val cartElementEntity: CartElementEntity,
    private val mode: CartDBTasks
) : AsyncTask<Void, Void, Boolean>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        return when (mode) {
            CartDBTasks.INSERT -> {
                db.cartElementDao().insertIntoCart(cartElementEntity)
                db.close()
                true
            }
            CartDBTasks.DELETE -> {
                db.cartElementDao().deleteFromCart(cartElementEntity)
                db.close()
                true
            }
        }
    }
}

class RetrieveCartAsyncTask(
    context: Context,
    private val userId: String,
    private val resId: String,
    private val listener: AsyncTaskCompleteListener
) : AsyncTask<Void, Void, List<CartElementEntity>>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): List<CartElementEntity> {
        return db.cartElementDao().getAll(userId, resId)
    }

    override fun onPostExecute(result: List<CartElementEntity>?) {
        db.close()
        listener.onTaskComplete()
        super.onPostExecute(result)
    }
}

class ClearCartAsyncTask(
    context: Context,
    private val userId: String,
    private val resId: String,
    private val listener: AsyncTaskCompleteListener
) : AsyncTask<Void, Void, Void>() {

    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg params: Void?): Void? {
        db.cartElementDao().clearCart(userId, resId)
        return null
    }

    override fun onPostExecute(result: Void?) {
        db.close()
        listener.onTaskComplete()
        super.onPostExecute(result)
    }
}

// Using this Listener to mimic the functionality of onPostExecute() when the task has completed execution.
interface AsyncTaskCompleteListener {
    fun onTaskComplete()
}
