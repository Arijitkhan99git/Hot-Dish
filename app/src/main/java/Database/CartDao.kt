package Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    fun cartInsert(cartEntities: CartEntities)

    @Delete
    fun cartDelete(cartEntities: CartEntities)

    @Query("SELECT * FROM cart")
    fun getAllCart():List<CartEntities>?

    @Query("SELECT * FROM cart WHERE cart_food_id=:foodId")
    fun getById(foodId:String):CartEntities?

    @Query("DELETE FROM cart")
    fun removeAll()
}