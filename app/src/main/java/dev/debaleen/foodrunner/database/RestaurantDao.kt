package dev.debaleen.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertFavouriteRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteFavouriteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants WHERE user_id= :userId")
    fun getAllFavouriteRestaurants(userId: String): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE res_id = :resId AND user_id = :userId")
    fun getFavouriteRestaurantById(resId: String, userId: String): RestaurantEntity
}