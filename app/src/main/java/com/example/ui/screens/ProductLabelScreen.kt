package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodProduct
import com.example.ui.FoodComplianceViewModel
import com.example.ui.components.FDAFormattedNutritionLabel
import com.example.ui.components.shareReport
import com.example.ui.theme.MintAccent

@Composable
fun ProductLabelScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeProduct by viewModel.selectedProduct.collectAsState()
    val auditResult by viewModel.auditResult.collectAsState()

    var name by remember(activeProduct) { mutableStateOf(activeProduct.name) }
    var netWeight by remember(activeProduct) { mutableStateOf(activeProduct.netWeight) }
    var distributor by remember(activeProduct) { mutableStateOf(activeProduct.distributorInfo) }
    var claims by remember(activeProduct) { mutableStateOf(activeProduct.fopClaims) }
    var ingredientsText by remember(activeProduct) { mutableStateOf(activeProduct.ingredientsList) }
    var storage by remember(activeProduct) { mutableStateOf(activeProduct.storageCondition) }

    LazyColumn(
        modifier = modifier
            .testTag("product_label_screen")
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Food Product Label Generator",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Generates complete packaging layout (Principal Display Panel + Information Panel) per FDA CFR 101.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Editable Label Metadata Form
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
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Statement of Identity (Name)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_product_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = netWeight,
                        onValueChange = { netWeight = it },
                        label = { Text("Net Quantity of Contents (e.g. 350g / 12.3 oz)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_net_weight_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = distributor,
                        onValueChange = { distributor = it },
                        label = { Text("Manufacturer / Distributor Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_distributor_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = claims,
                        onValueChange = { claims = it },
                        label = { Text("Front of Package Claims (e.g. Low Sodium, Non-GMO)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_claims_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ingredientsText,
                        onValueChange = { ingredientsText = it },
                        label = { Text("Ingredients List & Allergen Statement") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_ingredients_input"),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = storage,
                        onValueChange = { storage = it },
                        label = { Text("Storage & Handling Instructions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("label_storage_input"),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val updated = activeProduct.copy(
                                name = name,
                                netWeight = netWeight,
                                distributorInfo = distributor,
                                fopClaims = claims,
                                ingredientsList = ingredientsText,
                                storageCondition = storage
                            )
                            viewModel.saveCurrentProduct(updated)
                            viewModel.auditProduct(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("audit_label_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audit & Generate Label Artwork")
                    }
                }
            }
        }

        // Complete Label Artwork Canvas
        item {
            val previewProduct = activeProduct.copy(
                name = name,
                netWeight = netWeight,
                distributorInfo = distributor,
                fopClaims = claims,
                ingredientsList = ingredientsText,
                storageCondition = storage
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("label_canvas_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = previewProduct.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = previewProduct.fopClaims,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "NET WT: ${previewProduct.netWeight}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Divider(color = Color.Black, thickness = 2.dp)

                    // Information Panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left: Nutrition Panel
                        FDAFormattedNutritionLabel(
                            product = previewProduct,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Right: Ingredients & Barcode
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "INGREDIENTS:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = previewProduct.ingredientsList.ifBlank { "Rolled Oats, Sugar, Honey, Salt." },
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = Color.Black
                            )

                            Text(
                                text = "DISTRIBUTED BY:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = previewProduct.distributorInfo,
                                fontSize = 10.sp,
                                color = Color.Black
                            )

                            Text(
                                text = "STORAGE:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = previewProduct.storageCondition,
                                fontSize = 10.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Simulated UPC Barcode Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .border(1.dp, Color.Black)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Barcode",
                                        tint = Color.Black,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UPC 0-12345-67890-5",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = Color.Black, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveCurrentProduct(previewProduct) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Label")
                        }

                        OutlinedButton(
                            onClick = {
                                val report = """
                                    COMPLETE PACKAGING LABEL SPECIFICATION
                                    Product Name: ${previewProduct.name}
                                    Net Weight: ${previewProduct.netWeight}
                                    Distributor: ${previewProduct.distributorInfo}
                                    Claims: ${previewProduct.fopClaims}
                                    Ingredients: ${previewProduct.ingredientsList}
                                    Storage: ${previewProduct.storageCondition}
                                """.trimIndent()
                                shareReport(context, "Label Artwork Specs", report)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Specs")
                        }
                    }
                }
            }
        }

        // Compliance Checklist Result
        item {
            auditResult?.let { audit ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "FDA Label Compliance Score: ${audit.healthScore}% (${audit.overallStatus})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        audit.fdaChecklist.forEach { item ->
                            Text(text = item, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        if (audit.recommendedFixes.isNotEmpty()) {
                            Text(
                                text = "Action Items Required:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            audit.recommendedFixes.forEach { fix ->
                                Text(text = "• $fix", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
