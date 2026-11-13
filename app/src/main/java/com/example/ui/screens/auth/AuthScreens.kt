package com.example.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.RoyalBulletLogoBadge
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.CardWhite
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueBright
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyBorder
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthScreen(
    isBusy: Boolean,
    currentLanguage: AppLanguage,
    initialInviteCode: String = "",
    onToggleLanguage: () -> Unit,
    onLogin: (mobile: String, password: String) -> Unit,
    onRegister: (mobile: String, loginPass: String, withdrawPass: String, inviteCode: String, termsAccepted: Boolean) -> Unit,
    onResetPassword: (mobile: String, withdrawPass: String, newPass: String, onDone: () -> Unit) -> Unit,
    onOpenTerms: () -> Unit
) {
    val s = LocalStrings.current
    var isRegisterTab by remember { mutableStateOf(true) }

    var mobile by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var withdrawPassword by remember { mutableStateOf("") }
    var inviteCode by remember(initialInviteCode) { mutableStateOf(initialInviteCode) }
    var termsAccepted by remember { mutableStateOf(true) }
    var showForgotDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE3EEFF), IceBackground, Color(0xFFF9FBFF))
                )
            )
    ) {
        // Subtle architectural wave lines matching Screenshot 3
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val w = size.width
            val h = size.height
            for (i in 0..4) {
                val offset = i * 28f
                val path = Path().apply {
                    moveTo(0f, h * 0.45f + offset)
                    cubicTo(
                        w * 0.3f, h * 0.25f + offset,
                        w * 0.7f, h * 0.7f + offset,
                        w, h * 0.35f + offset
                    )
                }
                drawPath(
                    path = path,
                    color = RoyalBlueBright.copy(alpha = 0.08f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Language Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clickable(onClick = onToggleLanguage)
                        .testTag("auth_language_toggle")
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "EN / हिंदी" else "हिंदी / EN",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBluePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                RoyalBulletLogoBadge(size = 78.dp, showLabel = true)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isRegisterTab) s.register else s.login,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Centered Login | Register Tab Switcher (matching Screenshot 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AuthTabTitle(
                        title = s.login,
                        selected = !isRegisterTab,
                        testTag = "auth_tab_login",
                        onClick = { isRegisterTab = false }
                    )
                    Spacer(modifier = Modifier.width(42.dp))
                    AuthTabTitle(
                        title = s.register,
                        selected = isRegisterTab,
                        testTag = "auth_tab_register",
                        onClick = { isRegisterTab = true }
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Mobile Number (+91)
                AuthFieldLabel(s.mobileNumber)
                AuthPillInput(
                    value = mobile,
                    onValueChange = { if (it.length <= 10) mobile = it.filter { ch -> ch.isDigit() } },
                    placeholder = s.enterPhoneHint,
                    leadingIcon = Icons.Filled.PhoneAndroid,
                    prefixText = "+91",
                    keyboardType = KeyboardType.Phone,
                    testTag = "auth_mobile_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Login Password
                AuthFieldLabel(s.loginPassword)
                AuthPillInput(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    placeholder = s.enterPasswordHint,
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    testTag = "auth_password_input"
                )

                if (isRegisterTab) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AuthFieldLabel(s.withdrawalPassword)
                    AuthPillInput(
                        value = withdrawPassword,
                        onValueChange = { withdrawPassword = it },
                        placeholder = s.enterWithdrawPassHint,
                        leadingIcon = Icons.Filled.Security,
                        isPassword = true,
                        testTag = "auth_withdraw_password_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    AuthFieldLabel(s.invitationCode)
                    AuthPillInput(
                        value = inviteCode,
                        onValueChange = { inviteCode = it.uppercase() },
                        placeholder = s.enterInviteHint,
                        leadingIcon = Icons.Filled.MailOutline,
                        showClearButton = inviteCode.isNotEmpty(),
                        onClear = { inviteCode = "" },
                        testTag = "auth_invite_code_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBluePrimary),
                            modifier = Modifier.testTag("auth_terms_checkbox")
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(s.agreeTermsPrefix)
                                withStyle(
                                    SpanStyle(
                                        color = RoyalBluePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append(s.termsAndConditions)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.clickable(onClick = onOpenTerms)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showForgotDialog = true },
                            modifier = Modifier.testTag("auth_forgot_password_button")
                        ) {
                            Text(s.forgotPassword, color = RoyalBluePrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isBusy) {
                    CircularProgressIndicator(color = RoyalBluePrimary)
                } else {
                    BlueGradientButton(
                        text = if (isRegisterTab) s.register else s.login,
                        onClick = {
                            if (isRegisterTab) {
                                onRegister(
                                    mobile,
                                    loginPassword,
                                    withdrawPassword,
                                    inviteCode,
                                    termsAccepted
                                )
                            } else {
                                onLogin(mobile, loginPassword)
                            }
                        },
                        testTag = if (isRegisterTab) "auth_register_submit" else "auth_login_submit",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = if (isRegisterTab) s.alreadyHaveAccount else s.dontHaveAccount,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = if (isRegisterTab) s.login else s.registerNow,
                        style = MaterialTheme.typography.labelLarge,
                        color = RoyalBluePrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isRegisterTab = !isRegisterTab }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSubmit = { m, wp, np ->
                onResetPassword(m, wp, np) { showForgotDialog = false }
            }
        )
    }
}

@Composable
private fun AuthTabTitle(
    title: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) RoyalBlueDeep else TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) RoyalBluePrimary else Color.Transparent)
        )
    }
}

@Composable
private fun AuthFieldLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = TextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
private fun AuthPillInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    prefixText: String? = null,
    isPassword: Boolean = false,
    showClearButton: Boolean = false,
    onClear: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    testTag: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = CardWhite,
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(1.dp, RoyalSkyBorder, RoundedCornerShape(28.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(RoyalSkyLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = RoyalBluePrimary,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (prefixText != null) {
                Text(
                    text = prefixText,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(22.dp)
                        .background(RoyalSkyBorder)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    visualTransformation = if (isPassword && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPassword) KeyboardType.Password else keyboardType
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
                )
            }

            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = TextMuted
                    )
                }
            } else if (showClearButton) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (mobile: String, withdrawPass: String, newLoginPass: String) -> Unit
) {
    var mobile by remember { mutableStateOf("") }
    var withdrawPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Login Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Verify your registered mobile number and withdrawal password to set a new login password.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it.filter { c -> c.isDigit() }.take(10) },
                    label = { Text("+91 Mobile Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(
                    value = withdrawPass,
                    onValueChange = { withdrawPass = it },
                    label = { Text("Withdrawal Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Login Password (min 6 chars)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(mobile, withdrawPass, newPass) }) {
                Text("Update Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
