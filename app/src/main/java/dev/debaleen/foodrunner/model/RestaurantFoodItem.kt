package dev.debaleen.foodrunner.model

import org.json.JSONArray

data class RestaurantFoodItem(
    val id: String,
    val name: String,
    val cost: String
)


fun JSONArray.toRestaurantFoodItemList(
    idParamName: String,
    nameParamName: String,
    costParamName: String
): List<RestaurantFoodItem> {
    val returnList = mutableListOf<RestaurantFoodItem>()
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        returnList.add(
            RestaurantFoodItem(
                id = item.getString(idParamName),
                name = item.getString(nameParamName),
                cost = item.getString(costParamName)
            )
        )
    }
    return returnList
}

data class RestaurantFoodItemUIModel(
    val id: String,
    val name: String,
    val cost: String,
    var isInCart: Boolean
)

fun JSONArray.toRestaurantFoodItemUIModelList(
    idParamName: String,
    nameParamName: String,
    costParamName: String,
    isInCart: Boolean
): List<RestaurantFoodItemUIModel> {
    val returnList = mutableListOf<RestaurantFoodItemUIModel>()
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        returnList.add(
            RestaurantFoodItemUIModel(
                id = item.getString(idParamName),
                name = item.getString(nameParamName),
                cost = item.getString(costParamName),
                isInCart = isInCart
            )
        )
    }
    return returnList
}

fun RestaurantFoodItemUIModel.toRestaurantFoodItem(): RestaurantFoodItem {
    return RestaurantFoodItem(
        id = id,
        name = name,
        cost = cost
    )
}