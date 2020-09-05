package com.internshala.foodrunner.adapter

import Database.DishEntity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.Activity.DescriptionActivity
import com.internshala.foodrunner.R
import com.squareup.picasso.Picasso

class FavouriteAdapter(val context: Context,val dishList:List<DishEntity>)
    :RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>(){

    class FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view){
            val favdishName:TextView=view.findViewById(R.id.txtFavDishName)
            val favdishPrice:TextView=view.findViewById(R.id.txtFavDishPrice)
            val favdishRating:TextView=view.findViewById(R.id.txtFavDishRating)
            val favdishImage:ImageView=view.findViewById(R.id.imgFavDishImage)
            val addFav:TextView=view.findViewById(R.id.addFavFav)
            val llFav:LinearLayout=view.findViewById(R.id.llFavcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
            val view=LayoutInflater.from(context).inflate(R.layout.recycler_favourite_single_row,parent,false)
            return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val dish=dishList[position]

        holder.favdishName.text=dish.dishName
        holder.favdishPrice.text="Price: "+ dish.dishPrice+"/Dish"
        holder.favdishRating.text=dish.dishRating
        Picasso.get().load(dish.dishImage).error(R.drawable.default_dish_cover).into(holder.favdishImage)
        holder.addFav.setBackgroundResource(R.drawable.ic_action_addfav)

        val dishId=dish.dish_id.toString()
        val dishName=dish.dishName

        holder.llFav.setOnClickListener {
            val intent= Intent(context, DescriptionActivity::class.java)
            intent.putExtra("dishId",dishId)
            intent.putExtra("dishName",dishName)
            context.startActivity(intent)
        }

    }

}