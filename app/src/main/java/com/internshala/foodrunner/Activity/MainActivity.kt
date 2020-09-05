package com.internshala.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodrunner.R
import fragments.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences:SharedPreferences
    lateinit var txtUserName:TextView
    lateinit var txtUserMailId:TextView
    var previousmenuitem:MenuItem?=null
    lateinit var searchview:SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinateLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        searchview=findViewById(R.id.search_bar)

       setUpToolbar()
        openHomeFragment()

        //for saving the state
        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousmenuitem!=null){
                previousmenuitem?.isChecked=false
            }

            it.isChecked=true
            it.isCheckable=true
            previousmenuitem=it

            when(it.itemId)
            {
                    R.id.homePage ->
                    {
                        openHomeFragment()

                        drawerLayout.closeDrawers()

                    }

                    R.id.profile ->
                    {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        ).commit()

                        supportActionBar?.title="My Profile"
                        searchview.visibility=View.GONE
                        drawerLayout.closeDrawers()
                    }

                    R.id.favourite ->
                    {
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FavouritesFragment()).commit()

                        supportActionBar?.title="Favourite Restaurants"
                        searchview.visibility=View.GONE
                        drawerLayout.closeDrawers()
                    }

                    R.id.history ->
                    {
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout,HistoryFragment()).commit()
                        supportActionBar?.title="Order History"
                        searchview.visibility=View.GONE
                        drawerLayout.closeDrawers()
                    }

                    R.id.faq ->
                    {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frameLayout,
                            FaqFragment()
                        ).commit()

                        supportActionBar?.title="FAQs"
                        searchview.visibility=View.GONE
                        drawerLayout.closeDrawers()
                    }

                    R.id.logOut ->
                    {
                        sharedPreferences.edit().remove("isLoggedin").apply()

                        val dialog= AlertDialog.Builder(this@MainActivity)

                        dialog.setTitle("Logout")
                        dialog.setMessage("Are you sure you want to logout?")
                        dialog.setPositiveButton("Yes")
                        {
                                text,listner->
                            val intent=Intent(this@MainActivity,
                                LoginActivity::class.java)
                            startActivity(intent)

                            Toast.makeText(this@MainActivity,"LogOut Successful",Toast.LENGTH_SHORT).show()
                            this@MainActivity.finish()
                        }
                        dialog.setNegativeButton("No"){
                                text,listner ->
                            drawerLayout.closeDrawers()
                        }

                        dialog.create()
                        dialog.show()

                        drawerLayout.closeDrawers()
                    }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    fun openHomeFragment()
    {
        navigationView.setCheckedItem(R.id.homePage)
        val transaction
                =supportFragmentManager.beginTransaction().
                    replace(R.id.frameLayout,HomeFragment(searchview))

        transaction.commit()
        supportActionBar?.title="Home Page"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
         val id=item?.itemId

        if (id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
            val headers=navigationView.getHeaderView(0)

            txtUserName=headers.findViewById(R.id.txtUserName)
            txtUserMailId=headers.findViewById(R.id.txtUserMailId)

            txtUserMailId.text= sharedPreferences.getString("email",null)
            txtUserName.text=sharedPreferences.getString("name",null)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)

        when(frag){
            !is HomeFragment ->openHomeFragment()

            else->
            {
                ActivityCompat.finishAffinity(this@MainActivity)
            }
            //else->super.onBackPressed()
        }
    }
}
