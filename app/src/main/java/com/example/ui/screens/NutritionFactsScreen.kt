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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.FoodProduct
import com.example.ui.FoodComplianceViewModel
import com.example.ui.components.FDAFormattedNutritionLabel
import com.example.ui.components.shareReport

@Composable
fun NutritionFactsScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAnalyzing by viewModel.isAnalyzingNutrition.collectAsState()
    val nutritionResult by viewModel.nutritionResult.collectAsState()
    val activeProduct by viewModel.selectedProduct.collectAsState()

    var recipeText by remember { mutableStateOf("Rolled Oats (40g), Honey (15g), Almonds (10g), Dried Cranberries (10g), Coconut Oil (5g), Sea Salt (1g).") }
    var servingSizeText by remember { mutableStateOf("1 Snack Bar (55g)") }

    // Editable active product instance for live preview
    var currentProduct by remember(activeProduct) { mutableStateOf(activeProduct) }

    LazyColumn(
        modifier = modifier
            .testTag("nutrition_facts_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "FDA Nutrition Facts Generator",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Enter raw recipe formulation to auto-calculate FDA 21 CFR 101 compliant Nutrition Facts label.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Inputs Card
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
                    OutlinedTextField(
                        value = recipeText,
                        onValueChange = { recipeText = it },
                        label = { Text("Recipe Ingredients & Quantity (grams / %)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipe_input_field"),
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = servingSizeText,
                        onValueChange = { servingSizeText = it },
                        label = { Text("Serving Size (e.g. 1 Bar - 55g)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("serving_size_input_field"),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            viewModel.analyzeNutrition(recipeText, servingSizeText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_nutrition_btn"),
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Calculating FDA Nutrients...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Nutrition Facts via AI")
                        }
                    }
                }
            }
        }

        // Live Generated Result & FDA Label Preview
        item {
            nutritionResult?.let { result ->
                currentProduct = currentProduct.copy(
                    servingSize = result.servingSize,
                    calories = result.calories,
                    totalFatG = result.totalFatG,
                    saturatedFatG = result.saturatedFatG,
                    transFatG = result.transFatG,
                    cholesterolMg = result.cholesterolMg,
                    sodiumMg = result.sodiumMg,
                    totalCarbG = result.totalCarbG,
                    dietaryFiberG = result.dietaryFiberG,
                    totalSugarsG = result.totalSugarsG,
                    addedSugarsG = result.addedSugarsG,
                    proteinG = result.proteinG,
                    vitaminDMcg = result.vitaminDMcg,
                    calciumMg = result.calciumMg,
                    ironMg = result.ironMg,
                    potassiumMg = result.potassiumMg
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "FDA Formatted Nutrition Label Preview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                FDAFormattedNutritionLabel(
                    product = currentProduct,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.saveCurrentProduct(currentProduct)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_nutrition_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Product")
                    }

                    OutlinedButton(
                        onClick = {
                            val textReport = """
                                FOOD NUTRITION FACTS REPORT
                                Product: ${currentProduct.name}
                                Serving Size: ${currentProduct.servingSize}
                                Calories: ${currentProduct.calories}
                                Total Fat: ${currentProduct.totalFatG}g | Sat Fat: ${currentProduct.saturatedFatG}g
                                Sodium: ${currentProduct.sodiumMg}mg | Carbs: ${currentProduct.totalCarbG}g
                                Fiber: ${currentProduct.dietaryFiberG}g | Sugars: ${currentProduct.totalSugarsG}g
                                Protein: ${currentProduct.proteinG}g
                            """.trimIndent()
                            shareReport(context, "Nutrition Facts - ${currentProduct.name}", textReport)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("export_nutrition_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share / Export")
                    }
                }
            }
        }
    }
}
