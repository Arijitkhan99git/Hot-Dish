package com.internshala.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import org.json.JSONObject
import util.ConnectionManager

class LoginActivity : AppCompatActivity() {

    lateinit var etLMobileNumber:EditText
    lateinit var etLPassword:EditText
    lateinit var btnLLogin:Button
    lateinit var txtLForgotPassword:TextView
    lateinit var txtLRegister:TextView

    lateinit var sharedPreferences: SharedPreferences

  //  val validnumber="123456"
 //   val validpass="food"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title="Log In"

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        val isLoggedin=sharedPreferences.getBoolean("isLoggedin",false)
        val intent=Intent(this@LoginActivity, MainActivity::class.java)

        if(isLoggedin)
        {
            startActivity(intent)
            finish()
        }
    /*
       else
        {
            setContentView(R.layout.activity_login)
        }
*/
        etLMobileNumber=findViewById(R.id.etLMobileNumber)
        etLPassword=findViewById(R.id.etLPassword)
        btnLLogin=findViewById(R.id.btnLLogin)
        txtLForgotPassword=findViewById(R.id.txtLForgotPassword)
        txtLRegister=findViewById(R.id.txtLRegister)

        btnLLogin.setOnClickListener {

            val number=etLMobileNumber.text.toString()
            val pass=etLPassword.text.toString()

            val len=etLMobileNumber.text.length

          if (number !=null && pass !=null && len==10)
         {
                if (ConnectionManager().checkConnetivity(this@LoginActivity)){

                    val queue=Volley.newRequestQueue(this@LoginActivity)
                    val url="http://13.235.250.119/v2/login/fetch_result"

                    val jsonParam=JSONObject()
                    jsonParam.put("mobile_number",etLMobileNumber.text.toString())
                    jsonParam.put("password",etLPassword.text.toString())

                    val jsonObjectRequest=object:JsonObjectRequest(
                        Method.POST,url,jsonParam,Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    val accountData = data.getJSONObject("data")

                                    sharedPreferences.edit()
                                        .putString("user_id", accountData.getString("user_id"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("name", accountData.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("email", accountData.getString("email")).apply()
                                    sharedPreferences.edit().putString(
                                        "mobile",
                                        accountData.getString("mobile_number")
                                    ).apply()

                                    sharedPreferences.edit()
                                        .putString("address", accountData.getString("address"))
                                        .apply()

                                   // sharedPreferences.edit().putBoolean("isLoggedin", true).apply()
                                    savesharedpre()
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Successful",
                                        Toast.LENGTH_LONG
                                    ).show()


                                    startActivity(intent)

                                    this.finish()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Wrong Number/Password",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            catch (e:Exception){
                                Toast.makeText(this@LoginActivity,"API error",Toast.LENGTH_LONG).show()
                            }
                        },Response.ErrorListener {
                            Toast.makeText(this@LoginActivity,"some error occured,try again later!!",Toast.LENGTH_LONG).show()
                        }
                    ){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"] = "4caf407c2bc15a"

                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }
                else{
                    val dialog= AlertDialog.Builder(this@LoginActivity)

                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    {
                            text,listner->
                        val settingInntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingInntent)
                        this@LoginActivity.finish()
                    }
                    dialog.setNegativeButton("Exit"){
                            text,listner ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }

                    dialog.create()
                    dialog.show()
                }

           }

            else
            {
                Toast.makeText(this@LoginActivity,"Please provide some input",Toast.LENGTH_SHORT).show()
            }

        }


        txtLForgotPassword.setOnClickListener {
           val fintent=Intent(this@LoginActivity,
               ForgotActivity::class.java)
            startActivity(fintent)
        }

        txtLRegister.setOnClickListener {
            val rintent=Intent(this@LoginActivity,
                RegisterActivity::class.java)
            startActivity(rintent)
        }
    }

    fun savesharedpre()
    {
        sharedPreferences.edit().putBoolean("isLoggedin",true).apply()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@LoginActivity)
    }
}
