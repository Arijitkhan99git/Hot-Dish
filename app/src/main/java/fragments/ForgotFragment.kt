package fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.Activity.LoginActivity
import com.internshala.foodrunner.R
import org.json.JSONObject
import util.ConnectionManager


class ForgotFragment(val mobileNumber:String) : Fragment() {
    lateinit var etOtp:EditText
    lateinit var etResetPassword:EditText
    lateinit var etResetConfirmPassword:EditText
    lateinit var btnSubmit:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_forgot, container, false)

        etOtp=view.findViewById(R.id.etOtp)
        etResetPassword=view.findViewById(R.id.etResetPassword)
        etResetConfirmPassword=view.findViewById(R.id.etResetConfirmPassword)
        btnSubmit=view.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val otplen=etOtp.text.length
            val passlen=etResetPassword.text.length

            if (otplen==4 && passlen>=4) {
                if (etResetPassword.text.toString()
                        .equals(etResetConfirmPassword.text.toString())
                ) {
                    if (ConnectionManager().checkConnetivity(activity as Context)) {

                        val queue = Volley.newRequestQueue(activity as Context)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                        val jsonparams = JSONObject()
                        jsonparams.put("mobile_number", mobileNumber)
                        jsonparams.put("password", etResetPassword.text.toString())
                        jsonparams.put("otp", etOtp.text.toString())

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST, url, jsonparams, Response.Listener {
                                try {

                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {

                                        Toast.makeText(
                                            activity as Context,
                                            "Password Reset Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(activity as Context, LoginActivity::class.java)
                                        startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            activity as Context,
                                            "Incorrect OTP",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        activity as Context,
                                        "Some error Occurred !!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    activity as Context, "some error occured,try again later!!",
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
                        val dialog = AlertDialog.Builder(activity as Context)

                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found")
                        dialog.setPositiveButton("Open Settings")
                        { text, listner ->
                            val settingInntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingInntent)
                            activity?.finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listner ->
                            ActivityCompat.finishAffinity(activity as Activity)
                        }

                        dialog.create()
                        dialog.show()
                    }
                } else {
                    Toast.makeText(activity as Context, "Password does't match", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            else
            {
                Toast.makeText(activity as Context, "Enter correct input", Toast.LENGTH_SHORT)
                    .show()
            }
            //here
        }

        return view
    }

}
