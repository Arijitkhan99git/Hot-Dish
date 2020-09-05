package com.internshala.foodrunner.adapter

import Database.CartEntities
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R

class CartAdapter (val context: Context,val itemList:List<CartEntities>)
    :RecyclerView.Adapter<CartAdapter.CartViewHolder>(){
    class CartViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val foodName:TextView=view.findViewById(R.id.cartFoodName)
        val foodPrice:TextView=view.findViewById(R.id.cartFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val data=itemList[position]
        holder.foodName.text=data.foodName
        holder.foodPrice.text="Rs. "+data.foodPrice
    }
}