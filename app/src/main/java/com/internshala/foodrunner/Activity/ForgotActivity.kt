package com.internshala.foodrunner.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import fragments.ForgotFragment
import org.json.JSONObject
import util.ConnectionManager

class ForgotActivity : AppCompatActivity() {
    lateinit var btnFLogin:Button
    lateinit var etFMobileNumber:EditText
    lateinit var etFEmailAddress:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

         btnFLogin=findViewById(R.id.btnFLogin)
        etFMobileNumber=findViewById(R.id.etFMobileNumber)
        etFEmailAddress=findViewById(R.id.etFEmailAddress)

        btnFLogin.setOnClickListener {
                val numberlen=etFMobileNumber.text.length
            if (numberlen ==10) {
                if (ConnectionManager().checkConnetivity(this@ForgotActivity)) {

                    val queue = Volley.newRequestQueue(this@ForgotActivity)
                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonparams = JSONObject()
                    jsonparams.put("mobile_number", etFMobileNumber.text.toString())
                    jsonparams.put("email", etFEmailAddress.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST, url, jsonparams, Response.Listener {
                            try {

                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    val firstTry = data.getBoolean("first_try")
                                    Toast.makeText(
                                        this@ForgotActivity,
                                        "An OTP is sent in your registered mail",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    supportFragmentManager.beginTransaction().replace(
                                        R.id.resetFrame,
                                        ForgotFragment(etFMobileNumber.text.toString())
                                    ).addToBackStack("Forgot Password").commit()

                                    btnFLogin.visibility = View.GONE
                                } else {
                                    Toast.makeText(
                                        this@ForgotActivity,
                                        "Incorrect Email/Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ForgotActivity,
                                    "Some error Occurred !!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotActivity,
                                "some error occured,try again later!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "4caf407c2bc15a"

                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@ForgotActivity)

                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    { text, listner ->
                        val settingInntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingInntent)
                        this@ForgotActivity.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@ForgotActivity)
                    }

                    dialog.create()
                    dialog.show()
                }
            }
            else
            {
                Toast.makeText(
                    this@ForgotActivity,
                    "Enter the number of 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //upto
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.resetFrame)

        if (frag is ForgotFragment)
        {
            super.onBackPressed()
            etFMobileNumber.text=null
            etFEmailAddress.text=null
            btnFLogin.visibility=View.VISIBLE
        }
        else
            {
                val intent=Intent(this@ForgotActivity,
                    LoginActivity::class.java)
                startActivity(intent)
            }
      }
}
