package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.MembershipPlanEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.ProductThumbnail
import com.example.ui.components.RoyalBulletLogoBadge
import com.example.ui.components.RoyalCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatInr
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueBright
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBlueElectric
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyBorder
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    user: UserEntity,
    products: List<ProductEntity>,
    membershipPlans: List<MembershipPlanEntity>,
    ledger: List<LedgerEntryEntity>,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onOpenMyOrders: () -> Unit,
    onOpenRecharge: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenInvite: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onOpenVipScreen: () -> Unit,
    onActivateMembership: (String) -> Unit,
    onOpenRecords: () -> Unit
) {
    val s = LocalStrings.current
    var selectedSegment by remember { mutableIntStateOf(0) } // 0 = Royal Enfield, 1 = VIP Cars
    var selectedPlanForBuy by remember { mutableStateOf<MembershipPlanEntity?>(null) }

    val cashbackAndReferralTotal = remember(ledger) {
        ledger.filter { it.type == "CASHBACK" || it.type == "REFERRAL_REWARD" }
            .sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IceBackground),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Large Royal Bullet 350 Motorcycle Banner + Logo + Blue Gradient Header (matching Screenshot 2)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(RoyalBlueDeep, RoyalBluePrimary, IceBackground)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RoyalBulletLogoBadge(size = 40.dp, showLabel = false)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = s.brandName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "Royal Enfield Bikes • Luxury Car Fleet",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.88f)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable(onClick = onOpenNews)
                                    .testTag("home_news_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.Campaign,
                                        contentDescription = s.companyNews,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.22f),
                                modifier = Modifier
                                    .clickable(onClick = onToggleLanguage)
                                    .testTag("home_language_button")
                            ) {
                                Text(
                                    text = language.label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }

                    // Hero Showroom Motorcycle Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .shadow(10.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = stringResource(id = R.string.cd_hero_banner),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            RoyalBlueDeep.copy(alpha = 0.82f),
                                            RoyalBluePrimary.copy(alpha = 0.28f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp)
                                .fillMaxWidth(0.65f)
                        ) {
                            Surface(
                                color = GoldAccent,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "FLAGSHIP EDITION",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ROYAL BULLET 350",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Royal Enfield Fleet & VIP Luxury Showcase",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Quick Action Buttons Card (My Orders, Recharge, Withdraw, Invite, Chat)
        item {
            RoyalCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    QuickActionCircle(
                        label = s.myOrders,
                        icon = Icons.Filled.ShoppingCart,
                        tint = RoyalBluePrimary,
                        testTag = "quick_orders",
                        onClick = onOpenMyOrders,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCircle(
                        label = s.recharge,
                        icon = Icons.Filled.AccountBalanceWallet,
                        tint = Color(0xFF0284C7),
                        testTag = "quick_recharge",
                        onClick = onOpenRecharge,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCircle(
                        label = s.withdraw,
                        icon = Icons.Filled.Payments,
                        tint = Color(0xFF0D9488),
                        testTag = "quick_withdraw",
                        onClick = onOpenWithdraw,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCircle(
                        label = s.invite,
                        icon = Icons.Filled.GroupAdd,
                        tint = Color(0xFF4F46E5),
                        testTag = "quick_invite",
                        onClick = onOpenInvite,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCircle(
                        label = s.chat,
                        icon = Icons.Filled.SupportAgent,
                        tint = RoyalBlueDeep,
                        testTag = "quick_chat",
                        onClick = onOpenChat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Income / Rewards Summary Section (Daily Rewards, Eligible Cashback, Transaction Summary)
        item {
            RoyalCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onOpenRecords
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryMetricTile(
                        label = s.accountBalance,
                        value = formatInr(user.availableBalance),
                        sub = "Min Withdraw ₹150",
                        accent = RoyalBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(42.dp)
                            .background(RoyalSkyBorder)
                    )
                    SummaryMetricTile(
                        label = s.eligibleCashback,
                        value = formatInr(cashbackAndReferralTotal),
                        sub = "Verified Earned",
                        accent = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(42.dp)
                            .background(RoyalSkyBorder)
                    )
                    SummaryMetricTile(
                        label = s.transactionSummary,
                        value = "${ledger.size} Entries",
                        sub = "View Ledger",
                        accent = RoyalBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Segmented Pill Switcher (Products | VIP Plans) matching Screenshot 2
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SegmentTabButton(
                        title = s.productsTab,
                        icon = Icons.Filled.ShoppingBag,
                        selected = selectedSegment == 0,
                        testTag = "home_segment_products",
                        onClick = { selectedSegment = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentTabButton(
                        title = s.vipPlansTab,
                        icon = Icons.Filled.Diamond,
                        selected = selectedSegment == 1,
                        testTag = "home_segment_vip",
                        onClick = { selectedSegment = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Royal Enfield Bikes OR VIP Luxury Cars (Displayed Separately)
        if (selectedSegment == 0) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        title = "Royal Enfield Motorcycles & Scooty",
                        actionText = "All Shop",
                        onAction = { products.firstOrNull()?.let { onOpenProductDetail(it.id) } }
                    )
                }
            }
            items(products, key = { it.id }) { product ->
                HomeProductCard(
                    product = product,
                    buyLabel = s.buyNow,
                    onClick = { onOpenProductDetail(product.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        title = "VIP Luxury Car Collection",
                        actionText = "Compare All",
                        onAction = onOpenVipScreen
                    )
                }
            }
            items(membershipPlans, key = { it.id }) { plan ->
                HomeMembershipCard(
                    plan = plan,
                    isCurrentOrHigher = user.vipTier >= plan.tierLevel,
                    activateLabel = s.activate,
                    onActivate = { selectedPlanForBuy = plan },
                    onOpenDetails = onOpenVipScreen,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    selectedPlanForBuy?.let { plan ->
        val spendable = user.depositBalance + user.availableBalance
        val canAfford = spendable >= plan.price
        AlertDialog(
            onDismissRequest = { selectedPlanForBuy = null },
            title = { Text(plan.name, fontWeight = FontWeight.Bold, color = RoyalBlueDeep) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProductThumbnail(
                        imageKey = plan.imageKey,
                        contentDescription = plan.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Text(
                        "Price: ${formatInr(plan.price)} • Cycle: ${plan.validityDays} days",
                        style = MaterialTheme.typography.titleMedium,
                        color = RoyalBluePrimary
                    )
                    Text(
                        "Daily Earning: ${formatInr(plan.dailyEarning)} • Total Revenue: ${formatInr(plan.totalRevenue)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SuccessGreen
                    )
                    Text(
                        "Your Spendable Balance: ${formatInr(spendable)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (canAfford) TextPrimary else Color(0xFFDC2626)
                    )
                    if (!canAfford) {
                        Text(
                            "Insufficient balance (${formatInr(plan.price - spendable)} more needed). Tap Recharge Now to add funds.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = TextSecondary
                        )
                    }
                }
            },
            confirmButton = {
                if (canAfford) {
                    BlueGradientButton(
                        text = "Confirm Buy (${formatInr(plan.price)})",
                        onClick = {
                            onActivateMembership(plan.id)
                            selectedPlanForBuy = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BlueGradientButton(
                        text = "Recharge Now",
                        onClick = {
                            selectedPlanForBuy = null
                            onOpenRecharge()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlanForBuy = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun QuickActionCircle(
    label: String,
    icon: ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(tint.copy(alpha = 0.16f), tint.copy(alpha = 0.06f))
                    )
                )
                .border(1.dp, tint.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SummaryMetricTile(
    label: String,
    value: String,
    sub: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            color = accent,
            maxLines = 1
        )
        Text(
            text = sub,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondary
        )
    }
}

@Composable
private fun SegmentTabButton(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    Brush.horizontalGradient(listOf(RoyalBluePrimary, RoyalBlueElectric))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else TextMuted,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else TextMuted
            )
        }
    }
}

@Composable
fun HomeProductCard(
    product: ProductEntity,
    buyLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoyalCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductThumbnail(
                imageKey = product.imageKey,
                contentDescription = product.name,
                modifier = Modifier
                    .size(106.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalSkyLight)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBlueDeep,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("product_buy_${product.id}")
                    ) {
                        Text(
                            text = buyLabel,
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                CardMetricLine(
                    label = "Daily Earning:",
                    value = formatInr(product.dailyEarning),
                    valueColor = TextPrimary
                )
                CardMetricLine(
                    label = "Total Revenue:",
                    value = formatInr(product.totalRevenue),
                    valueColor = TextPrimary
                )
                CardMetricLine(
                    label = "Cycle:",
                    value = "${product.cycleDays} days",
                    valueColor = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Price:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatInr(product.price),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeMembershipCard(
    plan: MembershipPlanEntity,
    isCurrentOrHigher: Boolean,
    activateLabel: String,
    onActivate: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoyalCard(modifier = modifier, onClick = onOpenDetails) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductThumbnail(
                imageKey = plan.imageKey,
                contentDescription = plan.name,
                modifier = Modifier
                    .size(106.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalSkyLight)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBlueDeep,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = if (isCurrentOrHigher) onOpenDetails else onActivate,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentOrHigher) SuccessGreen else RoyalBluePrimary
                        ),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("vip_buy_${plan.id}")
                    ) {
                        Text(
                            text = if (isCurrentOrHigher) "Active" else activateLabel,
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                CardMetricLine(
                    label = "Daily Earning:",
                    value = formatInr(plan.dailyEarning),
                    valueColor = TextPrimary
                )
                CardMetricLine(
                    label = "Total Revenue:",
                    value = formatInr(plan.totalRevenue),
                    valueColor = TextPrimary
                )
                CardMetricLine(
                    label = "Cycle:",
                    value = "${plan.validityDays} days",
                    valueColor = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Price:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatInr(plan.price),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CardMetricLine(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
            color = TextMuted
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            ),
            color = valueColor
        )
    }
}
