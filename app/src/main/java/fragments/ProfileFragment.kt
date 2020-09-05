package fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.internshala.foodrunner.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
lateinit var sharedPreferences: SharedPreferences
    lateinit var pName:TextView
    lateinit var pNumber:TextView
    lateinit var pEmail:TextView
    lateinit var pAddress:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences=context !!.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE)

        pName=view.findViewById(R.id.PName)
        pNumber=view.findViewById(R.id.PNumber)
        pEmail=view.findViewById(R.id.PMail)
        pAddress=view.findViewById(R.id.PAddress)

        pName.text= sharedPreferences.getString("name",null)
        pNumber.text=sharedPreferences.getString("mobile",null)
        pEmail.text=sharedPreferences.getString("email",null)
        pAddress.text=sharedPreferences.getString("address",null)

        return view
    }

}
