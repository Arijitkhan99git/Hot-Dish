package fragments

import Database.DishDatabase
import Database.DishEntity
import com.internshala.foodrunner.adapter.FavouriteAdapter
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.R

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {
    lateinit var recyclerFav:RecyclerView
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recylerFavAdapter: FavouriteAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var dbDishList= listOf<DishEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFav=view.findViewById(R.id.recycleFavourite)
        progressLayout=view.findViewById(R.id.favProgressLayout)
        progressBar=view.findViewById(R.id.favProgressBar)
        layoutManager=LinearLayoutManager(activity)

        progressLayout.visibility=View.VISIBLE

        val  favDishesList=FavAsyncTask(activity as Context).execute().get()

        if(activity!=null)
        {
            progressLayout.visibility=View.GONE
            //find the RecyclerAdapter
            recylerFavAdapter= FavouriteAdapter(activity as Context,favDishesList)

            //setting the Recycler View with com.internshala.foodrunner.adapter Class , layoutManager
            recyclerFav.adapter=recylerFavAdapter
            recyclerFav.layoutManager=layoutManager
        }
        else
        {
            Toast.makeText(activity as Context,"Your Favourite List is Empty !!",Toast.LENGTH_LONG).show()
        }

        return view
    }

    class FavAsyncTask(val context: Context):AsyncTask<Void,Void,List<DishEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<DishEntity>? {
            val db=Room.databaseBuilder(context,DishDatabase::class.java,"dish-db").build()

            //return the all ListOfDishes
            return db.dishDao().getAllDishes()
        }
    }

}
