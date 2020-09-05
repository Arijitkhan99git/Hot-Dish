package com.internshala.foodrunner.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internshala.foodrunner.R

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        title="Welcome Page"
    }
}
