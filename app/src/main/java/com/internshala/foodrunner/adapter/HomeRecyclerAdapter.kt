package com.internshala.foodrunner.adapter

import Database.DishDatabase
import Database.DishEntity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.Activity.DescriptionActivity
import com.internshala.foodrunner.R
import com.squareup.picasso.Picasso
import model.Dish
import java.util.*
import kotlin.collections.ArrayList

class HomeRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Dish>
) :RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(),Filterable{

    var filteredListFull=ArrayList<Dish>(itemList)

    class HomeViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtDishName:TextView=view.findViewById(R.id.txtDishName)
        val txtDishPrice:TextView=view.findViewById(R.id.txtDishPrice)
        val txtDishRating:TextView=view.findViewById(R.id.txtDishRating)
        val imgDishImage:ImageView=view.findViewById(R.id.imgDishImage)
        val addFav:TextView=view.findViewById(R.id.addFav)
        val llcontent:LinearLayout=view.findViewById(R.id.llcontent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val dish=itemList[position]

        holder.txtDishName.text=dish.dishName
        holder.txtDishPrice.text="Price: "+dish.dishPrice+"/Dish"
        holder.txtDishRating.text=dish.dishRating
        Picasso.get().load(dish.dishImage).error(R.drawable.default_dish_cover).into(holder.imgDishImage)

        holder.llcontent.setOnClickListener {
            val intent=Intent(context,
                DescriptionActivity::class.java)
            intent.putExtra("dishId",dish.dishId)
            intent.putExtra("dishName",dish.dishName)
            context.startActivity(intent)
        }

        //create a DishEntity
        val dishEntity=DishEntity(dish.dishId.toInt(),dish.dishName,dish.dishRating,dish.dishPrice,dish.dishImage)

        val checkfav=DbAsyncTask(context,dishEntity,1).execute()
        val isFav=checkfav.get()
        if (isFav)
        {
            holder.addFav.setBackgroundResource(R.drawable.ic_action_addfav)
        }
        else
        {
            holder.addFav.setBackgroundResource(R.drawable.ic_action_favourite)
        }

        holder.addFav.setOnClickListener {
            val async=DbAsyncTask(context,dishEntity,1).execute()
            val result=async.get()
            if (!result)
            {
                Toast.makeText(context,"Added to favourite",Toast.LENGTH_SHORT).show()
                DbAsyncTask(context,dishEntity,2).execute().get()

                holder.addFav.setBackgroundResource(R.drawable.ic_action_addfav)
            }
            else
            {
                Toast.makeText(context,"Remove from favourite",Toast.LENGTH_SHORT).show()
                DbAsyncTask(context,dishEntity,3).execute().get()
                holder.addFav.setBackgroundResource(R.drawable.ic_action_favourite)
            }

        }


    }

    class DbAsyncTask(val context: Context,val dishEntity: DishEntity,val mode:Int):AsyncTask<Void,Void,Boolean>()
    {
        override fun doInBackground(vararg p0: Void?): Boolean {
            val db=Room.databaseBuilder(context,DishDatabase::class.java,"dish-db").build()
            when(mode)
            {
                1-> {
                    //check the db to dish added/not
                        val isFav: DishEntity? =db.dishDao().getById(dishEntity.dish_id.toString())
                        db.close()
                    return isFav!=null
                }

                2-> {
                    //added to the fav
                   db.dishDao().dishInsert(dishEntity)
                    db.close()

                    return true
                }

                3->{
                    //remove from fav
                    db.dishDao().dishDelete(dishEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    override fun getFilter(): Filter {
      return newFilteredList
    }

    var newFilteredList=object :Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val tempFilteredList=ArrayList<Dish>()
            if (constraint==null || constraint.length==0)
            {
                tempFilteredList.addAll(filteredListFull)
            }
            else
            {
                val pattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (item in filteredListFull)
                {
                    if (item.dishName.contains(pattern,true))
                    {
                        tempFilteredList.add(item)
                    }
                }
            }
            val filterResults=FilterResults()
            filterResults.values=tempFilteredList

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemList.clear()
            itemList.addAll(results?.values as ArrayList<Dish>)
            notifyDataSetChanged()
        }

    }
}