package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun RecipeCostScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val costResult by viewModel.costResult.collectAsState()

    var servingsYieldText by remember { mutableStateOf("10") }
    var targetMarginText by remember { mutableStateOf("65.0") }

    val ingredients = remember {
        mutableStateListOf(
            RecipeIngredientInput("Rolled Oats", 500.0, costPerKg = 3.50),
            RecipeIngredientInput("Honey", 200.0, costPerKg = 8.00),
            RecipeIngredientInput("Almonds", 150.0, costPerKg = 14.00),
            RecipeIngredientInput("Dried Cranberries", 100.0, costPerKg = 10.00)
        )
    }

    LazyColumn(
        modifier = modifier
            .testTag("recipe_cost_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Recipe Cost & Margin Calculator",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Calculates exact batch cost, cost per serving, food cost %, gross margin & suggested retail price.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Target Yield & Margin Inputs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = servingsYieldText,
                        onValueChange = { servingsYieldText = it },
                        label = { Text("Batch Yield (Servings)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cost_yield_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = targetMarginText,
                        onValueChange = { targetMarginText = it },
                        label = { Text("Target Margin %") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cost_margin_input"),
                        singleLine = true
                    )
                }
            }
        }

        // Ingredient Rows Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredient Prices",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        ingredients.add(RecipeIngredientInput("New Raw Material", 100.0, costPerKg = 5.00))
                    },
                    modifier = Modifier.testTag("add_cost_ingredient_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Row")
                }
            }
        }

        // Ingredient Cost Input Rows
        itemsIndexed(ingredients) { idx, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        OutlinedTextField(
                            value = item.name,
                            onValueChange = { newName ->
                                ingredients[idx] = item.copy(name = newName)
                            },
                            label = { Text("Material") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = item.weightGrams.toString(),
                            onValueChange = { w ->
                                ingredients[idx] = item.copy(weightGrams = w.toDoubleOrNull() ?: 0.0)
                            },
                            label = { Text("Grams") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = item.costPerKg.toString(),
                            onValueChange = { c ->
                                ingredients[idx] = item.copy(costPerKg = c.toDoubleOrNull() ?: 0.0)
                            },
                            label = { Text("$/kg") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    IconButton(
                        onClick = {
                            if (ingredients.size > 1) ingredients.removeAt(idx)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = {
                    val yield = servingsYieldText.toIntOrNull() ?: 10
                    val margin = targetMarginText.toDoubleOrNull() ?: 65.0
                    viewModel.calculateCost(ingredients.toList(), yield, margin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("calculate_cost_btn")
            ) {
                Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculate Batch Cost & Pricing")
            }
        }

        // Calculation Result Breakdown
        item {
            costResult?.let { res ->
                val totalCost = res["totalCost"] as Double
                val costPerServing = res["costPerServing"] as Double
                val suggestedPrice = res["suggestedPrice"] as Double
                val foodCostPct = res["foodCostPct"] as Double

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cost_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Financial & Pricing Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Total Batch Cost:", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(String.format("$%.2f", totalCost), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Cost Per Serving:", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(String.format("$%.2f", costPerServing), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Suggested Retail Price:", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(String.format("$%.2f", suggestedPrice), fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Food Cost %:", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(String.format("%.1f%%", foodCostPct), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val report = """
                                    RECIPE COSTING REPORT
                                    Total Batch Cost: $${String.format("%.2f", totalCost)}
                                    Cost Per Serving: $${String.format("%.2f", costPerServing)}
                                    Suggested Retail Price: $${String.format("%.2f", suggestedPrice)}
                                    Food Cost %: ${String.format("%.1f%%", foodCostPct)}
                                """.trimIndent()
                                shareReport(context, "Recipe Cost Analysis", report)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Financial Analysis")
                        }
                    }
                }
            }
        }
    }
}
