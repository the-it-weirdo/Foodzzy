package dev.debaleen.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartElementDao {

    @Insert
    fun insertIntoCart(cartElementEntity: CartElementEntity)

    @Delete
    fun deleteFromCart(cartElementEntity: CartElementEntity)

    /*@Query("SELECT * FROM cart_table WHERE user_id = :userId AND res_id = :resId")
    fun isInCart(userId: String, resId: String): CartElementEntity*/

    @Query("SELECT * FROM cart_table WHERE user_id = :userId AND res_id = :resId")
    fun getAll(userId: String, resId: String): List<CartElementEntity>


    @Query("DELETE FROM cart_table WHERE user_id = :userId AND res_id = :resId")
    fun clearCart(userId: String, resId: String)
}