package dev.debaleen.foodrunner.model

import org.json.JSONArray

data class RestaurantFoodItem(
    val id : String,
    val name: String,
    val costForOne: String,
    val restaurantId: String
)


fun JSONArray.toRestaurantFoodItemList(): List<RestaurantFoodItem> {
    val returnList = mutableListOf<RestaurantFoodItem>()
    for(i in 0 until length()) {
        val item = getJSONObject(i)
        returnList.add(
            RestaurantFoodItem(
                id = item.getString("id"),
                name = item.getString("name"),
                costForOne = item.getString("cost_for_one"),
                restaurantId = item.getString("restaurant_id")
            )
        )
    }
    return returnList
}

/*data class RestaurantFoodItemUIModel(
    val id : String,
    val name: String,
    val costForOne: String,
    val restaurantId: String,
    val isInCart: Boolean
)



/*fun RestaurantFoodItemUIModel.toRestaurantFoodItem() :RestaurantFoodItem {
    return RestaurantFoodItem(
        id = id,
        name = name,
        costForOne = costForOne,
        restaurantId = restaurantId
    )
}*/
/*
fun RestaurantFoodItem.toRestaurantFoodItemUIModel(isInCart: Boolean) :RestaurantFoodItemUIModel {
    return RestaurantFoodItemUIModel(
        id = id,
        name = name,
        costForOne = costForOne,
        restaurantId = restaurantId,
        isInCart = isInCart
    )
}
*/