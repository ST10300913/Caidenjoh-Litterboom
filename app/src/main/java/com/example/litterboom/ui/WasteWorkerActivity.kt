package com.example.litterboom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.litterboom.ui.theme.LitterboomTheme

class WasteWorkerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LitterboomTheme {
                WasteWorkerScreen()
            }
        }
    }
}

data class LitterEntry(
    val category: String,
    val description: String,
    val typeAndBrand: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteWorkerScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.litterboom_logo__2_),
                        contentDescription = "Litterboom Logo",
                        modifier = Modifier.height(40.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle Menu Click */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
        ) {
            WasteWorkerContent(innerPadding)
        }
    }
}

@Composable
fun WasteWorkerContent(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val sampleEntries = listOf(
        LitterEntry("Cardboard", "Paper plates", "Cardboard : FunCompany"),
        LitterEntry("Plastic", "2L Coke Bottle", "PET : Coca-Cola"),
        LitterEntry("Glass", "Beer Bottle", "Glass : Heineken"),
        LitterEntry("Tins", "Koo Baked Beans", "Tin : Koo"),
        LitterEntry("Plastic", "Milk Bottle", "HDPE : Clover"),
        LitterEntry("Cardboard", "Cereal Box", "Cardboard : Kellogg's"),
        LitterEntry("Plastic", "Water Bottle", "PET : Aquelle"),
        LitterEntry("Glass", "Jam Jar", "Glass : All Gold"),
        LitterEntry("Tins", "Tuna Can", "Tin : Lucky Star"),
        LitterEntry("Cardboard", "Shoe Box", "Cardboard : Nike"),
        LitterEntry("Plastic", "Shampoo Bottle", "HDPE : Head & Shoulders"),
        LitterEntry("Glass", "Wine Bottle", "Glass : Nederburg"),
        LitterEntry("Tins", "Pilchards Can", "Tin : Glenryck"),
        LitterEntry("Plastic", "Yoghurt Tub", "PP : Danone"),
        LitterEntry("Cardboard", "Amazon Box", "Cardboard : Amazon"),
        LitterEntry("Plastic", "Detergent Bottle", "HDPE : OMO"),
        LitterEntry("Glass", "Coffee Jar", "Glass : Nescafé"),
        LitterEntry("Tins", "Spaghetti Can", "Tin : Koo"),
        LitterEntry("Cardboard", "Pizza Box", "Cardboard : Debonairs"),
        LitterEntry("Plastic", "Ice Cream Tub", "PP : Ola")
    )

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome back, Worker!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )

        Text(
            text = "Not you? Logout",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable(
                    // *** THIS IS THE FIX ***
                    // We explicitly remove the conflicting ripple effect
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
        )

        // Table Container
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)

        ) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(text = "Category", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                Text(text = "Description", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Text(text = "Type & Brand", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.End)
            }

            // Table Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(sampleEntries) { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = entry.category, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = entry.description, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                        Text(text = entry.typeAndBrand, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End)
                    }
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Finished? Check your entries then send them to an admin by clicking the button below.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Handle post entries click */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Post Entries",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WasteWorkerScreenPreview() {
    LitterboomTheme {
        WasteWorkerScreen()
    }
}