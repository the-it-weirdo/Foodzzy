package dev.debaleen.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.debaleen.foodrunner.model.RestaurantUIModel

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey @ColumnInfo(name = "res_id") val resId: String,
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
