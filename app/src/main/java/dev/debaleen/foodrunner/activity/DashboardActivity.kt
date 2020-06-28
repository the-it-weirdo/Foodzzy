package dev.debaleen.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.fragment.*
import dev.debaleen.foodrunner.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var txtUserName: TextView
    private lateinit var txtUserPhone: TextView
    private lateinit var imgUserProfilePic: ImageView

    private var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = loadSharedPreferences()

        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        txtUserName = navigationView.getHeaderView(0).findViewById(R.id.txtUserName)
        txtUserPhone = navigationView.getHeaderView(0).findViewById(R.id.txtUserPhone)
        imgUserProfilePic = navigationView.getHeaderView(0).findViewById(R.id.imgUserProfilePic)

        val userName = sharedPreferences.getString(userNameKey, "No Name")
        val userPhone = sharedPreferences.getString(userMobileKey, "No phone")
        txtUserName.text = userName
        txtUserPhone.text = getString(R.string.mobile_number_template, userPhone)

        setUpToolbar()
        openHomeFragment()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DashboardActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        imgUserProfilePic.setOnClickListener {
            navigateFromDashboardActivity(FragmentDestinations.MY_PROFILE)
        }
        txtUserName.setOnClickListener {
            navigateFromDashboardActivity(FragmentDestinations.MY_PROFILE)
        }

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {

                R.id.home -> {
                    navigateFromDashboardActivity(FragmentDestinations.HOME)
                }

                R.id.profile -> {
                    navigateFromDashboardActivity(FragmentDestinations.MY_PROFILE)
                }

                R.id.favorites -> {
                    navigateFromDashboardActivity(FragmentDestinations.FAVORITE_RESTAURANTS)
                }

                R.id.order_history -> {
                    navigateFromDashboardActivity(FragmentDestinations.ORDER_HISTORY)
                }

                R.id.faq -> {
                    navigateFromDashboardActivity(FragmentDestinations.FAQs)
                }

                R.id.log_out -> {
                    // Logout
                    logOutDialog()
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun logOutDialog() {
        val dialog = MaterialAlertDialogBuilder(this@DashboardActivity)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Are you sure you want to log out?")

        dialog.setPositiveButton("YES") { _, _ ->
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialog.setNegativeButton("NO") { _, _ ->
            navigateFromDashboardActivity(FragmentDestinations.HOME)
        }
        dialog.create()
        dialog.show()
    }

    private fun navigateFromDashboardActivity(destinationFragment: FragmentDestinations) {
        val fragment = when (destinationFragment) {
            FragmentDestinations.HOME -> HomeFragment()
            FragmentDestinations.MY_PROFILE -> ProfileFragment()
            FragmentDestinations.ORDER_HISTORY -> OrderHistoryFragment()
            FragmentDestinations.FAVORITE_RESTAURANTS -> FavoriteFragment()
            FragmentDestinations.FAQs -> FaqFragment()
        }

        val title = when (destinationFragment) {
            FragmentDestinations.HOME -> "All Restaurants"
            FragmentDestinations.MY_PROFILE -> "My Profile"
            FragmentDestinations.FAVORITE_RESTAURANTS -> "Favorite Restaurants"
            FragmentDestinations.ORDER_HISTORY -> "Previous Orders"
            FragmentDestinations.FAQs -> "Frequently Asked Questions"
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = title
        drawerLayout.closeDrawers()
    }

    private fun openHomeFragment() {
        navigateFromDashboardActivity(FragmentDestinations.HOME)
        navigationView.checkedItem?.isChecked = false
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHomeFragment()
            else -> super.onBackPressed()
        }
    }
}