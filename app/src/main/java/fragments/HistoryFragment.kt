package fragments

import com.internshala.foodrunner.adapter.OrderHistoryRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import util.ConnectionManager
import java.lang.Exception


class HistoryFragment : Fragment() {
    lateinit var recyclerHistory:RecyclerView
    lateinit var recyclerAdapter:OrderHistoryRecyclerAdapter
    lateinit var layoutManager:RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_history, container, false)

        recyclerHistory=view.findViewById(R.id.recyclerHistory)
        layoutManager=LinearLayoutManager(activity as Context)

        if (ConnectionManager().checkConnetivity(activity as Context)){
            val userId=(activity as Context).
            getSharedPreferences(getString(R.string.preference_file),
                Context.MODE_PRIVATE).getString("user_id","999")

            val queue=Volley.newRequestQueue(activity as Context)
            val url="http://13.235.250.119/v2/orders/fetch_result/$userId"

            val jsonObjectRequest=
                object :JsonObjectRequest(Method.GET,url,null,
                    Response.Listener {
                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")

                        if (success)
                        {
                            val orderHistory=data.getJSONArray("data")

                            recyclerAdapter=OrderHistoryRecyclerAdapter(activity as Context,orderHistory)

                            recyclerHistory.adapter=recyclerAdapter
                            recyclerHistory.layoutManager=layoutManager
                        }
                        else
                        {
                            Toast.makeText(activity as Context,"No Previous Order found",Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception){
                        Toast.makeText(activity as Context,"Could not fetch your previous orders",Toast.LENGTH_SHORT).show()
                    }
                },
                    Response.ErrorListener {
                        Toast.makeText(activity as Context,"Some Error occurred",Toast.LENGTH_SHORT).show()
                    })
                {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"] = "4caf407c2bc15a"

                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }
        else
        {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){
                text,listener->
                val settingsIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){
                text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }

}
