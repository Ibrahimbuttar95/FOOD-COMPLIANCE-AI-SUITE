package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationPreferenceRecommendation
import com.example.data.model.RecipeIngredientDetail
import com.example.data.model.RecipeItem
import com.example.ui.FoodComplianceViewModel
import com.example.ui.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeListScreen(viewModel: FoodComplianceViewModel) {
    val recipes by viewModel.savedRecipes.collectAsState()
    val searchQuery by viewModel.recipeSearchQuery.collectAsState()
    val isGenerating by viewModel.isGeneratingLocationRecipes.collectAsState()
    val aiRecommendations by viewModel.locationRecipeRecommendations.collectAsState()

    var showAiDiscoveryDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<RecipeItem?>(null) }
    var selectedFilterChip by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Gluten-Free", "Vegan", "Keto", "Mediterranean", "East Asian", "Latin American")

    val filteredRecipes = remember(recipes, searchQuery, selectedFilterChip) {
        recipes.filter { recipe ->
            val matchesSearch = searchQuery.isBlank() ||
                    recipe.name.contains(searchQuery, ignoreCase = true) ||
                    recipe.location.contains(searchQuery, ignoreCase = true) ||
                    recipe.regionCategory.contains(searchQuery, ignoreCase = true) ||
                    recipe.dietaryPreference.contains(searchQuery, ignoreCase = true)

            val matchesChip = when (selectedFilterChip) {
                "All" -> true
                else -> recipe.dietaryPreference.equals(selectedFilterChip, ignoreCase = true) ||
                        recipe.regionCategory.equals(selectedFilterChip, ignoreCase = true)
            }
            matchesSearch && matchesChip
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recipe_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RestaurantMenu,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Recipe Management Suite",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Firestore Sync",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Manage ingredients, preparation steps, dietary profiles, and Cloud Firestore sync. Generate location-curated regional recipes powered by Gemini AI.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.selectRecipeForEdit(null)
                                        viewModel.navigateTo(ScreenRoute.RecipeForm)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("add_new_recipe_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add New Recipe", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { showAiDiscoveryDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("ai_location_discovery_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Recommendations", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar & Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateRecipeSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipe_search_input"),
                        placeholder = { Text("Search recipes by name, location, or dietary tag...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filterOptions) { chip ->
                            FilterChip(
                                selected = selectedFilterChip == chip,
                                onClick = { selectedFilterChip = chip },
                                label = { Text(chip, fontSize = 12.sp) },
                                leadingIcon = if (selectedFilterChip == chip) {
                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                shape = CircleShape
                            )
                        }
                    }
                }
            }

            // Recipe List Count Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Recipes (${filteredRecipes.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cloud Firestore + Room",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Empty State
            if (filteredRecipes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Recipes Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No recipes matched '$searchQuery'. Try clearing search." else "Tap 'Add New Recipe' or 'AI Recommendations' to build your recipe library.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(filteredRecipes, key = { it.id }) { recipe ->
                    RecipeCardItem(
                        recipe = recipe,
                        onEdit = {
                            viewModel.selectRecipeForEdit(recipe)
                            viewModel.navigateTo(ScreenRoute.RecipeForm)
                        },
                        onDelete = { recipeToDelete = recipe }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                viewModel.selectRecipeForEdit(null)
                viewModel.navigateTo(ScreenRoute.RecipeForm)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("recipe_fab_add"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Recipe")
        }
    }

    // AI Location & Preference Recommendation Dialog
    if (showAiDiscoveryDialog) {
        LocationRecipeDiscoveryDialog(
            viewModel = viewModel,
            isGenerating = isGenerating,
            recommendations = aiRecommendations,
            onDismiss = { showAiDiscoveryDialog = false }
        )
    }

    // Delete Confirmation Dialog
    recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("Delete Recipe?") },
            text = { Text("Are you sure you want to delete '${recipe.name}' from Cloud Firestore and local storage? This operation cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecipe(recipe)
                        recipeToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCardItem(
    recipe: RecipeItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val ingredients = remember(recipe.ingredientsJson) { recipe.parseIngredients() }
    val prepSteps = remember(recipe.prepStepsJson) { recipe.parsePrepSteps() }
    val allergenCount = ingredients.count { it.isAllergen }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recipe_card_${recipe.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = recipe.location,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = recipe.regionCategory,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (recipe.description.isNotBlank()) {
                Text(
                    text = recipe.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tags & Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dietary Tag
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = recipe.dietaryPreference,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Servings & Time
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes}m (${recipe.servings} Servings)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Calories
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocalDining, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.caloriesPerServing} kcal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Allergen Badge
                if (allergenCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFEBEE)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$allergenCount Allergen(s)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable details toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isExpanded) "Hide Details" else "View Ingredients (${ingredients.size}) & Steps (${prepSteps.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    // Ingredients List
                    Text(
                        text = "Ingredients (${ingredients.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ingredients.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "${item.quantity} ${item.unit} ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (item.isAllergen) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFFFCDD2),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "ALLERGEN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB71C1C),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = " (${item.notes})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preparation Steps List
                    Text(
                        text = "Preparation Steps (${prepSteps.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    prepSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationRecipeDiscoveryDialog(
    viewModel: FoodComplianceViewModel,
    isGenerating: Boolean,
    recommendations: List<LocationPreferenceRecommendation>,
    onDismiss: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf("Tokyo, Japan") }
    var selectedDiet by remember { mutableStateOf("Gluten-Free") }
    var selectedRegion by remember { mutableStateOf("East Asian") }

    val popularLocations = listOf(
        "Tokyo, Japan", "Naples, Italy", "Oaxaca, Mexico", "Mumbai, India",
        "Paris, France", "San Francisco, USA", "Bangkok, Thailand", "Athens, Greece"
    )
    val dietaryOptions = listOf("Gluten-Free", "Vegan", "Keto", "Halal", "Low Sodium", "High Protein")
    val regionOptions = listOf("East Asian", "Mediterranean", "Latin American", "South Asian", "European", "North American")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Location & Preference Recipes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Specify target location and personal dietary preferences to generate authentic regional recipes with Gemini AI.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Location Picker
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = { selectedLocation = it },
                    label = { Text("Target Location / City") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Dietary Preference Selector
                Column {
                    Text("Dietary Preference:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(dietaryOptions) { diet ->
                            FilterChip(
                                selected = selectedDiet == diet,
                                onClick = { selectedDiet = diet },
                                label = { Text(diet, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Cuisine Region Selector
                Column {
                    Text("Cuisine Region:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(regionOptions) { region ->
                            FilterChip(
                                selected = selectedRegion == region,
                                onClick = { selectedRegion = region },
                                label = { Text(region, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.discoverLocationBasedRecipes(
                            location = selectedLocation,
                            preference = selectedDiet,
                            region = selectedRegion
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_location_recipes_btn"),
                    enabled = !isGenerating,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating Regional Recipes...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Discover Recipes with AI")
                    }
                }

                // Display Recommendations
                if (recommendations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AI Recommended Regional Recipes:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    recommendations.forEach { rec ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(rec.recipeTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(rec.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• ${rec.ingredients.size} Ingredients | ${rec.preparationSteps.size} Steps | ${rec.caloriesPerServing} kcal", fontSize = 11.sp, fontWeight = FontWeight.Medium)

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val recipeItem = RecipeItem(
                                            name = rec.recipeTitle,
                                            description = rec.description,
                                            location = rec.locationName,
                                            regionCategory = rec.region,
                                            dietaryPreference = rec.dietaryPreference,
                                            ingredientsJson = RecipeItem.encodeIngredients(rec.ingredients),
                                            prepStepsJson = RecipeItem.encodePrepSteps(rec.preparationSteps),
                                            prepTimeMinutes = rec.prepTimeMinutes,
                                            cookTimeMinutes = rec.cookTimeMinutes,
                                            caloriesPerServing = rec.caloriesPerServing,
                                            isSyncedToFirestore = true
                                        )
                                        viewModel.saveRecipe(recipeItem)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save to Firestore & Local DB", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
