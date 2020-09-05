package com.internshala.foodrunner.Activity

import Database.CartDatabase
import Database.CartEntities
import com.internshala.foodrunner.adapter.CartAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var orderLayout: RelativeLayout
    lateinit var btnOrderPlace:Button
    lateinit var btnOrderOk:Button
    lateinit var cartToolbar:Toolbar
    lateinit var cartRecyclerView:RecyclerView
    lateinit var tvResName:TextView
    lateinit var emptyCart:RelativeLayout
    var itemList= listOf<CartEntities>()
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        orderLayout=findViewById(R.id.orderLayout)
        orderLayout.visibility=View.INVISIBLE
        btnOrderOk=findViewById(R.id.btnorderOk)
        btnOrderPlace=findViewById(R.id.btnOrderPlace)
        cartRecyclerView=findViewById(R.id.cartRecycler)
        layoutmanager=LinearLayoutManager(this@CartActivity)
        tvResName=findViewById(R.id.tvResName)
        emptyCart=findViewById(R.id.emptyCart)
        emptyCart.visibility=View.INVISIBLE

        val res_name=intent.getStringExtra("res_name")
        val res_id=intent.getStringExtra("res_id")

        tvResName.text="Ordering from $res_name"

        cartToolbar=findViewById(R.id.cartToolbar)
        setSupportActionBar(cartToolbar)
        supportActionBar?.title="My cart"

        itemList= CartData(
            applicationContext
        ).execute().get()

        var total=0
        if (!itemList.isEmpty())
        {
            for(i in 0 until itemList.size) {
                total=total+itemList[i].foodPrice.toInt()
            }
            btnOrderPlace.visibility=View.VISIBLE
            btnOrderPlace.text="Place Order (Total Rs. $total)"

            val cartAdapter=CartAdapter(this@CartActivity,itemList)
            cartRecyclerView.adapter=cartAdapter
            cartRecyclerView.layoutManager=layoutmanager
        }
        else{
            emptyCart.visibility=View.VISIBLE
            btnOrderPlace.visibility=View.INVISIBLE
        }

        btnOrderPlace.setOnClickListener {

            sharedPreferences=getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

            val queue=Volley.newRequestQueue(this@CartActivity)
            val url= "http://13.235.250.119/v2/place_order/fetch_result/"

            val param=JSONObject()
            param.put("user_id",sharedPreferences.getString("user_id",null))
            param.put("restaurant_id",res_id?.toString())
            param.put("total_cost",total.toString())

            val params=JSONArray()
            for (i in 0 until itemList.size)
            {
                val paramObj=JSONObject()
                paramObj.put("food_item_id",itemList[i].cart_food_id.toString())
                params.put(paramObj)
            }
            param.put("food",params)

            val jsonObjectRequest= object :JsonObjectRequest(
                Method.POST,url,param,Response.Listener {
                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")

                    if (success){
                        orderLayout.visibility=View.VISIBLE
                        DescriptionActivity.Cartdata(
                            applicationContext
                        ).execute()
                        Toast.makeText(this@CartActivity,"Order Placed Successfully",
                            Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this@CartActivity,"some error occured,try again later",
                            Toast.LENGTH_SHORT).show()
                    }

                },Response.ErrorListener {
                    Toast.makeText(this@CartActivity,"some error On Get Responce",
                        Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="4caf407c2bc15a"

                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }

        btnOrderOk.setOnClickListener {
            val MainIntent=Intent(this@CartActivity,
                MainActivity::class.java)
            startActivity(MainIntent)
            this@CartActivity.finish()
        }

    }

    class CartData(val context: Context):
        AsyncTask<Void,Void,List<CartEntities>>(){
        override fun doInBackground(vararg p0: Void?): List<CartEntities> ?{
            val db= Room.databaseBuilder(context,CartDatabase::class.java,"cart-db").build()
            val list=db.cartDao().getAllCart()

            return list
            }

         }

}
