package dev.debaleen.foodrunner.model

data class OrderHistoryItem(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val orderPlacedAt: String,
    val foodItems: List<FoodItem>
)