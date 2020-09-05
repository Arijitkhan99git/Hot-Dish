package com.internshala.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import org.json.JSONArray

class OrderHistoryRecyclerAdapter(val context: Context,val orderHistory:JSONArray):
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>()
{
    var foodItem=JSONArray()
    class OrderHistoryViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtOrderHistoryRestaurantName:TextView=view.findViewById(R.id.txtOrderHistoryRestaurantName)
        val txtOrderHistoryDate:TextView=view.findViewById(R.id.txtOrderHistoryDate)
        val txtOrderHistoryFoodName:TextView=view.findViewById(R.id.txtOrderHistoryFoodName)
        val txtOrderHistoryFoodPrice:TextView=view.findViewById(R.id.txtOrderHistoryFoodPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_single_row,parent,false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
          return  orderHistory.length()
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order=orderHistory.getJSONObject(position)

        holder.txtOrderHistoryRestaurantName.text=order.getString("restaurant_name")
        holder.txtOrderHistoryDate.text=order.getString("order_placed_at")

        foodItem=order.getJSONArray("food_items")

        var foodName=""
        var foodPrice=""

        for (i in 0 until foodItem.length())
        {
            foodName=foodName.plus("${foodItem.getJSONObject(i).getString("name")}\n")
            foodPrice=foodPrice.plus("Rs. ${foodItem.getJSONObject(i).getString("cost")}\n")

        }

        holder.txtOrderHistoryFoodName.text=foodName
        holder.txtOrderHistoryFoodPrice.text=foodPrice
    }
}