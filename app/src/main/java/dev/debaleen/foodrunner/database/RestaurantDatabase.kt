package dev.debaleen.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.debaleen.foodrunner.database.dao.CartElementDao
import dev.debaleen.foodrunner.database.dao.RestaurantDao
import dev.debaleen.foodrunner.database.entity.CartElementEntity
import dev.debaleen.foodrunner.database.entity.RestaurantEntity

@Database(entities = [RestaurantEntity::class, CartElementEntity::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao

    abstract fun cartElementDao(): CartElementDao
}