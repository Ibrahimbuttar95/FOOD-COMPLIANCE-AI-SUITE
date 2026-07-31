package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecipeIngredientDetail
import com.example.data.model.RecipeItem
import com.example.ui.FoodComplianceViewModel
import com.example.ui.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditScreen(viewModel: FoodComplianceViewModel) {
    val selectedRecipe by viewModel.selectedRecipeForEdit.collectAsState()

    var recipeName by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.name ?: "") }
    var description by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.description ?: "") }
    var location by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.location ?: "Naples, Italy") }
    var regionCategory by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.regionCategory ?: "Mediterranean") }
    var dietaryPreference by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.dietaryPreference ?: "Gluten-Free") }
    var servingsStr by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.servings?.toString() ?: "4") }
    var prepTimeStr by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.prepTimeMinutes?.toString() ?: "15") }
    var cookTimeStr by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.cookTimeMinutes?.toString() ?: "20") }
    var caloriesStr by remember(selectedRecipe) { mutableStateOf(selectedRecipe?.caloriesPerServing?.toString() ?: "350") }

    val ingredientsList = remember(selectedRecipe) {
        val initial = selectedRecipe?.parseIngredients()
            ?: listOf(
                RecipeIngredientDetail("Gluten-Free Flour", 200.0, "g", false, "High protein"),
                RecipeIngredientDetail("Crushed Tomatoes", 100.0, "g", false, "San Marzano"),
                RecipeIngredientDetail("Mozzarella Cheese", 80.0, "g", true, "Contains Dairy")
            )
        mutableStateListOf<RecipeIngredientDetail>().apply { addAll(initial) }
    }

    val prepStepsList = remember(selectedRecipe) {
        val initial = selectedRecipe?.parsePrepSteps()
            ?: listOf(
                "Prepare fresh ingredients and measure quantities precisely.",
                "Combine ingredients and cook according to traditional temperature standards.",
                "Garnish with herbs and serve warm."
            )
        mutableStateListOf<String>().apply { addAll(initial) }
    }

    var regionDropdownExpanded by remember { mutableStateOf(false) }
    var dietDropdownExpanded by remember { mutableStateOf(false) }

    val regionOptions = listOf("Mediterranean", "East Asian", "Latin American", "South Asian", "Middle Eastern", "European", "North American", "Global")
    val dietOptions = listOf("Standard", "Gluten-Free", "Vegan", "Keto", "Low Sodium", "Halal", "High Protein", "Kosher")
    val unitOptions = listOf("g", "kg", "ml", "L", "tsp", "tbsp", "cup", "pcs", "oz")

    var auditSummary by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation & Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.Recipes) },
                    modifier = Modifier.testTag("recipe_edit_back_btn")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Recipes")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (selectedRecipe == null) "Add New Recipe" else "Edit Recipe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cloud Firestore + Room Storage Sync",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 1: Basic Information Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Basic Recipe Metadata",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = recipeName,
                        onValueChange = { recipeName = it },
                        label = { Text("Recipe Name *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_recipe_name"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Compliance Overview") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_recipe_description"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location Origin") },
                            leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_recipe_location"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Region Dropdown
                        ExposedDropdownMenuBox(
                            expanded = regionDropdownExpanded,
                            onExpandedChange = { regionDropdownExpanded = !regionDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = regionCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cuisine Region") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("dropdown_region"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = regionDropdownExpanded,
                                onDismissRequest = { regionDropdownExpanded = false }
                            ) {
                                regionOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            regionCategory = option
                                            regionDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Dietary Preference Dropdown
                    ExposedDropdownMenuBox(
                        expanded = dietDropdownExpanded,
                        onExpandedChange = { dietDropdownExpanded = !dietDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = dietaryPreference,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Primary Dietary Preference") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("dropdown_diet"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dietDropdownExpanded,
                            onDismissRequest = { dietDropdownExpanded = false }
                        ) {
                            dietOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        dietaryPreference = option
                                        dietDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Numeric Specs: Servings, Prep Time, Cook Time, Calories
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = servingsStr,
                            onValueChange = { servingsStr = it },
                            label = { Text("Servings") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = prepTimeStr,
                            onValueChange = { prepTimeStr = it },
                            label = { Text("Prep (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cookTimeStr,
                            onValueChange = { cookTimeStr = it },
                            label = { Text("Cook (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("kcal/serv") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section 2: Ingredients with Quantities Editor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Ingredients with Quantities",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedButton(
                            onClick = {
                                ingredientsList.add(RecipeIngredientDetail("New Ingredient", 50.0, "g", false, ""))
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_ingredient_row_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Ingredient", fontSize = 12.sp)
                        }
                    }

                    ingredientsList.forEachIndexed { index, item ->
                        IngredientRowEditor(
                            item = item,
                            onUpdate = { updated -> ingredientsList[index] = updated },
                            onDelete = { ingredientsList.removeAt(index) }
                        )
                    }
                }
            }
        }

        // Section 3: Preparation Steps Editor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Preparation Steps",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedButton(
                            onClick = {
                                prepStepsList.add("New preparation instruction step.")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_prep_step_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Step", fontSize = 12.sp)
                        }
                    }

                    prepStepsList.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = step,
                                onValueChange = { prepStepsList[index] = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_step_$index"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = false,
                                maxLines = 3
                            )

                            IconButton(
                                onClick = { if (prepStepsList.size > 1) prepStepsList.removeAt(index) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Step", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Compliance & Allergen Audit Button
        item {
            OutlinedButton(
                onClick = {
                    val allergenNames = ingredientsList.filter { it.isAllergen }.map { it.name }
                    auditSummary = if (allergenNames.isNotEmpty()) {
                        "Allergen Risk Alert: Contains ${allergenNames.joinToString(", ")}. Regulatory advisory statement required on package."
                    } else {
                        "Compliance Pre-Check Passed: No major allergens flagged in formula."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipe_audit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run Allergen & Compliance Pre-Check")
            }

            auditSummary?.let { summary ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    if (recipeName.isBlank()) return@Button

                    val recipeToSave = RecipeItem(
                        id = selectedRecipe?.id ?: 0,
                        firestoreId = selectedRecipe?.firestoreId ?: "",
                        name = recipeName.ifBlank { "Untitled Recipe" },
                        description = description,
                        location = location,
                        regionCategory = regionCategory,
                        dietaryPreference = dietaryPreference,
                        ingredientsJson = RecipeItem.encodeIngredients(ingredientsList),
                        prepStepsJson = RecipeItem.encodePrepSteps(prepStepsList),
                        servings = servingsStr.toIntOrNull() ?: 4,
                        prepTimeMinutes = prepTimeStr.toIntOrNull() ?: 15,
                        cookTimeMinutes = cookTimeStr.toIntOrNull() ?: 20,
                        caloriesPerServing = caloriesStr.toIntOrNull() ?: 350,
                        isSyncedToFirestore = true
                    )

                    viewModel.saveRecipe(recipeToSave)
                    viewModel.navigateTo(ScreenRoute.Recipes)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_recipe_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Cloud Firestore & Room DB", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun IngredientRowEditor(
    item: RecipeIngredientDetail,
    onUpdate: (RecipeIngredientDetail) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = { onUpdate(item.copy(name = it)) },
                    label = { Text("Name", fontSize = 11.sp) },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = if (item.quantity == 0.0) "" else item.quantity.toString(),
                    onValueChange = { onUpdate(item.copy(quantity = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Qty", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = item.unit,
                    onValueChange = { onUpdate(item.copy(unit = it)) },
                    label = { Text("Unit", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Ingredient", tint = MaterialTheme.colorScheme.error)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.isAllergen,
                        onCheckedChange = { onUpdate(item.copy(isAllergen = it)) }
                    )
                    Text("Flag as Allergen", fontSize = 12.sp, color = if (item.isAllergen) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface)
                }

                OutlinedTextField(
                    value = item.notes,
                    onValueChange = { onUpdate(item.copy(notes = it)) },
                    label = { Text("Notes (e.g. Organic)", fontSize = 10.sp) },
                    modifier = Modifier
                        .width(180.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }
    }
}
