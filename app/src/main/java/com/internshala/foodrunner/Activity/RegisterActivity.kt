package com.internshala.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    lateinit var btnRRegister: Button
    lateinit var etRConfirmPassword: EditText
    lateinit var etRPassword: EditText
    lateinit var etRName: EditText
    lateinit var etREmailAddress: EditText
    lateinit var etRMobileNumber: EditText
    lateinit var etRDelivaryAddress: EditText

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register Page"

        //creating the obj of sharedPreferences
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        btnRRegister = findViewById(R.id.btnRRegister)

        etRName = findViewById(R.id.etRName)
        etREmailAddress = findViewById(R.id.etREmailAddress)
        etRMobileNumber = findViewById(R.id.etRMobileNumber)
        etRDelivaryAddress = findViewById(R.id.etRDelivaryAddress)
        etRPassword = findViewById(R.id.etRPassword)
        etRConfirmPassword = findViewById(R.id.etRConfirmPassword)

        val intent = Intent(this@RegisterActivity, MainActivity::class.java)

        btnRRegister.setOnClickListener {
            // intent.putExtra("Name", confirmpass)
            val namelen=etRName.text.length
            val numberlen=etRMobileNumber.text.length
            val passlen=etRPassword.text.length

            if ((namelen >=3) && (numberlen==10) && (passlen >=4)) {
                if (etRPassword.text.toString() == etRConfirmPassword.text.toString()) {
                    if (ConnectionManager().checkConnetivity(this@RegisterActivity)) {

                        val queue = Volley.newRequestQueue(this@RegisterActivity)
                        val url = "http://13.235.250.119/v2/register/fetch_result/"

                        val jsonParams = JSONObject()
                        jsonParams.put("name", etRName.text.toString())
                        jsonParams.put("mobile_number", etRMobileNumber.text.toString())
                        jsonParams.put("password", etRPassword.text.toString())
                        jsonParams.put("address", etRDelivaryAddress.text.toString())
                        jsonParams.put("email", etREmailAddress.text.toString())

                        val jsonObjectRequest = object :
                            JsonObjectRequest(Request.Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {

                                    try {

                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {

                                            println("success$it")
                                            val accountData = data.getJSONObject("data")

                                            sharedPreferences.edit()
                                                .putString(
                                                    "user_id",
                                                    accountData.getString("user_id")
                                                )
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("name", accountData.getString("name"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("email", accountData.getString("email"))
                                                .apply()
                                            sharedPreferences.edit().putString(
                                                "mobile",
                                                accountData.getString("mobile_number")
                                            ).apply()

                                            sharedPreferences.edit()
                                                .putString(
                                                    "address",
                                                    accountData.getString("address")
                                                )
                                                .apply()

                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Successfully Registered",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            sharedPreferences.edit().putBoolean("isLoggedin", true)
                                                .apply()

                                            startActivity(intent)

                                        } else {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Account could not be registered",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Some Error Occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Volley Error Occurred!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "4caf407c2bc15a"
                                return headers
                            }
                        }

                        queue.add(jsonObjectRequest)

                    } else {

                        val dialog = AlertDialog.Builder(this@RegisterActivity)
                        dialog.setMessage("Internet Connection Not Found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@RegisterActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }

                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password does not match!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else
            {
                Toast.makeText(
                    this@RegisterActivity,
                    "Enter Correct input",
                    Toast.LENGTH_SHORT
                ).show()
            }
//elseblock of len
        }
    }

    override fun onBackPressed() {
        val intent=Intent( this@RegisterActivity,
            LoginActivity::class.java)
        startActivity(intent)
    }

}

/*
imgBackArrow.setOnClickListener {
    val intent =
        Intent(this@RegisterActivity, LoginActivity::class.java)
    startActivity(intent)
}
  */
/*
    fun saveProfile()
    {
        if(etRPassword.text.toString()==etRConfirmPassword.text.toString())
        {
        sharedPreferences.edit().putString("name",etRName.text.toString()).apply()
        sharedPreferences.edit().putString("email",etREmailAddress.text.toString()).apply()
        sharedPreferences.edit().putString("mobile",etRMobileNumber.text.toString()).apply()
        sharedPreferences.edit().putString("address",etRDelivaryAddress.text.toString()).apply()
        sharedPreferences.edit().putString("password",etRPassword.text.toString()).apply()

            Toast.makeText(this@RegisterActivity, "you are succefully registered", Toast.LENGTH_LONG).show()
            }
        else
        {
            Toast.makeText(this@RegisterActivity, "Password Mismatch !, Please re-Enter ", Toast.LENGTH_LONG).show()
        }
    }
*/


