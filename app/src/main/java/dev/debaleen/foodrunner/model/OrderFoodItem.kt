package dev.debaleen.foodrunner.model

import org.json.JSONArray

data class OrderFoodItem(
    val foodItemId: String,
    val name: String,
    val cost: String
)

fun JSONArray.toFoodItemList(): List<OrderFoodItem> {
    val returnList = mutableListOf<OrderFoodItem>()
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        returnList.add(
            OrderFoodItem(
                foodItemId = item.getString("food_item_id"),
                name = item.getString("name"),
                cost = item.getString("cost")
            )
        )
    }
    return returnList
}