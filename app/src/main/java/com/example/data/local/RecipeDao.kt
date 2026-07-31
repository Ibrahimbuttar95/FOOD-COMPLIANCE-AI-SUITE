package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RecipeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY timestamp DESC")
    fun getAllRecipes(): Flow<List<RecipeItem>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeItem?

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR regionCategory LIKE '%' || :query || '%' OR dietaryPreference LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchRecipes(query: String): Flow<List<RecipeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeItem): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeItem)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()
}
