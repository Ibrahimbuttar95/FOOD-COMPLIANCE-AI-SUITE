package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "recipes")
data class RecipeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firestoreId: String = "",
    val name: String,
    val description: String = "",
    val location: String = "Global",
    val regionCategory: String = "Global",
    val dietaryPreference: String = "Standard",
    val ingredientsJson: String = "[]",
    val prepStepsJson: String = "[]",
    val servings: Int = 4,
    val prepTimeMinutes: Int = 15,
    val cookTimeMinutes: Int = 20,
    val caloriesPerServing: Int = 350,
    val isSyncedToFirestore: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun parseIngredients(): List<RecipeIngredientDetail> {
        return try {
            val list = mutableListOf<RecipeIngredientDetail>()
            val array = JSONArray(ingredientsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RecipeIngredientDetail(
                        name = obj.optString("name", ""),
                        quantity = obj.optDouble("quantity", 0.0),
                        unit = obj.optString("unit", "g"),
                        isAllergen = obj.optBoolean("isAllergen", false),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parsePrepSteps(): List<String> {
        return try {
            val list = mutableListOf<String>()
            val array = JSONArray(prepStepsJson)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        fun encodeIngredients(ingredients: List<RecipeIngredientDetail>): String {
            val array = JSONArray()
            ingredients.forEach { item ->
                val obj = JSONObject()
                obj.put("name", item.name)
                obj.put("quantity", item.quantity)
                obj.put("unit", item.unit)
                obj.put("isAllergen", item.isAllergen)
                obj.put("notes", item.notes)
                array.put(obj)
            }
            return array.toString()
        }

        fun encodePrepSteps(steps: List<String>): String {
            val array = JSONArray()
            steps.forEach { step ->
                if (step.isNotBlank()) {
                    array.put(step.trim())
                }
            }
            return array.toString()
        }
    }
}

data class RecipeIngredientDetail(
    val name: String,
    val quantity: Double,
    val unit: String = "g",
    val isAllergen: Boolean = false,
    val notes: String = ""
)

data class LocationPreferenceRecommendation(
    val recipeTitle: String,
    val description: String,
    val locationName: String,
    val region: String,
    val dietaryPreference: String,
    val ingredients: List<RecipeIngredientDetail>,
    val preparationSteps: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val caloriesPerServing: Int,
    val regionalNotes: String = ""
)
