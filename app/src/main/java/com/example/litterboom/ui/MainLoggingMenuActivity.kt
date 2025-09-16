package com.example.litterboom.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.litterboom.R
import com.example.litterboom.ui.theme.LitterboomTheme

class MainLoggingMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LitterboomTheme {
                MainLoggingMenuScreen()
            }
        }
    }
}

@Composable
fun MainLoggingMenuScreen() {
    val categories = listOf(
        "Cardboard", "Flexible Plastic", "Glass", "Mixed Waste",
        "Organic", "Polystyrene", "Rigid Plastic", "Category"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFFE0E0E0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.litterboom_logo__2_),
                contentDescription = "Litterboom Logo",
                modifier = Modifier
                    .height(60.dp)
                    .padding(vertical = 8.dp)
            )

            // Welcome and Logout text from wireframe
            Text("Welcome back, Username!", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Not you? Logout",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar Placeholder
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Filter a category?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Click a category below to begin logging",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Category buttons
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    Button(
                        onClick = { /* Handle category selection */ },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(60.dp)
                    ) {
                        Text(text = category)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainLoggingMenuScreenPreview() {
    LitterboomTheme {
        MainLoggingMenuScreen()
    }
}