package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.RestaurantMenu
import com.example.ui.FoodComplianceViewModel
import com.example.ui.ScreenRoute
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AllergenCheckerScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IngredientDeclarationScreen
import com.example.ui.screens.NutritionFactsScreen
import com.example.ui.screens.ProductLabelScreen
import com.example.ui.screens.RecipeCostScreen
import com.example.ui.screens.RecipeEditScreen
import com.example.ui.screens.RecipeListScreen
import com.example.ui.screens.SavedProductsScreen
import com.example.ui.screens.ShelfLifeScreen
import com.example.ui.theme.FoodComplianceTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FoodComplianceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()

            FoodComplianceTheme(darkTheme = isDark) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: FoodComplianceViewModel) {
    val currentRoute by viewModel.currentRoute.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentRoute.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.Auth) },
                        modifier = Modifier.testTag("account_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (currentRoute != ScreenRoute.Auth) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.Dashboard,
                        onClick = { viewModel.navigateTo(ScreenRoute.Dashboard) },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.Recipes || currentRoute == ScreenRoute.RecipeForm,
                        onClick = { viewModel.navigateTo(ScreenRoute.Recipes) },
                        icon = { Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = "Recipes") },
                        label = { Text("Recipes", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_recipes")
                    )
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.NutritionFacts,
                        onClick = { viewModel.navigateTo(ScreenRoute.NutritionFacts) },
                        icon = { Icon(imageVector = Icons.Default.Analytics, contentDescription = "Nutrition") },
                        label = { Text("Nutrition", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_nutrition")
                    )
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.Allergens,
                        onClick = { viewModel.navigateTo(ScreenRoute.Allergens) },
                        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Allergens") },
                        label = { Text("Allergens", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_allergens")
                    )
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.ProductLabel,
                        onClick = { viewModel.navigateTo(ScreenRoute.ProductLabel) },
                        icon = { Icon(imageVector = Icons.Default.Label, contentDescription = "Label") },
                        label = { Text("Label AI", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_label")
                    )
                    NavigationBarItem(
                        selected = currentRoute == ScreenRoute.AiAssistant,
                        onClick = { viewModel.navigateTo(ScreenRoute.AiAssistant) },
                        icon = { Icon(imageVector = Icons.Default.Psychology, contentDescription = "Assistant") },
                        label = { Text("AI Assistant", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_assistant")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                ScreenRoute.Dashboard -> DashboardScreen(viewModel = viewModel)
                ScreenRoute.Recipes -> RecipeListScreen(viewModel = viewModel)
                ScreenRoute.RecipeForm -> RecipeEditScreen(viewModel = viewModel)
                ScreenRoute.NutritionFacts -> NutritionFactsScreen(viewModel = viewModel)
                ScreenRoute.Ingredients -> IngredientDeclarationScreen(viewModel = viewModel)
                ScreenRoute.Allergens -> AllergenCheckerScreen(viewModel = viewModel)
                ScreenRoute.ProductLabel -> ProductLabelScreen(viewModel = viewModel)
                ScreenRoute.RecipeCost -> RecipeCostScreen(viewModel = viewModel)
                ScreenRoute.ShelfLife -> ShelfLifeScreen(viewModel = viewModel)
                ScreenRoute.AiAssistant -> AiAssistantScreen(viewModel = viewModel)
                ScreenRoute.SavedProducts -> SavedProductsScreen(viewModel = viewModel)
                ScreenRoute.Auth -> AuthScreen(viewModel = viewModel)
            }
        }
    }
}
