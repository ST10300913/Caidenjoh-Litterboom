package com.example.litterboom.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// Placeholder for db items
val subCategoryMap = mapOf(
    "Cardboard" to listOf(
        "Take-Away Packaging - Paper", "Take-Away Packaging - Cardboard", "Take-Away Packaging - Cups",
        "Paper Plates", "Charcoal Bags", "Wine Boxes - 5l", "Multilayer Cartons - Small",
        "Multilayer Cartons - Medium", "Alcohol Boxes - Corona - Large", "Alcohol Boxes - Corona, Sol - 6 Pack",
        "Cigarette Boxes", "Newspaper", "Paper Towel / Toilet Paper / Serviettes",
        "Johnsson - Gloves Packaging", "Medical Packaging", "Branded Packaging - Mixed"
    ),
    "Flexible Plastic" to listOf(
        "6 Pack - Packaging - Savanna Dry", "6 Pack - Packaging - Brutal Fruit", "6 Pack - Packaging - Flying Fish",
        "6 Pack - Packaging - Smirnoff - Berry Twist", "6 Pack - Packaging - Hunter's Dry", "6 Pack - Packaging - Kix Spritzer",
        "6 Pack - Packaging - Black Label", "6 Pack - Packaging - Castle Milk Stout", "6 Pack - Packaging - Espirit",
        "6 Pack - Packaging - Bernini", "6 Pack - Packaging - Windhoek", "Alcohol Sachets", "Bottle Labels",
        "Plastic Bags - Blue - Full", "Plastic Bags - Blue - Pieces", "Plastic Bags - Black - Full",
        "Plastic Bags - White - Bluff Meat", "Plastic Bags - White - Spar", "Plastic Bags - White - Oxford",
        "Plastic Bags - White - Clicks", "Plastic Bags - White - Check Save", "Plastic Bags - Teal - Checkers",
        "Plastic Bags - Red - Gorimas", "Chips - Lays - Thai Sweet Chilli", "Chips - Lays - Caribbean Onion & Balsamic Vinegar",
        "Chips - Lays - Salt & Vinegar", "Chips - Lays - American Style Onion", "Chips - Doritos - Sweet Chilli Pepper",
        "Chips - Doritos - Nacho Cheese", "Chips - Ghost Pops", "Chips - Niknaks - Fruit Chutney",
        "Chips - Simba - Mrs Balls Chutney", "Chips - Simba - Creamy Cheddar", "Chips - Bigga Naks - Cheese",
        "Sweets - Maynards - Wine Gums", "Sweets - Beacon - Jelly Tots", "Sweets - Beacon - Liquorice Allsorts",
        "Sweets - Lunch Bar", "Sweets - Beacon - Mint Imperials", "Sweets - PS - Chocolate", "Sweets - Kit Kat",
        "Sweets - Beacon - Fizz Pops", "Sweets - Super C", "Sweets - Mentos", "Sweets - Halls",
        "Sweets - Beacon - Heavenly", "Biscuits - Bakers - Eet Sum Mor", "Biscuits - Bakers - Topper",
        "Biscuits - Bakers - Blue Label", "Biscuits - Romany Creams", "Biscuits - Oreo", "Chocolate - Cadbury",
        "Chocolate - Beacon", "Bread - Blue Ribbon", "Bread - Sunbake", "Bread - BB - Bread",
        "Bread - Woolworths - Bread", "Bread - Checkers - Bread", "Unidentified Plastic Pieces & Items",
        "Unlabelled Plastic - Blue", "Unlabelled Plastic - Yellow", "Unlabelled Plastic - Red",
        "Unlabelled Plastic - Green", "Unlabelled Plastic - White", "Unlabelled Plastic - Ziplock Bags - Mixed"
    ),
    "Glass" to listOf(
        "Savanna - Dry - 500ml", "Savanna - Dry - 330ml", "Savanna - Neat", "Savanna - Angry Lemon - 500ml",
        "Savanna - Angry Lemon - 330ml", "Corona - Extra", "SOL - Cerveza", "Hansa - Pilsener",
        "Hunter's - Dry", "Windhoek - Draught", "Stella - Artois", "Bernini - Classis",
        "Heineken - Original", "Smirnoff - Spin", "Smirnoff - Storm", "Flying Fish - Lemon",
        "Brutal Fruit - Apple-Ginger", "Brutal Fruit - Strawberry", "KIX - Spritzer", "Coca Cola - Original",
        "Fitch & Leedes - Pink Tonic", "Fitch & Leedes - Indian Tonic", "Unbranded - Beer",
        "Belgravia - Gin & Dry Lemon", "Black Label - Beer", "Miller - Draught", "Robertson - Chapel Red",
        "Nederburg - Stein", "Nederburg - Rose", "SKYY Vodka - Infurions", "Lavida - Spirit Aperitif",
        "Olive Brook - White Moscato", "Ice Tropez - Cocktail Original"
    ),
    "Mixed Waste" to listOf(
        "Cans - Alcohol - Strongbow - Red Berries - 440ml", "Cans - Alcohol - Belgravia - Gin & Dark Cherry - 440ml",
        "Cans - Alcohol - Bernini - Mimosa - 500ml", "Cans - Energy Drink - Monster - Ultra Watermelon - 500ml",
        "Cans - Energy Drink - Reboost - Original - 500ml", "Cans - Coca Cola - Zero Sugar & Caffeine - 300ml",
        "Cans - Pepsi - Original - 300ml", "Cans - Fruit Juice - Cappy - Breakfast Blend - 330ml",
        "Cans - Loose Lid", "Cans - Aerosol - Deodorant - Sanex - Dermoactive - 120ml",
        "Cans - Aerosol - Deodorant - Bond - Fantastic Collection - 75ml", "Cigarette Butts",
        "Candles - Yellow - Candles", "Candles - Yellow - Pieces", "Candles - Blue - Candles",
        "Candles - Blue - Pieces", "Candles - White - Candles", "Metal Caps - Beer / Wine - Mixed Brands",
        "Metal Caps - Champagne - Mixed Brands", "Metal Caps - Bernini", "Fishing - Sinkers",
        "Fishing - Hooks", "Fishing - Swivels", "Fishing - Fishing Line", "Shoes - Full Shoes",
        "Shoes - Soles", "Shoes - Pieces", "Electronics - Components & Pieces", "Electronics - Wires",
        "Fabric - Hats, Caps, Headwear", "Fabric - Cloth & Rags (Unspecified)", "Fabric - Bags",
        "Fabric - Umbrella", "Fabric - Straps (Dog Collar)", "Fabric - Socks, Underwear & Face Mask",
        "Fabric - Shirts", "Fabric - Vests", "Fabric - Trousers", "Nappies - Full"
    ),
    "Organic" to listOf(
        "Bones", "Cuttlefish", "Sand, Sticks & Leaves (Possibly Contaminated with Plastic)"
    ),
    "Polystyrene" to listOf(
        "Appliances - Full", "Appliance - Pieces", "Cups - Full", "Cups - Pieces",
        "Takeaway - Double - Full", "Takeaway - Single - Full", "Takeaway - Pieces",
        "Food Tray - Black - Full", "Food Tray - Black - Pieces", "Food Tray - Blue - Pieces",
        "Rigid PS - Cup", "PS Lid Inserts", "Mixed Foam & PS"
    ),
    "Rigid Plastic" to listOf(
        "Sucker Sticks - White", "Sucker Sticks - Red", "Sucker Sticks - Yellow", "Sucker Sticks - Black",
        "Sucker Sticks - Green", "Sucker Sticks - Orange", "Sucker Sticks - Blue", "Sucker Sticks - Purple",
        "Sucker Sticks - Brown", "Sucker Sticks - Lime", "Earbud Sticks - White", "Earbud Sticks - Pink",
        "Earbud Sticks - Yellow", "Earbud Sticks - Blue", "Earbud Sticks - Green", "Straws - Clear",
        "Straws - Blue", "Straws - Yellow", "Straws - Pink", "Straws - Black", "Straws - White",
        "Straws - Red", "Take-Away Spoons - Small - White", "Take-Away Spoons - Small - Orange",
        "Take-Away Spoons - Medium - White", "Take-Away Spoons - Large - White", "Bottle Lids - Mixed",
        "Bottle Lids - Red", "Bottle Lids - Blue", "Bottle Lids - Green", "Bottle Lids - Black",
        "Bottle Lids - White", "Bottle Lids - Yellow", "Bottle Lids - Purple", "Bottle Lids - Orange",
        "Pens - Blue", "Pens - Red", "Pens - Black", "Pens - Lids", "Pens - Pieces",
        "Electrical Wire - Pieces", "Unidentified Plastic Pieces & Items", "Blister Pack",
        "Plastic Box Strapping", "Plastic - Margarine Tubs - Stork / Flora / Rama",
        "Plastic - Mixed Bottles - Small - Unbranded", "Plastic - Mixed Bottles - Medium - Unbranded",
        "Plastic - Mixed - Mini Yoghurt Tubs", "Plastic - Vaseline Containers", "Plastic - Aromat Container",
        "Plastic - Takeaway Cups - Milkylane, Unbranded", "Plastic - Orange Grove - Fresh Cream",
        "Plastic - Takeaway Sauce Containerss", "Plastic - Sta Soft", "Plastic - Domestos",
        "Plastic - Milk Bottle - 2 litre", "Plastic - Traditional Medicine Bottle - Imbiza"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLoggingMenuScreen() {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

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
            Crossfade(targetState = selectedCategory, label = "CategoryCrossfade") { category ->
                if (category == null) {
                    MainCategoryGrid(onCategorySelected = { selectedCategory = it })
                } else {
                    SubCategoryList(
                        categoryName = category,
                        onBack = { selectedCategory = null }
                    )
                }
            }
        }
    }
}

@Composable
fun MainCategoryGrid(onCategorySelected: (String) -> Unit) {
    val categories = subCategoryMap.keys.toList()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.litterboom_logo__2_),
            contentDescription = "Litterboom Logo",
            modifier = Modifier.height(60.dp).padding(vertical = 8.dp)
        )
        Text("Welcome back, Username!", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Not you? Logout",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 16.dp)
        )
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                Button(
                    onClick = { onCategorySelected(category) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(60.dp)
                ) {
                    Text(text = category)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryList(categoryName: String, onBack: () -> Unit) {
    val items = subCategoryMap[categoryName] ?: listOf()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log: $categoryName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Button(
                    onClick = { /* Handle sub-category selection */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = item)
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