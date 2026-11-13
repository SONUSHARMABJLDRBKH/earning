package com.example.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfigEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.MembershipSubscriptionEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.RechargeEntity
import com.example.data.local.ReferralRecordEntity
import com.example.data.local.SupportMessageEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.ProductThumbnail
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    users: List<UserEntity>,
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    recharges: List<RechargeEntity>,
    withdrawals: List<WithdrawalEntity>,
    referrals: List<ReferralRecordEntity>,
    subscriptions: List<MembershipSubscriptionEntity>,
    ledger: List<LedgerEntryEntity>,
    supportMessages: List<SupportMessageEntity>,
    config: AppConfigEntity,
    onBack: () -> Unit,
    onVerifyRecharge: (id: String, approve: Boolean, note: String) -> Unit,
    onFulfillOrder: (orderId: String) -> Unit,
    onProcessWithdrawal: (id: String, status: String, note: String) -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    onToggleBlockUser: (UserEntity) -> Unit,
    onRejectReferral: (ReferralRecordEntity) -> Unit,
    onUpdateConfig: (telegramUrl: String, upiId: String, appInstallUrl: String) -> Unit,
    onPublishNews: (title: String, summary: String, body: String, pinned: Boolean) -> Unit,
    onReplySupport: (SupportMessageEntity, reply: String) -> Unit
) {
    val tabs = listOf(
        "Reports", "Recharges", "Orders", "Withdrawals", "Products", "Users & Refs", "Config & News"
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddProduct by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { RoyalTopBar(title = "Admin Control Panel", onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs.indices.toList()) { idx ->
                    FilterChip(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        label = { Text(tabs[idx]) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("admin_tab_$idx")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> { // Reports
                        val verifiedRechargeSum = recharges.filter { it.status == "Successful" }.sumOf { it.amount }
                        val paidWithdrawSum = withdrawals.filter { it.status == "Paid" }.sumOf { it.amount }
                        val fulfilledOrdersCount = orders.count { it.status == "Fulfilled" }
                        item {
                            RoyalCard {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Platform Transaction Reports", style = MaterialTheme.typography.titleLarge, color = RoyalBlueDeep)
                                    Text("Registered Users: ${users.size}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Active VIP Subscriptions: ${subscriptions.count { it.status == "Active" }}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Pending Recharges: ${recharges.count { it.status == "Pending" }}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Verified Deposit Volume: ${formatInr(verifiedRechargeSum)}", style = MaterialTheme.typography.bodyLarge, color = SuccessGreen)
                                    Text("Orders Fulfilled / Total: $fulfilledOrdersCount / ${orders.size}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Pending Withdrawals: ${withdrawals.count { it.status == "Pending" }}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Completed Payouts: ${formatInr(paidWithdrawSum)}", style = MaterialTheme.typography.bodyLarge, color = RoyalBluePrimary)
                                    Text("Ledger Entries Recorded: ${ledger.size}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                }
                            }
                        }
                    }

                    1 -> { // Recharges
                        items(recharges, key = { it.id }) { rec ->
                            RoyalCard {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${formatInr(rec.amount)} • ${rec.method}", style = MaterialTheme.typography.titleMedium)
                                        StatusPill(rec.status)
                                    }
                                    Text("User: ${rec.userId} (+91 ${rec.userMobile}) • UTR: ${rec.referenceId}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    if (rec.status == "Pending") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = { onVerifyRecharge(rec.id, true, "UTR Verified by Admin") },
                                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Verify & Credit", color = Color.White)
                                            }
                                            OutlinedButton(
                                                onClick = { onVerifyRecharge(rec.id, false, "UTR Not Found") },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Mark Failed", color = ErrorRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> { // Orders
                        items(orders, key = { it.id }) { order ->
                            RoyalCard {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${order.id} • ${order.productName}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                        StatusPill(order.status)
                                    }
                                    Text("Buyer: ${order.userId} • Paid: ${formatInr(order.paidFromBalance)} • Ship: ${order.shippingAddress}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                                    if (order.status == "Paid") {
                                        BlueGradientButton(
                                            text = "Mark Order Fulfilled & Evaluate Referral",
                                            onClick = { onFulfillOrder(order.id) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    3 -> { // Withdrawals
                        items(withdrawals, key = { it.id }) { w ->
                            RoyalCard {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${formatInr(w.amount)} (Net ${formatInr(w.netAmount)})", style = MaterialTheme.typography.titleMedium)
                                        StatusPill(w.status)
                                    }
                                    Text(
                                        "User: ${w.userId} • ${w.payoutMethod}: ${if (w.payoutMethod == "UPI") w.upiId else "${w.bankAccountNumber} / ${w.ifscCode}"} • ${w.accountHolderName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = TextSecondary
                                    )
                                    if (w.status == "Pending" || w.status == "Approved") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (w.status == "Pending") {
                                                Button(
                                                    onClick = { onProcessWithdrawal(w.id, "Approved", "Approved for payout") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Approve", color = Color.White)
                                                }
                                            }
                                            Button(
                                                onClick = { onProcessWithdrawal(w.id, "Paid", "Bank/UPI payout settled") },
                                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Mark Paid", color = Color.White)
                                            }
                                            OutlinedButton(
                                                onClick = { onProcessWithdrawal(w.id, "Rejected", "Rejected & refunded") },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject", color = ErrorRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> { // Products
                        item {
                            BlueGradientButton(
                                text = "Add New Product",
                                onClick = { showAddProduct = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        items(products, key = { it.id }) { prod ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProductThumbnail(
                                        imageKey = prod.imageKey,
                                        contentDescription = prod.name,
                                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prod.name, style = MaterialTheme.typography.titleMedium)
                                        Text("${prod.category} • ${formatInr(prod.price)}", style = MaterialTheme.typography.labelLarge, color = RoyalBluePrimary)
                                    }
                                    OutlinedButton(onClick = { editingProduct = prod }) {
                                        Text("Edit")
                                    }
                                }
                            }
                        }
                    }

                    5 -> { // Users & Referrals
                        item {
                            Text("Users (${users.size})", style = MaterialTheme.typography.titleMedium)
                        }
                        items(users, key = { it.id }) { u ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${u.id} • +91 ${u.mobile} (${u.role})", style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp))
                                        Text(
                                            "Avail: ${formatInr(u.availableBalance)} | Dep: ${formatInr(u.depositBalance)} | Promo: ${formatInr(u.promotionalBalance)}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = TextSecondary
                                        )
                                    }
                                    if (u.role != "ADMIN") {
                                        OutlinedButton(onClick = { onToggleBlockUser(u) }) {
                                            Text(if (u.isBlocked) "Unblock" else "Block", color = if (u.isBlocked) SuccessGreen else ErrorRed)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Referral Eligibility Audit (${referrals.size})", style = MaterialTheme.typography.titleMedium)
                        }
                        items(referrals, key = { it.id }) { ref ->
                            RoyalCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${ref.referrerUserId} -> ${ref.referredUserId}", style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp))
                                        Text(
                                            "A Qualified: ${ref.referrerQualified} | B Qualified: ${ref.referredQualified}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = TextSecondary
                                        )
                                        StatusPill(ref.status)
                                    }
                                    if (ref.status != "Reward Paid" && ref.status != "Rejected") {
                                        OutlinedButton(onClick = { onRejectReferral(ref) }) {
                                            Text("Flag Fraud", color = ErrorRed)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    6 -> { // Config, News & Support
                        item {
                            AdminConfigNewsSupportSection(
                                config = config,
                                supportMessages = supportMessages,
                                onUpdateConfig = onUpdateConfig,
                                onPublishNews = onPublishNews,
                                onReplySupport = onReplySupport
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddProduct || editingProduct != null) {
        ProductEditorDialog(
            initial = editingProduct,
            onDismiss = {
                showAddProduct = false
                editingProduct = null
            },
            onSave = { prod ->
                onSaveProduct(prod)
                showAddProduct = false
                editingProduct = null
            }
        )
    }
}

@Composable
private fun AdminConfigNewsSupportSection(
    config: AppConfigEntity,
    supportMessages: List<SupportMessageEntity>,
    onUpdateConfig: (telegramUrl: String, upiId: String, appInstallUrl: String) -> Unit,
    onPublishNews: (title: String, summary: String, body: String, pinned: Boolean) -> Unit,
    onReplySupport: (SupportMessageEntity, reply: String) -> Unit
) {
    var telegramUrl by remember(config.telegramUrl) { mutableStateOf(config.telegramUrl) }
    var upiId by remember(config.upiMerchantId) { mutableStateOf(config.upiMerchantId) }
    var appInstallUrl by remember(config.appInstallUrl) { mutableStateOf(config.appInstallUrl) }

    var newsTitle by remember { mutableStateOf("") }
    var newsBody by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        RoyalCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("App Install Link & Merchant UPI Config", style = MaterialTheme.typography.titleMedium, color = RoyalBlueDeep)
                OutlinedTextField(
                    value = appInstallUrl,
                    onValueChange = { appInstallUrl = it },
                    label = { Text("App Install / Play Store / APK Download URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Default opens Google Play Store once published. Before Play Store goes live, paste your hosted APK link (e.g. Google Drive) so shared links install the APK directly.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.5.sp),
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = telegramUrl,
                    onValueChange = { telegramUrl = it },
                    label = { Text("Support / Channel URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Merchant UPI ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                BlueGradientButton(
                    text = "Save Config",
                    onClick = { onUpdateConfig(telegramUrl, upiId, appInstallUrl) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        RoyalCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Publish Company News", style = MaterialTheme.typography.titleMedium, color = RoyalBlueDeep)
                OutlinedTextField(
                    value = newsTitle,
                    onValueChange = { newsTitle = it },
                    label = { Text("Headline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newsBody,
                    onValueChange = { newsBody = it },
                    label = { Text("Announcement Body") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                BlueGradientButton(
                    text = "Publish News",
                    onClick = {
                        onPublishNews(newsTitle, newsBody.take(80), newsBody, false)
                        newsTitle = ""
                        newsBody = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text("Customer Support Inbox (${supportMessages.size})", style = MaterialTheme.typography.titleMedium)
        supportMessages.forEach { ticket ->
            var replyText by remember(ticket.id) { mutableStateOf(ticket.adminReply) }
            RoyalCard {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${ticket.subject} (${ticket.userId})", style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp))
                        StatusPill(ticket.status)
                    }
                    Text(ticket.message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(formatTimestamp(ticket.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Admin Reply") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onReplySupport(ticket, replyText) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary)
                    ) {
                        Text("Send Reply", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductEditorDialog(
    initial: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "Motorcycles") }
    var priceText by remember { mutableStateOf(initial?.price?.toInt()?.toString() ?: "700") }
    var dailyText by remember { mutableStateOf(initial?.dailyEarning?.toInt()?.toString() ?: "120") }
    var totalRevText by remember { mutableStateOf(initial?.totalRevenue?.toInt()?.toString() ?: "3600") }
    var cycleText by remember { mutableStateOf(initial?.cycleDays?.toString() ?: "30") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var imageKey by remember { mutableStateOf(initial?.imageKey ?: "re_black_bike") }

    // Play-Policy compliant zero-permission Android Photo Picker for Admin product images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageKey = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Product" else "Edit Product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Price (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cycleText,
                        onValueChange = { cycleText = it.filter { c -> c.isDigit() } },
                        label = { Text("Cycle (Days)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dailyText,
                        onValueChange = { dailyText = it.filter { c -> c.isDigit() } },
                        label = { Text("Daily Earning (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalRevText,
                        onValueChange = { totalRevText = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Revenue (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2
                )
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick Product Image (Photo Picker)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 700.0
                    val daily = dailyText.toDoubleOrNull() ?: 28.0
                    val totalRev = totalRevText.toDoubleOrNull() ?: 840.0
                    val cycle = cycleText.toIntOrNull() ?: 30
                    onSave(
                        ProductEntity(
                            id = initial?.id ?: "prod_${UUID.randomUUID().toString().take(6)}",
                            name = name.ifBlank { "Royal Bullet 350 Accessory" },
                            category = category.ifBlank { "Riding Gear" },
                            price = price,
                            dailyEarning = daily,
                            totalRevenue = totalRev,
                            cycleDays = cycle,
                            description = description.ifBlank { "Genuine Royal Bullet 350 product." },
                            specifications = initial?.specifications ?: "OEM Genuine Royal Bullet 350 Spec",
                            imageKey = imageKey,
                            fulfillmentNote = initial?.fulfillmentNote ?: "Dispatched within 48 hours."
                        )
                    )
                }
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
