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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import com.example.ui.components.shareReport

@Composable
fun ShelfLifeScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shelfResult by viewModel.shelfLifeResult.collectAsState()
    val isCalculating by viewModel.isCalculatingShelfLife.collectAsState()

    var awText by remember { mutableStateOf("0.62") }
    var phText by remember { mutableStateOf("5.4") }
    var moistureText by remember { mutableStateOf("12.5") }
    var packagingText by remember { mutableStateOf("High-barrier Nitrogen-flushed Mylar foil") }
    var tempText by remember { mutableStateOf("22") }
    var preservativesChecked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .testTag("shelf_life_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Shelf-Life & Stability AI Engine",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Estimates food product stability based on Water Activity (Aw), pH, Moisture %, Packaging & Storage Temp.",
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
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = awText,
                            onValueChange = { awText = it },
                            label = { Text("Water Activity (Aw)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("aw_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = phText,
                            onValueChange = { phText = it },
                            label = { Text("pH Level") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ph_input"),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = moistureText,
                            onValueChange = { moistureText = it },
                            label = { Text("Moisture %") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("moisture_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tempText,
                            onValueChange = { tempText = it },
                            label = { Text("Storage Temp (°C)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("temp_input"),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = packagingText,
                        onValueChange = { packagingText = it },
                        label = { Text("Packaging Type (e.g. MAP, Vacuum, Ambient)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("packaging_input"),
                        singleLine = true
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = preservativesChecked,
                            onCheckedChange = { preservativesChecked = it },
                            modifier = Modifier.testTag("preservatives_chk")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chemical / Natural Preservatives Added", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val aw = awText.toDoubleOrNull() ?: 0.65
                            val ph = phText.toDoubleOrNull() ?: 5.5
                            val moist = moistureText.toDoubleOrNull() ?: 12.0
                            val temp = tempText.toDoubleOrNull() ?: 20.0
                            viewModel.calculateShelfLife(aw, ph, moist, packagingText, temp, preservativesChecked)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("calculate_shelflife_btn"),
                        enabled = !isCalculating
                    ) {
                        if (isCalculating) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Simulating Shelf-Life Stability...")
                        } else {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run Shelf-Life AI Analysis")
                        }
                    }
                }
            }
        }

        item {
            shelfResult?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shelflife_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Estimated Shelf-Life: ${res.estimatedDays} Days (${res.estimatedDays / 30} Months)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Optimal Storage Condition: ${res.optimalStorageTemp}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Packaging Recommendation: ${res.packagingRecommendation}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(text = "Spoilage Hazards Breakdown:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        res.spoilageRiskFactors.forEach { factor ->
                            Text(text = "• $factor", fontSize = 12.sp)
                        }

                        Text(text = "Recommended Testing & Preservation Protocols:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        res.preservationTips.forEach { tip ->
                            Text(text = "✓ $tip", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        OutlinedButton(
                            onClick = {
                                val report = """
                                    SHELF-LIFE STABILITY AUDIT
                                    Estimated Shelf Life: ${res.estimatedDays} Days
                                    Storage: ${res.optimalStorageTemp}
                                    Packaging: ${res.packagingRecommendation}
                                    Risks: ${res.spoilageRiskFactors.joinToString("; ")}
                                """.trimIndent()
                                shareReport(context, "Shelf Life Audit", report)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Shelf-Life Report")
                        }
                    }
                }
            }
        }
    }
}
