package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FoodComplianceViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.QuickActionCard
import com.example.ui.components.StatCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintAccent

@Composable
fun DashboardScreen(
    viewModel: FoodComplianceViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.savedProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userName by viewModel.userDisplayName.collectAsState()

    val totalProducts = products.size
    val avgScore = if (products.isNotEmpty()) products.map { it.complianceHealthScore }.average().toInt() else 100

    LazyColumn(
        modifier = modifier
            .testTag("dashboard_screen")
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    EmeraldPrimary
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MintAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "FDA & EU Compliance Suite",
                                    fontSize = 12.sp,
                                    color = MintAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Welcome, $userName",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Automated AI audits, FDA 21 CFR labeling, Allergen warnings & QUID ingredient lists.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Audit Health",
                    value = "$avgScore%",
                    subtitle = "FDA / EU Pass Rate",
                    icon = Icons.Default.CheckCircle,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MintAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Products",
                    value = "$totalProducts",
                    subtitle = "Compliant Items",
                    icon = Icons.Default.Description,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Action Tools Title
        item {
            Text(
                text = "Compliance Action Tools",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Quick Action Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "Recipe Book",
                        description = "Firestore & Location AI",
                        icon = Icons.Default.RestaurantMenu,
                        accentColor = MintAccent,
                        onClick = { viewModel.navigateTo(ScreenRoute.Recipes) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Nutrition Facts",
                        description = "FDA 21 CFR label",
                        icon = Icons.Default.Analytics,
                        accentColor = EmeraldPrimary,
                        onClick = { viewModel.navigateTo(ScreenRoute.NutritionFacts) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "Allergen Scanner",
                        description = "FDA Top 9 & EU 14",
                        icon = Icons.Default.Warning,
                        accentColor = AmberWarning,
                        onClick = { viewModel.navigateTo(ScreenRoute.Allergens) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Label Generator",
                        description = "Package claims & QR",
                        icon = Icons.Default.Label,
                        accentColor = MintAccent,
                        onClick = { viewModel.navigateTo(ScreenRoute.ProductLabel) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "Recipe Costing",
                        description = "Batch cost & margins",
                        icon = Icons.Default.AttachMoney,
                        accentColor = EmeraldPrimary,
                        onClick = { viewModel.navigateTo(ScreenRoute.RecipeCost) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Shelf-Life AI",
                        description = "Aw, pH & storage temp",
                        icon = Icons.Default.Schedule,
                        accentColor = AmberWarning,
                        onClick = { viewModel.navigateTo(ScreenRoute.ShelfLife) },
                        modifier = Modifier.weight(1f)
                    )
                }

                QuickActionCard(
                    title = "AI Compliance Assistant (Thinking Mode)",
                    description = "Ask Gemini 3.1 Pro for FDA/EU food regulation guidance",
                    icon = Icons.Default.Psychology,
                    accentColor = MintAccent,
                    onClick = { viewModel.navigateTo(ScreenRoute.AiAssistant) }
                )
            }
        }

        // Saved Products Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Compliant Formulations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "View All (${products.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.navigateTo(ScreenRoute.SavedProducts) }
                )
            }
        }

        // Recent Saved Product list
        items(products.take(3)) { prod ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saved_item_${prod.id}")
                    .clickable {
                        viewModel.updateSelectedProduct(prod)
                        viewModel.navigateTo(ScreenRoute.ProductLabel)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${prod.complianceHealthScore}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prod.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${prod.category} • ${prod.servingSize}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Compliant",
                        tint = MintAccent
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
