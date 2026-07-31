package com.example.data.remote

import android.util.Log
import com.example.data.model.RecipeItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecipeFirestoreRepository {

    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreRepository", "Firebase Firestore instance unavailable: ${e.localizedMessage}")
            null
        }
    }

    suspend fun saveRecipeToFirestore(recipe: RecipeItem): String {
        val firestore = db ?: return recipe.firestoreId.ifBlank { UUID.randomUUID().toString() }
        return try {
            val docId = if (recipe.firestoreId.isNotBlank()) recipe.firestoreId else UUID.randomUUID().toString()
            val dataMap = mapOf(
                "id" to recipe.id,
                "firestoreId" to docId,
                "name" to recipe.name,
                "description" to recipe.description,
                "location" to recipe.location,
                "regionCategory" to recipe.regionCategory,
                "dietaryPreference" to recipe.dietaryPreference,
                "ingredientsJson" to recipe.ingredientsJson,
                "prepStepsJson" to recipe.prepStepsJson,
                "servings" to recipe.servings,
                "prepTimeMinutes" to recipe.prepTimeMinutes,
                "cookTimeMinutes" to recipe.cookTimeMinutes,
                "caloriesPerServing" to recipe.caloriesPerServing,
                "timestamp" to recipe.timestamp
            )
            firestore.collection("recipes")
                .document(docId)
                .set(dataMap)
                .await()
            docId
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving recipe to Cloud Firestore: ${e.localizedMessage}")
            recipe.firestoreId.ifBlank { UUID.randomUUID().toString() }
        }
    }

    suspend fun deleteRecipeFromFirestore(firestoreId: String) {
        if (firestoreId.isBlank()) return
        val firestore = db ?: return
        try {
            firestore.collection("recipes")
                .document(firestoreId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error deleting recipe from Cloud Firestore: ${e.localizedMessage}")
        }
    }
}
