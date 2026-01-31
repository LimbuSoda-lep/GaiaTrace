package com.example.gaiatrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaiatrace.ui.theme.GaiaTraceTheme
import java.util.Locale

/**
 * GaiaTrace - A Carbon Footprint Calculator
 * Fixed: Added subTextColor parameter to DropdownSelector to fix the unresolved reference.
 */

// Light Theme Palette
val OffWhite = Color(0xFFFAF9F6)
val LightDeepEcoGreen = Color(0xFF2E7D32)
val LightTextGrey = Color(0xFF546E7A)
val LightSurface = Color.White

// Dark Theme Palette
val DarkBackground = Color(0xFF1A1C19)
val DarkSurface = Color(0xFF222521)
val DarkDeepEcoGreen = Color(0xFF81C784)
val DarkTextGrey = Color(0xFFC4C7C0)
val DarkTextPrimary = Color(0xFFE2E3DE)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemTheme) }
            
            val backgroundColor = if (isDarkMode) DarkBackground else OffWhite
            val primaryGreen = if (isDarkMode) DarkDeepEcoGreen else LightDeepEcoGreen
            val subTextColor = if (isDarkMode) DarkTextGrey else LightTextGrey
            val surfaceColor = if (isDarkMode) DarkSurface else LightSurface

            GaiaTraceTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = backgroundColor,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GaiaLogo(modifier = Modifier.size(32.dp), color = primaryGreen)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "GaiaTrace", 
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        color = primaryGreen
                                    )
                                }
                            },
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = if (isDarkMode) "Dark" else "Light",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = primaryGreen
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = isDarkMode,
                                        onCheckedChange = { isDarkMode = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = primaryGreen,
                                            checkedTrackColor = primaryGreen.copy(alpha = 0.5f),
                                            uncheckedThumbColor = Color.Gray,
                                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = backgroundColor
                            )
                        )
                    }
                ) { innerPadding ->
                    GaiaTraceApp(
                        modifier = Modifier.padding(innerPadding),
                        isDarkMode = isDarkMode,
                        primaryGreen = primaryGreen,
                        subTextColor = subTextColor,
                        surfaceColor = surfaceColor
                    )
                }
            }
        }
    }
}

/**
 * Minimalist Logo Component
 */
@Composable
fun GaiaLogo(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val size = size.minDimension
        drawCircle(
            color = color.copy(alpha = 0.1f),
            radius = size / 2
        )
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = center.copy(x = center.x + size/4, y = center.y - size/4)
        )
    }
}

/**
 * Data model for calculation breakdown
 */
data class FootprintResult(
    val transportFuelCO2: Double,
    val electricityCO2: Double,
    val foodCO2: Double,
    val totalCO2: Double
)

@Composable
fun GaiaTraceApp(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    primaryGreen: Color,
    subTextColor: Color,
    surfaceColor: Color
) {
    var distance by remember { mutableStateOf("") }
    var electricity by remember { mutableStateOf("") }
    var transportType by remember { mutableStateOf("Petrol/Diesel Car") }
    var foodHabit by remember { mutableStateOf("Mixed") }
    var result by remember { mutableStateOf<FootprintResult?>(null) }

    val scrollState = rememberScrollState()

    // UI Logic for conditional visibility
    val isElectric = transportType == "Electric Bike" || transportType == "Electric Car"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
            .animateContentSize(animationSpec = tween(durationMillis = 500)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Track your daily carbon footprint",
            style = MaterialTheme.typography.bodyLarge,
            color = subTextColor,
            fontWeight = FontWeight.Medium
        )

        // 1. Transport Selection
        DropdownSelector(
            label = "Transport Type",
            options = listOf("Petrol Bike", "Electric Bike", "Petrol/Diesel Car", "Electric Car", "Public Transport", "Bicycle"),
            selectedOption = transportType,
            onOptionSelected = { transportType = it },
            primaryGreen = primaryGreen,
            subTextColor = subTextColor,
            surfaceColor = surfaceColor
        )

        // 2. Distance Input
        OutlinedTextField(
            value = distance,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) distance = it },
            label = { Text("Distance traveled (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceColor,
                focusedContainerColor = surfaceColor,
                focusedBorderColor = primaryGreen,
                unfocusedBorderColor = subTextColor.copy(alpha = 0.3f),
                focusedLabelColor = primaryGreen,
                unfocusedLabelColor = subTextColor
            )
        )

        // 3. Electricity Input
        AnimatedVisibility(
            visible = isElectric,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = electricity,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) electricity = it },
                label = { Text("Vehicle Charging (kWh)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceColor,
                    focusedContainerColor = surfaceColor,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = subTextColor.copy(alpha = 0.3f),
                    focusedLabelColor = primaryGreen,
                    unfocusedLabelColor = subTextColor
                )
            )
        }

        // 4. Food Habit Selection
        DropdownSelector(
            label = "Food Habit",
            options = listOf("Vegetarian", "Mixed", "Non-Vegetarian"),
            selectedOption = foodHabit,
            onOptionSelected = { foodHabit = it },
            primaryGreen = primaryGreen,
            subTextColor = subTextColor,
            surfaceColor = surfaceColor
        )

        Text(
            text = "Note: Results are estimates based on India's average emission factors. Bicycle emissions are 0.",
            style = MaterialTheme.typography.labelSmall,
            color = subTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // 5. Calculate Button
        Button(
            onClick = {
                val d = distance.toDoubleOrNull() ?: 0.0
                val e = electricity.toDoubleOrNull() ?: 0.0
                result = calculateDetailedCarbon(transportType, d, e, foodHabit)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text("Calculate Footprint", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.Black else Color.White)
        }

        // 6. Result Section
        AnimatedVisibility(
            visible = result != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            result?.let {
                ResultSection(it, primaryGreen, subTextColor, isDarkMode)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    primaryGreen: Color,
    subTextColor: Color,
    surfaceColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceColor,
                focusedContainerColor = surfaceColor,
                focusedBorderColor = primaryGreen,
                unfocusedBorderColor = subTextColor.copy(alpha = 0.3f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = surfaceColor
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontWeight = FontWeight.Medium) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ResultSection(res: FootprintResult, primaryGreen: Color, subTextColor: Color, isDarkMode: Boolean) {
    val (classification, baseColor) = when {
        res.totalCO2 < 4.0 -> "Low" to Color(0xFF66BB6A)
        res.totalCO2 <= 10.0 -> "Medium" to Color(0xFFFFA726)
        else -> "High" to Color(0xFFEF5350)
    }

    val tips = when (classification) {
        "Low" -> listOf("Great job! Keep maintaining your sustainable habits.", "Consider sharing your eco-lifestyle with others.", "Try to reach zero waste by composting.")
        "Medium" -> listOf("Try using public transport more often.", "Switch to energy-efficient LED bulbs.", "Consider reducing meat consumption.", "Unplug devices when not in use.")
        else -> listOf("Consider carpooling or switching to an electric vehicle.", "Audit your home energy usage.", "Adopt a more plant-based diet.", "Plant trees to offset your carbon footprint.")
    }

    val animatedColor by animateColorAsState(
        targetValue = baseColor,
        animationSpec = tween(durationMillis = 1000),
        label = "ColorAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor.copy(alpha = if (isDarkMode) 0.15f else 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Estimated Footprint for Given Inputs", style = MaterialTheme.typography.titleMedium, color = subTextColor, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format(Locale.getDefault(), "%.2f kg CO₂", res.totalCO2),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = animatedColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = animatedColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = classification,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = animatedColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ResultBreakdownRow("Transport CO₂", res.transportFuelCO2, subTextColor)
                ResultBreakdownRow("Electricity CO₂", res.electricityCO2, subTextColor)
                ResultBreakdownRow("Food CO₂", res.foodCO2, subTextColor)
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                "Eco-Friendly Tips",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            tips.forEach { tip ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = animatedColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip, 
                        style = MaterialTheme.typography.bodyMedium,
                        color = subTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun ResultBreakdownRow(label: String, value: Double, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = textColor)
        Text(text = String.format(Locale.getDefault(), "%.2f kg", value), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
    }
}

fun calculateDetailedCarbon(type: String, dist: Double, kwh: Double, food: String): FootprintResult {
    val transportFuelCO2 = when (type) {
        "Petrol Bike" -> dist * 0.134
        "Petrol/Diesel Car" -> dist * 0.20
        "Public Transport" -> dist * 0.05
        else -> 0.0
    }

    val electricityCO2 = if (type == "Electric Bike" || type == "Electric Car") {
        kwh * 0.82
    } else 0.0

    val foodCO2 = when (food) {
        "Vegetarian" -> 1.5
        "Mixed" -> 2.5
        "Non-Vegetarian" -> 3.5
        else -> 0.0
    }

    return FootprintResult(
        transportFuelCO2 = transportFuelCO2,
        electricityCO2 = electricityCO2,
        foodCO2 = foodCO2,
        totalCO2 = transportFuelCO2 + electricityCO2 + foodCO2
    )
}
