package com.example.ui.screens.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.AiUiState
import com.example.ai.GeminiAssistantViewModel
import com.example.data.local.AppConfigEntity
import com.example.data.local.CompanyNewsEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.RechargeEntity
import com.example.data.local.ReferralRecordEntity
import com.example.data.local.SupportMessageEntity
import com.example.data.local.WithdrawalEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TransparentRuleBanner
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedBg
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RecordsScreen(
    title: String,
    mode: String, // "INCOME", "WITHDRAWAL", "RECHARGE", "REFERRAL", "ALL"
    ledger: List<LedgerEntryEntity>,
    recharges: List<RechargeEntity>,
    withdrawals: List<WithdrawalEntity>,
    referrals: List<ReferralRecordEntity>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { RoyalTopBar(title = title, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (mode) {
                "RECHARGE" -> {
                    if (recharges.isEmpty()) {
                        item {
                            EmptyStateCard(Icons.Filled.ReceiptLong, "No Recharge Records", "Your recharge submissions appear here.")
                        }
                    } else {
                        items(recharges, key = { it.id }) { item ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${formatInr(item.amount)} • ${item.method}", style = MaterialTheme.typography.titleMedium)
                                        Text("UTR: ${item.referenceId}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                                        Text(formatTimestamp(item.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                    StatusPill(item.status)
                                }
                            }
                        }
                    }
                }
                "WITHDRAWAL" -> {
                    if (withdrawals.isEmpty()) {
                        item {
                            EmptyStateCard(Icons.Filled.ReceiptLong, "No Withdrawal Records", "Your withdrawal requests appear here.")
                        }
                    } else {
                        items(withdrawals, key = { it.id }) { item ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${formatInr(item.amount)} (${item.payoutMethod})", style = MaterialTheme.typography.titleMedium)
                                        Text("Net: ${formatInr(item.netAmount)} • ${item.accountHolderName}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                                        Text(formatTimestamp(item.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                    StatusPill(item.status)
                                }
                            }
                        }
                    }
                }
                "REFERRAL" -> {
                    if (referrals.isEmpty()) {
                        item {
                            EmptyStateCard(Icons.Filled.ReceiptLong, "No Referral Records", "Direct referrals & verified ₹85 rewards appear here.")
                        }
                    } else {
                        items(referrals, key = { it.id }) { item ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Member ${item.referredMobileMasked}", style = MaterialTheme.typography.titleMedium)
                                        Text("Reward: ${formatInr(item.rewardAmount)}", style = MaterialTheme.typography.bodyMedium, color = RoyalBluePrimary)
                                        Text(formatTimestamp(item.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                    StatusPill(item.status)
                                }
                            }
                        }
                    }
                }
                else -> {
                    val filtered = if (mode == "INCOME") {
                        ledger.filter { it.amount > 0 }
                    } else ledger

                    if (filtered.isEmpty()) {
                        item {
                            EmptyStateCard(Icons.Filled.ReceiptLong, "No Ledger Records Yet", "Verified credits and transactions are recorded here.")
                        }
                    } else {
                        items(filtered, key = { it.id }) { entry ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(entry.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp))
                                        Text(entry.description, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                                        Text("${entry.balanceBucket} • ${formatTimestamp(entry.createdAt)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                    Text(
                                        text = if (entry.amount >= 0) "+${formatInr(entry.amount)}" else formatInr(entry.amount),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (entry.amount >= 0) SuccessGreen else ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyNewsScreen(
    news: List<CompanyNewsEntity>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { RoyalTopBar(title = "Company News", onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        if (news.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                EmptyStateCard(Icons.Filled.Campaign, "No Announcements", "Official Royal Bullet 350 news will appear here.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(news, key = { it.id }) { item ->
                    RoyalCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (item.isPinned) {
                                Surface(color = RoyalSkyLight, shape = RoundedCornerShape(50)) {
                                    Text(
                                        "PINNED ANNOUNCEMENT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoyalBluePrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(item.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(item.body, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(formatTimestamp(item.publishedAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerSupportScreen(
    config: AppConfigEntity,
    messages: List<SupportMessageEntity>,
    onBack: () -> Unit,
    onSubmitTicket: (subject: String, message: String) -> Unit,
    aiViewModel: GeminiAssistantViewModel = viewModel()
) {
    var aiPrompt by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val aiState by aiViewModel.uiState.collectAsStateWithLifecycle()
    val aiMessages by aiViewModel.messages.collectAsStateWithLifecycle()
    val quickPrompts = remember {
        listOf(
            "Compare Bullet 350 & Hunter 350",
            "Explain ₹700 plan & VIP Cars",
            "Withdrawal & ₹120 bonus rules",
            "Bullet 350 servicing tips"
        )
    }

    Scaffold(
        topBar = { RoyalTopBar(title = "AI Rider Assistant & Support", onBack = onBack) },
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "Gemini AI Assistant",
                                    tint = RoyalBluePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Royal Bullet 350 AI Assistant",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlueDeep
                                )
                            }
                            if (aiMessages.isNotEmpty()) {
                                OutlinedButton(onClick = { aiViewModel.clearConversation() }) {
                                    Text("Clear")
                                }
                            }
                        }

                        Text(
                            text = "Powered by Firebase AI Logic (Gemini) • Ask in English or हिंदी",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(quickPrompts) { prompt ->
                                AssistChip(
                                    onClick = { aiViewModel.askAssistant(prompt) },
                                    label = { Text(prompt, fontSize = 12.sp) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = RoyalSkyLight,
                                        labelColor = RoyalBlueDeep
                                    )
                                )
                            }
                        }

                        aiMessages.forEach { msg ->
                            Surface(
                                color = if (msg.isUser) RoyalBluePrimary else RoyalSkyLight,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (msg.isUser) "You: ${msg.text}" else msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isUser) Color.White else TextPrimary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        when (val state = aiState) {
                            is AiUiState.Loading -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = RoyalBluePrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Gemini is thinking...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                            is AiUiState.Streaming -> {
                                Surface(
                                    color = RoyalSkyLight,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = state.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            is AiUiState.Error -> {
                                Surface(
                                    color = ErrorRedBg,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.ErrorOutline,
                                            contentDescription = null,
                                            tint = ErrorRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = state.message,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
                                            color = ErrorRed
                                        )
                                    }
                                }
                            }
                            else -> Unit
                        }

                        OutlinedTextField(
                            value = aiPrompt,
                            onValueChange = { aiPrompt = it },
                            label = { Text("Ask Gemini about bikes, VIP cars, or wallet...") },
                            minLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gemini_prompt_input")
                        )

                        BlueGradientButton(
                            text = "Ask Gemini AI",
                            enabled = aiPrompt.isNotBlank() && aiState !is AiUiState.Loading && aiState !is AiUiState.Streaming,
                            onClick = {
                                val query = aiPrompt
                                aiPrompt = ""
                                aiViewModel.askAssistant(query)
                            },
                            testTag = "gemini_ask_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                RoyalCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Human Support Ticket (${config.supportHours})", style = MaterialTheme.typography.labelLarge, color = RoyalBlueDeep)
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject (Order, Recharge, Withdrawal)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Describe your inquiry") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        BlueGradientButton(
                            text = "Send Support Message",
                            onClick = {
                                onSubmitTicket(subject, message)
                                subject = ""
                                message = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    EmptyStateCard(Icons.Filled.HeadsetMic, "No Support Tickets", "Submit a ticket above for assistance from Royal Bullet 350 support.")
                }
            } else {
                items(messages, key = { it.id }) { ticket ->
                    RoyalCard {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ticket.subject, style = MaterialTheme.typography.titleMedium)
                                StatusPill(ticket.status)
                            }
                            Text(ticket.message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            if (ticket.adminReply.isNotBlank()) {
                                Surface(color = RoyalSkyLight, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Admin Reply: ${ticket.adminReply}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = RoyalBlueDeep,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            Text(formatTimestamp(ticket.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegalInfoScreen(
    title: String,
    bodyText: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { RoyalTopBar(title = title, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            RoyalCard {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, color = RoyalBlueDeep)
                    Text(bodyText, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun BackendConfigScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { RoyalTopBar(title = "Firebase & Payment Config", onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RoyalCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = SuccessGreenBg, shape = RoundedCornerShape(50)) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Firebase AI Logic (Gemini API): Provisioned", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("App Check + gemini-3.8-flash active • Local Room wallet storage", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                        }
                    }

                    TransparentRuleBanner(
                        "Firebase AI Logic is provisioned with App Check attestation for the Gemini Rider Assistant in Support Chat. Wallet, orders, and referrals continue running on local Room storage."
                    )
                }
            }
        }
    }
}
