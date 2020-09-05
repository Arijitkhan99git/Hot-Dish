package Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishEntity(
    @PrimaryKey val dish_id:Int,
    @ColumnInfo(name = "dish_name") val dishName: String,
    @ColumnInfo(name = "dish_rating") val dishRating:String,
    @ColumnInfo(name = "dish_price") val dishPrice:String,
    @ColumnInfo(name = "dish_image") val dishImage:String
)