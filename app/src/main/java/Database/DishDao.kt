package Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DishDao {
    @Insert
    fun dishInsert(dishEntity: DishEntity)

    @Delete
    fun dishDelete(dishEntity: DishEntity)

    @Query("SELECT * FROM dishes")
    fun getAllDishes():List<DishEntity>?

    @Query("SELECT * FROM dishes WHERE dish_id=:dishId ")
    fun getById(dishId:String):DishEntity?
}