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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecipeIngredientInput
import com.example.ui.FoodComplianceViewModel
import com.example.ui.components.shareReport
import com.example.ui.theme.MintAccent

@Composable
fun IngredientDeclarationScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ingredientResult by viewModel.ingredientResult.collectAsState()
    val isGenerating by viewModel.isGeneratingIngredients.collectAsState()

    val ingredientsList = remember {
        mutableStateListOf(
            RecipeIngredientInput("Rolled Oats", 400.0),
            RecipeIngredientInput("Organic Honey", 150.0),
            RecipeIngredientInput("Almonds", 120.0, isAllergen = true, allergenCategory = "Tree Nut"),
            RecipeIngredientInput("Dried Cranberries", 100.0),
            RecipeIngredientInput("Coconut Oil", 50.0),
            RecipeIngredientInput("Sea Salt", 10.0)
        )
    }

    LazyColumn(
        modifier = modifier
            .testTag("ingredient_declaration_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Ingredient Declaration & QUID Engine",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Orders raw components by descending weight & highlights allergens per FDA & EU QUID laws.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Add Ingredient Form Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recipe Batch Formulation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        ingredientsList.add(RecipeIngredientInput("New Ingredient", 50.0))
                    },
                    modifier = Modifier.testTag("add_ingredient_row_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item")
                }
            }
        }

        // Ingredient Rows
        itemsIndexed(ingredientsList) { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ingredient_row_$index"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(2f)) {
                        OutlinedTextField(
                            value = item.name,
                            onValueChange = { newName ->
                                ingredientsList[index] = item.copy(name = newName)
                            },
                            label = { Text("Ingredient") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = item.weightGrams.toString(),
                            onValueChange = { newWt ->
                                ingredientsList[index] = item.copy(weightGrams = newWt.toDoubleOrNull() ?: 0.0)
                            },
                            label = { Text("Weight (g)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Allergen", fontSize = 10.sp)
                        Checkbox(
                            checked = item.isAllergen,
                            onCheckedChange = { chk ->
                                ingredientsList[index] = item.copy(isAllergen = chk)
                            },
                            modifier = Modifier.testTag("allergen_chk_$index")
                        )
                    }
                    IconButton(
                        onClick = {
                            if (ingredientsList.size > 1) {
                                ingredientsList.removeAt(index)
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Generate Declaration Action
        item {
            Button(
                onClick = {
                    viewModel.generateIngredientList(ingredientsList.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_declaration_btn"),
                enabled = !isGenerating
            ) {
                Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compile Compliant Ingredient List")
            }
        }

        // Result Card
        item {
            ingredientResult?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ingredient_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Formatted FDA / EU Declaration",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = res.formattedDeclaration,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = res.regulatoryNotes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedButton(
                            onClick = {
                                shareReport(context, "Ingredient Declaration", res.formattedDeclaration)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Declaration Text")
                        }
                    }
                }
            }
        }
    }
}
