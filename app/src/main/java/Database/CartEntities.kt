package Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntities (
    @PrimaryKey val cart_food_id:Int,
    @ColumnInfo(name="food_name") val foodName:String,
    @ColumnInfo(name="food_price") val foodPrice:String,
   @ColumnInfo(name="restaurant_id") val restaurantId:String
)