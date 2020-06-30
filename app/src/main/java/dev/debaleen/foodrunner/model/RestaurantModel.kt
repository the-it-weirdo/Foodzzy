package dev.debaleen.foodrunner.model

import dev.debaleen.foodrunner.database.entity.RestaurantEntity

data class RestaurantUIModel (
    val resId: String,
    val resName: String,
    val resRating: String,
    val resCostForOne: String,
    val resImageUrl: String,
    var isFavourite:Boolean = false
)

fun RestaurantUIModel.toRestaurantEntity(userId: String): RestaurantEntity {
    return RestaurantEntity(
        id = resId+userId,
        resId = resId,
        userId = userId,
        resName = resName,
        resRating = resRating,
        resCostForOne = resCostForOne,
        resImageUrl = resImageUrl
    )
}