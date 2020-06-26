package dev.debaleen.foodrunner.util

const val NETWORK_IP = "http://13.235.250.119/v2/"
const val REGISTER = "$NETWORK_IP/register/fetch_result"
const val LOGIN = "$NETWORK_IP/login/fetch_result"
const val FORGOT_PASSWORD = "$NETWORK_IP/forgot_password/fetch_result"
const val RESET_PASSWORD = "$NETWORK_IP/reset_password/fetch_result"
const val FETCH_RESTAURANTS = "$NETWORK_IP/restaurants/fetch_result"
const val FETCH_RESTAURANT_DETAILS = "$NETWORK_IP/restaurants/fetch_result/"
const val PLACE_ORDER = "$NETWORK_IP/place_order/fetch_result"
const val FETCH_PREVIOUS_ORDERS = "$NETWORK_IP/orders/fetch_result/"
const val TOKEN = "141f8efeca1968"

const val isLoggedInKey = "isLoggedIn"
const val userIdKey=  "userId"
const val userMobileKey = "userMobile"
const val userEmailKey = "userEmail"
const val userNameKey = "userName"
const val userAddressKey = "userAddress"

const val restaurantIdKey = "res_id"
const val restaurantNameKey = "res_name"

const val fromKey = "fromActivity"
const val forgotPasswordActivity = "ForgotPasswordActivity"
const val splashActivity = "SplashActivity"

const val emailRegexString =  "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:" +
        "[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
val emailRegex = emailRegexString.toRegex()
