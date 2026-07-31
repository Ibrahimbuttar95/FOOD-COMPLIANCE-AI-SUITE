package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FoodProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodProductDao {
    @Query("SELECT * FROM food_products ORDER BY timestamp DESC")
    fun getAllProducts(): Flow<List<FoodProduct>>

    @Query("SELECT * FROM food_products WHERE id = :id")
    suspend fun getProductById(id: Int): FoodProduct?

    @Query("SELECT * FROM food_products WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchProducts(query: String): Flow<List<FoodProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: FoodProduct): Long

    @Update
    suspend fun updateProduct(product: FoodProduct)

    @Query("DELETE FROM food_products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("DELETE FROM food_products")
    suspend fun deleteAllProducts()
}
