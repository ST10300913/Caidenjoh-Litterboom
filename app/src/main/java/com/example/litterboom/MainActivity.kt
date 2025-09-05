package com.example.litterboom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.litterboom.ui.theme.LitterboomTheme
import kotlinx.coroutines.launch
import com.example.litterboom.data.AppDatabase
import com.example.litterboom.data.User
import com.example.litterboom.data.Event
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import android.widget.Toast
import java.util.Date
import java.util.Calendar
import androidx.compose.material3.Button

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LitterboomTheme {
                AppWithNavDrawer() //wraps page in drawer for navigation
            }
        }
    }
}

@Composable
fun AppWithNavDrawer() { //main screen
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
        //Show different content based on selected item
        when (currentScreen) {
            "Source to Sea" -> {
                LoginScreenWithSwipeableSheet(
                    loggedIn = loggedIn,
                    onLoginChange = { loggedIn = it },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
            "Admin Panel" -> {
                AdminControlPanelScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onItemClick = { currentScreen = it }
                )
            }
            "Create Event" -> {
                CreateEventScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onEventCreated = { event -> currentScreen = "Event List"
                    }
                )
            }
            "Event List" -> {
                EventListScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onItemClick = { currentScreen = it }
                )
            }

            else -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = currentScreen, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
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
}

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
        "Our Story", "The Team", "Event List", "Contact", "Admin Panel"
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
fun LoginSheetContent(isExpanded: Boolean, loggedIn: Boolean, onLoginClick: () -> Unit, onLoginSuccess: () -> Unit) {

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
                    if (!loggedIn) {
                        Button(
                            onClick = {
                                scope.launch {
                                    if (username.isNotBlank() && password.isNotBlank()) {
                                        val db = AppDatabase.getDatabase(context)
                                        val user = db.userDao().getUser(username, password)
                                        if (user != null) {
                                            loginMessage = "Login successful!"
                                            onLoginSuccess()
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

@Composable
fun CreateEventScreen(onMenuClick: () -> Unit, onEventCreated: (Event) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(java.util.Calendar.getInstance().time) }
    var eventLocation by remember { mutableStateOf("") }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            eventDate = calendar.time
        },
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
        java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
    )

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
            Text("Creating an Event", style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
            Text("Select Event Date: ${android.text.format.DateFormat.format("yyyy-MM-dd", eventDate)}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = eventLocation,
            onValueChange = { eventLocation = it },
            label = { Text("Event Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val db = AppDatabase.getDatabase(context)
                scope.launch {
                    if (eventName.isNotBlank() && eventLocation.isNotBlank()) {
                        db.eventDao().insertEvent(
                            Event(
                                name = eventName,
                                date = eventDate.time,
                                location = eventLocation
                            )
                        )
                        Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show()
                        eventName = ""
                        eventLocation = ""
                        onEventCreated(
                            Event(name = eventName, date = eventDate.time, location = eventLocation)
                        )
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Event")
        }
    }
}

@Composable
fun EventListScreen(onMenuClick: () -> Unit, onItemClick: (String) -> Unit) {
    val context = LocalContext.current
    var events by remember { mutableStateOf(listOf<Event>()) }
    val scope = rememberCoroutineScope()

    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    val db = AppDatabase.getDatabase(context)

    LaunchedEffect(Unit) {
        events = db.eventDao().getAllEvents()
    }

    val startDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            startDate = cal.time
            // Filter events after picking start date
            scope.launch {
                events = db.eventDao().getAllEvents().filter { event ->
                    startDate?.let { event.date >= it.time } ?: true
                }
            }
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val endDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            endDate = cal.time
            // Filter events after picking end date
            scope.launch {
                events = db.eventDao().getAllEvents().filter { event ->
                    endDate?.let { event.date <= it.time } ?: true
                }
            }
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Event List", style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { startDatePicker.show() }) {
                Text(
                    startDate?.let { android.text.format.DateFormat.format("yyyy-MM-dd", it).toString() }
                        ?: "Start Date"
                )
            }

            Button(onClick = { endDatePicker.show() }) {
                Text(
                    endDate?.let { android.text.format.DateFormat.format("yyyy-MM-dd", it).toString() }
                        ?: "End Date"
                )
            }

            Button(onClick = {
                // Reset filters
                startDate = null
                endDate = null
                scope.launch {
                    events = db.eventDao().getAllEvents()
                }
            }) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(events) { event ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(event.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${android.text.format.DateFormat.format("yyyy-MM-dd", Date(event.date))} - ${event.location}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                }
            }
        }
    }
}

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