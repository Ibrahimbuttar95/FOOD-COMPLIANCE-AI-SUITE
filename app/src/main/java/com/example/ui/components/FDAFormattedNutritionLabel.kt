package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodProduct

@Composable
fun FDAFormattedNutritionLabel(
    product: FoodProduct,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("fda_nutrition_label_box")
            .background(Color.White)
            .border(2.dp, Color.Black)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "Nutrition Facts",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "${product.servingsPerContainer.toInt()} servings per container",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Serving size",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = product.servingSize,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Divider(color = Color.Black, thickness = 8.dp, modifier = Modifier.padding(vertical = 6.dp))

            Text(
                text = "Amount per serving",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Calories",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${product.calories}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Divider(color = Color.Black, thickness = 4.dp, modifier = Modifier.padding(vertical = 6.dp))

            Text(
                text = "% Daily Value*",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.End)
            )

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Total Fat
            NutrientRow(
                label = "Total Fat",
                amount = "${product.totalFatG}g",
                dvPct = "${((product.totalFatG / 78.0) * 100).toInt()}%",
                isBold = true
            )
            // Saturated Fat
            NutrientRow(
                label = "Saturated Fat",
                amount = "${product.saturatedFatG}g",
                dvPct = "${((product.saturatedFatG / 20.0) * 100).toInt()}%",
                indentLevel = 12
            )
            // Trans Fat
            NutrientRow(
                label = "Trans Fat",
                amount = "${product.transFatG}g",
                dvPct = "",
                indentLevel = 12
            )

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Cholesterol
            NutrientRow(
                label = "Cholesterol",
                amount = "${product.cholesterolMg.toInt()}mg",
                dvPct = "${((product.cholesterolMg / 300.0) * 100).toInt()}%",
                isBold = true
            )

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Sodium
            NutrientRow(
                label = "Sodium",
                amount = "${product.sodiumMg.toInt()}mg",
                dvPct = "${((product.sodiumMg / 2300.0) * 100).toInt()}%",
                isBold = true
            )

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Total Carbohydrate
            NutrientRow(
                label = "Total Carbohydrate",
                amount = "${product.totalCarbG}g",
                dvPct = "${((product.totalCarbG / 275.0) * 100).toInt()}%",
                isBold = true
            )
            // Dietary Fiber
            NutrientRow(
                label = "Dietary Fiber",
                amount = "${product.dietaryFiberG}g",
                dvPct = "${((product.dietaryFiberG / 28.0) * 100).toInt()}%",
                indentLevel = 12
            )
            // Total Sugars
            NutrientRow(
                label = "Total Sugars",
                amount = "${product.totalSugarsG}g",
                dvPct = "",
                indentLevel = 12
            )
            // Added Sugars
            NutrientRow(
                label = "Includes ${product.addedSugarsG}g Added Sugars",
                amount = "",
                dvPct = "${((product.addedSugarsG / 50.0) * 100).toInt()}%",
                indentLevel = 24
            )

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Protein
            NutrientRow(
                label = "Protein",
                amount = "${product.proteinG}g",
                dvPct = "${((product.proteinG / 50.0) * 100).toInt()}%",
                isBold = true
            )

            Divider(color = Color.Black, thickness = 6.dp, modifier = Modifier.padding(vertical = 4.dp))

            // Micronutrients
            NutrientRow(label = "Vitamin D", amount = "${product.vitaminDMcg}mcg", dvPct = "${((product.vitaminDMcg / 20.0) * 100).toInt()}%")
            NutrientRow(label = "Calcium", amount = "${product.calciumMg.toInt()}mg", dvPct = "${((product.calciumMg / 1300.0) * 100).toInt()}%")
            NutrientRow(label = "Iron", amount = "${product.ironMg}mg", dvPct = "${((product.ironMg / 18.0) * 100).toInt()}%")
            NutrientRow(label = "Potassium", amount = "${product.potassiumMg.toInt()}mg", dvPct = "${((product.potassiumMg / 4700.0) * 100).toInt()}%")

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "* The % Daily Value (DV) tells you how much a nutrient in a serving of food contributes to a daily diet. 2,000 calories a day is used for general nutrition advice.",
                fontSize = 8.sp,
                lineHeight = 10.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun NutrientRow(
    label: String,
    amount: String,
    dvPct: String,
    isBold: Boolean = false,
    indentLevel: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indentLevel.dp, top = 1.dp, bottom = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label + if (amount.isNotBlank()) " $amount" else "",
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        if (dvPct.isNotBlank()) {
            Text(
                text = dvPct,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
