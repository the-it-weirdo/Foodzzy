package dev.debaleen.foodrunner.util

const val isLoggedInKey = "isLoggedIn"
const val userIdKey=  "userId"
const val userMobileKey = "userMobile"
const val userEmailKey = "userEmail"
const val userNameKey = "userName"
const val userAddressKey = "userAddress"
const val restaurantIdKey = "res_id"
const val restaurantNameKey = "res_name"

const val emailRegexString =  "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:" +
        "[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
val emailRegex = emailRegexString.toRegex()
