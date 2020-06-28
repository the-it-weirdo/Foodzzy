package dev.debaleen.foodrunner.model

import org.json.JSONArray

data class RestaurantFoodItem(
    val id : String,
    val name: String,
    val cost: String
)


fun JSONArray.toRestaurantFoodItemList(idParamName:String, nameParamName:String, costParamName:String): List<RestaurantFoodItem> {
    val returnList = mutableListOf<RestaurantFoodItem>()
    for(i in 0 until length()) {
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