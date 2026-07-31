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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.FoodComplianceViewModel
import com.example.ui.components.AllergenChip
import com.example.ui.components.shareReport
import com.example.ui.theme.RoseAlert

@Composable
fun AllergenCheckerScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allergenResult by viewModel.allergenResult.collectAsState()
    val isChecking by viewModel.isCheckingAllergens.collectAsState()

    var ingredientsInput by remember {
        mutableStateOf("Wheat Flour, Milk Whey Powder, Soybean Oil, Almond Extract, Natural Salt.")
    }
    var facilityInput by remember {
        mutableStateOf("Shared line processing Peanuts, Sesame seeds, and Walnut protein.")
    }

    LazyColumn(
        modifier = modifier
            .testTag("allergen_checker_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "FDA & EU Allergen Risk Scanner",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Checks FDA FALCPA Top 9 & EU 14 major allergens, cross-contact hazards & generates advisory labels.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

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
                        value = ingredientsInput,
                        onValueChange = { ingredientsInput = it },
                        label = { Text("Product Ingredients List") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("allergen_ingredients_input"),
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = facilityInput,
                        onValueChange = { facilityInput = it },
                        label = { Text("Facility Shared Equipment / Cross-Contact") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("allergen_facility_input"),
                        maxLines = 3
                    )

                    Button(
                        onClick = {
                            viewModel.checkAllergens(ingredientsInput, facilityInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("scan_allergens_btn"),
                        enabled = !isChecking,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseAlert)
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Scanning Major Allergens...")
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run Full Allergen Audit")
                        }
                    }
                }
            }
        }

        item {
            allergenResult?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("allergen_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (res.detectedAllergens.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (res.detectedAllergens.isNotEmpty()) RoseAlert else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Risk Matrix Status: ${res.riskLevel}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Detected Formulation Allergens:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (res.detectedAllergens.isEmpty()) {
                                Text("None detected.", fontSize = 12.sp)
                            } else {
                                res.detectedAllergens.forEach { allergen ->
                                    AllergenChip(allergenName = allergen, isWarning = true)
                                }
                            }
                        }

                        Text(
                            text = "Cross-Contact Equipment Hazards:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (res.crossContactWarnings.isEmpty()) {
                                Text("No cross-contact warnings.", fontSize = 12.sp)
                            } else {
                                res.crossContactWarnings.forEach { warning ->
                                    AllergenChip(allergenName = warning, isWarning = false)
                                }
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Mandatory Label Advisory Text:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.recommendedAdvisoryText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val textReport = """
                                    ALLERGEN COMPLIANCE REPORT
                                    Detected Allergens: ${res.detectedAllergens.joinToString(", ")}
                                    Cross-Contact Hazards: ${res.crossContactWarnings.joinToString(", ")}
                                    Label Advisory: ${res.recommendedAdvisoryText}
                                """.trimIndent()
                                shareReport(context, "Allergen Compliance Scan", textReport)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Allergen Risk Audit")
                        }
                    }
                }
            }
        }
    }
}
