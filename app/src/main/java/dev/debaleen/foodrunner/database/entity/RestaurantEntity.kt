package dev.debaleen.foodrunner.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.debaleen.foodrunner.model.RestaurantUIModel

/*
Storing user_id along with entities so that multiple
user can use the app from the same device.
* */

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    // Since, we are also storing user_id, res_id alone cannot be the
    // primary key. That will violate UNIQUE Constraint of DB when a 2nd user tries to add the same
    // restaurant as favourite.
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "res_id") val resId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "res_name") val resName: String,
    @ColumnInfo(name = "res_rating") val resRating: String,
    @ColumnInfo(name = "res_cost_pp") val resCostForOne: String,
    @ColumnInfo(name = "res_image_url") val resImageUrl: String
)

fun List<RestaurantEntity>.toListRestaurantUIModel(): List<RestaurantUIModel> {
    val returnList = mutableListOf<RestaurantUIModel>()
    map {
        returnList.add(
            RestaurantUIModel(
                resId = it.resId,
                resName = it.resName,
                resCostForOne = it.resCostForOne,
                resRating = it.resRating,
                resImageUrl = it.resImageUrl
            )
        )
    }
    return returnList
}
