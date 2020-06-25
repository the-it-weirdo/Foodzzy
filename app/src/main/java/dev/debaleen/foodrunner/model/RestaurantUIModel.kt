package dev.debaleen.foodrunner.model

import dev.debaleen.foodrunner.database.RestaurantEntity

class RestaurantUIModel(
    val resId: String,
    val resName: String,
    val resRating: String,
    val resCostForOne: String,
    val resImageUrl: String
) {
    var isFavourite: Boolean = false
        set(value: Boolean) {
            field = value
        }

    fun toRestaurantEntity(userId: String): RestaurantEntity {
        return RestaurantEntity(
            resId = this.resId,
            userId = userId,
            resName = this.resName,
            resRating = this.resRating,
            resCostForOne = this.resCostForOne,
            resImageUrl = this.resImageUrl
        )
    }
}