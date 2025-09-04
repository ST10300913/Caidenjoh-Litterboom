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
fun AppWithNavDrawer() { //main page

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            AppDrawerContent {
                scope.launch { drawerState.close() }
            }
        }
    ) {

        LoginScreenWithSwipeableSheet(
            onMenuClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenWithSwipeableSheet(onMenuClick: () -> Unit) { //swipeable sheet for login page
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
                onLoginClick = {
                    scope.launch { sheetState.expand() }
                }
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
fun AppDrawerContent(onItemClick: () -> Unit) { //hamburger menu drawer for app
    val navItems = listOf(
        "Source to Sea", "Interception", "Education", "Innovation",
        "Our Story", "The Team", "Events", "Contact"
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
                        onItemClick()
                    } else {
                        // For other items, we still close the drawer but will add navigation later.
                        onItemClick()
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
fun LoginSheetContent(isExpanded: Boolean, onLoginClick: () -> Unit) { //login sheet components
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

        if (!isExpanded) { //if sheet is not expanded, show login button
            Text(
                "Login",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onLoginClick() })
                }
            )
        } else {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var isPasswordVisible by remember { mutableStateOf(false) }
            var rememberMe by remember { mutableStateOf(false) }

            Text("Login", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onSurface)
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
                    val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(image, "Toggle password visibility")
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Remember Me", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Login Logic */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOGIN", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
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