package dev.debaleen.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class CartElementEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "res_id") val restaurantId: String,
    @ColumnInfo(name =  "food_items") val foodItems: String
) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "element_id") var elementId: Int? = null
}


/*
@Entity
data class Food(
        var foodName: String,
        var foodDesc: String,
        var protein: Double,
        var carbs: Double,
        var fat: Double
){
    @PrimaryKey(autoGenerate = true)
    var foodId: Int = 0 // or foodId: Int? = null
    var calories: Double = 0.toDouble()
}
 */