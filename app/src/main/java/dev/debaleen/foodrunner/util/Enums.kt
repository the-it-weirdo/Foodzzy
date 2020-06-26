package dev.debaleen.foodrunner.util

enum class InputState {
    WRONG_MOBILE, WRONG_PASSWORD, OKAY, WRONG_EMAIL, WRONG_NAME, WRONG_ADDRESS, PSW_NO_MATCH
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