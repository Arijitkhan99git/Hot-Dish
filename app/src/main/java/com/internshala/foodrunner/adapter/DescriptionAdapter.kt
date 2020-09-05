package com.internshala.foodrunner.adapter

import Database.CartDatabase
import Database.CartEntities
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.R
import model.Foods

class DescriptionAdapter(val context: Context,
                         val itemList:List<Foods>
                         ):
    RecyclerView.Adapter<DescriptionAdapter.DescriptionViewHolder>() {
    var cartItem=0
    class DescriptionViewHolder(val view: View):RecyclerView.ViewHolder(view)
    {
        val itemId:TextView=view.findViewById(R.id.txtDesId)
        val itemName:TextView=view.findViewById(R.id.txtDesfoodName)
        val itemPrice:TextView=view.findViewById(R.id.txtDesFoodPrice)
        val btntoCart:Button=view.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.recycler_description_single_row,parent,false)
        return DescriptionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val data=itemList[position]

        holder.itemId.text="${position+1}. "
        holder.itemName.text=data.foodName
        holder.itemPrice.text="Rs."+data.foodPrice
        holder.btntoCart.text="ADD"
        val cartEntities=CartEntities(data.foodId.toInt(),data.foodName,data.foodPrice,data.restaurantId)


        holder.btntoCart.setOnClickListener {
            if (!cartAsyncyTask(context,cartEntities,1).execute().get())
            {
                Toast.makeText(context,"Added to cart",Toast.LENGTH_SHORT).show()
                holder.btntoCart.text="REMOVE"
                holder.btntoCart.setBackgroundColor(
                    ContextCompat.getColor(context,R.color.remove)
                )
                cartAsyncyTask(context,cartEntities,2).execute().get()

            }
            else
            {
                Toast.makeText(context,"Remove from cart",Toast.LENGTH_SHORT).show()
                holder.btntoCart.text="ADD"
                holder.btntoCart.setBackgroundColor(
                    ContextCompat.getColor(context,R.color.colorFav)
                )
                cartAsyncyTask(context,cartEntities,3).execute().get()
            }
/*
            if (cartItem==0)
            {
                btnProceed.visibility=View.GONE
            }
            else
            {
                btnProceed.text="Procced to cart"
                btnProceed.visibility=View.VISIBLE
            }
*/

        }
    }

    class cartAsyncyTask(val context: Context,val cartData:CartEntities,val mode:Int):
            AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean {
            val db= Room.databaseBuilder(context,CartDatabase::class.java,"cart-db").build()

            when(mode){
                1->{
                    //check food is present on Cart or Not
                    val isPresent=db.cartDao().getById(cartData.cart_food_id.toString())
                    return isPresent!=null
                }

                2->
                {//add to the cart
                    db.cartDao().cartInsert(cartData)
                    return true
                }

                3->
                {
                    //remove from the cart
                    db.cartDao().cartDelete(cartData)
                    return true
                }
            }
            return false
        }

    }



}