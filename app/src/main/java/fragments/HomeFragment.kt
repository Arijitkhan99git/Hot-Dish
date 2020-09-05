package fragments

import com.internshala.foodrunner.adapter.HomeRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import model.Dish
import org.json.JSONException
import util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment(val searchView:SearchView) : Fragment() {
    lateinit var recyclerHomePage: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var dishInfoList= arrayListOf<Dish>()

    val ratingComparator= Comparator<Dish>{food1,food2 ->
        if(food1.dishRating.compareTo(food2.dishRating,true)==0){
            food1.dishName.compareTo(food2.dishName,true)
        }
        else
        {
            food1.dishRating.compareTo(food2.dishRating,true)
        }

    }

    val pricecomparator= Comparator<Dish>{food1,food2->
        if(food1.dishPrice.compareTo(food2.dishPrice,true)==0){
            food1.dishName.compareTo(food2.dishName,true)
        }
        else
        {
            food1.dishPrice.compareTo(food2.dishPrice,true)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHomePage=view.findViewById(R.id.recyclerHome)
        layoutManager=LinearLayoutManager(activity)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        searchView.visibility=View.VISIBLE

        //show the progeess Layout
       progressLayout.visibility=View.VISIBLE


        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnetivity(activity as Context))
        {
            val jsonObjectRequest=object :JsonObjectRequest(
                Request.Method.GET,url,null,
                Response.Listener {
                    println("Response is $it")
                    val data=it.getJSONObject("data")
                        val success = data.getBoolean("success")


                        if (success) {

                            try {
                                val jsonArray=data.getJSONArray("data")

                                  progressLayout.visibility = View.GONE

                                for (i in 0 until jsonArray.length())
                                {
                                    val dishJsonObject = jsonArray.getJSONObject(i)

                                    val dish = Dish(
                                        dishJsonObject.getString("id"),
                                        dishJsonObject.getString("name"),
                                        dishJsonObject.getString("rating"),
                                        dishJsonObject.getString("cost_for_one"),
                                        dishJsonObject.getString("image_url")
                                    )

                                    dishInfoList.add(dish)
                                }
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, dishInfoList)

                                recyclerHomePage.layoutManager = layoutManager
                                recyclerHomePage.adapter = recyclerAdapter
                            }
                            catch (ex:JSONException)
                            {
                                Toast.makeText(activity,"fetching problem",Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                }, Response.ErrorListener {
                        if (activity!=null) {
                            Toast.makeText(
                                activity as Context,
                                " error occurred,try after some time",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = "4caf407c2bc15a"

                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)

            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, Listner ->
                //do Somethings
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, Listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }

        searchView.setOnQueryTextListener(
            object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                   recyclerAdapter.filter.filter(newText)
                    return false
                }

            }
        )

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId

        when(id)
        {
            R.id.sort_by_rating ->
            {
                Collections.sort(dishInfoList,ratingComparator)
                dishInfoList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }

            R.id.sort_by_high2low ->
            {
                Collections.sort(dishInfoList,pricecomparator)
                dishInfoList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }

            R.id.sort_by_low2high ->
            {
                Collections.sort(dishInfoList,pricecomparator)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
