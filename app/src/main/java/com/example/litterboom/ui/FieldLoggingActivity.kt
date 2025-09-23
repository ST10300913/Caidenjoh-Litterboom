package com.example.litterboom.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.litterboom.data.AppDatabase
import com.example.litterboom.data.LoggingField
import com.example.litterboom.ui.theme.LitterboomTheme
import kotlinx.coroutines.launch
import java.io.Serializable

class FieldLoggingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val subCategoryId = intent.getIntExtra("SUB_CATEGORY_ID", -1)
        val subCategoryName = intent.getStringExtra("SUB_CATEGORY_NAME") ?: "Item"
        val mainCategoryName = intent.getStringExtra("MAIN_CATEGORY_NAME") ?: "Waste"

        setContent {
            LitterboomTheme {
                FieldLoggingScreen(subCategoryId, subCategoryName, mainCategoryName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldLoggingScreen(subCategoryId: Int, subCategoryName: String, mainCategoryName: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var requiredFields by remember { mutableStateOf<List<LoggingField>>(emptyList()) }
    val fieldInputValues = remember { mutableStateMapOf<Int, String>() }

    LaunchedEffect(subCategoryId) {
        if (subCategoryId != -1) {
            val db = AppDatabase.getDatabase(context)
            requiredFields = db.wasteDao().getFieldsForSubCategory(subCategoryId)
            requiredFields.forEach { field -> fieldInputValues[field.id] = "" }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)))) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding().navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { (context as? Activity)?.finish() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Log Details for $subCategoryName", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(requiredFields) { field ->
                    OutlinedTextField(
                        value = fieldInputValues[field.id] ?: "",
                        onValueChange = { newValue -> fieldInputValues[field.id] = newValue },
                        label = { Text(field.fieldName) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f))
                    )
                }
            }

            Button(
                onClick = {
                    val activity = context as? Activity
                    val resultIntent = Intent().apply {
                        putExtra("LOGGED_CATEGORY", mainCategoryName)
                        putExtra("LOGGED_DESCRIPTION", subCategoryName)


                        val detailsMap = HashMap<String, String>()
                        requiredFields.forEach { field ->
                            val value = fieldInputValues[field.id]
                            if (!value.isNullOrBlank()) {
                                detailsMap[field.fieldName] = value
                            }
                        }
                        putExtra("LOGGED_DETAILS", detailsMap)
                    }
                    activity?.setResult(Activity.RESULT_OK, resultIntent)
                    activity?.finish()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Complete Entry", fontWeight = FontWeight.Bold)
            }
        }
    }
}