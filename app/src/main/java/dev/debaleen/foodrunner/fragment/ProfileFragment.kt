package dev.debaleen.foodrunner.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.util.*

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var txtUserName: TextView
    private lateinit var txtUserPhone: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtUserDeliveryAddress: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Getting the shared preference
        sharedPreferences = loadSharedPreferences()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtUserPhone = view.findViewById(R.id.txtUserPhone)
        txtUserEmail = view.findViewById(R.id.txtUserEmail)
        txtUserDeliveryAddress = view.findViewById(R.id.txtUserDeliveryAddress)

        val userName = sharedPreferences.getString(userNameKey, "No name")
        val userPhone = sharedPreferences.getString(userMobileKey, "No Phone")
        val userEmail = sharedPreferences.getString(userEmailKey, "No email")
        val userDeliveryAddress = sharedPreferences.getString(userAddressKey, "No address")

        txtUserName.text = userName
        txtUserPhone.text = getString(R.string.mobile_number_template, userPhone)
        txtUserEmail.text = userEmail
        txtUserDeliveryAddress.text = userDeliveryAddress

        return view
    }

}