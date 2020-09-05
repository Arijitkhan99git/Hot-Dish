package com.internshala.foodrunner.Activity

import Database.CartDatabase
import com.internshala.foodrunner.adapter.DescriptionAdapter
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import model.Foods
import util.ConnectionManager

class DescriptionActivity : AppCompatActivity() {
    lateinit var descriptionToolbar: Toolbar
    lateinit var btnProceed: Button
    lateinit var descRecyclerView: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DescriptionAdapter
    lateinit var res_id:String
    lateinit var res_name:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        descriptionToolbar=findViewById(R.id.descriptionToolbar)
        btnProceed=findViewById(R.id.btnproceed)
        descRecyclerView=findViewById(R.id.descRecycler)
        progressLayout=findViewById(R.id.descProgressLayout)
        layoutManager=LinearLayoutManager(this@DescriptionActivity)

        progressLayout.visibility=View.VISIBLE
        val intent=intent
     if (intent!=null) {
         res_id = intent.getStringExtra("dishId")
         res_name = intent.getStringExtra("dishName")
     }
     else
        {
            Toast.makeText(this@DescriptionActivity,"Some unexpected Error occured",Toast.LENGTH_SHORT).show()
        }

        descriptionToolbar.title=res_name
        setSupportActionBar(descriptionToolbar)

        btnProceed.setOnClickListener {
            val cartintent=Intent(this@DescriptionActivity,
                CartActivity::class.java)
            cartintent.putExtra("res_name",res_name)
            cartintent.putExtra("res_id",res_id)
            startActivity(cartintent)
        }

        if (ConnectionManager().checkConnetivity(this@DescriptionActivity))
        {
            val queue=Volley.newRequestQueue(this@DescriptionActivity)
            val url="http://13.235.250.119/v2/restaurants/fetch_result/$res_id"

            val jsonObjectRequest=object :JsonObjectRequest(
                Method.GET,url,null,Response.Listener {
                    println("food $it")

                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")
                    val itemList= arrayListOf<Foods>()

                    if (success)
                    {
                        progressLayout.visibility=View.GONE
                        val jsonArray=data.getJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            val food=jsonArray.getJSONObject(i)

                            val f=Foods(
                                food.getString("id"),
                                food.getString("name"),
                                food.getString("cost_for_one"),
                                food.getString("restaurant_id")
                            )
                            itemList.add(f)
                        }

                        recyclerAdapter=DescriptionAdapter(this@DescriptionActivity,itemList)
                        descRecyclerView.adapter=recyclerAdapter
                        descRecyclerView.layoutManager=layoutManager
                    }
                    else{
                        Toast.makeText(this@DescriptionActivity,"error occured",
                            Toast.LENGTH_SHORT).show()
                    }



                },Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity,"some error occured,try again later",
                        Toast.LENGTH_SHORT).show()
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
            val dialog=AlertDialog.Builder(this@DescriptionActivity)

            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings")
            {
                text,listner->
                val settingInntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingInntent)
                this@DescriptionActivity.finish()
            }
            dialog.setNegativeButton("Exit"){
                text,listner ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }

            dialog.create()
            dialog.show()
        }

    }

    class Cartdata(val context: Context):AsyncTask<Void,Void,Unit>(){
        override fun doInBackground(vararg p0: Void?) {
            val db= Room.databaseBuilder(context,CartDatabase::class.java,"cart-db").build()
            db.cartDao().removeAll()
        }
    }

    override fun onBackPressed() {
        Cartdata(
            applicationContext
        ).execute()
        val MainIntent=Intent(this@DescriptionActivity,
            MainActivity::class.java)
        startActivity(MainIntent)
        this@DescriptionActivity.finish()
    }

}
