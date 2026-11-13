package com.example.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfigEntity
import com.example.data.local.BankCardEntity
import com.example.data.local.RechargeEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalEntity
import com.example.data.repository.RoyalBulletRepository
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TransparentRuleBanner
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyBorder
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RechargeScreen(
    user: UserEntity?,
    config: AppConfigEntity,
    recharges: List<RechargeEntity>,
    onBack: () -> Unit,
    onSubmitRecharge: (amount: Double, method: String, referenceId: String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("700") }
    var selectedMethod by remember { mutableStateOf("UPI") }
    var utrReference by remember { mutableStateOf("") }

    val presets = listOf(700, 1500, 2000, 3000, 5000, 10000)
    val methods = listOf("UPI", "PhonePe", "Google Pay", "QR Code", "Bank Transfer")

    Scaffold(
        topBar = { RoyalTopBar(title = s.recharge, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RoyalCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Deposit Balance", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(
                                formatInr(user?.depositBalance ?: 0.0),
                                style = MaterialTheme.typography.titleMedium,
                                color = RoyalBluePrimary
                            )
                        }

                        Text("Select or Enter Amount (₹700 – ₹1,00,000)", style = MaterialTheme.typography.labelLarge)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presets.forEach { preset ->
                                FilterChip(
                                    selected = amountText == preset.toString(),
                                    onClick = { amountText = preset.toString() },
                                    label = { Text("₹$preset") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalBluePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Custom Recharge Amount (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recharge_amount_input")
                        )

                        Text("Payment Channel", style = MaterialTheme.typography.labelLarge)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            methods.forEach { method ->
                                FilterChip(
                                    selected = selectedMethod == method,
                                    onClick = { selectedMethod = method },
                                    label = { Text(method) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalBlueDeep,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Payment destination details
                        Surface(
                            color = RoyalSkyLight,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, RoyalSkyBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (selectedMethod == "Bank Transfer") {
                                    Text("Official Bank Transfer Details", style = MaterialTheme.typography.labelLarge, color = RoyalBlueDeep)
                                    Text("Beneficiary: ${config.bankBeneficiaryName}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Account No: ${config.bankAccountNumber}", style = MaterialTheme.typography.bodyMedium)
                                    Text("IFSC: ${config.bankIfscCode}", style = MaterialTheme.typography.bodyMedium)
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Pay via $selectedMethod", style = MaterialTheme.typography.labelLarge, color = RoyalBlueDeep)
                                            Text("Merchant UPI: ${config.upiMerchantId}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        IconButton(
                                            onClick = {
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                cm.setPrimaryClip(ClipData.newPlainText("UPI ID", config.upiMerchantId))
                                                onShowMessage("Merchant UPI ID copied.")
                                            }
                                        ) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy UPI", tint = RoyalBluePrimary)
                                        }
                                    }
                                    if (selectedMethod == "QR Code") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            DeterministicQrPreview(seed = "${config.upiMerchantId}_${amountText}")
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = utrReference,
                            onValueChange = { utrReference = it.uppercase() },
                            label = { Text("UTR / Payment Gateway Reference ID") },
                            placeholder = { Text("e.g. UTR4128990312") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recharge_utr_input")
                        )

                        TransparentRuleBanner(
                            "Security Notice: Wallet deposits are never credited automatically from screenshots. All requests stay Pending until verified via server/gateway UTR records."
                        )

                        BlueGradientButton(
                            text = "Submit for Verification",
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                onSubmitRecharge(amt, selectedMethod, utrReference)
                                utrReference = ""
                            },
                            testTag = "recharge_submit_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Text("Recharge History", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            if (recharges.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Filled.Wallet,
                        title = "No Recharges Yet",
                        subtitle = "Verified deposits appear here with their live Pending / Successful / Failed status."
                    )
                }
            } else {
                items(recharges, key = { it.id }) { item ->
                    RoyalCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${formatInr(item.amount)} • ${item.method}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Ref: ${item.referenceId}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                                Text(
                                    formatTimestamp(item.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                            StatusPill(item.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeterministicQrPreview(seed: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(136.dp)
                .border(1.dp, RoyalSkyBorder, RoundedCornerShape(12.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val grid = 11
                val cell = size.width / grid
                val hash = seed.hashCode()
                for (r in 0 until grid) {
                    for (c in 0 until grid) {
                        val isFinder = (r < 3 && c < 3) || (r < 3 && c >= grid - 3) || (r >= grid - 3 && c < 3)
                        val bit = ((hash ushr ((r * 3 + c) % 31)) and 1) == 1 || ((r + c) % 2 == 0 && (r * c + hash) % 3 == 0)
                        if (isFinder || bit) {
                            drawRect(
                                color = RoyalBlueDeep,
                                topLeft = Offset(c * cell, r * cell),
                                size = Size(cell * 0.9f, cell * 0.9f)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.QrCode2, contentDescription = null, tint = RoyalBluePrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Scan & enter UTR above after payment", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun WithdrawalScreen(
    user: UserEntity?,
    bankCards: List<BankCardEntity>,
    withdrawals: List<WithdrawalEntity>,
    onBack: () -> Unit,
    onOpenBankCards: () -> Unit,
    onSubmitWithdrawal: (
        amount: Double,
        payoutMethod: String,
        upiId: String,
        bankAccount: String,
        holderName: String,
        ifsc: String,
        withdrawPassword: String
    ) -> Unit
) {
    val s = LocalStrings.current
    var amountText by remember { mutableStateOf("") }
    var payoutMethod by remember { mutableStateOf("UPI") }
    var upiId by remember { mutableStateOf(bankCards.firstOrNull()?.upiId ?: "") }
    var bankAccount by remember { mutableStateOf(bankCards.firstOrNull()?.accountNumber ?: "") }
    var holderName by remember { mutableStateOf(bankCards.firstOrNull()?.accountHolderName ?: "") }
    var ifsc by remember { mutableStateOf(bankCards.firstOrNull()?.ifscCode ?: "") }
    var withdrawPassword by remember { mutableStateOf("") }

    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
    val fee = (parsedAmount * RoyalBulletRepository.WITHDRAWAL_FEE_PERCENT * 100).toInt() / 100.0
    val net = (parsedAmount - fee).coerceAtLeast(0.0)

    Scaffold(
        topBar = { RoyalTopBar(title = s.withdraw, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RoyalCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Eligible Available Balance", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Text(
                                    formatInr(user?.availableBalance ?: 0.0),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = RoyalBluePrimary
                                )
                            }
                            OutlinedButton(onClick = onOpenBankCards) {
                                Text("Saved Cards (${bankCards.size})")
                            }
                        }

                        Text(
                            "Promotional (${formatInr(user?.promotionalBalance ?: 0.0)}) & Deposit (${formatInr(user?.depositBalance ?: 0.0)}) balances are not withdrawable.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = TextMuted
                        )

                        if (bankCards.isNotEmpty()) {
                            Text("Auto-fill from Saved Bank Card:", style = MaterialTheme.typography.labelMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                bankCards.take(2).forEach { card ->
                                    FilterChip(
                                        selected = holderName == card.accountHolderName,
                                        onClick = {
                                            holderName = card.accountHolderName
                                            upiId = card.upiId
                                            bankAccount = card.accountNumber
                                            ifsc = card.ifscCode
                                        },
                                        label = { Text(card.accountHolderName) }
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(
                                selected = payoutMethod == "UPI",
                                onClick = { payoutMethod = "UPI" },
                                label = { Text("UPI Payout") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = payoutMethod == "Bank",
                                onClick = { payoutMethod = "Bank" },
                                label = { Text("Bank Transfer (IMPS/NEFT)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Withdrawal Amount (Min ₹150)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input")
                        )

                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text("Account Holder Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (payoutMethod == "UPI") {
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("UPI ID (e.g. rider@okaxis)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_upi_input")
                            )
                        } else {
                            OutlinedTextField(
                                value = bankAccount,
                                onValueChange = { bankAccount = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Bank Account Number") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = ifsc,
                                onValueChange = { ifsc = it.uppercase() },
                                label = { Text("IFSC Code") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = withdrawPassword,
                            onValueChange = { withdrawPassword = it },
                            label = { Text("Withdrawal Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_password_input")
                        )

                        Surface(
                            color = RoyalSkyLight,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Fee (2%): ${formatInr(fee)} • Estimated Net Payout: ${formatInr(net)}", style = MaterialTheme.typography.labelLarge, color = RoyalBlueDeep)
                                Text("Daily Limit: ₹150 – ₹50,000 • Processing: 24–48 business hours after verification.", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                            }
                        }

                        BlueGradientButton(
                            text = "Submit Withdrawal Request",
                            onClick = {
                                onSubmitWithdrawal(
                                    parsedAmount, payoutMethod, upiId, bankAccount,
                                    holderName, ifsc, withdrawPassword
                                )
                                withdrawPassword = ""
                            },
                            testTag = "withdraw_submit_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Text("Withdrawal History", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            if (withdrawals.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Filled.AccountBalance,
                        title = "No Withdrawal Requests",
                        subtitle = "Eligible verified earnings (min ₹150) can be withdrawn to UPI or Bank Account."
                    )
                }
            } else {
                items(withdrawals, key = { it.id }) { item ->
                    RoyalCard {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${formatInr(item.amount)} (${item.payoutMethod})", style = MaterialTheme.typography.titleMedium)
                                StatusPill(item.status)
                            }
                            Text(
                                "Net: ${formatInr(item.netAmount)} • Fee: ${formatInr(item.feeAmount)} • ${item.accountHolderName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextSecondary
                            )
                            Text(
                                formatTimestamp(item.createdAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BankCardScreen(
    bankCards: List<BankCardEntity>,
    onBack: () -> Unit,
    onSaveCard: (holder: String, bank: String, account: String, ifsc: String, upi: String) -> Unit,
    onDeleteCard: (String) -> Unit
) {
    val s = LocalStrings.current
    var holder by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var upi by remember { mutableStateOf("") }

    Scaffold(
        topBar = { RoyalTopBar(title = s.bankCard, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RoyalCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Add Bank Account / UPI", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = holder,
                            onValueChange = { holder = it },
                            label = { Text("Account Holder Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name (optional for UPI)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = account,
                            onValueChange = { account = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Account Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = ifsc,
                            onValueChange = { ifsc = it.uppercase() },
                            label = { Text("IFSC Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = upi,
                            onValueChange = { upi = it },
                            label = { Text("UPI ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        BlueGradientButton(
                            text = "Save Bank / UPI Card",
                            onClick = {
                                onSaveCard(holder, bankName, account, ifsc, upi)
                                holder = ""; bankName = ""; account = ""; ifsc = ""; upi = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            items(bankCards, key = { it.id }) { card ->
                RoyalCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(card.accountHolderName, style = MaterialTheme.typography.titleMedium)
                            if (card.accountNumber.isNotBlank()) {
                                Text(
                                    "${card.bankName.ifBlank { "Bank" }} • ${card.accountNumber} (${card.ifscCode})",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                            if (card.upiId.isNotBlank()) {
                                Text("UPI: ${card.upiId}", style = MaterialTheme.typography.labelMedium, color = RoyalBluePrimary)
                            }
                        }
                        IconButton(onClick = { onDeleteCard(card.id) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = ErrorRed)
                        }
                    }
                }
            }
        }
    }
}
