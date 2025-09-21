package com.example.litterboom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.litterboom.data.AppDatabase
import com.example.litterboom.data.Event
import com.example.litterboom.data.User
import com.example.litterboom.ui.theme.LitterboomTheme
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LitterboomTheme {
                AppWithNavDrawer()
            }
        }
    }
}

@Composable
fun AppWithNavDrawer() { //hamburger menu navigation
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("Source to Sea") }
    var loggedIn by rememberSaveable { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent { selectedItem ->
                scope.launch { drawerState.close() }
                currentScreen = selectedItem
            }
        }
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenCrossfade") { screen ->
            when (screen) {
                "Source to Sea" -> LoginScreenWithSwipeableSheet(
                    loggedIn = loggedIn,
                    onLoginChange = { loggedIn = it },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
                "Admin Panel" -> AdminPanelScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    navigateTo = { newScreen -> currentScreen = newScreen }
                )
                "Add User" -> AddUserScreen(
                    onBackClick = { currentScreen = "Admin Panel" }
                )
                "Create Event" -> CreateEventScreen(
                    onEventCreated = { currentScreen = "Event List" },
                    onBackClick = { currentScreen = "Admin Panel" }
                )
                "Event List" -> EventListScreen(
                    onBackClick = { currentScreen = "Admin Panel" }
                )
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = screen, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPanelScreen(onMenuClick: () -> Unit, navigateTo: (String) -> Unit) { //Central Menu Screen with icons for admin functionalities
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Admin Panel", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            AdminMenu(
                onAddUserClick = { navigateTo("Add User") },
                onCreateEventClick = { navigateTo("Create Event") },
                onEventListClick = { navigateTo("Event List") }
            )
        }
    }
}

@Composable
fun AdminMenu(onAddUserClick: () -> Unit, onCreateEventClick: () -> Unit, onEventListClick: () -> Unit) { //Menu with icons
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AdminIconButton(text = "Add User", icon = Icons.Default.PersonAdd, onClick = onAddUserClick)
            AdminIconButton(text = "Create Event", icon = Icons.Default.Event, onClick = onCreateEventClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        AdminIconButton(text = "Event List", icon = Icons.AutoMirrored.Filled.ViewList, onClick = onEventListClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(onBackClick: () -> Unit) { //Screen for adding users
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }

    fun refreshUsers() {
        scope.launch {
            users = AppDatabase.getDatabase(context).userDao().getAllUsers()
        }
    }

    LaunchedEffect(Unit) {
        refreshUsers()
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Add User", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = roleMenuExpanded, onExpandedChange = { roleMenuExpanded = !roleMenuExpanded }) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f))
                )
                ExposedDropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("User") }, onClick = { selectedRole = "User"; roleMenuExpanded = false })
                    DropdownMenuItem(text = { Text("Admin") }, onClick = { selectedRole = "Admin"; roleMenuExpanded = false })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            AppDatabase.getDatabase(context).userDao().insertUser(User(username = username, password = password, role = selectedRole))
                            username = ""
                            password = ""
                            successMessage = "User added successfully!"
                            refreshUsers()
                        } else {
                            successMessage = "Please fill all fields."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text("Add User", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            if (successMessage.isNotEmpty()) {
                Text(text = successMessage, color = Color.White, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Current Users:", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(users) { user ->
                    Text(text = "${user.id}: ${user.username} (${user.role})", style = MaterialTheme.typography.bodyLarge, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                    Divider(color = Color.White.copy(alpha = 0.3f))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIconButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) { //Icons and cards for admin functionality buttons
    Card(
        onClick = onClick,
        modifier = Modifier.size(150.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}


/*@Composable
fun AdminControlPanelScreen(onMenuClick: () -> Unit, onItemClick: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }

    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        users = db.userDao().getAllUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Admin Panel", style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Role: ", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(onClick = { roleMenuExpanded = true }) {
                    Text(selectedRole)
                }
                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    listOf("User", "Admin").forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole = role
                                roleMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        val db = AppDatabase.getDatabase(context)
                        db.userDao().insertUser(
                            User(
                                username = username,
                                password = password,
                                role = selectedRole
                            )
                        )
                        username = ""
                        password = ""
                        selectedRole = "User"
                        successMessage = "User added successfully!"

                        users = db.userDao().getAllUsers()
                    } else {
                        successMessage = "Please enter both username and password."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Add User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Current Users:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(users) { user ->
                Text(
                    text = "${user.id}: ${user.username} (${user.role})",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Divider(color = Color.Gray.copy(alpha = 0.3f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onItemClick("Create Event") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Create Event")
        }

    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenWithSwipeableSheet(loggedIn: Boolean, onLoginChange: (Boolean) -> Unit, onMenuClick: () -> Unit) { //swipeable sheet for login page
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()
    val isSheetExpanding = sheetState.targetValue == SheetValue.Expanded
    val isSheetFullyExpanded = sheetState.currentValue == SheetValue.Expanded

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 90.dp,
        sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        sheetShadowElevation = 8.dp,
        sheetDragHandle = null,
        sheetContent = {
            LoginSheetContent(
                isExpanded = isSheetFullyExpanded,
                loggedIn = loggedIn,
                onLoginClick = {
                    scope.launch { sheetState.expand() }},
                onLoginSuccess = { onLoginChange(true) }

            )
        },
        topBar = { //top bar for app

            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.litterboom_logo__2_),
                        contentDescription = "Home Logo",
                        modifier = Modifier.height(40.dp)
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                    )
                )
        ) {
            AnimatedVisibility(visible = !isSheetExpanding, enter = fadeIn(), exit = fadeOut()) {
                CollapsedStateContent()
            }
            AnimatedVisibility(visible = isSheetExpanding, enter = fadeIn(), exit = fadeOut()) {
                ExpandedStateContent()
            }
        }
    }
}

@Composable
fun AppDrawerContent(onItemClick: (String) -> Unit) { //hamburger menu drawer for app
    val navItems = listOf(
        "Source to Sea", "Interception", "Education", "Innovation",
        "Our Story", "The Team", "Contact", "Admin Panel"
    )

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = MaterialTheme.colorScheme.primary
    ) {
        Spacer(Modifier.height(16.dp))
        navItems.forEach { item -> //loop through nav items
            val isSelected = item == "Source to Sea"
            NavigationDrawerItem(
                label = { Text(item, style = MaterialTheme.typography.bodyLarge) },
                selected = isSelected,
                onClick = {
                    if (item == "Source to Sea") {
                        // This is the main activity, so we just close the drawer.
                        onItemClick(item)
                    } else {
                        // For other items, we still close the drawer but will add navigation later.
                        onItemClick(item)
                        // Add navigation logic here to go to the correct screen
                    }

                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors( //styling for nav items
                    selectedTextColor = Color.White,
                    selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White,
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun LoginSheetContent(isExpanded: Boolean, loggedIn: Boolean, onLoginClick: () -> Unit, onLoginSuccess: () -> Unit) { //login sheet content

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var loginMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val headerText = if (loggedIn) "Logged In" else "Login"

        if (!isExpanded) {
            Text(
                headerText,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onLoginClick() })
                }
            )
        } else {
            if (!loggedIn) {
                Text(
                    "Login",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image =
                            if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(image, "Toggle password visibility")
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                    Text(
                        "Remember Me",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (!isExpanded) {
                    Text(
                        "Login",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onTap = { onLoginClick() })
                        }
                    )
                } else {
                    if (!loggedIn) { //login button
                        Button(
                            onClick = {
                                scope.launch {
                                    if (username.isNotBlank() && password.isNotBlank()) {
                                        val db = AppDatabase.getDatabase(context)
                                        val user = db.userDao().getUser(username, password)
                                        if (user != null) {
                                            loginMessage = "Login successful!"
                                            onLoginSuccess()
                                            val intent = Intent(context, WasteWorkerActivity::class.java) //change to waste worker activity
                                            context.startActivity(intent)
                                        } else {
                                            loginMessage = "Invalid username or password."
                                        }
                                    } else {
                                        loginMessage = "Enter username and password."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("LOGIN", style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    if (loginMessage.isNotEmpty()) {
                        Text(
                            text = loginMessage,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(onEventCreated: (Event) -> Unit, onBackClick: () -> Unit) { //event creation screen
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(Calendar.getInstance().time) }
    var eventLocation by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    calendar.time = eventDate

    val datePickerDialog = android.app.DatePickerDialog(context, { _, year, month, dayOfMonth ->
        calendar.set(year, month, dayOfMonth)
        eventDate = calendar.time
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding().navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Create an Event", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(value = eventName, onValueChange = { eventName = it }, label = { Text("Event Name") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f)))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = eventLocation, onValueChange = { eventLocation = it }, label = { Text("Event Location") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.9f)))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Text("Date: ${android.text.format.DateFormat.format("yyyy-MM-dd", eventDate)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    scope.launch {
                        if (eventName.isNotBlank() && eventLocation.isNotBlank()) {
                            AppDatabase.getDatabase(context).eventDao().insertEvent(Event(name = eventName, date = eventDate.time, location = eventLocation))
                            Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show()
                            onEventCreated(Event(name = eventName, date = eventDate.time, location = eventLocation))
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text("Create Event", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EventListScreen(onBackClick: () -> Unit) { //event list screen
    val context = LocalContext.current
    var events by remember { mutableStateOf(listOf<Event>()) }
    val scope = rememberCoroutineScope()
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    val db = AppDatabase.getDatabase(context)

    LaunchedEffect(Unit) {
        events = db.eventDao().getAllEvents()
    }

    fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(context, { _, year, month, day ->
            val cal = Calendar.getInstance().apply { set(year, month, day) }
            if (isStartDate) startDate = cal.time else endDate = cal.time
            scope.launch {
                val allEvents = db.eventDao().getAllEvents()
                events = allEvents.filter { event ->
                    val afterStartDate = startDate?.let { event.date >= it.time } ?: true
                    val beforeEndDate = endDate?.let { event.date <= it.time } ?: true
                    afterStartDate && beforeEndDate
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

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
        Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding().navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Event List", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showDatePicker(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(startDate?.let { android.text.format.DateFormat.format("yyyy-MM-dd", it).toString() } ?: "Start Date", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = { showDatePicker(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(endDate?.let { android.text.format.DateFormat.format("yyyy-MM-dd", it).toString() } ?: "End Date", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = {
                        startDate = null
                        endDate = null
                        scope.launch { events = db.eventDao().getAllEvents() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn {
                items(events) { event ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(event.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("${android.text.format.DateFormat.format("yyyy-MM-dd", Date(event.date))} - ${event.location}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Divider(color = Color.White.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun ClickableWebsiteText(modifier: Modifier = Modifier) { //clickable text for redirect to website
    val context = LocalContext.current
    val url = "https://www.thelitterboomproject.com/"

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
            append("If you wish to support or contact us, please visit our website ")
        }
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("HERE")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        modifier = modifier,
        onClick = { offset ->

            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let {

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                    context.startActivity(intent)
                }
        }
    )
}

@Composable
fun CollapsedStateContent() { //background content when login is collapsed
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        Text(
            text = "Thank you for supporting the cause!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Join the movement to clean our rivers and oceans",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ImagePlaceholder(modifier = Modifier.size(150.dp))
                ImagePlaceholder(modifier = Modifier.size(120.dp).align(Alignment.Bottom))
            }
            ImagePlaceholder(modifier = Modifier.size(150.dp, 120.dp).offset(y = (-20).dp))
        }
        Spacer(modifier = Modifier.weight(1f))

        ClickableWebsiteText(modifier = Modifier.padding(bottom = 120.dp))
    }
}

@Composable
fun ExpandedStateContent() { //background content when login is expanded
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        Text(
            text = "Thank you for supporting the cause!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Regretfully,\nThis app is for Litterboom employees only",
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ImagePlaceholder(modifier = Modifier.size(150.dp))
            ImagePlaceholder(modifier = Modifier.size(120.dp).align(Alignment.Bottom))
        }
        Spacer(modifier = Modifier.height(16.dp))

        ClickableWebsiteText()
    }
}

@Composable
fun ImagePlaceholder(modifier: Modifier = Modifier) { //image placeholder for login screen
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.5f))
            .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Image Placeholder",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(40.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LitterboomTheme {
        AppWithNavDrawer()
    }
}