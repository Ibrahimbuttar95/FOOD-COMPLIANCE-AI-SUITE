package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_products")
data class FoodProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String = "General Food",
    val servingSize: String = "1 Portion (100g)",
    val servingsPerContainer: Double = 1.0,
    val calories: Int = 200,
    val totalFatG: Double = 8.0,
    val saturatedFatG: Double = 2.0,
    val transFatG: Double = 0.0,
    val cholesterolMg: Double = 0.0,
    val sodiumMg: Double = 150.0,
    val totalCarbG: Double = 28.0,
    val dietaryFiberG: Double = 3.0,
    val totalSugarsG: Double = 12.0,
    val addedSugarsG: Double = 6.0,
    val proteinG: Double = 5.0,
    val vitaminDMcg: Double = 2.0,
    val calciumMg: Double = 130.0,
    val ironMg: Double = 1.8,
    val potassiumMg: Double = 250.0,
    val ingredientsList: String = "",
    val allergensPresent: String = "",
    val allergenCrossContact: String = "",
    val fopClaims: String = "",
    val netWeight: String = "100g",
    val distributorInfo: String = "Quality Foods LLC, Chicago IL 60601",
    val batchCostTotal: Double = 15.00,
    val batchServingsYield: Int = 10,
    val costPerServing: Double = 1.50,
    val suggestedRetailPrice: Double = 4.99,
    val estimatedShelfLifeDays: Int = 180,
    val storageCondition: String = "Ambient dry storage below 25°C",
    val complianceHealthScore: Int = 95,
    val timestamp: Long = System.currentTimeMillis()
)

data class RecipeIngredientInput(
    val name: String,
    val weightGrams: Double,
    val costPerKg: Double = 0.0,
    val isAllergen: Boolean = false,
    val allergenCategory: String = ""
)

data class AllergenAnalysisResult(
    val detectedAllergens: List<String>,
    val crossContactWarnings: List<String>,
    val fdaComplianceStatus: String,
    val euComplianceStatus: String,
    val recommendedAdvisoryText: String,
    val riskLevel: String // LOW, MEDIUM, HIGH
)

data class NutritionAnalysisResult(
    val servingSize: String,
    val servingsPerContainer: Double,
    val calories: Int,
    val totalFatG: Double,
    val saturatedFatG: Double,
    val transFatG: Double,
    val cholesterolMg: Double,
    val sodiumMg: Double,
    val totalCarbG: Double,
    val dietaryFiberG: Double,
    val totalSugarsG: Double,
    val addedSugarsG: Double,
    val proteinG: Double,
    val vitaminDMcg: Double,
    val calciumMg: Double,
    val ironMg: Double,
    val potassiumMg: Double,
    val summaryNotes: String
)

data class IngredientDeclarationResult(
    val formattedDeclaration: String,
    val highlightedAllergens: List<String>,
    val quidPercentages: Map<String, String>,
    val regulatoryNotes: String
)

data class ShelfLifeAnalysisResult(
    val estimatedDays: Int,
    val optimalStorageTemp: String,
    val packagingRecommendation: String,
    val spoilageRiskFactors: List<String>,
    val preservationTips: List<String>
)

data class LabelComplianceCheckResult(
    val overallStatus: String, // COMPLIANT, ACTION_REQUIRED, NON_COMPLIANT
    val healthScore: Int,
    val fdaChecklist: List<String>,
    val euChecklist: List<String>,
    val claimValidations: List<String>,
    val recommendedFixes: List<String>
)
