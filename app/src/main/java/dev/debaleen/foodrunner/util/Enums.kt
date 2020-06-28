package dev.debaleen.foodrunner.util

enum class InputState {
    INVALID_MOBILE, INVALID_PASSWORD, OKAY, INVALID_EMAIL, INVALID_NAME, INVALID_ADDRESS, PSW_NO_MATCH, INVALID_OTP
}

enum class FragmentDestinations {
    HOME, MY_PROFILE, FAVORITE_RESTAURANTS, ORDER_HISTORY, FAQs
}

enum class FavouriteRestaurantsDBTasks {
    INSERT, DELETE, CHECK_FAVOURITE
}

enum class CartDBTasks {
    INSERT, DELETE
}

enum class LoginActivityDestinations {
    DASHBOARD, FORGOT_PASSWORD, REGISTRATION
}

enum class RestaurantSortOn {
    NONE, RATING, PRICE_HIGH_TO_LOW, PRICE_LOW_TO_HIGH
}