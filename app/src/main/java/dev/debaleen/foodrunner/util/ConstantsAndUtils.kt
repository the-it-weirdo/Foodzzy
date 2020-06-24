package dev.debaleen.foodrunner.util

const val isLoggedInKey = "isLoggedIn"
const val userMobileKey = "userMobile"
const val userPasswordKey = "userPassword"
const val userEmailKey = "userEmail"
const val userNameKey = "userName"
const val userAddressKey = "userAddress"


const val fromKey = "fromActivity"
const val loginActivity = "LoginActivity"
const val registerActivity = "RegistrationActivity"
const val forgotPasswordActivity = "ForgotPasswordActivity"
const val splashActivity = "SplashActivity"

const val emailRegexString =  "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:" +
        "[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
val emailRegex = emailRegexString.toRegex()

enum class InputState {
    WRONG_MOBILE, WRONG_PASSWORD, OKAY, WRONG_EMAIL, WRONG_NAME, WRONG_ADDRESS, PSW_NO_MATCH
}

enum class FragmentDestinations {
    HOME, MY_PROFILE, FAVORITE_RESTAURANTS, FAQs
}

enum class FavouriteRestaurantsDBTasks {
    INSERT, DELETE, GET_ALL, CHECK_FAVOURITE
}