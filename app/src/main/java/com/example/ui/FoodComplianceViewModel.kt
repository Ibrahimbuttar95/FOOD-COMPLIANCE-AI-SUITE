package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FoodDatabase
import com.example.data.model.AllergenAnalysisResult
import com.example.data.model.FoodProduct
import com.example.data.model.IngredientDeclarationResult
import com.example.data.model.LabelComplianceCheckResult
import com.example.data.model.NutritionAnalysisResult
import com.example.data.model.RecipeIngredientInput
import com.example.data.model.ShelfLifeAnalysisResult
import com.example.data.repository.FoodComplianceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class ScreenRoute(val route: String, val title: String) {
    object Dashboard : ScreenRoute("dashboard", "Dashboard")
    object Recipes : ScreenRoute("recipes", "Recipe Book & AI Discovery")
    object RecipeForm : ScreenRoute("recipe_form", "Recipe Editor")
    object NutritionFacts : ScreenRoute("nutrition", "Nutrition Facts")
    object Ingredients : ScreenRoute("ingredients", "Ingredient Declaration")
    object Allergens : ScreenRoute("allergens", "Allergen Checker")
    object ProductLabel : ScreenRoute("label", "Product Label Generator")
    object RecipeCost : ScreenRoute("costing", "Recipe Cost Calculator")
    object ShelfLife : ScreenRoute("shelflife", "Shelf-Life Calculator")
    object AiAssistant : ScreenRoute("assistant", "AI Compliance Consultant")
    object SavedProducts : ScreenRoute("saved", "Saved Compliant Products")
    object Auth : ScreenRoute("auth", "Account Sign-In")
}

data class ChatMessage(val sender: String, val message: String, val timestamp: Long = System.currentTimeMillis())

class FoodComplianceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FoodComplianceRepository

    init {
        val db = FoodDatabase.getDatabase(application)
        val productDao = db.foodProductDao()
        val recipeDao = db.recipeDao()
        repository = FoodComplianceRepository(dao = productDao, recipeDao = recipeDao)
        loadSavedProducts()
        loadSavedRecipes()
    }

    // Navigation & Theme State
    private val _currentRoute = MutableStateFlow<ScreenRoute>(ScreenRoute.Dashboard)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun navigateTo(route: ScreenRoute) {
        _currentRoute.value = route
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Auth State Simulation (Email & Google Sign-In)
    private val _userEmail = MutableStateFlow("qa.officer@foodcompliance.ai")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userDisplayName = MutableStateFlow("Compliance Specialist")
    val userDisplayName: StateFlow<String> = _userDisplayName.asStateFlow()

    fun signInWithGoogle(email: String = "food.engineer@google.com", name: String = "Lead Food Auditor") {
        _userEmail.value = email
        _userDisplayName.value = name
    }

    // Room Database State
    private val _savedProducts = MutableStateFlow<List<FoodProduct>>(emptyList())
    val savedProducts: StateFlow<List<FoodProduct>> = _savedProducts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                repository.allProducts.collectLatest { _savedProducts.value = it }
            } else {
                repository.searchProducts(query).collectLatest { _savedProducts.value = it }
            }
        }
    }

    private fun loadSavedProducts() {
        viewModelScope.launch {
            repository.allProducts.collectLatest { products ->
                if (products.isEmpty()) {
                    // Seed initial demo food product for standard compliance testing
                    val sample = FoodProduct(
                        name = "Organic Artisan Granola Bar",
                        category = "Bakery & Snack",
                        servingSize = "1 Bar (45g)",
                        servingsPerContainer = 8.0,
                        calories = 190,
                        totalFatG = 7.0,
                        saturatedFatG = 1.5,
                        transFatG = 0.0,
                        cholesterolMg = 0.0,
                        sodiumMg = 95.0,
                        totalCarbG = 26.0,
                        dietaryFiberG = 4.0,
                        totalSugarsG = 9.0,
                        addedSugarsG = 5.0,
                        proteinG = 6.0,
                        vitaminDMcg = 0.0,
                        calciumMg = 45.0,
                        ironMg = 1.6,
                        potassiumMg = 180.0,
                        ingredientsList = "Rolled Oats, Organic Honey, Almonds (12%), Dried Cranberries, Organic Coconut Oil, Sea Salt, Natural Vanilla Extract.",
                        allergensPresent = "Tree Nuts (Almonds)",
                        allergenCrossContact = "May contain peanuts, soy, and milk.",
                        fopClaims = "Low Sodium, Excellent Source of Fiber, Non-GMO",
                        netWeight = "360g (12.7 oz)",
                        distributorInfo = "Artisan Foods Corp, Portland OR 97201",
                        batchCostTotal = 12.40,
                        batchServingsYield = 8,
                        costPerServing = 1.55,
                        suggestedRetailPrice = 4.49,
                        estimatedShelfLifeDays = 210,
                        complianceHealthScore = 98
                    )
                    repository.saveProduct(sample)
                } else {
                    _savedProducts.value = products
                }
            }
        }
    }

    // --- RECIPE MANAGEMENT STATE (Cloud Firestore & Room DB) ---
    private val _savedRecipes = MutableStateFlow<List<com.example.data.model.RecipeItem>>(emptyList())
    val savedRecipes: StateFlow<List<com.example.data.model.RecipeItem>> = _savedRecipes.asStateFlow()

    private val _selectedRecipeForEdit = MutableStateFlow<com.example.data.model.RecipeItem?>(null)
    val selectedRecipeForEdit: StateFlow<com.example.data.model.RecipeItem?> = _selectedRecipeForEdit.asStateFlow()

    private val _recipeSearchQuery = MutableStateFlow("")
    val recipeSearchQuery: StateFlow<String> = _recipeSearchQuery.asStateFlow()

    private val _isGeneratingLocationRecipes = MutableStateFlow(false)
    val isGeneratingLocationRecipes: StateFlow<Boolean> = _isGeneratingLocationRecipes.asStateFlow()

    private val _locationRecipeRecommendations = MutableStateFlow<List<com.example.data.model.LocationPreferenceRecommendation>>(emptyList())
    val locationRecipeRecommendations: StateFlow<List<com.example.data.model.LocationPreferenceRecommendation>> = _locationRecipeRecommendations.asStateFlow()

    fun selectRecipeForEdit(recipe: com.example.data.model.RecipeItem?) {
        _selectedRecipeForEdit.value = recipe
    }

    fun updateRecipeSearchQuery(query: String) {
        _recipeSearchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                repository.allRecipes.collectLatest { _savedRecipes.value = it }
            } else {
                repository.searchRecipes(query).collectLatest { _savedRecipes.value = it }
            }
        }
    }

    fun saveRecipe(recipe: com.example.data.model.RecipeItem) {
        viewModelScope.launch {
            repository.saveRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: com.example.data.model.RecipeItem) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun discoverLocationBasedRecipes(location: String, preference: String, region: String) {
        viewModelScope.launch {
            _isGeneratingLocationRecipes.value = true
            val results = repository.generateLocationBasedRecipes(location, preference, region)
            _locationRecipeRecommendations.value = results
            _isGeneratingLocationRecipes.value = false
        }
    }

    private fun loadSavedRecipes() {
        viewModelScope.launch {
            repository.allRecipes.collectLatest { recipes ->
                if (recipes.isEmpty()) {
                    val sampleRecipe = com.example.data.model.RecipeItem(
                        name = "Neapolitan Wood-Fired Gluten-Free Margherita",
                        description = "Authentic Italian heritage pizza recipe crafted in Naples with gluten-free sourdough and San Marzano tomatoes.",
                        location = "Naples, Italy",
                        regionCategory = "Mediterranean",
                        dietaryPreference = "Gluten-Free",
                        ingredientsJson = com.example.data.model.RecipeItem.encodeIngredients(
                            listOf(
                                com.example.data.model.RecipeIngredientDetail("Gluten-Free Flour Blend (Rice, Potato, Tapioca)", 250.0, "g", false, "High hydration dough"),
                                com.example.data.model.RecipeIngredientDetail("San Marzano DOP Tomatoes", 120.0, "g", false, "Crushed by hand"),
                                com.example.data.model.RecipeIngredientDetail("Fresh Mozzarella di Bufala", 100.0, "g", true, "Contains Dairy"),
                                com.example.data.model.RecipeIngredientDetail("Extra Virgin Olive Oil", 15.0, "ml", false, "Cold pressed"),
                                com.example.data.model.RecipeIngredientDetail("Fresh Basil Leaves", 10.0, "g", false, "Organic local harvest")
                            )
                        ),
                        prepStepsJson = com.example.data.model.RecipeItem.encodePrepSteps(
                            listOf(
                                "Combine gluten-free flour blend, warm water (65%), active yeast, and sea salt. Knead into smooth elastic dough.",
                                "Proof dough at ambient room temperature (22°C) for 12 hours for fermentation.",
                                "Stretch dough gently on cornstarch surface to 12-inch round with raised cornicione crust border.",
                                "Ladle crushed San Marzano tomatoes, arrange buffalo mozzarella slices, and drizzle extra virgin olive oil.",
                                "Bake in wood-fired oven at 450°C for 90 seconds (or home oven at 250°C on pizza stone for 7 minutes)."
                            )
                        ),
                        servings = 2,
                        prepTimeMinutes = 20,
                        cookTimeMinutes = 8,
                        caloriesPerServing = 410,
                        isSyncedToFirestore = true
                    )
                    repository.saveRecipe(sampleRecipe)
                } else {
                    _savedRecipes.value = recipes
                }
            }
        }
    }

    // Active Product state for details / preview
    private val _selectedProduct = MutableStateFlow(FoodProduct(name = "New Snack Bar"))
    val selectedProduct: StateFlow<FoodProduct> = _selectedProduct.asStateFlow()

    fun updateSelectedProduct(product: FoodProduct) {
        _selectedProduct.value = product
    }

    fun saveCurrentProduct(product: FoodProduct) {
        viewModelScope.launch {
            repository.saveProduct(product)
            _selectedProduct.value = product
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    // Module 1: Nutrition Facts State
    private val _nutritionResult = MutableStateFlow<NutritionAnalysisResult?>(null)
    val nutritionResult: StateFlow<NutritionAnalysisResult?> = _nutritionResult.asStateFlow()
    private val _isAnalyzingNutrition = MutableStateFlow(false)
    val isAnalyzingNutrition: StateFlow<Boolean> = _isAnalyzingNutrition.asStateFlow()

    fun analyzeNutrition(recipeText: String, servingSize: String) {
        viewModelScope.launch {
            _isAnalyzingNutrition.value = true
            val result = repository.generateNutritionFacts(recipeText, servingSize)
            _nutritionResult.value = result
            _isAnalyzingNutrition.value = false
        }
    }

    // Module 2: Ingredient Declaration & QUID State
    private val _ingredientResult = MutableStateFlow<IngredientDeclarationResult?>(null)
    val ingredientResult: StateFlow<IngredientDeclarationResult?> = _ingredientResult.asStateFlow()
    private val _isGeneratingIngredients = MutableStateFlow(false)
    val isGeneratingIngredients: StateFlow<Boolean> = _isGeneratingIngredients.asStateFlow()

    fun generateIngredientList(ingredients: List<RecipeIngredientInput>) {
        viewModelScope.launch {
            _isGeneratingIngredients.value = true
            val result = repository.generateIngredientDeclaration(ingredients)
            _ingredientResult.value = result
            _isGeneratingIngredients.value = false
        }
    }

    // Module 3: Allergen Checker State
    private val _allergenResult = MutableStateFlow<AllergenAnalysisResult?>(null)
    val allergenResult: StateFlow<AllergenAnalysisResult?> = _allergenResult.asStateFlow()
    private val _isCheckingAllergens = MutableStateFlow(false)
    val isCheckingAllergens: StateFlow<Boolean> = _isCheckingAllergens.asStateFlow()

    fun checkAllergens(ingredientsText: String, facilityCrossContact: String) {
        viewModelScope.launch {
            _isCheckingAllergens.value = true
            val result = repository.checkAllergens(ingredientsText, facilityCrossContact)
            _allergenResult.value = result
            _isCheckingAllergens.value = false
        }
    }

    // Module 4: Recipe Cost Calculator State
    private val _costResult = MutableStateFlow<Map<String, Any>?>(null)
    val costResult: StateFlow<Map<String, Any>?> = _costResult.asStateFlow()

    fun calculateCost(ingredients: List<RecipeIngredientInput>, yieldServings: Int, marginPct: Double) {
        val result = repository.calculateRecipeCost(ingredients, yieldServings, marginPct)
        _costResult.value = result
    }

    // Module 5: Shelf-Life Calculator State
    private val _shelfLifeResult = MutableStateFlow<ShelfLifeAnalysisResult?>(null)
    val shelfLifeResult: StateFlow<ShelfLifeAnalysisResult?> = _shelfLifeResult.asStateFlow()
    private val _isCalculatingShelfLife = MutableStateFlow(false)
    val isCalculatingShelfLife: StateFlow<Boolean> = _isCalculatingShelfLife.asStateFlow()

    fun calculateShelfLife(
        waterActivityAw: Double,
        pHLevel: Double,
        moisturePct: Double,
        packagingType: String,
        storageTempC: Double,
        preservativesAdded: Boolean
    ) {
        viewModelScope.launch {
            _isCalculatingShelfLife.value = true
            val result = repository.calculateShelfLife(
                waterActivityAw, pHLevel, moisturePct, packagingType, storageTempC, preservativesAdded
            )
            _shelfLifeResult.value = result
            _isCalculatingShelfLife.value = false
        }
    }

    // Module 6: Label Audit Engine State
    private val _auditResult = MutableStateFlow<LabelComplianceCheckResult?>(null)
    val auditResult: StateFlow<LabelComplianceCheckResult?> = _auditResult.asStateFlow()

    fun auditProduct(product: FoodProduct) {
        viewModelScope.launch {
            val result = repository.auditProductLabel(product)
            _auditResult.value = result
        }
    }

    // Module 7: AI Assistant State
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI Compliance Officer", "Hello! I am your AI Food Regulatory Consultant. Ask me anything about FDA CFR 101, EU 1169/2011, allergen guidelines, health claims, or food safety protocols.")
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    fun sendAiMessage(prompt: String, useThinking: Boolean = false) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage("You", prompt)
        _chatHistory.value = _chatHistory.value + userMsg
        viewModelScope.launch {
            _isAiThinking.value = true
            val response = repository.askComplianceAssistant(prompt, useThinking)
            val aiMsg = ChatMessage("AI Compliance Officer", response)
            _chatHistory.value = _chatHistory.value + aiMsg
            _isAiThinking.value = false
        }
    }
}
